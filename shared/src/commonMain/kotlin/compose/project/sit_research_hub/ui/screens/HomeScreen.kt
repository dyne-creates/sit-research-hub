
package compose.project.sit_research_hub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import compose.project.sit_research_hub.domain.model.Study
import compose.project.sit_research_hub.domain.model.StudyType
import compose.project.sit_research_hub.presentation.home.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onResearchClick: (String) -> Unit,
    onSettingsClick: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<String?>(null) }
    var eligibleOnly by remember { mutableStateOf(false) }
    val visibleStudies = viewModel.visibleStudies.collectAsState().value
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 760.dp)
                .weight(1f)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            // HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Research Hub",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Medium
                )

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .border(1.5.dp, MaterialTheme.colorScheme.onPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "♙",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 21.sp
                    )
                }
            }

            // SEARCH BAR
            OutlinedTextField(
                value = query,
                onValueChange = { query = it; viewModel.updateQuery(it) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 12.dp),
                singleLine = true,
                label = { Text("Search studies") }
            )

            // RESEARCH FILTERS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, top = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip("All", selectedType == null) { selectedType = null; viewModel.updateType(null) }

                Spacer(modifier = Modifier.width(5.dp))

                FilterChip("Qualitative", selectedType == "Qualitative") { selectedType = "Qualitative"; viewModel.updateType(StudyType.QUALITATIVE) }

                Spacer(modifier = Modifier.width(5.dp))

                FilterChip("Quantitative", selectedType == "Quantitative") { selectedType = "Quantitative"; viewModel.updateType(StudyType.QUANTITATIVE) }

                Spacer(modifier = Modifier.weight(1f))

                Checkbox(checked = eligibleOnly, onCheckedChange = { eligibleOnly = it; viewModel.updateEligibleOnly(it) })
                Text("Eligible", color = MaterialTheme.colorScheme.onSurface, fontSize = 11.sp)
            }

            // RESEARCH CARDS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (visibleStudies.isEmpty()) {
                    Text("No studies match your search or filters.", color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(16.dp))
                } else {
                    visibleStudies.forEach { study ->
                        ResearchCard(study) {
                            onResearchClick(study.id)
                        }
                    }
                }
            }

            // BOTTOM NAVIGATION
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .background(MaterialTheme.colorScheme.primary),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    symbol = "◆",
                    label = "Home",
                    selected = true
                )

                BottomNavItem(
                    symbol = "▣",
                    label = "Your\nResearches",
                    selected = false
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clickable { onSettingsClick() },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "⚙",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 23.sp
                    )
                    Text(
                        text = "Settings",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(5.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 9.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ResearchCard(
    study: Study,
    onReadMore: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(13.dp)
            )
            .clip(RoundedCornerShape(13.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = study.title,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.width(7.dp))

            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.tertiary,
                        RoundedCornerShape(3.dp)
                    )
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = study.type.name.lowercase().replaceFirstChar { it.uppercase() },
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 8.sp
                )
            }
        }

        Text(
            text = "Respondent requirements",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = study.requirements.joinToString(separator = "\n") { "• $it" },
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 10.sp,
            lineHeight = 14.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .clickable { onReadMore() }
                    .padding(horizontal = 9.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Read More",
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
private fun RowScope.BottomNavItem(
    symbol: String,
    label: String,
    selected: Boolean
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = symbol,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimary.copy(alpha = .72f),
            fontSize = 22.sp
        )

        Text(
            text = label,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 9.sp,
            lineHeight = 10.sp
        )
    }
}
