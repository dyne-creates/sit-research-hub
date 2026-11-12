
package compose.project.sit_research_hub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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

private val Navy = Color(0xFF263A63)
private val Blue = Color(0xFF4776B4)
private val Background = Color(0xFFF4F6FA)
private val DarkText = Color(0xFF252B36)
private val BorderColor = Color(0xFFD5DCE8)
private val White = Color.White

@Composable
fun HomeScreen(
    onResearchClick: (String) -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .width(420.dp)
                .weight(1f)
                .background(White)
        ) {
            // HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(Navy)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Research Hub",
                    color = White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Medium
                )

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .border(1.5.dp, White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "♙",
                        color = White,
                        fontSize = 21.sp
                    )
                }
            }

            // SEARCH BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 15.dp)
                    .height(43.dp)
                    .border(1.dp, BorderColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⌕",
                    color = DarkText,
                    fontSize = 27.sp
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Search",
                    color = DarkText,
                    fontSize = 14.sp
                )
            }

            // RESEARCH FILTERS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip("All", true)

                Spacer(modifier = Modifier.width(5.dp))

                FilterChip("Qualitative", false)

                Spacer(modifier = Modifier.width(5.dp))

                FilterChip("Quantitative", false)

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "▼",
                    color = Navy,
                    fontSize = 21.sp
                )
            }

            // RESEARCH CARDS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResearchCard {
                    onResearchClick("1")
                }

                ResearchCard {
                    onResearchClick("2")
                }
            }

            // BOTTOM NAVIGATION
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .background(Navy),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    symbol = "◆",
                    label = "Home",
                    selected = true
                )

                BottomNavItem(
                    symbol = "▣",
                    label = "Your\nResearches",
                    selected = false
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clickable { onSettingsClick() },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "⚙",
                        color = White,
                        fontSize = 23.sp
                    )
                    Text(
                        text = "Settings",
                        color = White,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    selected: Boolean
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .background(if (selected) Navy else White)
            .border(
                width = 1.dp,
                color = if (selected) Navy else Blue,
                shape = RoundedCornerShape(5.dp)
            )
            .padding(horizontal = 9.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) White else Navy,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ResearchCard(
    onReadMore: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Blue,
                shape = RoundedCornerShape(13.dp)
            )
            .clip(RoundedCornerShape(13.dp))
            .background(White)
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Research Title",
                color = Navy,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.width(7.dp))

            Box(
                modifier = Modifier
                    .background(
                        Color(0xFFEAF0FA),
                        RoundedCornerShape(3.dp)
                    )
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Qualitative",
                    color = Navy,
                    fontSize = 8.sp
                )
            }
        }

        Text(
            text = "Respondents Qualification",
            color = DarkText,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = "• Qualification 1\n• Qualification 2\n• Qualification 3",
            color = DarkText,
            fontSize = 10.sp,
            lineHeight = 14.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(Blue)
                    .clickable { onReadMore() }
                    .padding(horizontal = 9.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Read More",
                    color = White,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
private fun RowScope.BottomNavItem(
    symbol: String,
    label: String,
    selected: Boolean
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = symbol,
            color = if (selected) White else Color(0xFFD5DCE8),
            fontSize = 22.sp
        )

        Text(
            text = label,
            color = White,
            fontSize = 9.sp,
            lineHeight = 10.sp
        )
    }
}
