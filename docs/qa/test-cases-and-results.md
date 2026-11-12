# SIT Research Hub Test Cases and Results

## Week 2 Test Cases

| ID | Test scenario | Expected result |
| --- | --- | --- |
| TC-001 | Complete profile setup with program, year level, and consent. | Home screen opens. |
| TC-002 | Select Continue with missing profile data or consent. | Validation message appears. |
| TC-003 | Open research feed after profile setup. | Sample studies appear. |
| TC-004 | Search using a study title or description keyword. | Matching study cards appear. |
| TC-005 | Select Qualitative filter. | Only qualitative studies appear. |
| TC-006 | Select Quantitative filter. | Only quantitative studies appear. |
| TC-007 | Select All filter. | All available studies appear. |
| TC-008 | Enable Eligible-only using BSIT and 2nd Year. | Eligible studies appear, including restricted study. |
| TC-009 | Enable Eligible-only using BSCS and 2nd Year. | Restricted study does not appear. |
| TC-010 | Open a study from the feed. | Selected study details appear. |
| TC-011 | Select Back from detail screen. | Home screen opens. |
| TC-012 | Change Light theme to Dark. | Dark theme applies. |
| TC-013 | Change Dark theme to Light. | Light theme applies. |
| TC-014 | Select Research Title A to Z sorting. | Study cards appear in alphabetical title order. |
| TC-015 | Select Newest to Oldest sorting. | Study cards appear from newest to oldest. |
| TC-016 | Select Your Researches from bottom navigation. | Your Researches screen opens. |
| TC-017 | Select Privacy Policy. | Privacy Policy content opens. |
| TC-018 | Select About Research Hub. | App information opens. |
| TC-019 | Restart app after profile setup. | Selected program and year level remain saved. |
| TC-020 | Select Submit from research detail screen. | Form submission process starts or validation appears. |
| TC-021 | Select Choose Schedule. | Available interview schedules appear. |
| TC-022 | View Your Researches label in bottom navigation. | Label remains centered below icon. |

## Week 2 Test Execution Record

| Test ID | Actual result | Status | Bug ID |
| --- | --- | --- | --- |
| TC-001 | Home screen opened after selecting a program, year level, and consent. | Pass | |
| TC-002 | Validation message appeared. Profile setup screen remained open. | Pass | |
| TC-003 | Research feed opened and displayed two sample studies. | Pass | |
| TC-004 | Search displayed study cards matching entered keywords. | Pass | |
| TC-005 | Qualitative filter displayed only qualitative study cards. | Pass | |
| TC-006 | Quantitative filter displayed only quantitative study cards. | Pass | |
| TC-007 | All filter displayed available qualitative and quantitative studies. | Pass | |
| TC-008 | Eligible-only filter displayed unrestricted studies and the BSIT restricted study. | Pass | |
| TC-009 | Eligible-only filter hid the BSIT restricted study for BSCS profile. | Pass | |
| TC-010 | Placeholder data appears instead of selected study data. | Fail | BUG-001 |
| TC-011 | Back button returned user to Home screen. | Pass | |
| TC-012 | Dark colors applied after selecting Dark theme. | Pass | |
| TC-013 | Light colors applied after selecting Light theme. | Pass | |
| TC-014 | Study cards appeared in alphabetical title order. | Pass | |
| TC-015 | Study cards appeared from newest to oldest. | Pass | |
| TC-016 | Your Researches navigation has no action. | Blocked | BUG-004 |
| TC-017 | Privacy Policy row has no action. | Blocked | BUG-005 |
| TC-018 | About Research Hub row has no action. | Blocked | BUG-005 |
| TC-019 | Profile selections reset after app restart. | Fail | BUG-006 |
| TC-020 | Submit button has no action. | Blocked | BUG-002 |
| TC-021 | Choose Schedule button has no action. | Blocked | BUG-003 |
| TC-022 | Your Researches label wraps with uneven alignment. | Fail | BUG-007 |

## Status Definitions

| Status | Meaning |
| --- | --- |
| Pass | Actual result matches expected result. |
| Fail | Actual result differs from expected result. |
| Blocked | Testing cannot continue due to a missing feature, crash, setup issue, or dependency. |
| Not Tested | Test has not started. |

