
package compose.project.sit_research_hub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.project.sit_research_hub.domain.model.SortOrder
import compose.project.sit_research_hub.domain.model.ThemeMode
import compose.project.sit_research_hub.presentation.settings.SettingsViewModel

private enum class SettingsPage {
    MAIN, PRIVACY, ABOUT
}

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit,
    onYourResearches: () -> Unit
) {
    val settings = viewModel.settings.collectAsState().value
    var currentPage by remember { mutableStateOf(SettingsPage.MAIN) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 640.dp)
                .weight(1f)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            when (currentPage) {
                SettingsPage.MAIN -> {
                    Text(
                        "Settings",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(horizontal = 20.dp, vertical = 18.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            "Theme",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ChoiceButton("System", settings.themeMode == ThemeMode.SYSTEM) {
                                viewModel.setTheme(ThemeMode.SYSTEM)
                            }
                            ChoiceButton("Dark", settings.themeMode == ThemeMode.DARK) {
                                viewModel.setTheme(ThemeMode.DARK)
                            }
                            ChoiceButton("Light", settings.themeMode == ThemeMode.LIGHT) {
                                viewModel.setTheme(ThemeMode.LIGHT)
                            }
                        }

                        Text(
                            "Sort order",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )

                        ChoiceRow(
                            "Newest to Oldest",
                            settings.sortOrder == SortOrder.NEWEST_FIRST
                        ) {
                            viewModel.setSortOrder(SortOrder.NEWEST_FIRST)
                        }

                        ChoiceRow(
                            "Research Title (A-Z)",
                            settings.sortOrder == SortOrder.TITLE_A_TO_Z
                        ) {
                            viewModel.setSortOrder(SortOrder.TITLE_A_TO_Z)
                        }

                        Text(
                            "Information",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )

                        InfoRow("ⓘ", "Privacy Policy") {
                            currentPage = SettingsPage.PRIVACY
                        }

                        InfoRow("ⓘ", "About Research Hub") {
                            currentPage = SettingsPage.ABOUT
                        }
                    }

                    BottomNavigation(
                        onBackClick = onBackClick,
                        onYourResearches = onYourResearches
                    )
                }

                SettingsPage.PRIVACY -> {
                    InformationPageHeader(
                        title = "Privacy Policy",
                        onBack = { currentPage = SettingsPage.MAIN }
                    )

                    PrivacyPolicyContent()
                }

                SettingsPage.ABOUT -> {
                    InformationPageHeader(
                        title = "About Research Hub",
                        onBack = { currentPage = SettingsPage.MAIN }
                    )

                    AboutContent()
                }
            }
        }
    }
}

