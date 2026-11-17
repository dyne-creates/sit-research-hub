package compose.project.sit_research_hub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.project.sit_research_hub.domain.model.AppResult
import compose.project.sit_research_hub.domain.model.InterviewSlot
import compose.project.sit_research_hub.domain.repository.InterviewRepository
import org.koin.compose.koinInject

@Composable
fun InterviewSchedulesScreen(userId: String, onBack: () -> Unit) {
    val repository = koinInject<InterviewRepository>()
    var slots by remember { mutableStateOf<List<InterviewSlot>>(emptyList()) }
    var message by remember { mutableStateOf("Loading schedules…") }
    LaunchedEffect(userId) {
        when (val result = repository.bookedSlots(userId)) {
            is AppResult.Success -> { slots = result.data; message = if (slots.isEmpty()) "No interview schedules yet." else "${slots.size} booked interview(s)" }
            is AppResult.Failure -> message = result.error.message ?: "Could not load interview schedules."
        }
    }
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(16.dp)) {
            TextButton(onClick = onBack) { Text("← Back", color = MaterialTheme.colorScheme.onPrimary) }
            Text("Interview Schedules", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        Text(message, Modifier.padding(18.dp), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(slots, key = { it.id }) { slot ->
                ElevatedCard(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(slot.startsAt.replace('T', ' '), fontWeight = FontWeight.Bold)
                        Text(slot.location)
                        if (slot.notes.isNotBlank()) Text(slot.notes, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
