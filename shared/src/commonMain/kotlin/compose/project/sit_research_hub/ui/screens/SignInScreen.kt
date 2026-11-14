
package compose.project.sit_research_hub.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource

import compose.project.sit_research_hub.presentation.auth.AuthUiState
import sit_research_hub.shared.generated.resources.Res
import sit_research_hub.shared.generated.resources.sit_logo

private val BackgroundBlue = Color(0xFFF1F4FA)
private val NavyBlue = Color(0xFF263D68)
private val PrimaryBlue = Color(0xFF477DBB)
private val BorderBlue = Color(0xFF8DA9D1)
private val TextBlue = Color(0xFF344767)

@Composable
fun SignInScreen(
    uiState: AuthUiState,
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
    onCancel: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = BackgroundBlue
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 460.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 55.dp)
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(36.dp)
                        )
                        .border(
                            width = 1.5.dp,
                            color = BorderBlue,
                            shape = RoundedCornerShape(36.dp)
                        )
                        .padding(
                            horizontal = 28.dp,
                            vertical = 30.dp
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(Modifier.height(55.dp))

                    Text(
                        text = "WELCOME TO",
                        color = NavyBlue,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(Modifier.height(28.dp))

                    Text(
                        text = "School of Information\nTechnology",
                        color = NavyBlue,
                        fontSize = 27.sp,
                        lineHeight = 34.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Research Hub",
                        color = TextBlue,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(Modifier.height(28.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.88f)
                            .height(2.dp)
                            .background(BorderBlue)
                    )

                    Spacer(Modifier.height(28.dp))

                    if (uiState is AuthUiState.Loading) {
                        CircularProgressIndicator(
                            color = PrimaryBlue
                        )

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Complete Google sign-in in your browser if prompted.",
                            color = TextBlue,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(12.dp))

                        TextButton(onClick = onCancel) {
                            Text(
                                text = "Cancel",
                                color = NavyBlue
                            )
                        }
                    } else {
                        Button(
                            onClick = onSignIn,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NavyBlue,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Sign in with Google",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        OutlinedButton(
                            onClick = onSignUp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, PrimaryBlue)
                        ) {
                            Text(
                                text = "Sign up with Google",
                                color = NavyBlue,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (uiState is AuthUiState.Error) {
                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = uiState.message,
                            color = Color(0xFFB3261E),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    Text(
                        text = "SIT © 2026",
                        color = TextBlue,
                        fontSize = 15.sp
                    )
                }

                Image(
                    painter = painterResource(Res.drawable.sit_logo),
                    contentDescription = "SIT Research Hub logo",
                    modifier = Modifier
                        .offset(y = 5.dp)
                        .size(112.dp)
                        .background(Color.White, CircleShape)
                        .padding(5.dp)
                )
            }
        }
    }
}
