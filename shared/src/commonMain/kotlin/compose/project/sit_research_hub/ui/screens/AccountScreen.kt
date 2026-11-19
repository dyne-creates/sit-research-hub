package compose.project.sit_research_hub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.project.sit_research_hub.domain.model.Programs
import compose.project.sit_research_hub.domain.model.StudentProfile
import compose.project.sit_research_hub.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    user: User,
    profile: StudentProfile,
    onBack: () -> Unit,
    onSaveProfile: (StudentProfile) -> Unit,
    onMyResponses: () -> Unit,
    onInterviewSchedules: () -> Unit,
    onSignOut: () -> Unit
) {
    var editing by remember { mutableStateOf(false) }
    var program by remember(profile) { mutableStateOf(profile.program) }
    var year by remember(profile) { mutableStateOf(profile.yearLevel) }
    var programExpanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("←", color = MaterialTheme.colorScheme.onPrimary, fontSize = 22.sp) }
            Text("My Account", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ElevatedCard(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(user.displayName ?: "SIT Student", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(user.email, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                }
            }
            ElevatedCard(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Academic information", fontWeight = FontWeight.Bold)
                        TextButton(onClick = { editing = !editing }) { Text(if (editing) "Cancel" else "Edit") }
                    }
                    if (editing) {
                        ExposedDropdownMenuBox(
                            expanded = programExpanded,
                            onExpandedChange = { programExpanded = !programExpanded }
                        ) {
                            OutlinedTextField(
                                value = program,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Program") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = programExpanded)
                                },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = programExpanded,
                                onDismissRequest = { programExpanded = false }
                            ) {
                                Programs.options.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            program = option
                                            programExpanded = false

                                            val currentYear = year.firstOrNull(Char::isDigit)?.digitToIntOrNull()
                                            if (option == "ACT-MM" && currentYear != null && currentYear > 2) {
                                                year = "1st Year"
                                            }
                                        }
                                    )
                                }
                            }
                        }

                        ExposedDropdownMenuBox(
                            expanded = yearExpanded,
                            onExpandedChange = { yearExpanded = !yearExpanded }
                        ) {
                            OutlinedTextField(
                                value = year,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Year level") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = yearExpanded)
                                },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = yearExpanded,
                                onDismissRequest = { yearExpanded = false }
                            ) {
                                (if (program == "ACT-MM") {
                                    listOf("1st Year", "2nd Year")
                                } else {
                                    listOf("1st Year", "2nd Year", "3rd Year", "4th Year")
                                }).forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            year = option
                                            yearExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        error?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
                        Button(onClick = {
                            val level = year.firstOrNull(Char::isDigit)?.digitToIntOrNull()
                            error = when {
                                program !in Programs.options -> "Choose a valid academic program."
                                level !in Programs.yearLevels -> "Choose a valid year level."
                                program == "ACT-MM" && level !in 1..2 -> "ACT-MM is available only for first and second year."
                                else -> null
                            }
                            if (error == null) { onSaveProfile(StudentProfile(program, year)); editing = false }
                        }, modifier = Modifier.fillMaxWidth()) { Text("Save academic information") }
                    } else {
                        Text("Program: ${profile.program}")
                        Text("Year level: ${profile.yearLevel}")
                    }
                }
            }
            AccountLink("My Responses", onMyResponses)
            AccountLink("Interview Schedules", onInterviewSchedules)
            TextButton(onClick = onSignOut, modifier = Modifier.fillMaxWidth()) { Text("Log Out", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) }
        }
    }
}


@Composable
private fun AccountLink(label: String, onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "›",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
