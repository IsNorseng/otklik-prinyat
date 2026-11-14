package ru.jobquest.meme;

public final class Run {
    public enum Screen { RESUME, DECK, RECRUITER, LINK, END }

    private final int cardCount;
    private final int[] recruiterQuestionCounts;
    private Screen screen;
    private int card, recruiter, question, applications, passes;
    private String name, title, skills;

    public Run(int cardCount, int... recruiterQuestionCounts) {
        if (recruiterQuestionCounts.length == 0 || cardCount < recruiterQuestionCounts.length) {
            throw new IllegalArgumentException("Each recruiter needs at least one preceding card");
        }
        for (int count : recruiterQuestionCounts) {
            if (count < 1) throw new IllegalArgumentException("Each recruiter needs a question");
        }
        this.cardCount = cardCount;
        this.recruiterQuestionCounts = recruiterQuestionCounts.clone();
        reset();
    }

    public Screen screen() { return screen; }
    public int card() { return card; }
    public int recruiter() { return recruiter; }
    public int question() { return question; }
    public int applications() { return applications; }
    public int passes() { return passes; }
    public String name() { return name; }
    public String title() { return title; }
    public String skills() { return skills; }

    public boolean start(String name, String title, String skills) {
        if (screen != Screen.RESUME || blank(name) || blank(title) || blank(skills)) return false;
        this.name = name.trim();
        this.title = title.trim();
        this.skills = skills.trim();
        screen = Screen.DECK;
        return true;
    }

    public boolean swipe(boolean apply) {
        if (screen != Screen.DECK) return false;
        if (apply) applications++;
        else passes++;
        card++;
        int nextContact = recruiter + 1;
        if (card == (long) cardCount * (nextContact + 1) / recruiterQuestionCounts.length) {
            recruiter = nextContact;
            question = 0;
            screen = Screen.RECRUITER;
        }
        return true;
    }

    public int questionCount() {
        return recruiter < 0 ? 0 : recruiterQuestionCounts[recruiter];
    }

    public boolean awaitingReply() {
        return screen == Screen.RECRUITER && question == questionCount();
    }

    public boolean answer(String text) {
        if (screen != Screen.RECRUITER || awaitingReply() || blank(text)) return false;
        question++;
        if (question == questionCount() && recruiter == recruiterQuestionCounts.length - 1) {
            screen = Screen.LINK;
        }
        return true;
    }

    public boolean closeRecruiter() {
        if (!awaitingReply()) return false;
        screen = Screen.DECK;
        return true;
    }

    public boolean openLink() {
        if (screen != Screen.LINK) return false;
        screen = Screen.END;
        return true;
    }

    public void reset() {
        screen = Screen.RESUME;
        card = 0;
        recruiter = -1;
        question = -1;
        applications = 0;
        passes = 0;
        name = "";
        title = "";
        skills = "";
    }

    private static boolean blank(String text) {
        if (text == null) return true;
        for (int offset = 0; offset < text.length();) {
            int character = text.codePointAt(offset);
            if (!Character.isWhitespace(character) && !Character.isSpaceChar(character)) return false;
            offset += Character.charCount(character);
        }
        return true;
    }
}
