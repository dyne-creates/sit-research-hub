# SIT Research Hub Bug Log

## Purpose

The bug log records errors and unexpected behavior found during application testing. The QA member uses this tracker to communicate defects to developers and monitor their fixes.

## Week 2 Bug Tracking Record

| Bug ID | Test ID | Bug title | Severity | Status | Assigned to |
| --- | --- | --- | --- | --- | --- |
| BUG-001 | TC-010 | Research detail screen does not show selected study data | High | Open | Dedicated Developer |
| BUG-002 | TC-020 | Submit button has no action | High | Open | Dedicated Developer |
| BUG-003 | TC-021 | Choose Schedule button has no action | Medium | Open | Dedicated Developer |
| BUG-004 | TC-016 | Your Researches navigation has no action | Medium | Open | Dedicated Developer |
| BUG-005 | TC-017, TC-018 | Privacy Policy and About Research Hub rows have no action | Medium | Open | Dedicated Developer |
| BUG-006 | TC-019 | Profile setup selections do not persist after app restart | Medium | Open | Dedicated Developer |
| BUG-007 | TC-022 | Your Researches navigation label is not centered | Low | Open | BA Developer |

## Bug Report Format

| Field | Details |
| --- | --- |
| Bug ID | Unique defect identifier, such as BUG-001. |
| Bug title | Short defect name. |
| Date reported | Date QA found the defect. |
| Related test case | Related test ID. |
| Feature | Affected app feature. |
| Steps to reproduce | Ordered actions leading to defect. |
| Expected result | Intended app behavior. |
| Actual result | Observed app behavior. |
| Severity | Critical, High, Medium, or Low. |
| Status | Open, In Progress, Fixed, Retest, Closed, or Reopened. |
| Assigned developer | Group member handling fix. |
| Fix description | Developer explanation after fix. |
| Retest result | Pass or Fail after QA retest. |

## Bug Details

### BUG-001 Research Detail Data

| Field | Details |
| --- | --- |
| Related test case | TC-010 |
| Feature | Research Feed and Study Details |
| Steps to reproduce | Open Home. Select any study card. |
| Expected result | Detail screen shows data from selected study. |
| Actual result | Detail screen uses hardcoded study ID 1 and placeholder labels. |
| Severity | High |
| Status | Open |
| Retest result | Pending |

### BUG-002 Submit Button

| Field | Details |
| --- | --- |
| Related test case | TC-020 |
| Feature | Research Detail |
| Steps to reproduce | Open a study. Select Submit. |
| Expected result | Submission process starts, validation appears, or confirmation appears. |
| Actual result | Submit button produces no action. |
| Severity | High |
| Status | Open |
| Retest result | Pending |

### BUG-003 Choose Schedule Button

| Field | Details |
| --- | --- |
| Related test case | TC-021 |
| Feature | Interview Schedule |
| Steps to reproduce | Open a study detail screen. Select Choose Schedule. |
| Expected result | Available interview schedules appear. |
| Actual result | Choose Schedule button produces no action. |
| Severity | Medium |
| Status | Open |
| Retest result | Pending |

### BUG-004 Your Researches Navigation

| Field | Details |
| --- | --- |
| Related test case | TC-016 |
| Feature | Bottom Navigation |
| Steps to reproduce | Open Home. Select Your Researches. |
| Expected result | Your Researches screen opens. |
| Actual result | Selecting Your Researches produces no screen change. |
| Severity | Medium |
| Status | Open |
| Retest result | Pending |

### BUG-005 Information Rows

| Field | Details |
| --- | --- |
| Related test cases | TC-017 and TC-018 |
| Feature | Settings |
| Steps to reproduce | Open Settings. Select Privacy Policy or About Research Hub. |
| Expected result | Selected information page opens. |
| Actual result | Both rows produce no screen change. |
| Severity | Medium |
| Status | Open |
| Retest result | Pending |

### BUG-006 Profile Setup Persistence

| Field | Details |
| --- | --- |
| Related test case | TC-019 |
| Feature | Profile Setup |
| Steps to reproduce | Complete profile setup. Close app. Open app again. |
| Expected result | Program and year-level selections remain saved. |
| Actual result | Program and year-level selections reset after app restart. |
| Severity | Medium |
| Status | Open |
| Retest result | Pending |

### BUG-007 Your Researches Label Alignment

| Field | Details |
| --- | --- |
| Related test case | TC-022 |
| Feature | Bottom Navigation |
| Steps to reproduce | Open Home. View Your Researches label. |
| Expected result | Label stays centered below icon. |
| Actual result | Label wraps into two lines with uneven alignment. |
| Severity | Low |
| Status | Open |
| Retest result | Pending |

## Severity Classification

| Severity | Description |
| --- | --- |
| Critical | App crash, serious data loss, or privacy breach. |
| High | Main feature fails. |
| Medium | Feature works incorrectly or lacks an expected action. |
| Low | Minor visual or usability issue. |

## Bug Status

| Status | Meaning |
| --- | --- |
| Open | QA reported defect. |
| In Progress | Developer is fixing defect. |
| Fixed | Developer completed fix. |
| Retest | QA must verify fix. |
| Closed | QA verified fix. |
| Reopened | Defect remains after retest. |

