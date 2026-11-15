package compose.project.sit_research_hub.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.project.sit_research_hub.domain.model.*
import compose.project.sit_research_hub.domain.repository.StudyRepository
import compose.project.sit_research_hub.domain.repository.SubmissionRepository
import compose.project.sit_research_hub.domain.usecase.ComputeResultsUseCase
import compose.project.sit_research_hub.domain.usecase.summaryText
import compose.project.sit_research_hub.domain.usecase.oneDecimal
import compose.project.sit_research_hub.domain.repository.TextSharer
import org.koin.compose.koinInject

@Composable
fun ResearchSummaryScreen(studyId: String, onBack: () -> Unit) {
    val studies = koinInject<StudyRepository>()
    val submissions = koinInject<SubmissionRepository>()
    val compute = koinInject<ComputeResultsUseCase>()
    val textSharer = koinInject<TextSharer>()
    var study by remember(studyId) { mutableStateOf<Study?>(null) }
    var results by remember(studyId) { mutableStateOf<List<QuestionResult>>(emptyList()) }
    var total by remember(studyId) { mutableIntStateOf(0) }
    var message by remember(studyId) { mutableStateOf("Loading results…") }
    LaunchedEffect(studyId) {
        when (val studyResult = studies.getStudy(studyId)) {
            is AppResult.Failure -> message = studyResult.error.message ?: "Could not load this research."
            is AppResult.Success -> {
                study = studyResult.data
                when (val responseResult = submissions.fetchResponses(studyId)) {
                    is AppResult.Failure -> message = responseResult.error.message ?: "Could not load responses."
                    is AppResult.Success -> {
                        total = responseResult.data.size
                        results = compute(studyResult.data, responseResult.data)
                        message = if (responseResult.data.isEmpty()) "No responses yet." else "$total responses"
                    }
                }
            }
        }
    }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        TextButton(onClick = onBack) { Text("← Back") }
        Text(study?.title ?: "Research results", style = MaterialTheme.typography.headlineSmall)
        Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
        study?.let { item ->
            TextButton(onClick = {
                val text = summaryText(item, total, results)
                message = runCatching { textSharer.share(item.title, text) }.getOrElse { "Could not share this summary." }
            }) { Text(textSharer.actionLabel) }
        }
        Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            results.forEachIndexed { index, result ->
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("${index + 1}. ${result.question.text}", style = MaterialTheme.typography.titleMedium)
                        Text("${result.answered} answers")
                        result.counts.forEach { count -> Text("${count.label}: ${count.count} (${count.percent.toInt()}%)") }
                        result.mean?.let { Text("Average: ${it.oneDecimal()}") }
                        result.median?.let { Text("Median: ${it.oneDecimal()}") }
                        result.textAnswers.forEach { answer ->
                            Text(answer.text)
                            answer.respondent?.let { Text("— $it", style = MaterialTheme.typography.bodySmall) }
                        }
                    }
                }
            }
        }
    }
}
