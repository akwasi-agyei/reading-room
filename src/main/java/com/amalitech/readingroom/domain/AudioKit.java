package com.amalitech.readingroom.domain;

public class AudioKit extends LendingItem {
    public AudioKit(String title) {
        super(title);
    }

    @Override
    public String getType() {
        return "AUDIO_KIT";
    }

    @Override
    public int loanDaysFor(MemberKind kind) {
        if (kind == MemberKind.STUDENT) {
            return 14;
        }
        return 7;
    }
}