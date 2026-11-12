package compose.project.sit_research_hub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StudyDetailScreen(researchId: String, onBackClick: () -> Unit) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), horizontalAlignment = Alignment.CenterHorizontally) {
        Column(Modifier.fillMaxWidth().widthIn(max = 760.dp).weight(1f).background(MaterialTheme.colorScheme.surface)) {
            Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(horizontal = 20.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("←", color = MaterialTheme.colorScheme.onPrimary, fontSize = 30.sp, modifier = Modifier.clickable(onClick = onBackClick))
                Spacer(Modifier.width(16.dp))
                Text("Research Title", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DetailTag("Qualitative", true)
                    DetailTag("Quantitative", false)
                }
                DetailCard {
                    Text("Research Title", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    Text("Add Description", color = MaterialTheme.colorScheme.onSurface)
                    Text("Researchers", color = MaterialTheme.colorScheme.onSurface)
                    Text("Respondents Qualification", color = MaterialTheme.colorScheme.onSurface)
                }
                DetailCard {
                    Text("Interview Schedule", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    ScheduleLine("Date")
                    ScheduleLine("Time")
                    ScheduleLine("Room")
                    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.secondary).padding(vertical = 12.dp), contentAlignment = Alignment.Center) { Text("Choose Schedule", color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Medium) }
                }
                DetailCard {
                    Text("Email", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    Text("Required field for contact purposes", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                    Box(Modifier.fillMaxWidth().height(2.dp).background(MaterialTheme.colorScheme.outline))
                }
                Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.secondary).clickable { }.padding(vertical = 13.dp), contentAlignment = Alignment.Center) { Text("Submit", color = MaterialTheme.colorScheme.onSecondary, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
private fun DetailCard(content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxWidth().border(1.5.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(14.dp)).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
}

@Composable
private fun DetailTag(label: String, selected: Boolean) {
    Text(label, modifier = Modifier.clip(RoundedCornerShape(7.dp)).background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface).border(1.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(7.dp)).padding(horizontal = 14.dp, vertical = 9.dp), color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
}

@Composable
private fun ScheduleLine(label: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("•", color = MaterialTheme.colorScheme.secondary)
        Text(label, color = MaterialTheme.colorScheme.onSurface)
    }
}
