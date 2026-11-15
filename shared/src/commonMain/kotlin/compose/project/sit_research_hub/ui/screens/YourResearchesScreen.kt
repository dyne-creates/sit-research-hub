package compose.project.sit_research_hub.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.project.sit_research_hub.domain.model.*
import compose.project.sit_research_hub.domain.repository.StudyRepository
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun YourResearchesScreen(userId: String, onBack: () -> Unit, onStudyClick: (String) -> Unit, onResultsClick: (String) -> Unit) {
    val repository = koinInject<StudyRepository>()
    val studies by repository.observeStudies().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var message by remember { mutableStateOf<String?>(null) }
    var offline by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { offline = repository.refresh() is AppResult.Failure }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = onBack) { Text("← Home") }
            TextButton(onClick = { scope.launch {
                val result = repository.refresh()
                offline = result is AppResult.Failure
                message = when (result) {
                    is AppResult.Success -> "Research refreshed."
                    is AppResult.Failure -> result.error.message
                }
            } }) { Text("Refresh") }
        }
        Text("Your Researches", style = MaterialTheme.typography.headlineSmall)
        if (offline) Text("Showing saved research offline.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
        val owned = studies.filter { it.ownerId == userId }
        if (owned.isEmpty()) Text("You have not created any research yet.", Modifier.padding(top = 20.dp))
        LazyColumn(Modifier.fillMaxWidth().weight(1f), contentPadding = PaddingValues(vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(owned, key = { it.id }) { study ->
                ElevatedCard(Modifier.fillMaxWidth().clickable { onStudyClick(study.id) }) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(study.title, style = MaterialTheme.typography.titleMedium)
                        Text("${study.type.label} · ${study.status}")
                        Text(study.description, maxLines = 2)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { onStudyClick(study.id) }) { Text("Open") }
                            TextButton(onClick = { onResultsClick(study.id) }) { Text("Results") }
                        }
                        if (study.status != StudyStatus.DRAFT) {
                            TextButton(onClick = {
                                scope.launch {
                                    val next = if (study.status == StudyStatus.PUBLISHED) StudyStatus.CLOSED else StudyStatus.PUBLISHED
                                    when (val result = repository.setStatus(study.id, next)) {
                                        is AppResult.Success -> message = "Research ${next.name.lowercase()}."
                                        is AppResult.Failure -> message = result.error.message
                                    }
                                }
                            }) { Text(if (study.status == StudyStatus.PUBLISHED) "Close responses" else "Reopen responses") }
                        }
                    }
                }
            }
        }
    }
}
