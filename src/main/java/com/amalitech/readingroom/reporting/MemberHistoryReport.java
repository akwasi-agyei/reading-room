package com.amalitech.readingroom.reporting;

import com.amalitech.readingroom.domain.Member;
import java.util.List;

public class MemberHistoryReport {
    public void printReport(Member member) {
        System.out.println();
        System.out.println("=== Borrowing history: " + member.getName() + " ===");
        List<String> history = member.getHistory();
        if (history.isEmpty()) {
            System.out.println("No borrowing history.");
            return;
        }
        for (String entry : history) {
            System.out.println(entry);
        }
    }
}
