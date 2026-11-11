package com.amalitech.readingroom.domain;

public abstract class LendingItem {
    private final String title;
    private final int id;
    private static int counter = 1;

    protected LendingItem(String title) {
        this.id = counter;
        counter++;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public abstract String getType();

    public abstract int loanDaysFor(MemberKind kind);

    public int getId() {
        return id;
    }
}