package com.amalitech.readingroom.domain;

import java.util.ArrayList;
import java.util.List;

public class Member {
    //fields
    public String name;
    public String kind;
    public int id;
    public List<String> history = new ArrayList<>();
    static int counter = 1;

    //constructor
    public Member(String name, String kind) {
        this.id = counter;
        counter++;
        this.name = name;
        this.kind = kind;
    }
}
