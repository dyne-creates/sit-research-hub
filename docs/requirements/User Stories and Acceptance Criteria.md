**User Stories and Acceptance Criteria**

**US-01: Create and Publish Research Forms**

| Field | Details |
| :---- | :---- |
| **Story ID** | US-01 |
| **User Story** | As a researcher, I want to create and publish research forms so that I can collect data from respondents. |
| **AC-01** | Given I am logged in as a researcher, when I select Create Research, then the app opens a form creation screen. |
| **AC-02** | Given I am creating a research form, when I enter the research title, description, and other required details, then the app allows me to save the form. |
| **AC-03** | Given my research form contains all required information, when I select Publish, then the research becomes visible to eligible respondents. |

**US-02: Add Different Question Types**

| Field | Details |
| :---- | :---- |
| **Story ID** | US-02 |
| **User Story** | As a researcher, I want to add different question types to my research forms so that I can collect various kinds of data. |
| **AC-01** | Given I am creating a research form, when I select Add Question, then the app displays the available question types. |
| **AC-02** | Given I select a question type, when I create a question, then the app provides the appropriate input field for that question. |
| **AC-03** | Given I have added questions to my research form, when I save the form, then the questions are saved with their corresponding types and answer options. |

**US-03: Browse Available Research Studies**

| Field | Details |
| :---- | :---- |
| **Story ID** | US-03 |
| **User Story** | As a respondent, I want to browse available research studies so that I can find opportunities to participate in academic research. |
| **AC-01** | Given I am logged in, when I open the Research Studies screen, then the app displays published research studies. |
| **AC-02** | Given research studies are displayed, when I view a study, then I can see its title, description, researcher, and respondent requirements. |

**US-04: Filter Research Studies by Eligibility**

| Field | Details |
| :---- | :---- |
| **Story ID** | US-04 |
| **User Story** | As a respondent, I want to filter research studies based on my qualifications so that I can find studies I am eligible to participate in. |
| **AC-01** | Given the research study list is displayed, when I select an eligibility filter, then the app displays studies matching the selected qualifications. |
| **AC-02** | Given a study has specific respondent requirements, when my qualifications do not meet those requirements, then the app identifies that I am not eligible to participate. |
| **AC-03** | Given I change or clear my selected filters, when the filter is updated, then the displayed research studies reflect the current selection. |

**US-05: Calculate Quantitative Responses Automatically**

| Field | Details |
| :---- | :---- |
| **Story ID** | US-05 |
| **User Story** | As a researcher, I want the system to automatically calculate quantitative responses so that I can analyze the collected data more efficiently. |
| **AC-01** | Given respondents have submitted answers to a research form, when I open the research results, then the app displays the total number of responses received. |
| **AC-02** | Given a question contains quantitative answers, when I view its results, then the app automatically calculates the frequency and percentage of each answer. |
| **AC-03** | Given the research receives additional responses, when the results are updated, then the app recalculates the displayed statistics based on the updated responses. |

**US-06: Prevent Researchers from Answering Their Own Research**

| Field | Details |
| :---- | :---- |
| **Story ID** | US-06 |
| **User Story** | As a researcher, I want the system to prevent me from answering my own research so that the collected data remains valid. |
| **AC-01** | Given I created a research study, when I open that study using my account, then the app does not allow me to submit a response. |
| **AC-02** | Given I open a research study created by another researcher, when I meet its eligibility requirements, then the app allows me to participate. |
| **AC-03** | Given I attempt to access my own research study as a respondent, when the app checks my account against the study owner, then the app displays a message explaining that I cannot answer my own research. |

**US-07: Submit Anonymous Responses**

| Field | Details |
| :---- | :---- |
| **Story ID** | US-07 |
| **User Story** | As a respondent, I want to submit anonymous responses when permitted so that my identity remains protected. |
| **AC-01** | Given a research study allows anonymous participation, when I open its form, then the app indicates that anonymous responses are accepted. |
| **AC-02** | Given I submit an anonymous response, when the app saves my answers, then my name and other directly identifying information are not displayed to the researcher alongside my submitted answers. |
| **AC-03** | Given a research study does not allow anonymous participation, when I open its form, then the app informs me that my participation is not anonymous before submission. |

