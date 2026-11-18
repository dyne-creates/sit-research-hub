
package compose.project.sit_research_hub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun YourResearchesScreen(
    userId: String,
    onBack: () -> Unit,
    onAddResearch: () -> Unit,
    onSettingsClick: () -> Unit,
    onStudyClick: (String) -> Unit,
    onResultsClick: (String) -> Unit
) {
    val repository = koinInject<StudyRepository>()
    val studies by repository.observeStudies().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var message by remember { mutableStateOf<String?>(null) }
    var offline by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        offline = repository.refresh() is AppResult.Failure
    }

    val owned = studies.filter { it.ownerId == userId }

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
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your Researches",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Manage your published and ongoing studies.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
        }

        // STUDY COUNT AND REFRESH
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "My studies",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${owned.size} ${if (owned.size == 1) "study" else "studies"}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { scope.launch { val result = repository.refresh(); offline = result is AppResult.Failure; message = when (result) { is AppResult.Success -> "Research refreshed."; is AppResult.Failure -> result.error.message } } },
                    shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)
                ) { Text("Refresh", fontSize = 11.sp) }
                Button(onClick = onAddResearch, shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp)) { Text("+ Add", fontSize = 11.sp) }
            }
        }

        // OFFLINE MESSAGE
        if (offline) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                color = MaterialTheme.colorScheme.tertiary,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "You're viewing saved research offline.",
                    modifier = Modifier.padding(12.dp),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // FEEDBACK MESSAGE
        message?.let {
            Text(
                text = it,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 5.dp),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp
            )
        }

        // STUDY LIST
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(
                start = 18.dp,
                end = 18.dp,
                top = 6.dp,
                bottom = 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            if (owned.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp)
                            .background(
                                MaterialTheme.colorScheme.surface,
                                RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 22.dp, vertical = 30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "▤",
                            fontSize = 36.sp,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "No research yet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Studies you create will appear here. You can manage them and view their results from this page.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                }
            } else {
                items(owned, key = { it.id }) { study ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onStudyClick(study.id)
                            },
                        shape = RoundedCornerShape(15.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(15.dp),
                            verticalArrangement = Arrangement.spacedBy(11.dp)
                        ) {
                            // TITLE AND STATUS
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = study.title,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                StatusBadge(status = study.status)
                            }

                            // STUDY TYPE
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.tertiary,
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(
                                        text = study.type.label,
                                        modifier = Modifier.padding(
                                            horizontal = 10.dp,
                                            vertical = 5.dp
                                        ),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            // DESCRIPTION
                            Text(
                                text = study.description.ifBlank {
                                    "No description provided."
                                },
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 17.sp,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant
                            )

                            // ACTIONS
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        onStudyClick(study.id)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(9.dp),
                                    contentPadding = PaddingValues(
                                        horizontal = 6.dp,
                                        vertical = 7.dp
                                    )
                                ) {
                                    Text("Open", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = {
                                        onResultsClick(study.id)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(9.dp),
                                    contentPadding = PaddingValues(
                                        horizontal = 6.dp,
                                        vertical = 7.dp
                                    )
                                ) {
                                    Text("Results", fontSize = 11.sp)
                                }
                            }

                            // RESPONSE STATUS ACTION
                            if (study.status != StudyStatus.DRAFT) {
                                TextButton(
                                    onClick = {
                                        scope.launch {
                                            val next =
                                                if (study.status == StudyStatus.PUBLISHED) {
                                                    StudyStatus.CLOSED
                                                } else {
                                                    StudyStatus.PUBLISHED
                                                }

                                            when (
                                                val result = repository.setStatus(
                                                    study.id,
                                                    next
                                                )
                                            ) {
                                                is AppResult.Success -> {
                                                    message =
                                                        "Research ${next.name.lowercase()}."
                                                }

                                                is AppResult.Failure -> {
                                                    message = result.error.message
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (
                                            study.status == StudyStatus.PUBLISHED
                                        ) {
                                            "Close responses"
                                        } else {
                                            "Reopen responses"
                                        },
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().height(68.dp).background(MaterialTheme.colorScheme.primary),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationItem("⌂", "Home", false, onBack)
            NavigationItem("▤", "Your Researches", true, {})
            NavigationItem("⚙", "Settings", false, onSettingsClick)
        }
    }
}

@Composable
private fun RowScope.NavigationItem(symbol: String, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(Modifier.weight(1f).fillMaxHeight().clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(symbol, color = MaterialTheme.colorScheme.onPrimary, fontSize = 22.sp)
        Text(label, color = MaterialTheme.colorScheme.onPrimary, fontSize = 10.sp)
    }
}

@Composable
private fun StatusBadge(status: StudyStatus) {
    val label = status.name.lowercase()
        .replaceFirstChar { it.uppercase() }

    Surface(
        color = when (status) {
            StudyStatus.PUBLISHED -> MaterialTheme.colorScheme.primaryContainer
            StudyStatus.CLOSED -> MaterialTheme.colorScheme.surfaceVariant
            StudyStatus.DRAFT -> MaterialTheme.colorScheme.tertiary
        },
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
