
package compose.project.sit_research_hub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    val answers = remember(researchId) {
        mutableStateMapOf<String, List<String>>()
    }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // TOP BAR

// TOP BAR
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(start = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onBackClick,
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    )
                ) {
                    Text(
                        text = "←  Back",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        val item = study

        if (item == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (loadError == null) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Loading study...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        "Unable to load study",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        loadError.orEmpty(),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        } else {
            val isOwner = item.ownerId == userId

            // SCROLLABLE CONTENT
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // STUDY INFORMATION
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = item.type.label,
                                modifier = Modifier.padding(
                                    horizontal = 11.dp,
                                    vertical = 6.dp
                                ),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        StudyStatusBadge(item.status)
                    }

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "Research by ${item.ownerName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 21.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // ELIGIBILITY REQUIREMENTS
                if (item.eligibility.description.isNotBlank()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                "Respondent requirements",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                item.eligibility.description,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // ANONYMOUS NOTICE
                if (item.isAnonymous) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(13.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "✓",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                "Your answers are anonymous.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // QUESTIONS
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "Research questions",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Read each question carefully and provide your response.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    item.questions.forEachIndexed { index, question ->
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    "QUESTION ${index + 1}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Text(
                                    text = question.text +
                                            if (question.isRequired) " *" else "",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                when (question.type) {
                                    QuestionType.MULTIPLE_CHOICE -> {
                                        question.options.forEach { option ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        answers[question.id] = listOf(option)
                                                    }
                                                    .padding(vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = answers[question.id]
                                                        ?.contains(option) == true,
                                                    onClick = {
                                                        answers[question.id] = listOf(option)
                                                    }
                                                )
                                                Text(
                                                    text = option,
                                                    modifier = Modifier.padding(start = 6.dp),
                                                    fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }

                                    QuestionType.CHECKBOX -> {
                                        question.options.forEach { option ->
                                            val selected =
                                                option in answers[question.id].orEmpty()

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        answers[question.id] =
                                                            if (selected) {
                                                                answers[question.id]
                                                                    .orEmpty() - option
                                                            } else {
                                                                answers[question.id]
                                                                    .orEmpty() + option
                                                            }
                                                    }
                                                    .padding(vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Checkbox(
                                                    checked = selected,
                                                    onCheckedChange = { checked ->
                                                        answers[question.id] =
                                                            if (checked) {
                                                                answers[question.id]
                                                                    .orEmpty() + option
                                                            } else {
                                                                answers[question.id]
                                                                    .orEmpty() - option
                                                            }
                                                    }
                                                )
                                                Text(
                                                    text = option,
                                                    modifier = Modifier.padding(start = 6.dp),
                                                    fontSize = 13.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }

                                    QuestionType.RATING_SCALE -> {
                                        Text(
                                            "Select a rating",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            (1..question.ratingMax).forEach { value ->
                                                FilterChip(
                                                    selected = answers[question.id]
                                                        ?.firstOrNull() == value.toString(),
                                                    onClick = {
                                                        answers[question.id] =
                                                            listOf(value.toString())
                                                    },
                                                    label = {
                                                        Text(value.toString())
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    QuestionType.SHORT_ANSWER -> {
                                        OutlinedTextField(
                                            value = answers[question.id]
                                                ?.firstOrNull().orEmpty(),
                                            onValueChange = {
                                                answers[question.id] = listOf(it)
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            singleLine = true,
                                            placeholder = {
                                                Text("Enter your answer...")
                                            },
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    }

                                    QuestionType.PARAGRAPH -> {
                                        OutlinedTextField(
                                            value = answers[question.id]
                                                ?.firstOrNull().orEmpty(),
                                            onValueChange = {
                                                answers[question.id] = listOf(it)
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            minLines = 3,
                                            placeholder = {
                                                Text("Write your response here...")
                                            },
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // INTERVIEW SCHEDULE
                if (item.type == StudyType.QUALITATIVE) {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    "Interview schedule",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Available interview times and meeting details.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (isOwner) {
                                Text(
                                    "Add an interview slot",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )

                                OutlinedTextField(
                                    value = interviewDate,
                                    onValueChange = { interviewDate = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Date (YYYY-MM-DD)") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )

                                OutlinedTextField(
                                    value = interviewTime,
                                    onValueChange = { interviewTime = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Time (HH:MM)") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )

                                OutlinedTextField(
                                    value = interviewLocation,
                                    onValueChange = { interviewLocation = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Location or meeting link") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp)
                                )

                                Button(
                                    onClick = {
                                        scope.launch {
                                            when (
                                                val result = createSlot(
                                                    item,
                                                    interviewDate,
                                                    interviewTime,
                                                    interviewLocation,
                                                    ""
                                                )
                                            ) {
                                                is AppResult.Success -> {
                                                    slots = slots + result.data
                                                    message = "Interview slot added."
                                                }

                                                is AppResult.Failure -> {
                                                    message = result.error.message
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Add interview slot")
                                }
                            }

                            HorizontalDivider()

                            Text(
                                "Available times",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )

                            if (slots.isEmpty()) {
                                Text(
                                    "No interview times are available yet.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            slots.forEach { slot ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                slot.startsAt.replace('T', ' '),
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                slot.location,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        if (isOwner) {
                                            Column(horizontalAlignment = Alignment.End) {
                                                Text(
                                                    slot.bookedByName?.let { "Booked: $it" } ?: "Available",
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                TextButton(onClick = {
                                                    scope.launch {
                                                        when (val result = interviews.delete(slot.id)) {
                                                            is AppResult.Success -> {
                                                                slots = slots.filterNot { it.id == slot.id }
                                                                message = "Interview slot removed."
                                                            }
                                                            is AppResult.Failure -> message = result.error.message
                                                        }
                                                    }
                                                }) { Text("Remove") }
                                            }
                                        } else if (slot.isAvailable) {
                                            TextButton(
                                                onClick = {
                                                    scope.launch {
                                                        when (
                                                            val result = chooseSlot(
                                                                item,
                                                                userId,
                                                                slot,
                                                                slots
                                                            )
                                                        ) {
                                                            is AppResult.Success -> {
                                                                when (
                                                                    val latest = interviews.slots(
                                                                        researchId
                                                                    )
                                                                ) {
                                                                    is AppResult.Success -> {
                                                                        slots = latest.data
                                                                    }

                                                                    is AppResult.Failure -> {
                                                                        message =
                                                                            latest.error.message
                                                                    }
                                                                }
                                                                message =
                                                                    "Interview time selected."
                                                            }

                                                            is AppResult.Failure -> {
                                                                message =
                                                                    result.error.message
                                                            }
                                                        }
                                                    }
                                                }
                                            ) {
                                                Text("Choose")
                                            }
                                        } else if (!slot.isAvailable) {
                                            Text(
                                                "Booked",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // FEEDBACK
                message?.let {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = it,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // SUBMIT BUTTON
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 5.dp
            ) {
                Button(
                    enabled = !sending &&
                            !isOwner &&
                            item.status == StudyStatus.PUBLISHED,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        scope.launch {
                            sending = true

                            when (
                                val result = submit(
                                    item,
                                    userId,
                                    profile,
                                    answers.toMap()
                                )
                            ) {
                                is AppResult.Success -> {
                                    message = "Your response was submitted."
                                }

                                is AppResult.Failure -> {
                                    message = result.error.message
                                }
                            }

                            sending = false
                        }
                    }
                ) {
                    Text(
                        text = when {
                            sending -> "Submitting..."
                            isOwner -> "You own this study"
                            item.status != StudyStatus.PUBLISHED -> "Responses unavailable"
                            else -> "Submit response"
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun StudyStatusBadge(status: StudyStatus) {
    val label = status.name.lowercase()
        .replaceFirstChar { it.uppercase() }

    val containerColor = when (status) {
        StudyStatus.PUBLISHED -> MaterialTheme.colorScheme.primaryContainer
        StudyStatus.CLOSED -> MaterialTheme.colorScheme.surfaceVariant
        StudyStatus.DRAFT -> MaterialTheme.colorScheme.tertiary
    }

    Surface(
        color = containerColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
