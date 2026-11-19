
package compose.project.sit_research_hub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class BottomNavTab {
    HOME,
    RESEARCHES,
    SETTINGS
}

@Composable
fun BottomBar(
    selectedTab: BottomNavTab,
    onHomeClick: () -> Unit,
    onResearchesClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomBarItem("⌂", "Home", selectedTab == BottomNavTab.HOME, onHomeClick)
        BottomBarItem("▤", "Your Researches", selectedTab == BottomNavTab.RESEARCHES, onResearchesClick)
        BottomBarItem("⚙", "Settings", selectedTab == BottomNavTab.SETTINGS, onSettingsClick)
    }
}

@Composable
private fun RowScope.BottomBarItem(
    icon: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = icon,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 21.sp
        )

        Text(
            text = label,
            color = MaterialTheme.colorScheme.onPrimary.copy(
                alpha = if (selected) 1f else 0.72f
            ),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