@Composable
private fun InformationPageHeader(
    title: String,
    onBack: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "‹",
                modifier = Modifier
                    .clickable(onClick = onBack)
                    .padding(end = 16.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 30.sp
            )

            Text(
                title,
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun PrivacyPolicyContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Your Privacy Matters",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            "SIT Research Hub values your privacy. This Privacy Policy explains what information we collect, how we use it, who can access it, and how we protect your information when you use the application.",
            color = MaterialTheme.colorScheme.onSurface
        )

        PolicyHeading("Information We Collect")

        PolicyParagraph(
            "1. Account Information – Your name, email address, program, and year level."
        )
        PolicyParagraph(
            "2. Research Details and Study Information – Research titles, descriptions, questionnaires, and other study-related materials."
        )
        PolicyParagraph(
            "3. Research Responses – Answers and information you submit when participating in research studies."
        )
        PolicyParagraph(
            "4. Interview Information – Interview schedules, availability, and other details needed to coordinate interviews."
        )
        PolicyParagraph(
            "5. Eligibility Information – Details such as age range, academic program, year level, or other qualifications required for participation in a particular study."
        )

        PolicyHeading("How We Use Your Information")

        PolicyParagraph(
            "1. To manage your account."
        )
        PolicyParagraph(
            "2. To display research studies that match your eligibility."
        )
        PolicyParagraph(
            "3. To allow researchers to create, collect, and manage responses."
        )
        PolicyParagraph(
            "4. To support quantitative and qualitative research activities."
        )
        PolicyParagraph(
            "5. To coordinate interview schedules."
        )

        PolicyHeading("Who Can See Your Data")

        PolicySubheading("Researchers")
        PolicyParagraph(
            "Researchers can access responses and related information from their own research studies. They should not have access to responses from studies they do not manage."
        )

        PolicySubheading("Respondents")
        PolicyParagraph(
            "Respondents cannot view the personal information or individual responses of other respondents."
        )

        PolicyHeading("Anonymity vs. Confidentiality")

        PolicyParagraph(
            "SIT Research Hub recognizes the importance of protecting respondents' identities and research responses. For quantitative research, researchers may choose to enable anonymous responses, in which identifying information is not attached to the responses or made visible to the researcher. However, whether anonymous or not, all quantitative responses are treated as confidential and access is restricted to the researcher responsible for the study and other specifically authorized individuals, when applicable. If a quantitative study does not allow anonymous responses, respondents will be informed before submitting their answers. For qualitative research, particularly studies that involve interviews, identifying information such as the respondent's name and email address may be necessary for communication and interview scheduling. In these cases, responses are treated as confidential rather than anonymous, and access is limited to the researcher responsible for the study and other specifically authorized individuals, when applicable. SIT Research Hub aims to ensure that respondents' personal information and research responses are handled responsibly and are not disclosed to unauthorized users."
        )

        PolicyHeading("Researcher Responsibilities")

        PolicyParagraph(
            "1. Use collected information only for the purposes stated in their research studies."
        )
        PolicyParagraph(
            "2. Protect the confidentiality of respondents and their submitted information."
        )
        PolicyParagraph(
            "3. Follow applicable university research ethics policies and guidelines."
        )
        PolicyParagraph(
            "4. Obtain the necessary consent from participants before collecting or using their information."
        )
        PolicyParagraph(
            "5. Avoid unauthorized sharing, disclosure, or misuse of research responses."
        )
        PolicyParagraph(
            "6. Observe the applicable requirements of the Data Privacy Act of 2012 (Republic Act No. 10173)."
        )

        PolicyParagraph(
            "Failure to comply with applicable privacy laws and university research ethics requirements may result in appropriate institutional or legal consequences."
        )

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun AboutContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "About the Application",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        PolicyParagraph(
            "SIT Research Hub is a research platform designed to connect researchers and respondents within the School of Information Technology."
        )

        PolicyParagraph(
            "It provides a centralized space where users can create research studies, discover opportunities to participate in studies, submit responses, and coordinate interviews."
        )

        PolicyHeading("Our Purpose")

        PolicyParagraph(
            "To make academic research more organized, accessible, and convenient by providing a platform for researchers and respondents to connect."
        )

        PolicyHeading("Key Features")

        PolicySubheading("Research Creation")
        PolicyParagraph(
            "Create and manage quantitative and qualitative studies."
        )

        PolicySubheading("Research Participation")
        PolicyParagraph(
            "Discover studies and participate based on eligibility requirements."
        )

        PolicySubheading("Response Management")
        PolicyParagraph(
            "Organize responses and support quantitative data summaries."
        )

        PolicySubheading("Interview Scheduling")
        PolicyParagraph(
            "Coordinate interviews for qualitative research."
        )

        PolicyHeading("Who Can Use It?")

        PolicyParagraph(
            "Students and researchers within the intended SIT community can use the platform as researchers and respondents."
        )

        Spacer(Modifier.height(12.dp))
    }
}

@Composable
private fun PolicyHeading(text: String) {
    Text(
        text,
        modifier = Modifier.padding(top = 8.dp),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun PolicySubheading(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun PolicyParagraph(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
        lineHeight = 22.sp
    )
}

@Composable
private fun RowScope.ChoiceButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        label,
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(6.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        color = if (selected) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Medium
    )
}

@Composable
private fun ChoiceRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(6.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            label,
            color = if (selected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun InfoRow(
    icon: String,
    label: String,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(7.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            icon,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.width(12.dp))

        Text(
            label,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            "›",
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun BottomNavigation(
    onBackClick: () -> Unit,
    onYourResearches: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(MaterialTheme.colorScheme.primary),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomItem("⌂", "Home", false, onBackClick)
        BottomItem("▣", "Your Researches", false, onYourResearches)
        BottomItem("⚙", "Settings", true) {}
    }
}

@Composable
private fun RowScope.BottomItem(
    icon: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        Modifier
            .weight(1f)
            .fillMaxSize()
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            icon,
            color = MaterialTheme.colorScheme.onPrimary.copy(
                alpha = if (selected) 1f else .75f
            ),
            fontSize = 24.sp
        )

        Text(
            label,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 10.sp,
            lineHeight = 11.sp
        )
    }
}
