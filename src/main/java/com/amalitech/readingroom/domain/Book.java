package com.amalitech.readingroom.domain;

public class Book extends LendingItem {
    public Book(String title) {
        super(title);
    }

    @Override
    public String getType() {
        return "BOOK";
    }

    @Override
    public int loanDaysFor(MemberKind kind) {
        if (kind == MemberKind.STUDENT) {
            return 28;
        }
        return 14;
    }
}
