package com.amalitech.readingroom.domain;

import java.util.ArrayList;
import java.util.List;

public class Library {
    //fields
    public String name;
    public List<LendingItem> items = new ArrayList<>();
    public List<Member> members = new ArrayList<>();
    private List<Loan> loans = new ArrayList<>();

    //constructor
    public Library(String name) {
        this.name = name;
    }

    //methods
    public void addItem(LendingItem item) {
        items.add(item);
    }

    public void addMember(Member member) {
        members.add(member);
    }

    public void borrow(Member member, LendingItem item) {
        int loanDays = 0;
        if (item.type.equals("BOOK")) {
            loanDays = 14;
            if (member.kind.equals("STUDENT")) {
                loanDays = 28;
            }
            item.status = "ON_LOAN";
            item.currentBorrower = member.name;
            System.out.println("Lent \"" + item.title + "\" to " + member.name + "for " + loanDays + " days");
        } else if (item.type.equals("AUDIO_KIT")) {
            loanDays = 7;
            if (member.kind.equals("STUDENT")) {
                loanDays = 14;
            }
            item.status = "ON_LOAN";
            item.currentBorrower = member.name;
            System.out.println("Lent \"" + item.title + "\" to " + member.name + "for " + loanDays + " days");
        }
        member.history.add("Borrowed " + item.title);
        loans.add(new Loan(member.name, item.title, loanDays));
    }

    public void returnItem(LendingItem item) {
        item.status = "AVAILABLE";
        item.currentBorrower = null;
        for (Loan l : loans) {
            if (l.itemTitle.equals(item.title) && l.status.equals("ACTIVE")) {
                l.status = "RETURNED";
            }
        }
        System.out.println("Returned \"" + item.title + "\"");
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public void printReport() {
        System.out.println();
        System.out.println("=== " + name + " - end of day ===");
        System.out.println("Items:");
        for (LendingItem i : items) {
            System.out.println(" \"" + i.title + "\" (" + i.type + "): " + i.status
                    + (i.currentBorrower != null ? " - with " + i.currentBorrower : ""));
        }
        System.out.println("Active loans:");
        for (Loan l : loans) {
            if (l.status.equals("ACTIVE")) {
                System.out.println(" Loan #" + l.id + ": \"" + l.itemTitle + "\" -> " + l.memberName);
            }
        }
    }
}
