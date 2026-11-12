package compose.project.sit_research_hub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.project.sit_research_hub.domain.model.SortOrder
import compose.project.sit_research_hub.domain.model.ThemeMode
import compose.project.sit_research_hub.presentation.settings.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onBackClick: () -> Unit) {
    val settings = viewModel.settings.collectAsState().value
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), horizontalAlignment = Alignment.CenterHorizontally) {
        Column(Modifier.fillMaxWidth().widthIn(max = 640.dp).weight(1f).background(MaterialTheme.colorScheme.surface)) {
            Text("Settings", modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(horizontal = 20.dp, vertical = 18.dp), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("Theme", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ChoiceButton("Dark", settings.themeMode == ThemeMode.DARK) { viewModel.setTheme(ThemeMode.DARK) }
                    ChoiceButton("Light", settings.themeMode == ThemeMode.LIGHT) { viewModel.setTheme(ThemeMode.LIGHT) }
                }
                Text("Sort order", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                ChoiceRow("Newest to Oldest", settings.sortOrder == SortOrder.NEWEST_FIRST) { viewModel.setSortOrder(SortOrder.NEWEST_FIRST) }
                ChoiceRow("Research Title (A-Z)", settings.sortOrder == SortOrder.TITLE_A_TO_Z) { viewModel.setSortOrder(SortOrder.TITLE_A_TO_Z) }
                Text("Information", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                InfoRow("ⓘ", "Privacy policy")
                InfoRow("ⓘ", "About Research hub")
            }
            Row(Modifier.fillMaxWidth().height(68.dp).background(MaterialTheme.colorScheme.primary), verticalAlignment = Alignment.CenterVertically) {
                BottomItem("⌂", "Home", false, onBackClick)
                BottomItem("▣", "Your\nResearches", false) { }
                BottomItem("⚙", "Settings", true) { }
            }
        }
    }
}

@Composable
private fun RowScope.ChoiceButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(label, modifier = Modifier.weight(1f).clip(RoundedCornerShape(6.dp)).background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface).border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp)).clickable(onClick = onClick).padding(vertical = 10.dp), color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary, textAlign = androidx.compose.ui.text.style.TextAlign.Center, fontWeight = FontWeight.Medium)
}

@Composable
private fun ChoiceRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)).background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface).border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(6.dp)).clickable(onClick = onClick).padding(horizontal = 14.dp, vertical = 9.dp), horizontalArrangement = Arrangement.Center) {
        Text(label, color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun InfoRow(icon: String, label: String) {
    Row(Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(7.dp)).padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(icon, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Text(label, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface)
        Text("›", fontSize = 24.sp, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun RowScope.BottomItem(icon: String, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(Modifier.weight(1f).fillMaxSize().clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(icon, color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary.copy(alpha = .75f), fontSize = 24.sp)
        Text(label, color = MaterialTheme.colorScheme.onPrimary, fontSize = 10.sp, lineHeight = 11.sp)
    }
}
