package gui;

import java.awt.*;

/**
 * Single source of truth for the application's visual palette and typography.
 *
 * Palette: warm parchment / Obsidian-notes aesthetic — no blue, all warm tones.
 * Changing one value here propagates everywhere automatically.
 */
public final class Theme {

    private Theme() {}

    // ── Background colours ────────────────────────────────────────────────────
    public static final Color BG         = new Color(247, 243, 232); // warm parchment
    public static final Color CARD_BG    = new Color(255, 252, 244); // warm off-white card
    public static final Color HOVER_BG   = new Color(236, 229, 210); // warm tan hover

    // ── Foreground / text colours ─────────────────────────────────────────────
    public static final Color HEADING    = new Color(44,  36,  24);  // warm near-black brown
    public static final Color DESC_FG    = new Color(95,  83,  66);  // warm medium brown
    public static final Color HINT_FG    = new Color(148, 133, 112); // warm muted tan
    public static final Color DIVIDER    = new Color(212, 204, 186); // warm tan divider

    // ── Accent — olive green, matching Obsidian note links ────────────────────
    public static final Color ACCENT      = new Color(88,  116, 60);  // olive green
    public static final Color ACCENT_DARK = new Color(64,  86,  42);  // darker olive

    // ── Terminal output colours ───────────────────────────────────────────────
    public static final Color OUTPUT_BG  = new Color(26,  20,  12);  // warm espresso dark
    public static final Color OUTPUT_FG  = new Color(108, 200, 90);  // warm olive-green text

    // ── Sidebar colours (used by ChemApp) ─────────────────────────────────────
    public static final Color SIDEBAR_BG     = new Color(36,  28,  18);  // warm espresso
    public static final Color SIDEBAR_BTN    = new Color(50,  40,  26);  // slightly lighter
    public static final Color SIDEBAR_TEXT   = new Color(210, 200, 182); // warm cream text
    public static final Color SIDEBAR_MUTED  = new Color(148, 136, 116); // muted sidebar text

    // ── Success / answer colours ──────────────────────────────────────────────
    public static final Color ANS_BG     = new Color(232, 248, 222); // warm light green
    public static final Color ANS_FG     = new Color(50,  88,  36);  // warm forest green
    public static final Color ANS_BORDER = new Color(130, 190, 90);  // warm olive border

    // ── Facts-panel card colours ──────────────────────────────────────────────
    public static final Color NOTE_BG    = new Color(250, 246, 234); // warm paper
    public static final Color EXAM_BG    = new Color(255, 250, 224); // warm amber

    // ── Typography ────────────────────────────────────────────────────────────
    public static final Font TITLE_FONT  = new Font("SansSerif",  Font.BOLD,  20);
    public static final Font LABEL_FONT  = new Font("SansSerif",  Font.PLAIN, 14);
    public static final Font MONO_FONT   = new Font("Monospaced", Font.PLAIN, 13);
    public static final Font HINT_FONT   = new Font("SansSerif",  Font.ITALIC, 12);
    public static final Font TAB_FONT    = new Font("SansSerif",  Font.PLAIN, 13);
    public static final Font SMALL_FONT  = new Font("SansSerif",  Font.PLAIN, 12);
    public static final Font BOLD_SMALL  = new Font("SansSerif",  Font.BOLD,  12);
}
