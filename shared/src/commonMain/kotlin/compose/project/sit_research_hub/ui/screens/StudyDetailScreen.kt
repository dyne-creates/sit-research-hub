package compose.project.sit_research_hub.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.project.sit_research_hub.domain.model.*
import compose.project.sit_research_hub.domain.repository.StudyRepository
import compose.project.sit_research_hub.domain.repository.InterviewRepository
import compose.project.sit_research_hub.domain.usecase.SubmitResponseUseCase
import compose.project.sit_research_hub.domain.usecase.CreateInterviewSlotUseCase
import compose.project.sit_research_hub.domain.usecase.ChooseInterviewSlotUseCase
import org.koin.compose.koinInject
import kotlinx.coroutines.launch

@Composable
fun StudyDetailScreen(
    researchId: String,
    userId: String,
    profile: StudentProfile,
    onBackClick: () -> Unit
) {
    val studies = koinInject<StudyRepository>()
    val submit = koinInject<SubmitResponseUseCase>()
    val interviews = koinInject<InterviewRepository>()
    val createSlot = koinInject<CreateInterviewSlotUseCase>()
    val chooseSlot = koinInject<ChooseInterviewSlotUseCase>()
    val scope = rememberCoroutineScope()
    var study by remember(researchId) { mutableStateOf<Study?>(null) }
    var loadError by remember(researchId) { mutableStateOf<String?>(null) }
    var message by remember(researchId) { mutableStateOf<String?>(null) }
    var sending by remember { mutableStateOf(false) }
    var slots by remember(researchId) { mutableStateOf<List<InterviewSlot>>(emptyList()) }
    var interviewDate by remember { mutableStateOf("") }
    var interviewTime by remember { mutableStateOf("") }
    var interviewLocation by remember { mutableStateOf("") }
    val answers = remember(researchId) { mutableStateMapOf<String, List<String>>() }

    LaunchedEffect(researchId) {
        when (val result = studies.getStudy(researchId)) {
            is AppResult.Success -> {
                study = result.data
                if (result.data.type == StudyType.QUALITATIVE) {
                    when (val slotResult = interviews.slots(researchId)) {
                        is AppResult.Success -> slots = slotResult.data
                        is AppResult.Failure -> message = slotResult.error.message
                    }
                }
            }
            is AppResult.Failure -> loadError = result.error.message
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        TextButton(onClick = onBackClick) { Text("← Back") }
        val item = study
        if (item == null) {
            Text(loadError ?: "Loading study…", style = MaterialTheme.typography.bodyLarge)
        } else {
            Column(Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(item.title, style = MaterialTheme.typography.headlineSmall)
                Text("${item.type.label} · ${item.ownerName}", style = MaterialTheme.typography.labelLarge)
                Text(item.description)
                if (item.eligibility.description.isNotBlank()) Text("Requirements: ${item.eligibility.description}")
                if (item.isAnonymous) Text("Your answers are anonymous.", color = MaterialTheme.colorScheme.primary)
                item.questions.forEachIndexed { index, question ->
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("${index + 1}. ${question.text}${if (question.isRequired) " *" else ""}", style = MaterialTheme.typography.titleMedium)
                            when (question.type) {
                                QuestionType.MULTIPLE_CHOICE -> question.options.forEach { option ->
                                    Row(Modifier.fillMaxWidth().clickable { answers[question.id] = listOf(option) }, horizontalArrangement = Arrangement.Start) {
                                        RadioButton(answers[question.id]?.contains(option) == true, onClick = { answers[question.id] = listOf(option) })
                                        Text(option, Modifier.padding(top = 12.dp))
                                    }
                                }
                                QuestionType.CHECKBOX -> question.options.forEach { option ->
                                    val selected = option in answers[question.id].orEmpty()
                                    Row(Modifier.fillMaxWidth().clickable {
                                        answers[question.id] = if (selected) answers[question.id].orEmpty() - option else answers[question.id].orEmpty() + option
                                    }) {
                                        Checkbox(selected, onCheckedChange = { checked ->
                                            answers[question.id] = if (checked) answers[question.id].orEmpty() + option else answers[question.id].orEmpty() - option
                                        })
                                        Text(option, Modifier.padding(top = 12.dp))
                                    }
                                }
                                QuestionType.RATING_SCALE -> Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    (1..question.ratingMax).forEach { value ->
                                        FilterChip(answers[question.id]?.firstOrNull() == value.toString(), onClick = { answers[question.id] = listOf(value.toString()) }, label = { Text(value.toString()) })
                                    }
                                }
                                QuestionType.SHORT_ANSWER -> OutlinedTextField(
                                    value = answers[question.id]?.firstOrNull().orEmpty(),
                                    onValueChange = { answers[question.id] = listOf(it) }, modifier = Modifier.fillMaxWidth(), singleLine = true
                                )
                                QuestionType.PARAGRAPH -> OutlinedTextField(
                                    value = answers[question.id]?.firstOrNull().orEmpty(),
                                    onValueChange = { answers[question.id] = listOf(it) }, modifier = Modifier.fillMaxWidth(), minLines = 3
                                )
                            }
                        }
                    }
                }
                if (item.type == StudyType.QUALITATIVE) {
                    ElevatedCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Interview schedule", style = MaterialTheme.typography.titleMedium)
                            if (isOwner(item, userId)) {
                                OutlinedTextField(interviewDate, { interviewDate = it }, Modifier.fillMaxWidth(), label = { Text("Date (YYYY-MM-DD)") })
                                OutlinedTextField(interviewTime, { interviewTime = it }, Modifier.fillMaxWidth(), label = { Text("Time (HH:MM)") })
                                OutlinedTextField(interviewLocation, { interviewLocation = it }, Modifier.fillMaxWidth(), label = { Text("Location or meeting link") })
                                Button(onClick = {
                                    scope.launch {
                                        when (val result = createSlot(item, interviewDate, interviewTime, interviewLocation, "")) {
                                            is AppResult.Success -> { slots = slots + result.data; message = "Interview slot added." }
                                            is AppResult.Failure -> message = result.error.message
                                        }
                                    }
                                }) { Text("Add interview slot") }
                            }
                            if (slots.isEmpty()) Text("No interview times are available yet.")
                            slots.forEach { slot ->
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column(Modifier.weight(1f)) {
                                        Text(slot.startsAt.replace('T', ' '))
                                        Text(slot.location, style = MaterialTheme.typography.bodySmall)
                                    }
                                    if (!isOwner(item, userId) && slot.isAvailable) TextButton(onClick = {
                                        scope.launch {
                                            when (val result = chooseSlot(item, userId, slot, slots)) {
                                                is AppResult.Success -> {
                                                    when (val latest = interviews.slots(researchId)) {
                                                        is AppResult.Success -> slots = latest.data
                                                        is AppResult.Failure -> message = latest.error.message
                                                    }
                                                    message = "Interview time selected."
                                                }
                                                is AppResult.Failure -> message = result.error.message
                                            }
                                        }
                                    }) { Text("Choose") }
                                    else if (!slot.isAvailable) Text("Booked", style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    }
                }
                message?.let { Text(it, color = MaterialTheme.colorScheme.primary) }
            }
            val isOwner = item.ownerId == userId
            Button(
                enabled = !sending && !isOwner && item.status == StudyStatus.PUBLISHED,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                onClick = {
                    scope.launch {
                        sending = true
                        when (val result = submit(item, userId, profile, answers.toMap())) {
                            is AppResult.Success -> message = "Your response was submitted."
                            is AppResult.Failure -> message = result.error.message
                        }
                        sending = false
                    }
                }
            ) { Text(if (sending) "Submitting…" else if (isOwner) "You own this study" else "Submit response") }
        }
    }
}

private fun isOwner(study: Study, userId: String) = study.ownerId == userId
