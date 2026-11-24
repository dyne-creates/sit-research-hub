package compose.project.sit_research_hub.ui.screens

import androidx.compose.foundation.background
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
import compose.project.sit_research_hub.domain.repository.InterviewRepository
import compose.project.sit_research_hub.domain.repository.StudyRepository
import compose.project.sit_research_hub.domain.repository.SubmissionRepository
import compose.project.sit_research_hub.domain.repository.TextSharer
import compose.project.sit_research_hub.domain.usecase.ComputeResultsUseCase
import compose.project.sit_research_hub.domain.usecase.oneDecimal
import compose.project.sit_research_hub.domain.usecase.summaryText
import org.koin.compose.koinInject

@Composable
fun ResearchSummaryScreen(studyId: String, onBack: () -> Unit) {
    val studies = koinInject<StudyRepository>()
    val submissions = koinInject<SubmissionRepository>()
    val interviews = koinInject<InterviewRepository>()
    val compute = koinInject<ComputeResultsUseCase>()
    val textSharer = koinInject<TextSharer>()

    var study by remember(studyId) { mutableStateOf<Study?>(null) }
    var results by remember(studyId) {
        mutableStateOf<List<QuestionResult>>(emptyList())
    }
    var total by remember(studyId) { mutableIntStateOf(0) }
    var bookedSlots by remember(studyId) {
        mutableStateOf<List<InterviewSlot>>(emptyList())
    }
    var totalSchedules by remember(studyId) { mutableIntStateOf(0) }
    var message by remember(studyId) {
        mutableStateOf("Loading results...")
    }

    LaunchedEffect(studyId) {
        when (val studyResult = studies.getStudy(studyId)) {
            is AppResult.Failure -> {
                message = studyResult.error.message
                    ?: "Could not load this research."
            }

            is AppResult.Success -> {
                val loadedStudy = studyResult.data
                study = loadedStudy

                if (loadedStudy.type == StudyType.QUALITATIVE) {
                    // Qualitative studies use interview bookings, not questionnaire responses.
                    when (val slotResult = interviews.slots(studyId)) {
                        is AppResult.Failure -> {
                            message = slotResult.error.message
                                ?: "Could not load interview bookings."
                        }

                        is AppResult.Success -> {
                            totalSchedules = slotResult.data.size
                            bookedSlots = slotResult.data
                                .filter { !it.bookedBy.isNullOrBlank() }
                                .sortedBy { it.startsAt }

                            message = if (bookedSlots.isEmpty()) {
                                "No interview schedules have been booked yet."
                            } else {
                                "${bookedSlots.size} interview booking(s)"
                            }
                        }
                    }
                } else {
                    // Keep the existing quantitative response summary.
                    when (val responseResult = submissions.fetchResponses(studyId)) {
                        is AppResult.Failure -> {
                            message = responseResult.error.message
                                ?: "Could not load responses."
                        }

                        is AppResult.Success -> {
                            total = responseResult.data.size
                            results = compute(loadedStudy, responseResult.data)
                            message = if (responseResult.data.isEmpty()) {
                                "No responses yet."
                            } else {
                                "$total responses"
                            }
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // HEADER
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            TextButton(
                onClick = onBack,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("← Back")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Research Summary",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = study?.title ?: "Research results",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
            )
        }

        // OVERVIEW
        study?.let { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (item.type == StudyType.QUALITATIVE) {
                    SummaryStatCard(
                        label = "Interview bookings",
                        value = bookedSlots.size.toString(),
                        modifier = Modifier.weight(1f)
                    )

                    SummaryStatCard(
                        label = "Total schedules",
                        value = totalSchedules.toString(),
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    SummaryStatCard(
                        label = "Total responses",
                        value = total.toString(),
                        modifier = Modifier.weight(1f)
                    )

                    SummaryStatCard(
                        label = "Questions",
                        value = results.size.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // STATUS MESSAGE
        Text(
            text = message,
            modifier = Modifier.padding(horizontal = 20.dp),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // SHARE BUTTON: quantitative summaries only
        study?.let { item ->
            if (item.type == StudyType.QUANTITATIVE) {
                OutlinedButton(
                    onClick = {
                        val text = summaryText(item, total, results)
                        message = runCatching {
                            textSharer.share(item.title, text)
                        }.getOrElse {
                            "Could not share this summary."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "↗  ${textSharer.actionLabel}",
                        fontSize = 12.sp
                    )
                }
            }
        }

        // TYPE-SPECIFIC CONTENT
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            val currentStudy = study

            if (currentStudy != null) {
                if (currentStudy.type == StudyType.QUALITATIVE) {
                    if (bookedSlots.isEmpty()) {
                        EmptyInterviewBookingsCard()
                    } else {
                        Text(
                            text = "BOOKED INTERVIEW SCHEDULES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        bookedSlots.forEachIndexed { index, slot ->
                            InterviewBookingCard(
                                index = index,
                                slot = slot
                            )
                        }
                    }
                } else {
                    if (total == 0 && results.isEmpty()) {
                        EmptyResultsCard()
                    } else {
                        results.forEachIndexed { index, result ->
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(15.dp),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "QUESTION ${index + 1}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    Text(
                                        text = result.question.text,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    HorizontalDivider(
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )

                                    Text(
                                        text = "${result.answered} answers",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    // ANSWER DISTRIBUTION
                                    result.counts.forEach { count ->
                                        Column(
                                            verticalArrangement = Arrangement.spacedBy(5.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = count.label,
                                                    modifier = Modifier.weight(1f),
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )

                                                Text(
                                                    text = "${count.count} (${count.percent.toInt()}%)",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }

                                            LinearProgressIndicator(
                                                progress = {
                                                    (count.percent / 100.0)
                                                        .coerceIn(0.0, 1.0)
                                                        .toFloat()
                                                },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(7.dp),
                                                color = MaterialTheme.colorScheme.primary,
                                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                                            )
                                        }
                                    }

                                    // AVERAGE AND MEDIAN
                                    if (result.mean != null || result.median != null) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            result.mean?.let {
                                                MetricCard(
                                                    label = "Average",
                                                    value = it.oneDecimal(),
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }

                                            result.median?.let {
                                                MetricCard(
                                                    label = "Median",
                                                    value = it.oneDecimal(),
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }
                                        }
                                    }

                                    // WRITTEN ANSWERS
                                    if (result.textAnswers.isNotEmpty()) {
                                        HorizontalDivider(
                                            color = MaterialTheme.colorScheme.outlineVariant
                                        )

                                        Text(
                                            text = "Written responses",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        result.textAnswers.forEach { answer ->
                                            Surface(
                                                modifier = Modifier.fillMaxWidth(),
                                                color = MaterialTheme.colorScheme.surfaceVariant,
                                                shape = RoundedCornerShape(10.dp)
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(12.dp),
                                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Text(
                                                        text = answer.text,
                                                        fontSize = 12.sp,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )

                                                    answer.respondent?.let {
                                                        Text(
                                                            text = "— $it",
                                                            fontSize = 10.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun InterviewBookingCard(
    index: Int,
    slot: InterviewSlot
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "INTERVIEW ${index + 1}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Booked",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Text(
                text = slot.bookedByName?.takeIf { it.isNotBlank() }
                    ?: "Respondent name unavailable",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
            )

            BookingDetailRow(
                label = "Schedule",
                value = slot.startsAt.replace("T", " ")
            )

            BookingDetailRow(
                label = "Location",
                value = slot.location.ifBlank { "Not specified" }
            )

            if (slot.notes.isNotBlank()) {
                BookingDetailRow(
                    label = "Notes",
                    value = slot.notes
                )
            }
        }
    }
}

@Composable
private fun BookingDetailRow(
    label: String,
    value: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SummaryStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(13.dp),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.tertiary,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun EmptyResultsCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(
            modifier = Modifier.padding(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Text(
                text = "No responses yet",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Once participants submit their responses, the results and statistics will appear here.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyInterviewBookingsCard() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(
            modifier = Modifier.padding(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Text(
                text = "No interview bookings yet",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "When respondents choose an interview schedule, their names and selected schedules will appear here.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
