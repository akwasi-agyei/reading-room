package com.amalitech.readingroom.domain;

public class Loan {
    //fields
    public int id;
    public String memberName;
    public String itemTitle;
    public int loanDays;
    public String status;
    static int counter = 1;

    //constructor
    public Loan(String memberName, String itemTitle, int loanDays) {
        this.id = counter;
        counter++;
        this.memberName = memberName;
        this.itemTitle = itemTitle;
        this.loanDays = loanDays;
        this.status = "ACTIVE";
    }
}
