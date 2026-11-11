package com.amalitech.readingroom.domain;

public class Loan {
    private final int id;
    private final Member member;
    private final LendingItem item;
    private final int loanDays;
    private String status; // "ACTIVE" or "RETURNED"
    private static int counter = 1;

    Loan(Member member, LendingItem item, int loanDays) {
        this.id = counter;
        counter++;
        this.member = member;
        this.item = item;
        this.loanDays = loanDays;
        this.status = "ACTIVE";
    }

    public int getId() {
        return id;
    }

    public String getMemberName() {
        return member.getName();
    }

    public String getItemTitle() {
        return item.getTitle();
    }

    public Member getMember() {
        return member;
    }

    public LendingItem getItem() {
        return item;
    }

    public int getLoanDays() {
        return loanDays;
    }

    public String getStatus() {
        return status;
    }

    void markReturned() {
        status = "RETURNED";
    }
}