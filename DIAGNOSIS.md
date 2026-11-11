## Overview

The main issue is that an item can be on loan to two different members simultaneously. Realistically, this cannot happen. An example is what happens when the current code executes. "Things Fall Apart" is lent to Kofi Mensah for 14 days. The same item is lent to Ama Owusu without the item being returned by Kofi Mensah. At the end of the day, it shows that "Things Fall Apart" is on loan to both Kofi Mensah and Ama Owusu at the same time. 

Here is the output for reference:
```
Lent "Things Fall Apart" to Kofi Mensah for 14 days
Lent "French Audio Course" to Ama Owusu for 14 days
Returned "French Audio Course"
Lent "Things Fall Apart" to Ama Owusu for 28 days

=== The Reading Room - end of day ===
Items:
"Things Fall Apart" (BOOK): ON_LOAN - with Ama Owusu
"French Audio Course" (AUDIO_KIT): AVAILABLE
Active loans:
Loan #1: "Things Fall Apart" -> Kofi Mensah
Loan #3: "Things Fall Apart" -> Ama Owusu

Process finished with exit code 0
```

I have listed all the problems in the starter code that I found as part of my diagnosis below, starting with those of highest priority:

## 1. An item can have multiple active loans

**Observation:** The demo program lends the same novel "Things Fall Apart" to both Kofi Mensah and Ama Owusu. Both loans remain active, but the item names only Ama as its borrower
```
=== The Reading Room - end of day ===
Items:
  "Things Fall Apart" (BOOK): ON_LOAN - with Ama Owusu
  "French Audio Course" (AUDIO_KIT): AVAILABLE
Active loans:
  Loan #1: "Things Fall Apart" -> Kofi Mensah
  Loan #3: "Things Fall Apart" -> Ama Owusu
```
**Root cause:** Library.borrow never checks whether an active loan already exists. It overwrites item.status and item.currentBorrower, appends a history entry, and creates another Loan unconditionally. There is no controlled transition that enforces availability before changing state.
```java
item.status = "ON_LOAN";
item.currentBorrower = m.name;
//...
m.history.add("Borrowd " + item.title);
loans.add(new Loan(m.name, item.title, loanDays));
```
These writes run without an availability guard. The item update replaces the borrower while the previous loan remains active. 
**This is the most dangerous problem for this particular business because as a Lending tracker, having trustworthy custody records of items and members is it's main purpose and it is currently lacking that.**

**Business risk:** Volunteers cannot reliably determine who holds library property. They may promise an unavailable item to another member. They may also pursue the wrong borrower of an item. This makes the library seem incompetent to members and that will damage their reputation.

## 2. Callers can bypass the lending rules

**Observation:** Code can directly replace item IDs, statuses, borrower names, member categories, or loan details. It can clear Library.items or Member.history. Also, Library.getLoans returns the actual internal list, allowing callers to remove or add loans and mutate the loans it contains.

**Root cause:** Several fields and collections are public. Although the loans field is private, its mutable reference escapes through getLoans. The ID counters also have package access and can be reset by other code in the same package. Ownership of changes is not enforced.
```java
//LendingItem
public String status;
public int id;
static int counter = 1;

//Member
public List<String> history = new ArrayList<>();

//Library
public List<Loan> getLoans() {
    return loans;
}
```
**Business risk:** A future volunteer can accidentally erase records or bypass a duplicate-loan check without realizing it. Making fields private is insufficient on its own if getters still expose mutable collections or objects with unrestricted state changes. This can lead to lost assets since when records are lost, volunteers cannot track members who have been lent items.

 ## 3. Lending state has several independently writable versions
**Observations:** The program stores the answer to "is this item borrowed"? in multiple places, and those answers can disagree. 


**Root cause:** Availability and borrower information are stored separately from Loan.status.
```java
//LendingItem
item.status = "ON_LOAN";
item.currentBorrower = "Kofi Mensah";

//Loan
loan.memberName = "Kofi Mensah";
loan.status = "ACTIVE";
```
Both objects have fields that describe the same borrowing event. The item says it is borrowed by Kofi, and the loan says Kofi's loan is active. Those fields are not connected. Changing one object's field does not update the other object. If a future volunteer updates one assignment and forgets the other, the records become inconsistent.

**Business risk:** Different parts of the report can give different answers about availability and custody. Even after adding a borrow check, future changes could create inconsistent records if they update only one representation. Without proper records, the business loses track of their assets. They may expect a book to be returned by a certain member when that member does not have that book.

## 4. Returns confuse a title with an item's identity
**Observations:** In returnItem, every active loan with a matching itemTitle is marked returned. If two distinct copies of a title are borrwed, returning one would close both loans, even though the other item object would remain on loan.

**Root cause:** Loan stores a title and member name instead of references or IDs linking it to the specific item and member. returnItem compares titles despite LendingItem already having an ID. 

```java
//Loan
public String memberName;
public String itemTitle;

//Library.returnItem
if (l.itemTitle.equals(item.title) && l.status.equal("ACTIVE")){
    l.status = "RETURNED";
}
```
The condition never checks which item ID the loan belongs to.

