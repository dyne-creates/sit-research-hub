
package compose.project.sit_research_hub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DetailNavy = Color(0xFF263A63)
private val DetailBlue = Color(0xFF4776B4)
private val DetailBackground = Color(0xFFF4F6FA)
private val DetailText = Color(0xFF252B36)
private val DetailBorder = Color(0xFFD5DCE8)
private val DetailWhite = Color.White

@Composable
fun StudyDetailScreen(
    researchId: String,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DetailBackground),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .width(420.dp)
                .weight(1f)
                .background(DetailWhite)
        ) {
            // HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(DetailNavy)
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "←",
                    color = DetailWhite,
                    fontSize = 30.sp,
                    modifier = Modifier.clickable {
                        onBackClick()
                    }
                )

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = "Research Title",
                    color = DetailWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // RESEARCH TYPE TABS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DetailTab(
                    text = "Qualitative",
                    selected = true
                )

                DetailTab(
                    text = "Quantitative",
                    selected = false
                )
            }

            // MAIN CONTENT
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // RESEARCH INFORMATION
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            1.dp,
                            DetailBlue,
                            RoundedCornerShape(14.dp)
                        )
                        .background(DetailWhite)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Research Title",
                        color = DetailNavy,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Add Description",
                        color = DetailText,
                        fontSize = 14.sp
                    )

                    Text(
                        text = "Researchers",
                        color = DetailText,
                        fontSize = 14.sp
                    )

                    Text(
                        text = "Respondents Qualification",
                        color = DetailText,
                        fontSize = 14.sp
                    )
                }

                // INTERVIEW SCHEDULE
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            1.dp,
                            DetailBlue,
                            RoundedCornerShape(12.dp)
                        )
                        .background(DetailWhite)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Interview Schedule",
                        color = DetailNavy,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            ScheduleItem("Date")
                            ScheduleItem("Time")
                            ScheduleItem("Room")
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(DetailBlue)
                                .clickable { }
                                .padding(
                                    horizontal = 9.dp,
                                    vertical = 6.dp
                                )
                        ) {
                            Text(
                                text = "Choose Schedule",
                                color = DetailWhite,
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                // EMAIL FIELD
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(
                            1.dp,
                            DetailBlue,
                            RoundedCornerShape(12.dp)
                        )
                        .background(DetailWhite)
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Email:",
                        color = DetailNavy,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "(Required field for contact purposes)",
                        color = DetailText,
                        fontSize = 10.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(DetailBorder)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // SUBMIT BUTTON
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .width(125.dp)
                            .height(36.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(DetailBlue)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Submit",
                            color = DetailWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailTab(
    text: String,
    selected: Boolean
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .background(
                if (selected) DetailNavy else DetailWhite
            )
            .border(
                1.dp,
                DetailBlue,
                RoundedCornerShape(5.dp)
            )
            .padding(horizontal = 11.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = if (selected) DetailWhite else DetailNavy,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ScheduleItem(
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "•",
            color = DetailBlue,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = text,
            color = DetailText,
            fontSize = 11.sp
        )
    }
}
