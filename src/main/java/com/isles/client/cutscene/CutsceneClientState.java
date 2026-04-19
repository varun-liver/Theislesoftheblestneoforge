package com.isles.client.cutscene;

public final class CutsceneClientState {
    private CutsceneClientState() {}

    private static boolean active = false;
    private static boolean ending = false;
    private static int totalTicks = 0;
    private static int elapsedTicks = 0;

    private static int card1Ticks = 0;
    private static int gap1Ticks = 0;
    private static int card2Ticks = 0;
    private static int gap2Ticks = 0;
    private static int card3Ticks = 0;
    private static int gap3Ticks = 0;
    private static int card4Ticks = 0;
    private static int gap4Ticks = 0;
    private static int scrollTicks = 0;

    private static String card1Title = "";
    private static String card1Subtitle = "";
    private static String card2Title = "";
    private static String card2Subtitle = "";
    private static String card3Title = "";
    private static String card3Subtitle = "";
    private static String card4Title = "";
    private static String card4Subtitle = "";
    private static String scrollText = "";

    public static void start(
        int totalTicks,
        int card1Ticks,
        int gap1Ticks,
        int card2Ticks,
        int gap2Ticks,
        int card3Ticks,
        int gap3Ticks,
        int card4Ticks,
        int gap4Ticks,
        int scrollTicks,
        String card1Title,
        String card1Subtitle,
        String card2Title,
        String card2Subtitle,
        String card3Title,
        String card3Subtitle,
        String card4Title,
        String card4Subtitle,
        String scrollText
    ) {
        CutsceneClientState.active = true;
        CutsceneClientState.ending = false;
        CutsceneClientState.totalTicks = Math.max(1, totalTicks);
        CutsceneClientState.elapsedTicks = 0;
        CutsceneClientState.card1Ticks = Math.max(0, card1Ticks);
        CutsceneClientState.gap1Ticks = Math.max(0, gap1Ticks);
        CutsceneClientState.card2Ticks = Math.max(0, card2Ticks);
        CutsceneClientState.gap2Ticks = Math.max(0, gap2Ticks);
        CutsceneClientState.card3Ticks = Math.max(0, card3Ticks);
        CutsceneClientState.gap3Ticks = Math.max(0, gap3Ticks);
        CutsceneClientState.card4Ticks = Math.max(0, card4Ticks);
        CutsceneClientState.gap4Ticks = Math.max(0, gap4Ticks);
        CutsceneClientState.scrollTicks = Math.max(0, scrollTicks);
        CutsceneClientState.card1Title = card1Title == null ? "" : card1Title;
        CutsceneClientState.card1Subtitle = card1Subtitle == null ? "" : card1Subtitle;
        CutsceneClientState.card2Title = card2Title == null ? "" : card2Title;
        CutsceneClientState.card2Subtitle = card2Subtitle == null ? "" : card2Subtitle;
        CutsceneClientState.card3Title = card3Title == null ? "" : card3Title;
        CutsceneClientState.card3Subtitle = card3Subtitle == null ? "" : card3Subtitle;
        CutsceneClientState.card4Title = card4Title == null ? "" : card4Title;
        CutsceneClientState.card4Subtitle = card4Subtitle == null ? "" : card4Subtitle;
        CutsceneClientState.scrollText = scrollText == null ? "" : scrollText;
    }

    public static void end() {
        CutsceneClientState.active = false;
        CutsceneClientState.ending = false;
        CutsceneClientState.totalTicks = 0;
        CutsceneClientState.elapsedTicks = 0;
        CutsceneClientState.card1Ticks = 0;
        CutsceneClientState.gap1Ticks = 0;
        CutsceneClientState.card2Ticks = 0;
        CutsceneClientState.gap2Ticks = 0;
        CutsceneClientState.card3Ticks = 0;
        CutsceneClientState.gap3Ticks = 0;
        CutsceneClientState.card4Ticks = 0;
        CutsceneClientState.gap4Ticks = 0;
        CutsceneClientState.scrollTicks = 0;
        CutsceneClientState.card1Title = "";
        CutsceneClientState.card1Subtitle = "";
        CutsceneClientState.card2Title = "";
        CutsceneClientState.card2Subtitle = "";
        CutsceneClientState.card3Title = "";
        CutsceneClientState.card3Subtitle = "";
        CutsceneClientState.card4Title = "";
        CutsceneClientState.card4Subtitle = "";
        CutsceneClientState.scrollText = "";
    }

    public static boolean isActive() {
        return active;
    }

    public static boolean isEnding() {
        return ending;
    }

    public static int getTotalTicks() {
        return totalTicks;
    }

    public static int getElapsedTicks() {
        return elapsedTicks;
    }

    public static int getCard1Ticks() {
        return card1Ticks;
    }

    public static int getGap1Ticks() {
        return gap1Ticks;
    }

    public static int getCard2Ticks() {
        return card2Ticks;
    }

    public static int getGap2Ticks() {
        return gap2Ticks;
    }

    public static int getCard3Ticks() {
        return card3Ticks;
    }

    public static int getGap3Ticks() {
        return gap3Ticks;
    }

    public static int getCard4Ticks() {
        return card4Ticks;
    }

    public static int getGap4Ticks() {
        return gap4Ticks;
    }

    public static int getScrollTicks() {
        return scrollTicks;
    }

    public static String getCard1Title() {
        return card1Title;
    }

    public static String getCard1Subtitle() {
        return card1Subtitle;
    }

    public static String getCard2Title() {
        return card2Title;
    }

    public static String getCard2Subtitle() {
        return card2Subtitle;
    }

    public static String getCard3Title() {
        return card3Title;
    }

    public static String getCard3Subtitle() {
        return card3Subtitle;
    }

    public static String getCard4Title() {
        return card4Title;
    }

    public static String getCard4Subtitle() {
        return card4Subtitle;
    }

    public static String getScrollText() {
        return scrollText;
    }

    /**
     * Begin a fade-out-to-clear and end the overlay when complete.
     * The overlay will stop drawing text during this phase.
     */
    public static void beginEnd(int fadeOutTicks) {
        if (!active) return;
        if (ending) return;
        ending = true;
        int extra = Math.max(1, fadeOutTicks);
        totalTicks = Math.max(elapsedTicks + 1, elapsedTicks + extra);
    }

    public static void clientTick() {
        if (!active) return;
        elapsedTicks++;
        if (elapsedTicks >= totalTicks) {
            end();
        }
    }
}
