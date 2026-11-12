
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SettingsNavy = Color(0xFF263A63)
private val SettingsBlue = Color(0xFF4776B4)
private val SettingsBackground = Color(0xFFF4F6FA)
private val SettingsText = Color(0xFF252B36)
private val SettingsBorder = Color(0xFFD5DCE8)
private val SettingsWhite = Color.White

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SettingsBackground),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .width(420.dp)
                .weight(1f)
                .background(SettingsWhite)
        ) {
            // HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(SettingsNavy)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Settings",
                    color = SettingsWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // SETTINGS CONTENT
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // APPEARANCE
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Appearance",
                        color = SettingsNavy,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Theme",
                        color = SettingsText,
                        fontSize = 13.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ThemeOption(
                            text = "Light",
                            selected = true
                        )

                        ThemeOption(
                            text = "Dark",
                            selected = false
                        )
                    }
                }

                // RESEARCH PREFERENCES
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Research Preferences",
                        color = SettingsNavy,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Sort Order",
                        color = SettingsText,
                        fontSize = 13.sp
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                SettingsBorder,
                                RoundedCornerShape(9.dp)
                            )
                            .clip(RoundedCornerShape(9.dp))
                    ) {
                        SortOption(
                            text = "Newest First",
                            selected = true
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(SettingsBorder)
                        )

                        SortOption(
                            text = "Oldest First",
                            selected = false
                        )
                    }
                }

                // INFORMATION
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Information",
                        color = SettingsNavy,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    InformationOption(
                        title = "Privacy Policy",
                        description = "Learn how your information is handled."
                    )

                    InformationOption(
                        title = "About Research Hub",
                        description = "Learn more about SIT Research Hub."
                    )
                }
            }

            // BOTTOM NAVIGATION
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .background(SettingsNavy),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SettingsBottomItem(
                    symbol = "◆",
                    label = "Home",
                    selected = false,
                    onClick = onBackClick
                )

                SettingsBottomItem(
                    symbol = "▣",
                    label = "Your\nResearches",
                    selected = false,
                    onClick = {}
                )

                SettingsBottomItem(
                    symbol = "⚙",
                    label = "Settings",
                    selected = true,
                    onClick = {}
                )
            }
        }
    }
}

@Composable
private fun RowScope.ThemeOption(
    text: String,
    selected: Boolean
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(40.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(
                if (selected) SettingsNavy else SettingsWhite
            )
            .border(
                1.dp,
                SettingsBlue,
                RoundedCornerShape(7.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) SettingsWhite else SettingsNavy,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SortOption(
    text: String,
    selected: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (selected) "●" else "○",
            color = SettingsBlue,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = text,
            color = SettingsText,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun InformationOption(
    title: String,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(9.dp))
            .border(
                1.dp,
                SettingsBorder,
                RoundedCornerShape(9.dp)
            )
            .background(SettingsWhite)
            .padding(12.dp)
    ) {
        Text(
            text = title,
            color = SettingsNavy,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = description,
            color = SettingsText,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun RowScope.SettingsBottomItem(
    symbol: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxSize()
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = symbol,
            color = if (selected) SettingsWhite else Color(0xFFD5DCE8),
            fontSize = 22.sp
        )

        Text(
            text = label,
            color = SettingsWhite,
            fontSize = 9.sp,
            lineHeight = 10.sp
        )
    }
}
