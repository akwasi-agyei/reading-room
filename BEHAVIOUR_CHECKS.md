# Behavior preservation checks

BASELINE_OUTPUT.txt contains the output of the unchanged starter code.
The rebuild will preserve normal behavior and explicitly document corrections
to invalid or inconsistent behavior.

## Lending periods

These periods come from inspecting Library.borrow.

| Item | Member | Expected period | Rebuild result |
|---|---|---:|---|
| Book | Standard | 14 days |  14 days|
| Book | Student | 28 days |  28 days |
| Audio kit | Standard | 7 days |  7 days|
| Audio kit | Student | 14 days |  14 days|

## Normal operations to preserve

| Check | Expected behavior | Rebuild result |
|---|---|---|
| Successful borrow | Creates one active loan and adds one Borrowed title entry to the member's history | Works as before|
| Return a borrowed item | Item becomes available, borrower is cleared, and its active loan becomes returned | Works as before |
| Borrow after return | Creates a new active loan and another history entry | Works as before |
| Return an already available item | Prints the return message without creating a loan or history entry | Works as before |
| Loan numbering | Successful loans receive consecutive IDs in creation order within a fresh run | Works as before |
| Report | Lists items in their registration order and active loans in creation order; omits returned loans | Works as before |

## Console output to preserve

Successful borrow message:

```text
Lent "Things Fall Apart" to Kofi Mensah for 14 days
```

Return message:

```text
Returned "French Audio Course"
```

Use BASELINE_OUTPUT.txt to compare the report heading, blank line, indentation,
quotes, item labels, and loan formatting. Preserve ordinary successful messages
when moving printing outside the lending methods.

## Intentional corrections

| Check | Starter behavior | Required/planned rebuilt behavior | Rebuild result |
|---|---|---|---|
| Another member borrows an item already on loan | Overwrites the item's borrower and creates a second active loan | Reject the attempt; keep the original loan and borrower | Works as expected |
| Same member borrows the same item again | Also creates another active loan | Reject the attempt | Works as expected |
| State after rejected borrowing | Starter has no rejection path | No changes to loans, history, item state, or ID counters | Works as expected |
| Return one of two distinct copies sharing a title | Closes every active loan with that title | Close only the loan for the returned item identity | Works as expected |
| Invalid item/member category | Unknown item type can create a zero-day loan; unknown member kind receives standard terms | Prevent unsupported categories through the proposed domain types | Works as expected |

Duplicate-loan rejection is explicitly required by the brief. Identity-based
returns and category protection are suggested corrections described in
DIAGNOSIS.md

For the supplied demonstration, the rebuilt report should keep the novel with
Kofi and list only Loan #1 as active. The audio kit should remain available.
The rejected attempt by Ama should not create Loan #3 or add a borrowing history
entry. The console should explain the rejection and still print the report.
