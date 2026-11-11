package com.amalitech.readingroom.domain;

import java.util.ArrayList;
import java.util.List;

public class Library {
    private final String name;
    private final List<LendingItem> items = new ArrayList<>();
    private final List<Member> members = new ArrayList<>();
    private final List<Loan> loans = new ArrayList<>();

    public Library(String name) {
        this.name = name;
    }

    public void addItem(LendingItem item) {
        items.add(item);
    }

    public void addMember(Member member) {
        members.add(member);
    }

    // Lend an item to a member. The loan period depends on the kind of item and member.
    public void borrow(Member member, LendingItem item) {
        int loanDays = 0;
        if (item.getType().equals("BOOK")) {
            loanDays = 14;
            if (member.getKind().equals("STUDENT")) {
                loanDays = 28; // students get longer loans
            }
            item.markOnLoan(member.getName());
            System.out.println("Lent \"" + item.getTitle() + "\" to " + member.getName() + " for " + loanDays + " days");
        } else if (item.getType().equals("AUDIO_KIT")) {
            loanDays = 7;
            if (member.getKind().equals("STUDENT")) {
                loanDays = 14; // students get longer loans
            }
            item.markOnLoan(member.getName());
            System.out.println("Lent \"" + item.getTitle() + "\" to " + member.getName() + " for " + loanDays + " days");
        }
        member.recordBorrowing(item.getTitle());
        loans.add(new Loan(member.getName(), item.getTitle(), loanDays));
    }

    public void returnItem(LendingItem item) {
        item.markAvailable();
        for (Loan l : loans) {
            if (l.getItemTitle().equals(item.getTitle()) && l.getStatus().equals("ACTIVE")) {
                l.markReturned();
            }
        }
        System.out.println("Returned \"" + item.getTitle() + "\"");
    }

    public String getName() {
        return name;
    }

    public List<LendingItem> getItems() {
        return List.copyOf(items);
    }

    public List<Member> getMembers() {
        return List.copyOf(members);
    }

    public List<Loan> getLoans() {
        return List.copyOf(loans);
    }

    public void printReport() {
        System.out.println();
        System.out.println("=== " + name + " - end of day ===");
        System.out.println("Items:");
        for (LendingItem i : items) {
            System.out.println("  \"" + i.getTitle() + "\" (" + i.getType() + "): " + i.getStatus()
                    + (i.getCurrentBorrower() != null ? " - with " + i.getCurrentBorrower() : ""));
        }
        System.out.println("Active loans:");
        for (Loan l : loans) {
            if (l.getStatus().equals("ACTIVE")) {
                System.out.println("  Loan #" + l.getId() + ": \"" + l.getItemTitle() + "\" -> " + l.getMemberName());
            }
        }
    }
}