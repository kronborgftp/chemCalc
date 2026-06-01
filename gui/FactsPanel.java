package gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Searchable chemistry facts panel.
 *
 * All fact/note/exam data comes from {@link LectureData}; this class is purely
 * a view. Colours are sourced from {@link Theme} so only one place needs to
 * change if the palette changes.
 */
public class FactsPanel extends JPanel {

    // Panel-specific palette — shared values come from Theme
    private static final Color BG      = Theme.BG;
    private static final Color CARD_BG = Theme.CARD_BG;
    private static final Color HEADING = Theme.HEADING;
    private static final Color ACCENT  = Theme.ACCENT;
    private static final Color DIVIDER = Theme.DIVIDER;
    private static final Color ANS_BG  = Theme.ANS_BG;
    private static final Color ANS_FG  = Theme.ANS_FG;
    private static final Color NOTE_BG = Theme.NOTE_BG;
    private static final Color EXAM_BG = Theme.EXAM_BG;

    private final BiConsumer<String, Integer> nav;
    private JPanel     resultsPanel;
    private JTextField searchField;
    private JPanel     filterPanel;
    private JPanel     topBar;          // field so the resize listener can reach it
    private String     activeFilter = "All";

    private final CardLayout centerCards = new CardLayout();
    private final JPanel     centerPanel = new JPanel(centerCards);
    private final CardLayout controlCards = new CardLayout();
    private final JPanel     controlPanel = new JPanel(controlCards);

    public FactsPanel(BiConsumer<String, Integer> nav) {
        this.nav = nav;
        setLayout(new BorderLayout());
        setBackground(BG);

        // ── Hero ──────────────────────────────────────────────────────────────
        JPanel hero = new JPanel();
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBackground(HEADING);
        hero.setBorder(BorderFactory.createEmptyBorder(18, 32, 16, 32));

        JLabel heroTitle = new JLabel("Chemistry Facts");
        heroTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        heroTitle.setForeground(Color.WHITE);
        heroTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heroSub = new JLabel("Searchable answers for conceptual exam questions. Click any card to read the full lecture notes.");
        heroSub.setFont(Theme.SMALL_FONT);
        heroSub.setForeground(new Color(160, 180, 230));
        heroSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        hero.add(heroTitle);
        hero.add(Box.createVerticalStrut(5));
        hero.add(heroSub);

        // ── Control bar ───────────────────────────────────────────────────────
        controlPanel.setBackground(BG);

        JPanel listControls = new JPanel();
        listControls.setLayout(new BoxLayout(listControls, BoxLayout.Y_AXIS));
        listControls.setBackground(BG);

        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setBackground(BG);
        searchRow.setBorder(BorderFactory.createEmptyBorder(12, 24, 6, 24));
        searchRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        searchField = new JTextField();
        searchField.setFont(Theme.SMALL_FONT);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 205, 220), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(Theme.SMALL_FONT);
        clearBtn.setBackground(new Color(240, 241, 245));
        clearBtn.setForeground(new Color(80, 85, 100));
        clearBtn.setBorderPainted(false);
        clearBtn.setFocusPainted(false);
        clearBtn.addActionListener(e -> {
            searchField.setText(""); activeFilter = "All";
            recolorFilters("All"); refresh("");
        });

        searchRow.add(new JLabel("Search: "), BorderLayout.WEST);
        searchRow.add(searchField,             BorderLayout.CENTER);
        searchRow.add(clearBtn,                BorderLayout.EAST);
        listControls.add(searchRow);

        filterPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 8, 4));
        filterPanel.setBackground(BG);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 8, 16));
        // When the window is resized and buttons wrap onto a new row, the top
        // bar must revalidate so its preferred height grows to fit all rows.
        filterPanel.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentResized(java.awt.event.ComponentEvent e) {
                if (topBar != null) topBar.revalidate();
            }
        });

        List<String> topics = new ArrayList<>();
        topics.add("All");
        for (LectureData.Fact f : LectureData.FACTS)
            if (!topics.contains(f.topic())) topics.add(f.topic());
        for (String topic : topics) {
            JButton btn = filterBtn(topic, topic.equals("All"));
            btn.addActionListener(e -> {
                activeFilter = topic; recolorFilters(topic); refresh(searchField.getText());
            });
            filterPanel.add(btn);
        }
        listControls.add(filterPanel);

        JPanel detailNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        detailNav.setBackground(BG);
        detailNav.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));

        JButton backBtn = new JButton("← Back to Facts");
        backBtn.setFont(Theme.BOLD_SMALL);
        backBtn.setBackground(new Color(235, 237, 245));
        backBtn.setForeground(HEADING);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> showList());
        detailNav.add(backBtn);

        controlPanel.add(listControls, "list");
        controlPanel.add(detailNav,    "detail");
        controlCards.show(controlPanel, "list");

        topBar = new JPanel(new BorderLayout());
        topBar.add(hero,         BorderLayout.NORTH);
        topBar.add(controlPanel, BorderLayout.CENTER);
        add(topBar, BorderLayout.NORTH);

        // ── Center ────────────────────────────────────────────────────────────
        centerPanel.setBackground(BG);
        resultsPanel = new JPanel(new GridBagLayout());
        resultsPanel.setBackground(BG);

        JScrollPane listScroll = new JScrollPane(resultsPanel,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        listScroll.setBorder(null);
        listScroll.getViewport().setBackground(BG);
        listScroll.getVerticalScrollBar().setUnitIncrement(20);

        centerPanel.add(listScroll,   "list");
        centerPanel.add(new JPanel(), "detail");
        centerCards.show(centerPanel, "list");
        add(centerPanel, BorderLayout.CENTER);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { refresh(searchField.getText()); }
            public void removeUpdate(DocumentEvent e)  { refresh(searchField.getText()); }
            public void changedUpdate(DocumentEvent e) {}
        });
        refresh("");
    }

    // ── Public navigation entry (called from SuggestPanel) ────────────────────

    public void showFact(LectureData.Fact fact) { showDetail(fact); }

    // ── List view ─────────────────────────────────────────────────────────────

    private void showList() {
        controlCards.show(controlPanel, "list");
        centerCards.show(centerPanel,  "list");
    }

    private void showDetail(LectureData.Fact fact) {
        JPanel detail = buildDetailPanel(fact);
        centerPanel.remove(centerPanel.getComponent(1));
        centerPanel.add(detail, "detail", 1);
        controlCards.show(controlPanel, "detail");
        centerCards.show(centerPanel,   "detail");
    }

    private void recolorFilters(String active) {
        for (Component c : filterPanel.getComponents()) {
            if (c instanceof JButton btn) {
                boolean sel = btn.getText().equals(active);
                btn.setBackground(sel ? ACCENT : new Color(235, 237, 245));
                btn.setForeground(sel ? Color.WHITE : HEADING);
            }
        }
    }

    private JButton filterBtn(String label, boolean active) {
        JButton btn = new JButton(label);
        btn.setFont(Theme.BOLD_SMALL);
        btn.setBackground(active ? ACCENT : new Color(235, 237, 245));
        btn.setForeground(active ? Color.WHITE : HEADING);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(4, 12, 4, 12));
        return btn;
    }

    private void refresh(String query) {
        resultsPanel.removeAll();
        String q = query.toLowerCase().trim();

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1.0; g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST;
        g.insets = new Insets(0, 20, 12, 20);

        int row = 0, shown = 0;
        for (LectureData.Fact fact : LectureData.FACTS) {
            if (!activeFilter.equals("All") && !fact.topic().equals(activeFilter)) continue;
            if (!q.isEmpty()) {
                String hay = (fact.topic() + " " + fact.title() + " " + fact.answer()
                              + " " + fact.explanation() + " " + fact.keywords()).toLowerCase();
                boolean match = false;
                for (String w : q.split("\\s+")) if (hay.contains(w)) { match = true; break; }
                if (!match) continue;
            }
            g.gridy = row++;
            resultsPanel.add(factCard(fact), g);
            shown++;
        }

        if (shown == 0) {
            g.gridy = 0;
            JLabel none = new JLabel("No facts match. Try different keywords.");
            none.setFont(new Font("SansSerif", Font.ITALIC, 13));
            none.setForeground(new Color(130, 135, 150));
            none.setBorder(BorderFactory.createEmptyBorder(20, 4, 10, 4));
            resultsPanel.add(none, g);
            row = 1;
        }

        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0; filler.gridy = row; filler.weightx = 1; filler.weighty = 1;
        filler.fill = GridBagConstraints.BOTH;
        JPanel pad = new JPanel(); pad.setOpaque(false);
        resultsPanel.add(pad, filler);
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private JPanel factCard(LectureData.Fact fact) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(fact.hasDetail() ? new Color(196, 210, 250) : DIVIDER, 1, true),
            BorderFactory.createEmptyBorder(14, 18, 10, 18)));
        card.setCursor(fact.hasDetail()
                ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                : Cursor.getDefaultCursor());

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST;

        // Topic chip
        g.gridy = 0; g.insets = new Insets(0, 0, 8, 0);
        JPanel chipRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        chipRow.setBackground(CARD_BG);
        JLabel chip = new JLabel("  " + fact.topic() + "  ");
        chip.setFont(new Font("SansSerif", Font.BOLD, 10));
        chip.setForeground(ACCENT);
        chip.setBackground(new Color(219, 234, 254));
        chip.setOpaque(true);
        chip.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        chipRow.add(chip);
        card.add(chipRow, g);

        g.gridy = 1; g.insets = new Insets(0, 0, 8, 0);
        JLabel titleLbl = new JLabel("<html>" + esc(fact.title()) + "</html>");
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLbl.setForeground(HEADING);
        card.add(titleLbl, g);

        g.gridy = 2; g.insets = new Insets(0, 0, 8, 0);
        JPanel ansBox = answerBox(fact.answer(), 13);
        card.add(ansBox, g);

        g.gridy = 3; g.insets = new Insets(0, 0, fact.hasDetail() ? 8 : 0, 0);
        JLabel explLbl = new JLabel("<html><font color='#4b5563'>" + esc(fact.explanation()) + "</font></html>");
        explLbl.setFont(Theme.SMALL_FONT);
        card.add(explLbl, g);

        if (fact.hasDetail()) {
            g.gridy = 4; g.insets = new Insets(0, 0, 0, 0);
            JLabel hint = new JLabel("<html><font color='#2563eb'>📖 Click to read lecture notes &amp; past exam questions →</font></html>");
            hint.setFont(new Font("SansSerif", Font.PLAIN, 11));
            card.add(hint, g);

            card.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) { showDetail(fact); }
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    card.setBackground(Theme.HOVER_BG);
                    chipRow.setBackground(Theme.HOVER_BG);
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    card.setBackground(CARD_BG);
                    chipRow.setBackground(CARD_BG);
                }
            });
        }
        return card;
    }

    // ── Detail view ───────────────────────────────────────────────────────────

    private JPanel buildDetailPanel(LectureData.Fact fact) {
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(BG);
        content.setBorder(BorderFactory.createEmptyBorder(20, 28, 28, 28));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST;
        int row = 0;

        g.gridy = row++; g.insets = new Insets(0, 0, 20, 0);
        content.add(staticFactCard(fact), g);

        List<LectureData.Note>     notes     = LectureData.findNotes(fact.lectureKeys());
        List<LectureData.ExamQ>    examQs    = LectureData.findExamQuestions(fact.lectureKeys());
        List<LectureData.CalcLink> calcLinks = LectureData.findCalcLinks(fact.lectureKeys());

        if (!notes.isEmpty()) {
            g.gridy = row++; g.insets = new Insets(0, 0, 10, 0);
            content.add(sectionHeader("Lecture Notes", new Color(239, 246, 255), ACCENT), g);
            for (LectureData.Note note : notes) {
                g.gridy = row++; g.insets = new Insets(0, 0, 12, 0);
                content.add(noteCard(note), g);
            }
        }

        if (!examQs.isEmpty()) {
            g.gridy = row++; g.insets = new Insets(8, 0, 10, 0);
            content.add(sectionHeader("Past Exam Questions", new Color(255, 252, 235), new Color(146, 64, 14)), g);
            for (LectureData.ExamQ q : examQs) {
                g.gridy = row++; g.insets = new Insets(0, 0, 10, 0);
                content.add(examCard(q), g);
            }
        }

        if (!calcLinks.isEmpty()) {
            g.gridy = row++; g.insets = new Insets(8, 0, 10, 0);
            content.add(sectionHeader("Related Calculators", new Color(240, 253, 244), ANS_FG), g);
            JPanel calcRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
            calcRow.setBackground(BG);
            for (LectureData.CalcLink link : calcLinks) {
                JButton btn = new JButton(link.label() + " →");
                btn.setFont(Theme.BOLD_SMALL);
                btn.setBackground(new Color(219, 234, 254));
                btn.setForeground(ACCENT);
                btn.setBorderPainted(false);
                btn.setFocusPainted(false);
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn.addActionListener(e -> nav.accept(link.panelKey(), link.tabIndex()));
                calcRow.add(btn);
            }
            g.gridy = row++; g.insets = new Insets(0, 0, 0, 0);
            content.add(calcRow, g);
        }

        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0; filler.gridy = row; filler.weightx = 1; filler.weighty = 1;
        filler.fill = GridBagConstraints.BOTH;
        JPanel pad = new JPanel(); pad.setOpaque(false);
        content.add(pad, filler);

        JScrollPane scroll = new JScrollPane(content,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getVerticalScrollBar().setValue(0);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel staticFactCard(LectureData.Fact fact) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(196, 210, 250), 2, true),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST;

        g.gridy = 0; g.insets = new Insets(0, 0, 8, 0);
        JLabel topicLbl = new JLabel("  " + fact.topic() + "  ");
        topicLbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        topicLbl.setForeground(ACCENT);
        topicLbl.setBackground(new Color(219, 234, 254));
        topicLbl.setOpaque(true);
        topicLbl.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        JPanel cr = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        cr.setBackground(CARD_BG); cr.add(topicLbl);
        card.add(cr, g);

        g.gridy = 1; g.insets = new Insets(0, 0, 10, 0);
        JLabel titleLbl = new JLabel("<html>" + esc(fact.title()) + "</html>");
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        titleLbl.setForeground(HEADING);
        card.add(titleLbl, g);

        g.gridy = 2; g.insets = new Insets(0, 0, 10, 0);
        card.add(answerBox(fact.answer(), 13), g);

        g.gridy = 3; g.insets = new Insets(0, 0, 0, 0);
        JLabel explLbl = new JLabel("<html><font color='#4b5563'>" + esc(fact.explanation()) + "</font></html>");
        explLbl.setFont(Theme.SMALL_FONT);
        card.add(explLbl, g);

        return card;
    }

    private JPanel noteCard(LectureData.Note note) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(NOTE_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(196, 200, 240), 1, true),
            BorderFactory.createEmptyBorder(14, 18, 14, 18)));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST;

        g.gridy = 0; g.insets = new Insets(0, 0, 4, 0);
        JLabel srcLbl = new JLabel(note.lectureRef());
        srcLbl.setFont(new Font("SansSerif", Font.ITALIC, 11));
        srcLbl.setForeground(new Color(100, 110, 160));
        card.add(srcLbl, g);

        g.gridy = 1; g.insets = new Insets(0, 0, 10, 0);
        JLabel secLbl = new JLabel(note.title());
        secLbl.setFont(Theme.BOLD_SMALL);
        secLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        secLbl.setForeground(HEADING);
        card.add(secLbl, g);

        g.gridy = 2; g.insets = new Insets(0, 0, 0, 0);
        JEditorPane editor = new JEditorPane("text/html",
            "<html><body style='font-family:SansSerif;font-size:12pt;color:#1e293b;'>"
            + note.html() + "</body></html>");
        editor.setEditable(false);
        editor.setBackground(NOTE_BG);
        editor.setOpaque(true);
        editor.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        editor.setFont(Theme.SMALL_FONT);
        card.add(editor, g);

        return card;
    }

    private JPanel examCard(LectureData.ExamQ q) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(EXAM_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(253, 230, 138), 1, true),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST;

        g.gridy = 0; g.insets = new Insets(0, 0, 6, 0);
        JLabel examLbl = new JLabel(q.examLabel());
        examLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        examLbl.setForeground(new Color(146, 64, 14));
        card.add(examLbl, g);

        g.gridy = 1; g.insets = new Insets(0, 0, 8, 0);
        JLabel qLbl = new JLabel("<html><i>" + esc(q.question()) + "</i></html>");
        qLbl.setFont(Theme.SMALL_FONT);
        qLbl.setForeground(HEADING);
        card.add(qLbl, g);

        g.gridy = 2; g.insets = new Insets(0, 0, 0, 0);
        JLabel aLbl = new JLabel("<html><b>Answer:</b> " + esc(q.answer()) + "</html>");
        aLbl.setFont(Theme.SMALL_FONT);
        aLbl.setForeground(ANS_FG);
        card.add(aLbl, g);

        return card;
    }

    // ── Shared sub-component factories ────────────────────────────────────────

    private JPanel answerBox(String answer, int fontSize) {
        JPanel box = new JPanel(new GridBagLayout());
        box.setBackground(ANS_BG);
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.ANS_BORDER, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        GridBagConstraints ag = new GridBagConstraints();
        ag.gridx = 0; ag.weightx = 1; ag.fill = GridBagConstraints.HORIZONTAL;
        JLabel lbl = new JLabel("<html><b>Answer:</b> " + esc(answer) + "</html>");
        lbl.setFont(new Font("SansSerif", Font.PLAIN, fontSize));
        lbl.setForeground(ANS_FG);
        box.add(lbl, ag);
        return box;
    }

    private JPanel sectionHeader(String title, Color bg, Color fg) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(bg);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 230), 1, true),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(fg);
        p.add(lbl);
        return p;
    }

    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
