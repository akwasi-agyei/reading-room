package com.amalitech.readingroom.app;

import com.amalitech.readingroom.domain.AudioKit;
import com.amalitech.readingroom.domain.Book;
import com.amalitech.readingroom.domain.Library;
import com.amalitech.readingroom.domain.Loan;
import com.amalitech.readingroom.reporting.LibraryReport;
import com.amalitech.readingroom.domain.MemberKind;
import com.amalitech.readingroom.domain.LendingItem;
import com.amalitech.readingroom.domain.Member;


public class  LendingTracker {

    public static void main(String[] args) {
        Library lib = new Library("The Reading Room");

        LendingItem novel = new Book("Things Fall Apart");
        LendingItem audioCourse = new AudioKit("French Audio Course");
        lib.addItem(novel);
        lib.addItem(audioCourse);

        Member kofi = new Member("Kofi Mensah", MemberKind.STANDARD);
        Member ama = new Member("Ama Owusu", MemberKind.STUDENT);
        lib.addMember(kofi);
        lib.addMember(ama);

        printBorrowMessage(lib.borrow(kofi, novel));
        printBorrowMessage(lib.borrow(ama, audioCourse));
        lib.returnItem(audioCourse);
        System.out.println("Returned \"" + audioCourse.getTitle() + "\"");

        try {
            printBorrowMessage(lib.borrow(ama, novel));
        } catch (IllegalStateException exception) {
            System.out.println(exception.getMessage());
        }

        new LibraryReport().printReport(lib);
    }

    private static void printBorrowMessage(Loan loan) {
        System.out.println("Lent \"" + loan.getItemTitle() + "\" to " + loan.getMemberName()
                + " for " + loan.getLoanDays() + " days");
    }
}