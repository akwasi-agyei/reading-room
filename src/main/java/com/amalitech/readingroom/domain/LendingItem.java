package com.amalitech.readingroom.domain;

public class LendingItem {
    //fields
    public String title;
    public String type;
    public String status;
    public String currentBorrower;
    public int id;
    static int counter = 1;

    //constructor
    public LendingItem(String title, String type) {
        this.id = counter;
        counter++;
        this.title = title;
        this.type = type;
        this.status = "AVAILABLE";
    }

}
