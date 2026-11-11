package com.amalitech.readingroom.domain;

import java.util.ArrayList;
import java.util.List;

public class Member {
    private final String name;
    private final MemberKind kind;
    private final int id;
    private final List<String> history = new ArrayList<>();
    private static int counter = 1;

    public Member(String name, MemberKind kind) {
        this.id = counter;
        counter++;
        this.name = name;
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public MemberKind getKind() {
        return kind;
    }

    public int getId() {
        return id;
    }

    public List<String> getHistory() {
        return List.copyOf(history);
    }

    void recordBorrowing(String itemTitle) {
        history.add("Borrowed " + itemTitle);
    }
}