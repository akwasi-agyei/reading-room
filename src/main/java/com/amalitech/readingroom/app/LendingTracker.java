package com.amalitech.readingroom.app;

import com.amalitech.readingroom.domain.LendingItem;
import com.amalitech.readingroom.domain.Library;
import com.amalitech.readingroom.domain.Member;

public class LendingTracker {

    public static void main(String[] args) {
        Library lib = new Library("The Reading Room");

        LendingItem novel = new LendingItem("Things Fall Apart", "BOOK");
        LendingItem audioCourse = new LendingItem("French Audio Course", "AUDIO_KIT");
        lib.addItem(novel);
        lib.addItem(audioCourse);

        Member kofi = new Member("Kofi Mensah", "STANDARD");
        Member ama = new Member("Ama Owusu", "STUDENT");
        lib.addMember(kofi);
        lib.addMember(ama);

        lib.borrow(kofi, novel);
        lib.borrow(ama, audioCourse);
        lib.returnItem(audioCourse);

        lib.borrow(ama, novel);

        lib.printReport();
    }
}
