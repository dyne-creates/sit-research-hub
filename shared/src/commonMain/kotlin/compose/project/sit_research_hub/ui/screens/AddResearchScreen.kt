package compose.project.sit_research_hub.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.project.sit_research_hub.domain.model.*
import compose.project.sit_research_hub.domain.repository.StudyRepository
import compose.project.sit_research_hub.domain.usecase.ValidateStudyUseCase
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

private data class QuestionDraft(
    val prompt: String = "",
    val type: QuestionType = QuestionType.SHORT_ANSWER,
    val options: String = "",
    val ratingMax: Int = 5
)

@Composable
fun AddResearchScreen(ownerId: String, ownerName: String, onBack: () -> Unit) {
    val repository = koinInject<StudyRepository>()
    val validate = koinInject<ValidateStudyUseCase>()
    val scope = rememberCoroutineScope()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var requirements by remember { mutableStateOf("") }
    var eligiblePrograms by remember { mutableStateOf("") }
    var eligibleYears by remember { mutableStateOf("") }
    var questionDrafts by remember { mutableStateOf(listOf(QuestionDraft())) }
    var type by remember { mutableStateOf(StudyType.QUALITATIVE) }
    var anonymous by remember { mutableStateOf(false) }
    var oneResponse by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TextButton(onClick = onBack) { Text("← Back") }
        Text("Create Research", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), label = { Text("Research title") }, singleLine = true)
        OutlinedTextField(description, { description = it }, Modifier.fillMaxWidth(), label = { Text("Description") }, minLines = 3)
        OutlinedTextField(requirements, { requirements = it }, Modifier.fillMaxWidth(), label = { Text("Who can participate? (optional)") })
        OutlinedTextField(eligiblePrograms, { eligiblePrograms = it }, Modifier.fillMaxWidth(), label = { Text("Eligible programs, comma separated (optional)") })
        OutlinedTextField(eligibleYears, { eligibleYears = it }, Modifier.fillMaxWidth(), label = { Text("Eligible year levels 1–6, comma separated (optional)") })
        Text("Study type")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StudyType.entries.forEach { value -> FilterChip(type == value, onClick = { type = value }, label = { Text(value.label) }) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Checkbox(anonymous, onCheckedChange = { anonymous = it }); Text("Collect answers anonymously", Modifier.padding(top = 12.dp))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Checkbox(oneResponse, onCheckedChange = { oneResponse = it }); Text("Limit each student to one response", Modifier.padding(top = 12.dp))
        }
        HorizontalDivider()
        Text("Questions", style = MaterialTheme.typography.titleLarge)
        questionDrafts.forEachIndexed { index, draft ->
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Question ${index + 1}", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(draft.prompt, { value -> questionDrafts = questionDrafts.toMutableList().also { it[index] = draft.copy(prompt = value) } }, Modifier.fillMaxWidth(), label = { Text("Question") })
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        QuestionType.entries.forEach { value -> FilterChip(draft.type == value, onClick = { questionDrafts = questionDrafts.toMutableList().also { it[index] = draft.copy(type = value) } }, label = { Text(value.label) }) }
                    }
                    if (draft.type.hasOptions) {
                        OutlinedTextField(draft.options, { value -> questionDrafts = questionDrafts.toMutableList().also { it[index] = draft.copy(options = value) } }, Modifier.fillMaxWidth(), label = { Text("Options, separated by commas") })
                    }
                    if (draft.type == QuestionType.RATING_SCALE) {
                        OutlinedTextField(draft.ratingMax.toString(), { value -> questionDrafts = questionDrafts.toMutableList().also { it[index] = draft.copy(ratingMax = value.toIntOrNull() ?: 5) } }, Modifier.fillMaxWidth(), label = { Text("Rating scale maximum (3–10)") }, singleLine = true)
                    }
                }
            }
        }
        TextButton(onClick = { questionDrafts = questionDrafts + QuestionDraft() }) { Text("Add question") }
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(enabled = !saving, modifier = Modifier.fillMaxWidth(), onClick = {
            val questions = questionDrafts.mapIndexed { index, draft -> Question(
                id = "q${index + 1}", text = draft.prompt.trim(), type = draft.type,
                options = if (draft.type.hasOptions) draft.options.split(',').map(String::trim).filter(String::isNotEmpty) else emptyList(),
                ratingMax = draft.ratingMax
            ) }
            val invalidYear = eligibleYears.split(',').map(String::trim).filter(String::isNotEmpty).any {
                val level = it.toIntOrNull(); level == null || level !in 1..6
            }
            if (invalidYear) { error = "Year levels must be numbers from 1 to 6."; return@Button }
            val study = Study(
                id = "", ownerId = ownerId, ownerName = ownerName, title = title.trim(), description = description.trim(),
                type = type, eligibility = Eligibility(
                    programs = eligiblePrograms.split(',').map(String::trim).filter(String::isNotEmpty),
                    yearLevels = eligibleYears.split(',').mapNotNull { it.trim().toIntOrNull() },
                    description = requirements.trim()
                ), isAnonymous = anonymous,
                oneResponsePerRespondent = oneResponse, status = StudyStatus.PUBLISHED, questions = questions
            )
            val validation = validate(study, forPublish = true)
            if (validation.isNotEmpty()) { error = validation.first(); return@Button }
            scope.launch {
                saving = true
                when (val result = repository.saveStudy(study)) {
                    is AppResult.Success -> onBack()
                    is AppResult.Failure -> error = result.error.message
                }
                saving = false
            }
        }) { Text(if (saving) "Publishing…" else "Publish research") }
    }
}
