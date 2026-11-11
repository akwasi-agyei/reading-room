package com.amalitech.readingroom.reporting;

import com.amalitech.readingroom.domain.Library;
import com.amalitech.readingroom.domain.LendingItem;
import com.amalitech.readingroom.domain.Loan;

public class LibraryReport {
    public void printReport(Library library) {
        System.out.println();
        System.out.println("=== " + library.getName() + " - end of day ===");
        System.out.println("Items:");
        for (LendingItem i : library.getItems()) {
            String borrower = library.getCurrentBorrower(i);
            System.out.println("  \"" + i.getTitle() + "\" (" + i.getType() + "): " + (library.isAvailable(i) ? "AVAILABLE" : "ON_LOAN")
                    + (borrower != null ? " - with " + borrower : ""));
        }
        System.out.println("Active loans:");
        for (Loan l : library.getLoans()) {
            if (l.getStatus().equals("ACTIVE")) {
                System.out.println("  Loan #" + l.getId() + ": \"" + l.getItemTitle() + "\" -> " + l.getMemberName());
            }
        }
    }
}
