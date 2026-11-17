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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.project.sit_research_hub.domain.model.Programs
import compose.project.sit_research_hub.domain.model.StudentProfile
import compose.project.sit_research_hub.domain.model.User

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
                        OutlinedTextField(program, { program = it }, Modifier.fillMaxWidth(), label = { Text("Program") }, singleLine = true)
                        OutlinedTextField(year, { year = it }, Modifier.fillMaxWidth(), label = { Text("Year level") }, singleLine = true)
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

@Composable private fun AccountLink(label: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = FontWeight.Medium); Text("›", fontSize = 22.sp)
    }
    HorizontalDivider()
}
