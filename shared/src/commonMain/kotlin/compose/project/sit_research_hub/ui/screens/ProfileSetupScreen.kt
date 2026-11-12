
package compose.project.sit_research_hub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PrimaryBlue = Color(0xFF263A63)
private val SecondaryBlue = Color(0xFF4776B4)
private val BackgroundColor = Color(0xFFF4F6FA)
private val CardColor = Color.White
private val TextColor = Color(0xFF252B36)
private val BorderColor = Color(0xFFD5DCE8)

@Composable
fun ProfileSetupScreen(
    onContinue: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(420.dp)
                .fillMaxHeight(0.85f)
                .background(
                    CardColor,
                    RoundedCornerShape(22.dp)
                )
                .border(
                    1.dp,
                    BorderColor,
                    RoundedCornerShape(22.dp)
                )
                .padding(horizontal = 28.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            // SIT LOGO PLACEHOLDER
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .background(
                        PrimaryBlue,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SIT",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // WELCOME MESSAGE
            Text(
                text = "Welcome to SIT\nResearch Hub!",
                color = PrimaryBlue,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Let's personalize your feed so you\nwill see studies you qualify for.",
                color = TextColor,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // DIVIDER
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BorderColor)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ACADEMIC PROGRAM
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Your Academic Program",
                    color = TextColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                SelectionField(
                    text = "Select Program"
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // YEAR LEVEL
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Year Level",
                    color = TextColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                SelectionField(
                    text = "Enter Year Level"
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BorderColor)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // PRIVACY AGREEMENT - VISUAL ONLY
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(17.dp)
                        .border(
                            1.5.dp,
                            SecondaryBlue,
                            RoundedCornerShape(4.dp)
                        )
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "I agree to the Privacy Policy & Data\nCollection Terms. Read",
                    color = TextColor,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // CONTINUE
            TextButton(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Continue to Home Page",
                    color = SecondaryBlue,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                )
            }
        }
    }
}

@Composable
private fun SelectionField(
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .border(
                1.dp,
                BorderColor,
                RoundedCornerShape(8.dp)
            )
            .background(
                CardColor,
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            color = Color(0xFF858D9B),
            fontSize = 14.sp
        )

        Text(
            text = "⌄",
            color = SecondaryBlue,
            fontSize = 22.sp
        )
    }
}
