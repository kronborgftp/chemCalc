package gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Base class for every calculator panel.
 * Provides: header, scrollable input card, dark terminal output area.
 */
public abstract class BaseCalcPanel extends JPanel {

    protected JTextArea outputArea;
    protected JPanel    inputPanel;

    // ── Shared palette ────────────────────────────────────────────────────────
    protected static final Color BG         = new Color(245, 246, 250);
    protected static final Color CARD_BG    = Color.WHITE;
    protected static final Color ACCENT     = new Color(37, 99, 235);
    protected static final Color OUTPUT_BG  = new Color(18, 22, 30);
    protected static final Color OUTPUT_FG  = new Color(100, 210, 130);
    protected static final Color HINT_FG    = new Color(130, 130, 140);
    protected static final Color HEADER_FG  = new Color(20, 25, 40);

    protected static final Font TITLE_FONT  = new Font("SansSerif", Font.BOLD, 20);
    protected static final Font LABEL_FONT  = new Font("SansSerif", Font.PLAIN, 14);
    protected static final Font MONO_FONT   = new Font("Monospaced", Font.PLAIN, 13);
    protected static final Font HINT_FONT   = new Font("SansSerif", Font.ITALIC, 12);
    protected static final Font TAB_FONT    = new Font("SansSerif", Font.PLAIN, 13);

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

        // ── Input card (top split component) ─────────────────────────────────
        inputPanel = new JPanel();
        inputPanel.setBackground(CARD_BG);
        inputPanel.setBorder(card());

        JScrollPane inputScroll = new JScrollPane(inputPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        inputScroll.setBorder(null);
        inputScroll.getViewport().setBackground(CARD_BG);

        // ── Output panel (bottom) ─────────────────────────────────────────────
        outputArea = new JTextArea(9, 50);
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

        JPanel outWrapper = new JPanel(new BorderLayout(0, 2));
        outWrapper.setBackground(BG);
        outWrapper.add(outLbl, BorderLayout.NORTH);
        outWrapper.add(outScroll, BorderLayout.CENTER);

        // ── Split pane ────────────────────────────────────────────────────────
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inputScroll, outWrapper);
        split.setResizeWeight(0.58);
        split.setBorder(null);
        split.setBackground(BG);
        split.setDividerSize(6);
        add(split, BorderLayout.CENTER);

        buildUI();
    }

    protected abstract void buildUI();

    // ── Factory helpers ───────────────────────────────────────────────────────

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
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(29, 78, 216));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(ACCENT);
            }
        });
        return btn;
    }

    protected JTextField field() {
        JTextField tf = new JTextField(18);
        tf.setFont(LABEL_FONT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 202, 215), 1, true),
                BorderFactory.createEmptyBorder(3, 6, 3, 6)));
        return tf;
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

    /** Adds a label+field row in a GridBagLayout panel. Returns the text field. */
    protected JTextField addRow(JPanel p, GridBagConstraints g, int row, String labelText) {
        g.gridx = 0; g.gridy = row; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        p.add(lbl(labelText), g);
        JTextField tf = field();
        g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        p.add(tf, g);
        g.weightx = 0; g.fill = GridBagConstraints.NONE; // reset
        return tf;
    }

    /** Adds a centred calculate button spanning two columns. */
    protected void addCalcRow(JPanel p, GridBagConstraints g, int row, JButton btn) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 2;
        g.anchor = GridBagConstraints.CENTER; g.fill = GridBagConstraints.NONE;
        p.add(btn, g);
        g.gridwidth = 1; g.anchor = GridBagConstraints.WEST;
    }

    /** Standard GridBagConstraints with comfortable insets. */
    protected GridBagConstraints gbc() {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.anchor = GridBagConstraints.WEST;
        return g;
    }

    /** Creates a standard white tab panel with GridBagLayout. */
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

    protected void output(String text) {
        outputArea.setText(text.strip());
        outputArea.setCaretPosition(0);
    }

    protected double parse(JTextField f) {
        return calculators.PHCalculator.parseExpression(f.getText().trim());
    }
}
