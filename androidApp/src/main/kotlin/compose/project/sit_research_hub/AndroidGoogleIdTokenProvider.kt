package compose.project.sit_research_hub

import android.app.Activity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import compose.project.sit_research_hub.domain.model.AppResult
import compose.project.sit_research_hub.domain.model.AuthException
import compose.project.sit_research_hub.domain.repository.GoogleCredentialResult
import compose.project.sit_research_hub.domain.repository.GoogleIdTokenProvider
import java.security.MessageDigest
import java.util.UUID

class AndroidGoogleIdTokenProvider(
    private val activity: Activity,
    private val webClientId: String
) : GoogleIdTokenProvider {
    private val credentialManager = CredentialManager.create(activity)

    override suspend fun getCredential(): AppResult<GoogleCredentialResult> {
        if (webClientId.isBlank()) return AppResult.Failure(AuthException("Google sign-in is not configured. Add GOOGLE_WEB_CLIENT_ID to local.properties."))
        val rawNonce = UUID.randomUUID().toString()
        val hashedNonce = MessageDigest.getInstance("SHA-256")
            .digest(rawNonce.toByteArray())
            .joinToString("") { "%02x".format(it) }
        val option = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setNonce(hashedNonce)
            .setAutoSelectEnabled(false)
            .build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
        return try {
            val response = try {
                credentialManager.getCredential(activity, request)
            } catch (_: NoCredentialException) {
                // The chooser flow can return no credential when the device has no
                // previously usable Google account. Google's explicit button flow
                // lets the user choose or add an account in that case.
                val buttonOption = GetSignInWithGoogleOption.Builder(webClientId)
                    .setNonce(hashedNonce)
                    .build()
                val buttonRequest = GetCredentialRequest.Builder()
                    .addCredentialOption(buttonOption)
                    .build()
                credentialManager.getCredential(activity, buttonRequest)
            }
            val credential = response.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val google = GoogleIdTokenCredential.createFrom(credential.data)
                val email = google.email ?: return AppResult.Failure(AuthException("Google did not provide an email address."))
                AppResult.Success(GoogleCredentialResult.IdToken(google.idToken, rawNonce, email))
            } else AppResult.Failure(AuthException("Unexpected Google credential type."))
        } catch (_: GetCredentialCancellationException) {
            AppResult.Failure(AuthException("Sign-in was cancelled."))
        } catch (_: NoCredentialException) {
            AppResult.Failure(AuthException("No Google account is available. Add a Google account to this device, then try again."))
        } catch (e: GetCredentialException) {
            AppResult.Failure(AuthException("Google sign-in failed: ${e.message ?: "Please try again."}"))
        } catch (_: GoogleIdTokenParsingException) {
            AppResult.Failure(AuthException("Could not read the Google sign-in response."))
        }
    }
}

