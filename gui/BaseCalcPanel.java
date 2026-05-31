package gui;

import chemistry.ChemUtils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Base class for every calculator panel.
 * Provides: title header, split-pane layout (input top / terminal output bottom),
 * and a library of factory helpers shared by all subclasses.
 *
 * All visual constants are pulled from {@link Theme} so the palette is defined
 * in exactly one place.
 */
public abstract class BaseCalcPanel extends JPanel {

    protected JTextArea   outputArea;
    protected JPanel      inputPanel;
    protected JTabbedPane mainTabs;

    // ── Palette & typography (delegates to Theme) ─────────────────────────────
    protected static final Color BG        = Theme.BG;
    protected static final Color CARD_BG   = Theme.CARD_BG;
    protected static final Color ACCENT    = Theme.ACCENT;
    protected static final Color OUTPUT_BG = Theme.OUTPUT_BG;
    protected static final Color OUTPUT_FG = Theme.OUTPUT_FG;
    protected static final Color HINT_FG   = Theme.HINT_FG;
    protected static final Color HEADER_FG = Theme.HEADING;

    protected static final Font TITLE_FONT = Theme.TITLE_FONT;
    protected static final Font LABEL_FONT = Theme.LABEL_FONT;
    protected static final Font MONO_FONT  = Theme.MONO_FONT;
    protected static final Font HINT_FONT  = Theme.HINT_FONT;
    protected static final Font TAB_FONT   = Theme.TAB_FONT;

    public BaseCalcPanel(String title) {
        setLayout(new BorderLayout(0, 10));
        setBackground(BG);
        setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));

        // ── Header ────────────────────────────────────────────────────────────
        JLabel header = new JLabel(title);
        header.setFont(TITLE_FONT);
        header.setForeground(HEADER_FG);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        add(header, BorderLayout.NORTH);

        // ── Input area — no outer scroll pane so each tab owns its own scrolling
        inputPanel = new JPanel();
        inputPanel.setBackground(CARD_BG);
        inputPanel.setBorder(card());
        inputPanel.setMinimumSize(new Dimension(0, 180));

        // ── Output area ───────────────────────────────────────────────────────
        outputArea = new JTextArea(8, 50);
        outputArea.setEditable(false);
        outputArea.setFont(MONO_FONT);
        outputArea.setBackground(OUTPUT_BG);
        outputArea.setForeground(OUTPUT_FG);
        outputArea.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);

        JLabel outLbl = new JLabel("  Output");
        outLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        outLbl.setForeground(HINT_FG);
        outLbl.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        JScrollPane outScroll = new JScrollPane(outputArea);
        outScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 202, 210), 1, true));
        outScroll.getVerticalScrollBar().setUnitIncrement(18);

        JPanel outWrapper = new JPanel(new BorderLayout(0, 2));
        outWrapper.setBackground(BG);
        outWrapper.setMinimumSize(new Dimension(0, 80));
        outWrapper.add(outLbl,    BorderLayout.NORTH);
        outWrapper.add(outScroll, BorderLayout.CENTER);

        // ── Split pane ────────────────────────────────────────────────────────
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inputPanel, outWrapper);
        split.setResizeWeight(0.65);
        split.setContinuousLayout(true);
        split.setBorder(null);
        split.setBackground(BG);
        split.setDividerSize(5);
        add(split, BorderLayout.CENTER);

        buildUI();
    }

    protected abstract void buildUI();

    // ── Component factories ───────────────────────────────────────────────────

    protected JButton calcButton(String label) {
        JButton btn = new JButton(label);
        btn.setBackground(ACCENT);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 34));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(Theme.ACCENT_DARK); }
            public void mouseExited (java.awt.event.MouseEvent e) { btn.setBackground(ACCENT); }
        });
        return btn;
    }

    protected JTextField field() {
        JTextField tf = new JTextField(18);
        tf.setFont(LABEL_FONT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 202, 215), 1, true),
                BorderFactory.createEmptyBorder(3, 6, 3, 6)));
        attachPasteMenu(tf);
        return tf;
    }

    protected static void attachPasteMenu(javax.swing.text.JTextComponent tc) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem paste = new JMenuItem("Paste");
        paste.addActionListener(e -> {
            try {
                java.awt.datatransfer.Clipboard cb =
                        java.awt.Toolkit.getDefaultToolkit().getSystemClipboard();
                String text = (String) cb.getData(java.awt.datatransfer.DataFlavor.stringFlavor);
                if (text != null) tc.replaceSelection(text.trim());
            } catch (Exception ignored) {}
        });
        JMenuItem clear = new JMenuItem("Clear");
        clear.addActionListener(e -> tc.setText(""));
        menu.add(paste);
        menu.add(clear);
        tc.setComponentPopupMenu(menu);
    }

    protected JTextField monoField() {
        JTextField tf = field();
        tf.setFont(MONO_FONT);
        tf.setPreferredSize(new Dimension(320, 28));
        return tf;
    }

    protected JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(LABEL_FONT);
        return l;
    }

    protected JLabel hint(String html) {
        JLabel l = new JLabel("<html>" + html + "</html>");
        l.setFont(HINT_FONT);
        l.setForeground(HINT_FG);
        return l;
    }

    protected JTextField addRow(JPanel p, GridBagConstraints g, int row, String labelText) {
        g.gridx = 0; g.gridy = row; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        p.add(lbl(labelText), g);
        JTextField tf = field();
        g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        p.add(tf, g);
        g.weightx = 0; g.fill = GridBagConstraints.NONE;
        return tf;
    }

    protected void addCalcRow(JPanel p, GridBagConstraints g, int row, JButton btn) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 2;
        g.anchor = GridBagConstraints.CENTER; g.fill = GridBagConstraints.NONE;
        p.add(btn, g);
        g.gridwidth = 1; g.anchor = GridBagConstraints.WEST;
    }

    /**
     * Adds a labelled calculate button at the given grid row.
     * Catches {@link NumberFormatException} from {@code action} and writes a
     * friendly error to the output area — so individual panels never need to
     * repeat that try/catch boilerplate.
     */
    protected void calcBtn(JPanel p, GridBagConstraints g, int row, String label, Runnable action) {
        JButton btn = calcButton(label);
        btn.addActionListener(e -> {
            try { action.run(); }
            catch (NumberFormatException ex) { output("Error: enter valid numbers."); }
        });
        addCalcRow(p, g, row, btn);
    }

    protected GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.anchor = GridBagConstraints.WEST;
        return g;
    }

    protected JPanel tabPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        return p;
    }

    protected Border card() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(215, 218, 228), 1, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16));
    }

    public void goToTab(int index) {
        if (mainTabs != null && index >= 0 && index < mainTabs.getTabCount())
            mainTabs.setSelectedIndex(index);
    }

    protected void output(String text) {
        outputArea.setText(text.strip());
        outputArea.setCaretPosition(0);
    }

    /** Parses a field value, accepting scientific notation and 10^x shorthand. */
    protected double parse(JTextField f) {
        return ChemUtils.parseExpression(f.getText().trim());
    }
}
