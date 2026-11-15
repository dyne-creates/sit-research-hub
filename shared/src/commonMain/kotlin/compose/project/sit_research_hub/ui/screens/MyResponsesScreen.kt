package compose.project.sit_research_hub.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.project.sit_research_hub.domain.model.AppResult
import compose.project.sit_research_hub.domain.repository.SubmissionRepository
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun MyResponsesScreen(onBack: () -> Unit) {
    val repository = koinInject<SubmissionRepository>()
    val participations by repository.observeParticipations().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var offline by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        offline = repository.refreshParticipations() is AppResult.Failure
    }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        TextButton(onClick = onBack) { Text("← Back") }
        Text("My Responses", style = MaterialTheme.typography.headlineSmall)
        if (offline) Text("Showing saved participation history offline.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        TextButton(onClick = { scope.launch { offline = repository.refreshParticipations() is AppResult.Failure } }) { Text("Refresh") }
        if (participations.isEmpty()) Text("No submitted responses found.", Modifier.padding(top = 16.dp))
        LazyColumn(Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(vertical = 10.dp)) {
            items(participations) { participation ->
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text(participation.studyTitle, style = MaterialTheme.typography.titleMedium)
                        Text("Researcher: ${participation.researcherName}")
                        if (participation.createdAt.isNotBlank()) Text(participation.createdAt, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
