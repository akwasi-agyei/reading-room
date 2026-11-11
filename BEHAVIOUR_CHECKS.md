# Behavior preservation checks

BASELINE_OUTPUT.txt contains the output of the unchanged starter code.
The rebuild will preserve normal behavior and explicitly document corrections
to invalid or inconsistent behavior.

## Lending periods

These periods come from inspecting Library.borrow.

| Item | Member | Expected period | Rebuild result |
|---|---|---:|---|
| Book | Standard | 14 days | Not checked yet |
| Book | Student | 28 days | Not checked yet |
| Audio kit | Standard | 7 days | Not checked yet |
| Audio kit | Student | 14 days | Not checked yet |

## Normal operations to preserve

| Check | Expected behavior | Rebuild result |
|---|---|---|
| Successful borrow | Creates one active loan and adds one Borrowed title entry to the member's history | Not checked yet |
| Return a borrowed item | Item becomes available, borrower is cleared, and its active loan becomes returned | Not checked yet |
| Borrow after return | Creates a new active loan and another history entry | Not checked yet |
| Return an already available item | Prints the return message without creating a loan or history entry | Not checked yet |
| Loan numbering | Successful loans receive consecutive IDs in creation order within a fresh run | Not checked yet |
| Report | Lists items in their registration order and active loans in creation order; omits returned loans | Not checked yet |

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
| Another member borrows an item already on loan | Overwrites the item's borrower and creates a second active loan | Reject the attempt; keep the original loan and borrower | Not checked yet |
| Same member borrows the same item again | Also creates another active loan | Reject the attempt | Not checked yet |
| State after rejected borrowing | Starter has no rejection path | No changes to loans, history, item state, or ID counters | Not checked yet |
| Return one of two distinct copies sharing a title | Closes every active loan with that title | Close only the loan for the returned item identity | Not checked yet |
| Invalid item/member category | Unknown item type can create a zero-day loan; unknown member kind receives standard terms | Prevent unsupported categories through the proposed domain types | Not checked yet |

Duplicate-loan rejection is explicitly required by the brief. Identity-based
returns and category protection are suggested corrections described in
DIAGNOSIS.md

For the supplied demonstration, the rebuilt report should keep the novel with
Kofi and list only Loan #1 as active. The audio kit should remain available.
The rejected attempt by Ama should not create Loan #3 or add a borrowing history
entry. The console should explain the rejection and still print the report.
