package com.amalitech.readingroom.domain;

public abstract class LendingItem {
    private final String title;
    private String status; // "AVAILABLE" or "ON_LOAN"
    private String currentBorrower;
    private final int id;
    private static int counter = 1;

    protected LendingItem(String title) {
        this.id = counter;
        counter++;
        this.title = title;
        this.status = "AVAILABLE";
    }

    public String getTitle() {
        return title;
    }

    public abstract String getType();

    public abstract int loanDaysFor(MemberKind kind);

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getCurrentBorrower() {
        return currentBorrower;
    }

    // Package access: lending transitions are internal to the domain package.
    void markOnLoan(String borrowerName) {
        status = "ON_LOAN";
        currentBorrower = borrowerName;
    }

    void markAvailable() {
        status = "AVAILABLE";
        currentBorrower = null;
    }
}