**US-08: Schedule Qualitative Research Interviews**

| Field | Details |
| :---- | :---- |
| **Story ID** | US-08 |
| **User Story** | As a researcher, I want to create and manage interview schedules so that I can arrange interviews for qualitative data collection. |
| **AC-01** | Given I am managing a research study, when I select Schedule Interview, then the app opens an interview scheduling screen. |
| **AC-02** | Given I am creating an interview schedule, when I enter the interview date, time, and other necessary details, then the app allows me to save the schedule. |
| **AC-03** | Given an interview schedule has been created, when the schedule is saved, then respondents of that study can view the interview details and choose an available schedule. |

**US-09: Sign In with a University Account**

| Field | Details |
| :---- | :---- |
| **Story ID** | US-09 |
| **User Story** | As a user, I want to sign in with my university Google account so that I can access the app securely without creating a separate password. |
| **AC-01** | Given that I open the app, when I select Sign in with my University account, then the app starts the Google sign-in process. |
| **AC-02** | Given I sign in for the first time, when sign-in succeeds, then the app asks me to select my academic program and year level and to agree to the privacy policy and data collection terms before I continue to the home page. |
| **AC-03** | Given I attempt to sign in with an account that is not a university account, when the sign-in fails, then the app denies access and displays a clear error message. |
| **AC-04** | Given I am signed in, when I select Log Out on the My Account screen, then the app ends my session and returns me to the sign-in screen. |

**US-10: Use One Account for Both Roles**

| Field | Details |
| :---- | :---- |
| **Story ID** | US-10 |
| **User Story** | As a user, I want to act as both a researcher and a respondent using one account so that I can create research and participate in other studies without switching accounts. |
| **AC-01** | Given I am signed in, when I open the Home screen, then I can browse and answer research studies as a respondent. |
| **AC-02** | Given I am signed in, when I open the Your Researches screen, then I can create and manage my own research as a researcher. |
| **AC-03** | Given I use both functions, when I move between screens, then the app does not require me to sign in again. |

**US-11: Prevent Duplicate Submissions**

| Field | Details |
| :---- | :---- |
| **Story ID** | US-11 |
| **User Story** | As a researcher, I want the system to prevent duplicate submissions when a study allows only one response per respondent so that each respondent is counted once. |
| **AC-01** | Given a study allows only one response per respondent, when a respondent who has not yet responded submits answers, then the app accepts and counts the response once. |
| **AC-02** | Given a study allows only one response per respondent, when a respondent who has already responded opens the study, then the app does not allow another submission. |
| **AC-03** | Given a respondent attempts to submit a duplicate response, when the app detects the earlier submission, then the app displays a message explaining that the study has already been answered. |

**US-12: View Participation History**

| Field | Details |
| :---- | :---- |
| **Story ID** | US-12 |
| **User Story** | As a respondent, I want to view my previous research participation so that I can keep track of the studies I have answered. |
| **AC-01** | Given I am logged in, when I open My Responses on the My Account screen, then the app displays the research studies I have participated in. |
| **AC-02** | Given I have not participated in any study, when I open My Responses, then the app displays a message explaining that there is no participation history yet. |
| **AC-03** | Given my participation history is displayed, when I select a study, then I can see its title and researcher. |

**US-13: Answer and Submit a research form**

| Field | Details |
| :---- | :---- |
| **Story ID** | US-13 |
| **User Story** | As a respondent, I want to answer the questions in a research study and submit my responses so that I can contribute to the research. |
| **AC-01** | Given I am eligible for a published study, when I open it and choose to participate, then the app displays its questions with the appropriate input field for each question type. |
| **AC-02** | Given I have answered all required questions, when I select Submit, then the app saves my responses and confirms the submission. |
| **AC-03** | Given I have left a required question unanswered or entered invalid input, when I select Submit, then the app displays a clear error message and does not submit my responses. |

