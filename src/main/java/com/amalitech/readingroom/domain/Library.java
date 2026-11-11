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
        if (findActiveLoan(item) != null) {
            throw new IllegalStateException("Cannot lend \"" + item.getTitle() + "\": item is already on loan");
        }
        int loanDays = item.loanDaysFor(member.getKind());
        System.out.println("Lent \"" + item.getTitle() + "\" to " + member.getName() + " for " + loanDays + " days");
        member.recordBorrowing(item.getTitle());
        loans.add(new Loan(member, item, loanDays));
    }

    public void returnItem(LendingItem item) {
        Loan loan = findActiveLoan(item);
        if (loan != null) {
            loan.markReturned();
        }
        System.out.println("Returned \"" + item.getTitle() + "\"");
    }

    private Loan findActiveLoan(LendingItem item) {
        for (Loan loan : loans) {
            // Reference equality identifies the exact item object, not its title.
            if (loan.getItem() == item && loan.getStatus().equals("ACTIVE")) {
                return loan;
            }
        }
        return null;
    }

    public boolean isAvailable(LendingItem item) {
        return findActiveLoan(item) == null;
    }

    public String getCurrentBorrower(LendingItem item) {
        Loan loan = findActiveLoan(item);
        return loan == null ? null : loan.getMemberName();
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
            String borrower = getCurrentBorrower(i);
            System.out.println("  \"" + i.getTitle() + "\" (" + i.getType() + "): " + (isAvailable(i) ? "AVAILABLE" : "ON_LOAN")
                    + (borrower != null ? " - with " + borrower : ""));
        }
        System.out.println("Active loans:");
        for (Loan l : loans) {
            if (l.getStatus().equals("ACTIVE")) {
                System.out.println("  Loan #" + l.getId() + ": \"" + l.getItemTitle() + "\" -> " + l.getMemberName());
            }
        }
    }
}