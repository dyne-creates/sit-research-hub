package compose.project.sit_research_hub.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import compose.project.sit_research_hub.presentation.auth.AuthUiState

@Composable
fun SignInScreen(
    uiState: AuthUiState,
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
    onCancel: () -> Unit
) {
    Surface(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().safeDrawingPadding().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(Modifier.widthIn(max = 440.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("SIT Research Hub", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Continue with your @s.ubaguio.edu Google account. Choose sign up if this is your first time here.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(24.dp))
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text("Finish Google sign-in in your browser if prompted.", textAlign = TextAlign.Center)
                    TextButton(onClick = onCancel) { Text("Cancel") }
                } else {
                    Button(onClick = onSignIn) { Text("Sign in with Google") }
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = onSignUp) { Text("Sign up with Google") }
                }
                if (uiState is AuthUiState.Error) {
                    Spacer(Modifier.height(16.dp))
                    Text(uiState.message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

