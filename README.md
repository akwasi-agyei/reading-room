# The Reading Room — Community Library Lending Tracker

This is the starter repository for the **Java Basics** applied lab. Read the lab
brief alongside this repository; it explains the business, what you are being
asked to deliver, and how your work is assessed.

## What this is

A small program The Reading Room uses to track what is borrowed. It works, but it has become risky to change. Your task is to diagnose it
and rebuild it well — **not** to change what it does, but to make it safe to
change and impossible to leave in an invalid state.

## Running it

The program is plain Java (no external dependencies). You need JDK 21.

**With Maven:**

```bash
mvn compile
mvn exec:java -Dexec.mainClass=LendingTracker
```

**Or directly with the JDK:**

```bash
javac -d target/classes src/main/java/LendingTracker.java
java -cp target/classes LendingTracker
```

Run it first, before you change anything, so you can see how it currently
behaves. Look carefully at the end-of-day report.

## What to hand in

See the lab brief for the full list: a written diagnosis, your rebuilt version
(behaving exactly as the original does today), a short note on your key design
decisions, and one improvement beyond the brief. You will talk through your work
in a live review.


## Proposed structure for rebuild

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

## Design reasoning

### Separate responsibilities using classes and packages

The app package contains LendingTracker, which creates the demonstration objects,
calls lending operations, prints operation messages, and handles rejection.
The domain package contains the library's data and lending rules. The reporting
package contains LibraryReport and MemberHistoryReport, which display information.

Keeping these jobs together in Library would require editing lending code to
change presentation. With the chosen separation, Library.borrow returns a Loan,
and the application uses it to print the success message. Library itself does
not print. A volunteer can change report wording without changing the method
that creates loans. The packages group related classes; private fields and
restricted methods provide the access protection.

### Encapsulation instead of unrestricted setters

IDs and descriptive fields are private and final, and ID counters are private.
Library owns its collections and returns copies using List.copyOf; Member does
the same for history. Callers cannot clear the library's loan list through a
getter or replace a member's history. A final collection field prevents replacing
the collection reference; it does not by itself prevent changing its contents.

Adding public setters for every field would still let callers invent an
availability state or mark a loan returned without following the lending
workflow. Instead, the Loan constructor and state-changing methods have package
access, and Library performs the transitions. Loan getters expose information
without public setters. Package access trusts other code inside domain, so new
domain code must preserve Library's ownership of lending operations.

### One authoritative loan state instead of duplicated item state

The starter stored item status, current borrower, and loan status separately.
The rebuild keeps loan status and derives item availability and borrower from
an active loan. Library.findActiveLoan searches with a simple loop;
isAvailable and getCurrentBorrower use that result.

This avoids maintaining several writable answers to the same question. Before
creating a loan, Library.borrow checks for an existing active loan and throws
IllegalStateException if it finds one. This check precedes history updates and
loan creation, so rejection consumes no loan ID and changes no records. The
application catches that specific exception and prints its message, allowing
the final report to run. A broad catch of Exception would risk hiding unrelated
programming errors.

The check protects operations within a Library instance in this single-threaded
application. Sharing an item between independent Library instances is outside
the demonstrated ownership model; each instance maintains its own loan list.

### Object identity instead of matching titles

A Loan holds references to its actual Member and LendingItem. Library matches
an item using reference equality (==), meaning the exact same object, rather
than comparing titles. Two copies of a book can share a title while remaining
different objects. Returning one therefore closes only its active loan.

Matching immutable IDs would also be possible. References are sufficient for
this small in-memory program because it passes the same objects between methods;
IDs remain available for identification. The design does not introduce storage
or reconstruct objects from saved records. Names and titles remain display data.

### Abstract items and polymorphism instead of growing type checks

LendingItem is abstract because it represents shared item identity and title,
but an actual item must supply its own type label and loan period. Book and
AudioKit extend it and override getType and loanDaysFor. Library uses:

```java
int loanDays = item.loanDaysFor(member.getKind());
```

Although item has the declared type LendingItem, Java calls the method of the
actual Book or AudioKit object. This is polymorphism. The common borrowing steps
are written once in Library rather than repeated in each type branch. MemberKind
uses the named constants STANDARD and STUDENT instead of accepting arbitrary
category strings. The getType string is now a display label supplied by the
class, not a switch controlling lending behavior.

An interface alone could define lending behavior, but it would not hold the
shared per-object identity and title fields. The abstract class provides both
shared state and a required contract for the subclasses. Another option is a
LoanPolicy interface stored inside an item through composition. That would be
useful if policies changed independently of item types. With the current fixed
rules, the shallow class hierarchy needs fewer pieces and is easier to explain.

Composition is used where the relationship is “has-a”: Library has collections
of items, members, and loans; a Loan has references to a member and an item.
Inheritance is reserved for the “is-a” relationship between Book/AudioKit and
LendingItem. Collection declarations use the List interface with ArrayList as
the implementation; no custom interface is needed solely to demonstrate syntax.

### Adding the planned e-loan item

Once the business supplies its digital loan periods:

1. Add an ELoan class in domain that extends LendingItem.
2. Call super(title) in its constructor to reuse identity and title setup.
3. Override getType to supply its report label and loanDaysFor to return the
   agreed standard/student periods.
4. Create and register an ELoan object in application setup using addItem.
5. Manually check its periods, duplicate rejection, return, and report output,
   then repeat the existing book/audio checks.

Library.borrow, Library.returnItem, Loan, and LibraryReport need no additional
item-type branch for that change. They already operate on LendingItem. The
original would have required another branch inside Library.borrow, including
period selection and repeated item updates and printing. The new structure
keeps the new rule in the new item class and reduces contact with existing rules.
This addresses different item loan periods; digital requirements such as shared
licenses or expiry have not been specified and may require further design.

## Extra improvement: member borrowing history

Volunteers can now view a member's successful borrowing history through
MemberHistoryReport.printReport(member). The demonstration displays Kofi's and
Ama's histories after the end-of-day report. An empty history displays
“No borrowing history.” Entries remain in borrowing order, including repeat
borrowing after a return. Rejected borrowing attempts do not appear.

This makes information already recorded by the tracker accessible to volunteers
without exposing mutable member data. It can help answer questions about a
member's previous borrowing. Entries contain titles only: they do not establish
current custody, dates, or return history. Use the active-loan report for current
custody. History lasts only for the current application run.

The implementation uses a class, a method, a List, an if statement, and a loop.
It reads the defensive copy provided by Member.getHistory() and performs no
lending changes. Presentation stays in the reporting package.


