# SIT Research Hub Test Plan

## 1. Introduction

SIT Research Hub is a mobile application designed to help students and researchers create, publish, and participate in academic research studies.

The application provides built-in research forms that let users create questions, collect responses, and view research results. Users can also browse published studies and filter them by eligibility requirements.

This test plan defines the procedures, scope, and methods for testing SIT Research Hub throughout its four-week development period.

## 2. Objectives

The QA team will:

- Verify that all required application features work according to project requirements.
- Identify errors and unexpected behavior during development.
- Check that research forms save and publish correctly.
- Verify correct handling of research questions and submitted answers.
- Ensure anonymous responses protect respondent identities.
- Verify the accuracy of response counts and statistical results.
- Check application usability and navigation.
- Test the application with SIT students and collect feedback.
- Verify fixes before final submission.

## 3. Scope of Testing

| Feature | Testing coverage |
| --- | --- |
| Account Management | Google sign-in, profile setup for program and year level, logout, and access to researcher and respondent functions with one account. |
| Research Management | Create, edit, save, publish, and view research. |
| Question Builder | Multiple choice, checkbox, short answer, paragraph, and rating scale. |
| Research Feed | Search, category filters, sorting, and study details. |
| Eligibility Filtering | Display studies based on respondent qualifications. |
| Response Submission | Answer questions and submit research forms. |
| Research Ownership | Prevent users from answering their own studies. |
| Anonymous Responses | Hide respondent identities from researchers. |
| Duplicate Prevention | Prevent repeated submissions when restricted. |
| Research Results | Response counts, frequencies, percentages, and accuracy of computed results. |
| Interview Scheduling | Create and manage interview schedules. Allow respondents to view interview details and choose an available schedule. |
| Participation History | View previous research participation. |
| Settings | Dark theme, light theme, and sorting preferences. |
| Usability and Navigation | Screen navigation, clear layout, and ease of use. |
| Security | Protect accounts, research records, and responses. Confirm that data backups are configured. |
| Error Handling | Handle invalid input and failed operations. |

## 4. Testing Methods

| Testing method | Description |
| --- | --- |
| Functional Testing | Verify whether application features work according to requirements. |
| Manual Testing | Perform actions manually and compare actual results with expected results. |
| Validation Testing | Check how the app handles missing or invalid input. |
| Integration Testing | Verify communication between application components and the Supabase backend, including authentication, REST API, and database access. |
| Automated Testing | Run test scripts for shared Kotlin business logic. |
| User Acceptance Testing | Ask SIT students to try the application and provide feedback. |
| Regression Testing | Retest previously working features after code changes. |

## 5. Testing Environment

| Component | Environment |
| --- | --- |
| IDE | IntelliJ IDEA |
| Framework | Kotlin Multiplatform |
| UI Framework | Compose Multiplatform |
| Primary Platform | Android |
| Testing Device | Android emulator or physical Android device |
| Backend Service | Supabase, including Google OAuth authentication, REST API, and database |
| Local Storage | Room and DataStore |
| Data | Sample accounts, research forms, and responses |
| Internet | Stable connection and simulated connection failure |
| Automated Testing | Kotlin Test |

## 6. Testing Schedule

| Week | Testing activities | Expected output |
| --- | --- | --- |
| Week 1 | Prepare the test plan, test cases, bug log, and testing procedures. | QA preparation documents |
| Week 2 | Test account management, research creation, question builder, and navigation. | Initial test results and bug reports |
| Week 3 | Test response submission, eligibility, anonymity, statistics, and integrations. | Updated test results and automated test records |
| Week 4 | Conduct user testing, retest fixes, and evaluate final quality. | User-testing report and final QA summary |

## 7. QA Responsibilities

- Prepare and maintain the test plan.
- Create test cases based on project requirements.
- Perform manual and automated testing.
- Record and report application defects.
- Coordinate with developers regarding reported issues.
- Retest fixes.
- Organize user testing with at least five SIT students and obtain consent before testing, in line with the Data Privacy Act of 2012, RA 10173.
- Prepare the final quality report.