**Business risk:** Outstanding loans can disappear from the active-loan report while members still hold items. Having multiple copies of the same book makes return tracking unreliable and so volunteers cannot safely lend out copies of the same book to members. Members become dissatisfied. 

## 5. The program accepts category names as ordinary text.
**Observations:** Item types, member kinds, and statuses are unrestricted strings. An unknown item type skips both borrowing branches but still adds history and creates an active zero-day loan. 

**Root cause:** There is no type or constructor validation restricting categories, and loan creation occurs after they type conditional conditional regardless of whether a valid rule was found. 

```java
int loanDays = 0;
if (item.type.equals("BOOK")){
    loanDays = 14;
    if (m.kind.equals("STUDENT")){
        loanDays = 28;
    }
    //...
} else if (item.type.equals("AUDIO_KIT")){
    loanDays = 7;
    if (m.kind.equals("STUDENT")){
        loanDays = 14;
    }
    //...
}
m.history.add("Borrowd " + item.title);
loans.add(new Loan(m.name, item.title, loanDays));
```
When an unknown type is entered, there is no branch to handle that. Execution reaches loan creation even when loanDays is still zero.

**Business risk:** A typo can create misleading records or apply the wrong terms. A student can be given the wrong number of loan days due to a typo. Students would feel cheated. Also, a loan can be created with zero days, which does not make sense. 

## 6. Responsibilities are coupled
**Observations:** Code that handles lending is closely tied to code that displays information. This makes either part difficult to change independently.

**Root cause:** The program has several jobs: 
* Create the sample items and members
* Decide how long an item can be borrowed
* Record borrowing and returns
* Display messages and reports.

```java
//Library.borrow
item.status = "ON_LOAN";
item.currentBorrower = m.name;

System.out.println(
        "Lent \"" + item.title + "\" to " + m.name
        + " for " + loanDays + " days"
);
```
The first two statements change the library's records. The last statement displays a message. But calling borrow always does both. These are two unrelated actions and should not be placed close together because this increases the chance of accidentally disturbing the borrowing logic.

**Business risk:** A volunteer changing a lending rule must work among unrelated presentation code. This increases the uncertainty of handing the program to its next maintainer as the chance of program logic being disturbed by something unrelated is high. Handing over the program after a volunteer moves on becomes difficult.

## Proposed structure

```text
app.LendingTracker
  |-- sets up and calls --> domain.Library
  |                          |-- contains --> Member
  |                          |-- contains --> Loan
  |                          `-- contains --> LendingItem (abstract)
  |                                              ^
  |                                              | extends
  |                                         Book / AudioKit
  `-- calls --> reporting.LibraryReport
                    `-- reads safe views from --> Library

Loan -- references --> one Member and one LendingItem
```
Proposed source layout:

```text
src/main/java/com/amalitech/readingroom/
  app/
    LendingTracker.java
  domain/
    Library.java
    LendingItem.java
    Book.java
    AudioKit.java
    Member.java
    MemberKind.java
    Loan.java
  reporting/
    LibraryReport.java
```
In the first diagram, each name represents a proposed class. Each class defines the data and behavior its objects will have. For example, Book is a class: a particular copy of "Things Fall Apart" is an object created from that Book class.

- **LendingTracker uses Library:** The application creates the sample objects and calls methods such as borrow and returnItem. Library performs those operations and checks the lending rules.
- **Library contains Member, Loan, and LendingItem objects:** Library keeps collections of these objects. This is a "has-a" relationship, implemented using collections rather than inheritance. A library has books, members, and loans, it is not any of these, hence inheritance is not needed here.
- **Book and AudioKit extend LendingItem:** This is an "is-a" relationship. A Book is a LendingItem, and AudioKit is also a LendingItem and so inheritance is appropriate here (shown by the upward arrows). LendingItem holds their common identity and title data. Since LendingItem is an abstract class, objects cannot be instantiated directly and so concrete Book and AudioKit objects are created instead. Book and AudioKir inherit the common structure and each implement loanDaysFor(MemberKind kind). Library can work with a LendingItem reference and call that method. Java runs the implementation belonging to the actual Book or AudioKit object. This is polymorphism.
- **Loan references one Member and one LendingItem:** A reference lets one object refer to another existing object. The loan points to the specific member and item involved, rather than identifying them only by name or title. Two books with the same title remain separate objects with separate identities.
- **LendingTracker uses LibraryReport:** The application asks LibraryReport to display the report. LibraryReport reads information from Library through safe methods. It does not create or return loans. This separates displaying information from changing lending records.

The second diagram shows where the source files belong. A package groups related classes and gives them a namespace:

- **app** contains the entry point and demonstration setup.
- **domain** contains the objects and rules of the library: items, members, loans, and lending operations.
- **reporting** contains the code that formats the report.

For example, domain/Book.java declares the package com.amalitech.readingroom.domain. Its full class name is com.amalitech.readingroom.domain.Book. Classes in other packages can import it when they need to use it.

These packages organize responsibilities. Encapsulation controls access. Fields will be private, and methods will expose only the operations other classes need. This is done because putting classes in separate files or packages does not by itself prevent callers from changing state incorrectly.


 

