package com.amalitech.readingroom.domain;

public class LendingItem {
    private final String title;
    private final String type;   // "BOOK" or "AUDIO_KIT"
    private String status; // "AVAILABLE" or "ON_LOAN"
    private String currentBorrower;
    private final int id;
    private static int counter = 1;

    public LendingItem(String title, String type) {
        this.id = counter;
        counter++;
        this.title = title;
        this.type = type;
        this.status = "AVAILABLE";
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

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
