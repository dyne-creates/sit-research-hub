package compose.project.sit_research_hub

import com.sun.net.httpserver.HttpServer
import compose.project.sit_research_hub.domain.model.AppResult
import compose.project.sit_research_hub.domain.model.AuthException
import compose.project.sit_research_hub.domain.repository.GoogleCredentialResult
import compose.project.sit_research_hub.domain.repository.GoogleIdTokenProvider
import java.awt.Desktop
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import kotlin.coroutines.resume
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout

private const val DESKTOP_REDIRECT_PORT = 53682

private fun codeVerifier(): String = ByteArray(32).also(SecureRandom()::nextBytes)
    .let { Base64.getUrlEncoder().withoutPadding().encodeToString(it) }

private fun codeChallenge(verifier: String): String = Base64.getUrlEncoder().withoutPadding()
    .encodeToString(MessageDigest.getInstance("SHA-256").digest(verifier.toByteArray(StandardCharsets.US_ASCII)))

class DesktopGoogleSignInProvider(
    private val supabaseUrl: String,
    private val allowedEmailDomain: String,
    private val timeoutMillis: Long = 120_000
) : GoogleIdTokenProvider {
    override suspend fun getCredential(): AppResult<GoogleCredentialResult> {
        if (supabaseUrl.isBlank()) return AppResult.Failure(AuthException("Supabase sign-in is not configured. Add SUPABASE_URL to local.properties."))
        val verifier = codeVerifier()
        val redirectUri = "http://localhost:$DESKTOP_REDIRECT_PORT/callback"
        val authorizeUrl = supabaseUrl.trimEnd('/') + "/auth/v1/authorize" +
            "?provider=google" +
            "&redirect_to=${enc(redirectUri)}" +
            "&code_challenge=${enc(codeChallenge(verifier))}" +
            "&code_challenge_method=s256" +
            "&prompt=select_account" +
            "&hd=${enc(allowedEmailDomain)}"
        return try {
            val params = withTimeout(timeoutMillis) { awaitCallback { openBrowser(authorizeUrl) } }
            val error = params["error_description"] ?: params["error"]
            when {
                error != null -> AppResult.Failure(AuthException(error))
                params["code"] == null -> AppResult.Failure(AuthException("Google did not return a sign-in code. Try again."))
                else -> AppResult.Success(GoogleCredentialResult.AuthCode(params.getValue("code"), verifier))
            }
        } catch (_: TimeoutCancellationException) {
            AppResult.Failure(AuthException("Sign-in timed out. Please try again."))
        } catch (_: java.net.BindException) {
            AppResult.Failure(AuthException("The desktop sign-in callback port is busy. Close the other app and try again."))
        }
    }

    private suspend fun awaitCallback(onReady: () -> Unit): Map<String, String> {
        val server = HttpServer.create(InetSocketAddress(InetAddress.getLoopbackAddress(), DESKTOP_REDIRECT_PORT), 0)
        try {
            return suspendCancellableCoroutine { continuation ->
                server.createContext("/callback") { exchange ->
                    val params = exchange.requestURI.rawQuery.orEmpty().split('&').mapNotNull { pair ->
                        val index = pair.indexOf('=')
                        if (index < 0) null else URLDecoder.decode(pair.substring(0, index), StandardCharsets.UTF_8) to
                            URLDecoder.decode(pair.substring(index + 1), StandardCharsets.UTF_8)
                    }.toMap()
                    val bytes = "<html><body><h2>SIT Research Hub</h2><p>You can close this tab and return to the app.</p></body></html>"
                        .toByteArray(StandardCharsets.UTF_8)
                    exchange.responseHeaders.add("Content-Type", "text/html; charset=utf-8")
                    exchange.sendResponseHeaders(200, bytes.size.toLong())
                    exchange.responseBody.use { it.write(bytes) }
                    if (continuation.isActive) continuation.resume(params)
                }
                server.start()
                onReady()
            }
        } finally {
            server.stop(0)
        }
    }

    private fun openBrowser(url: String) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(URI(url))
                return
            }
        } catch (_: Exception) {
            // Fall back to a copyable link in headless or restricted desktop environments.
        }
        println("Open this link in your browser to sign in:\n$url")
    }

    private fun enc(value: String) = URLEncoder.encode(value, StandardCharsets.UTF_8)
}
