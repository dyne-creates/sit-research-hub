package compose.project.sit_research_hub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import compose.project.sit_research_hub.di.AppConfig
import compose.project.sit_research_hub.di.initKoin
import org.koin.core.context.GlobalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (GlobalContext.getOrNull() == null) {
            initKoin(AppConfig(
                supabaseUrl = BuildConfig.SUPABASE_URL,
                supabaseAnonKey = BuildConfig.SUPABASE_ANON_KEY,
                googleWebClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID,
                allowedEmailDomain = BuildConfig.ALLOWED_EMAIL_DOMAIN
            ))
        }
        val googleProvider = AndroidGoogleIdTokenProvider(this, BuildConfig.GOOGLE_WEB_CLIENT_ID)

        setContent {
            App(googleProvider)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
}
