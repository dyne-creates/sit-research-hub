
package compose.project.sit_research_hub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

private val programOptions = listOf(
    "BSIT",
    "BSCS",
    "BSCPE",
    "ACT-MM"
)

private val yearOptions = listOf(1, 2, 3, 4)

@Composable
fun AddResearchScreen(
    ownerId: String,
    ownerName: String,
    onBack: () -> Unit
) {
    val repository = koinInject<StudyRepository>()
    val validate = koinInject<ValidateStudyUseCase>()
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // An empty set means All is selected.
    var selectedPrograms by remember { mutableStateOf(setOf<String>()) }
    var selectedYears by remember { mutableStateOf(setOf<Int>()) }

    var questionDrafts by remember {
        mutableStateOf(listOf(QuestionDraft()))
    }

    var type by remember {
        mutableStateOf(StudyType.QUALITATIVE)
    }

    var anonymous by remember { mutableStateOf(false) }
    var oneResponse by remember { mutableStateOf(true) }

    var error by remember { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }

    val actMmOnly = selectedPrograms == setOf("ACT-MM")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // HEADER
        Column {
            TextButton(
                onClick = onBack,
                contentPadding = PaddingValues(horizontal = 0.dp)
            ) {
                Text("← Back")
            }

            Text(
                text = "Create Research",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Set up your study and invite students to participate.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // SECTION 1: BASIC INFORMATION
        FormSection(
            title = "Basic information",
            description = "Start with the details of your research."
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Research title") },
                placeholder = { Text("Enter your research title") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Description") },
                placeholder = {
                    Text("Describe what your research is about...")
                },
                minLines = 3,
                shape = RoundedCornerShape(12.dp)
            )
        }

        // SECTION 2: RESPONDENT ELIGIBILITY
        FormSection(
            title = "Respondent eligibility",
            description = "Specify which students are eligible to participate."
        ) {

            // ELIGIBLE PROGRAMS
            Text(
                text = "Eligible programs",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Select one or more programs.",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            EligibilityCheckbox(
                label = "All programs",
                checked = selectedPrograms.isEmpty(),
                onCheckedChange = {
                    selectedPrograms = emptySet()
                }
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
            )

            programOptions.forEach { program ->
                EligibilityCheckbox(
                    label = program,
                    checked = program in selectedPrograms,
                    onCheckedChange = { checked ->
                        val updatedPrograms = when {
                            selectedPrograms.isEmpty() && checked -> {
                                setOf(program)
                            }

                            checked -> {
                                selectedPrograms + program
                            }

                            else -> {
                                selectedPrograms - program
                            }
                        }

                        selectedPrograms = updatedPrograms

                        // ACT-MM only offers year levels 1 and 2.
                        // Remove year levels 3 and 4 if ACT-MM becomes
                        // the only selected program.
                        if (updatedPrograms == setOf("ACT-MM")) {
                            selectedYears = selectedYears.intersect(
                                setOf(1, 2)
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            // ELIGIBLE YEAR LEVELS
            Text(
                text = "Eligible year levels",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = if (actMmOnly) {
                    "ACT-MM is selected as the only program. Only years 1 and 2 are available."
                } else {
                    "Select one or more year levels."
                },
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            EligibilityCheckbox(
                label = "All year levels",
                checked = selectedYears.isEmpty(),
                onCheckedChange = {
                    selectedYears = emptySet()
                }
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
            )

            yearOptions.forEach { year ->
                val enabled = !(actMmOnly && year in listOf(3, 4))

                EligibilityCheckbox(
                    label = "Year $year",
                    checked = year in selectedYears,
                    enabled = enabled,
                    onCheckedChange = { checked ->
                        selectedYears = when {
                            selectedYears.isEmpty() && checked -> {
                                setOf(year)
                            }

                            checked -> {
                                selectedYears + year
                            }

                            else -> {
                                selectedYears - year
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            // STUDY TYPE
            Text(
                text = "Study type",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StudyType.entries.forEach { value ->
                    FilterChip(
                        selected = type == value,
                        onClick = { type = value },
                        label = { Text(value.label) }
                    )
                }
            }
        }

        // SECTION 3: RESPONSE SETTINGS
        FormSection(
            title = "Response settings",
            description = "Choose how responses will be collected."
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = anonymous,
                    onCheckedChange = { anonymous = it }
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Anonymous responses",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Collect answers without displaying respondent identities.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = oneResponse,
                    onCheckedChange = { oneResponse = it }
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "One response per student",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Limit each respondent to one submission.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // SECTION 4: RESEARCH QUESTIONS
        FormSection(
            title = "Research questions",
            description = "Add the questions respondents will answer."
        ) {

            questionDrafts.forEachIndexed { index, draft ->
                QuestionDraftCard(
                    index = index,
                    draft = draft,
                    onDraftChange = { updated ->
                        questionDrafts = questionDrafts.toMutableList().also {
                            it[index] = updated
                        }
                    }
                )
            }

            OutlinedButton(
                onClick = {
                    questionDrafts = questionDrafts + QuestionDraft()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("+  Add another question")
            }
        }

        // ERROR MESSAGE
        error?.let {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(14.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontSize = 12.sp
                )
            }
        }

        // PUBLISH BUTTON
        Button(
            enabled = !saving,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            onClick = {

                val questions = questionDrafts.mapIndexed { index, draft ->
                    Question(
                        id = "q${index + 1}",
                        text = draft.prompt.trim(),
                        type = draft.type,
                        options = if (draft.type.hasOptions) {
                            draft.options.split(',')
                                .map(String::trim)
                                .filter(String::isNotEmpty)
                        } else {
                            emptyList()
                        },
                        ratingMax = draft.ratingMax
                    )
                }

                // Convert UI selections into the existing eligibility model.
                //
                // An empty program list means all programs.
                // An empty year list means all year levels.
                //
                // Exception: ACT-MM only has years 1 and 2.
                // Therefore, selecting All year levels for ACT-MM
                // must save [1, 2], not an empty list.

                val programsToSave = selectedPrograms
                    .sortedBy { programOptions.indexOf(it) }

                val yearsToSave = when {
                    actMmOnly && selectedYears.isEmpty() -> {
                        listOf(1, 2)
                    }

                    else -> {
                        selectedYears.sorted()
                    }
                }

                val study = Study(
                    id = "",
                    ownerId = ownerId,
                    ownerName = ownerName,
                    title = title.trim(),
                    description = description.trim(),
                    type = type,
                    eligibility = Eligibility(
                        programs = programsToSave,
                        yearLevels = yearsToSave
                    ),
                    isAnonymous = anonymous,
                    oneResponsePerRespondent = oneResponse,
                    status = StudyStatus.PUBLISHED,
                    questions = questions
                )

                val validation = validate(
                    study,
                    forPublish = true
                )

                if (validation.isNotEmpty()) {
                    error = validation.first()
                    return@Button
                }

                scope.launch {
                    saving = true
                    error = null

                    when (val result = repository.saveStudy(study)) {
                        is AppResult.Success -> {
                            onBack()
                        }

                        is AppResult.Failure -> {
                            error = result.error.message
                        }
                    }

                    saving = false
                }
            }
        ) {
            Text(
                text = if (saving) "Publishing..." else "Publish research",
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun EligibilityCheckbox(
    label: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange
        )

        Text(
            text = label,
            fontSize = 13.sp,
            color = if (enabled) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            }
        )
    }
}

@Composable
private fun FormSection(
    title: String,
    description: String,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
            )

            content()
        }
    }
}

@Composable
private fun QuestionDraftCard(
    index: Int,
    draft: QuestionDraft,
    onDraftChange: (QuestionDraft) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.45f
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(13.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Text(
                text = "QUESTION ${index + 1}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.7.sp
            )

            OutlinedTextField(
                value = draft.prompt,
                onValueChange = {
                    onDraftChange(draft.copy(prompt = it))
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Question prompt") },
                placeholder = {
                    Text("Enter your question...")
                },
                minLines = 2,
                shape = RoundedCornerShape(10.dp)
            )

            Text(
                text = "Answer format",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                QuestionType.entries.forEach { value ->
                    FilterChip(
                        selected = draft.type == value,
                        onClick = {
                            onDraftChange(draft.copy(type = value))
                        },
                        label = {
                            Text(
                                text = value.label,
                                fontSize = 10.sp
                            )
                        }
                    )
                }
            }

            if (draft.type.hasOptions) {
                OutlinedTextField(
                    value = draft.options,
                    onValueChange = {
                        onDraftChange(draft.copy(options = it))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Answer options") },
                    placeholder = {
                        Text("Option 1, Option 2, Option 3")
                    },
                    supportingText = {
                        Text("Separate each option with a comma.")
                    },
                    shape = RoundedCornerShape(10.dp)
                )
            }

            if (draft.type == QuestionType.RATING_SCALE) {
                OutlinedTextField(
                    value = draft.ratingMax.toString(),
                    onValueChange = {
                        onDraftChange(
                            draft.copy(
                                ratingMax = it.toIntOrNull() ?: 5
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Rating scale maximum") },
                    supportingText = {
                        Text("Choose a maximum value from 3 to 10.")
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }
    }
}
