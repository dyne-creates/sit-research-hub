package compose.project.sit_research_hub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private val Programs = listOf("BSIT", "BSCS", "BSCPE", "ACT-MM")
private val Years = listOf("1st Year", "2nd Year", "3rd Year", "4th Year")

@Composable
fun ProfileSetupScreen(onContinue: (program: String, yearLevel: String) -> Unit) {
    var program by remember { mutableStateOf<String?>(null) }
    var year by remember { mutableStateOf<String?>(null) }
    var consent by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
        Card(Modifier.fillMaxWidth().widthIn(max = 560.dp).padding(20.dp)) {
            Column(
                Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 28.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(Modifier.size(72.dp).background(MaterialTheme.colorScheme.primary, CircleShape), contentAlignment = Alignment.Center) {
                    Text("SIT", color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                Text("Welcome to SIT Research Hub", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text("Tell us about yourself so we can show studies you qualify for.", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)

                ProfileDropdown("Academic program", program ?: "Select program", Programs, onSelected = { program = it; if (it == "ACT-MM" && year in listOf("3rd Year", "4th Year")) year = null })
                ProfileDropdown("Year level", year ?: "Select year level", Years.filterNot { program == "ACT-MM" && it in listOf("3rd Year", "4th Year") }, enabled = program != null, onSelected = { year = it })

                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = consent, onCheckedChange = { consent = it })
                    Text("I agree to the Privacy Policy and Data Collection Terms.", modifier = Modifier.padding(start = 8.dp))
                }
                error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                Button(
                    onClick = { if (program == null || year == null || !consent) error = "Select your program, year level, and consent to continue." else { error = null; onContinue(program!!, year!!) } },
                    modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) { Text("Continue to Home") }
            }
        }
    }
}

@Composable
private fun ProfileDropdown(label: String, value: String, options: List<String>, enabled: Boolean = true, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
        Box {
            OutlinedButton(onClick = { expanded = true }, enabled = enabled, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(value); Text(if (expanded) "⌃" else "⌄") }
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option -> DropdownMenuItem(text = { Text(option) }, onClick = { onSelected(option); expanded = false }) }
            }
        }
    }
}
