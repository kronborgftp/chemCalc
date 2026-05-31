package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Paste a question → keyword-scores every calculator → shows the top matches as
 * clickable navigation links. Works fully offline; no AI — pure keyword matching
 * with both English and Danish chemistry vocabulary.
 */
public class SuggestPanel extends JPanel {

    private static final Color BG      = new Color(245, 246, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color HEADING = new Color(18, 32, 68);
    private static final Color ACCENT  = new Color(37, 99, 235);
    private static final Color DIVIDER = new Color(220, 222, 232);
    private static final Color BAR_BG  = new Color(220, 228, 250);

    // ── Keyword table ─────────────────────────────────────────────────────────
    // Each entry: {label matching ALL_CALCS[i][0], pipe-delimited phrases}
    // Phrases can be single words or multi-word strings (matched as substrings).
    // Longer phrases score higher (+3 each) than single words (+1 each).
    private static final String[][] KEYWORD_MAP = {
        {"Equation Balancer",
            "balance|afstem|koefficienter|coefficient|sum of coefficients|summen af|reaktionsskema|balance the following"},
        {"pH — Strong Acid",
            "strong acid|stærk syre|fully dissociates|fully ionised|hcl|h2so4|hno3|hbr|hclo4|[h+]|ph of a 0"},
        {"pH — Strong Base",
            "strong base|stærk base|naoh|koh|fully dissociates|[oh-]|hydroxide concentration"},
        {"pH — Weak Acid",
            "weak acid|svag syre|pka|ka =|ka=|acetic acid|eddikesyre|ch3cooh|hf|hno2|ionisation|percent ionized|0.10 m solution|aqueous solution"},
        {"pH — Weak Base",
            "weak base|svag base|kb|pkb|nh3|ammonia|ammoniak|pyridine|methylamine"},
        {"Buffer pH",
            "buffer|henderson|hasselbalch|conjugate base|conjugate acid|[a-]/[ha]|ratio|buffered"},
        {"Neutralisation",
            "neutralisation|neutralization|neutralisere|titration|equivalence point|ml of|strong acid and strong base|mixing|blandede"},
        {"Buffer + Titrant",
            "buffer solution was prepared|buffer was prepared|added to the buffer|resulting ph|tilsættes buffer|naoh was added|hcl was added|ml of naoh|ml of hcl|buffer after adding|30.0 ml"},
        {"Full Redox Balance",
            "balance the following reaction|afstem følgende reaktion|oxidation of|sum of coefficients|summen af koefficienterne|h2so4 + hi|mno4|fe2+|fe3+|cr2o7|balance full|full equation|skeleton equation|oxidises|oxidized|reduced|hvad er summen"},
        {"Oxidation States",
            "oxidation state|oxidation number|oxidationstallet|assign oxidation|svovl i svovlsyre|sulfur in h2so4|ox. state"},
        {"Balance Half-Rxn (Acid)",
            "half-reaction|half reaction|acidic solution|acid solution|balance in acid|halfreaktion i syre"},
        {"Balance Half-Rxn (Base)",
            "half-reaction|half reaction|basic solution|alkaline|balance in base|halfreaktion i base"},
        {"Combine Half-Reactions",
            "combine half|lcm|electrons transferred|overall equation|factor|multiplier|full redox equation"},
        {"Galvanic Cell E°",
            "galvanic|galvanisk element|half-cell|halvcelle|e° =|reduction potential|ecell|cathode|anode|e°cell|konstrueres|constructued|0.77 v|1.6 v"},
        {"Formal Charge",
            "formal charge|formel ladning|lewis structure|lewis-struktur|fc =|v - l - b"},
        {"E°cell Calculator",
            "e°cell|ecell|standard cell potential|cell potential|e° reduction|reduction potential|two half-cell"},
        {"Nernst Equation",
            "nernst|non-standard|non standard|reaction quotient q|concentration cell|at 25|at 298"},
        {"ΔG° / K / E° Triangle",
            "delta g and k|delta g° from k|k from delta g|equilibrium constant from e°|convert between"},
        {"Faraday's Law",
            "faraday|electrolysis|elektrolyse|current|ampere|coulomb|mass deposited|mass plated|time of electrolysis"},
        {"Concentration Cell",
            "concentration cell|same electrode|different concentration|ln(c2/c1)"},
        {"Ksp from E°",
            "ksp from e|solubility from potential|ksp using nernst"},
        {"ΔG = ΔH − TΔS",
            "delta h|delta s|delta g|spontaneous|spontan|ikke-spontan|non-spontaneous|gibbs|entropy|enthalpy|crossover temperature|skifter fra spontan|at which temperature|hvilken temperatur|boiling point of br|kogepunkt for|estimate the boiling"},
        {"ΔG° ↔ K",
            "delta g° = -rt ln|equilibrium constant from delta g|standard free energy|delta g standard|k from delta g"},
        {"Non-standard ΔG",
            "non-standard delta g|delta g = delta g°|reaction quotient|current concentrations|non-equilibrium"},
        {"Hess's Law",
            "hess|givet følgende information|given the following information|combine reactions|target reaction|calculate delta h for|udregn delta h|delta h1|delta h2|delta h3"},
        {"ΔH from ΔHf°",
            "delta hf|standard enthalpy of formation|enthalpies of formation|delta h°f|hf° |formation enthalpy|products minus reactants"},
        {"Clausius-Clapeyron",
            "clausius|clapeyron|vapor pressure|dampopyk|damptryk|boiling point|kogepunkt|delta hvap|vaporisation|ln(p2/p1)"},
        {"Bond Enthalpy",
            "bond enthalpy|bindingsentalpi|bond energy|estimate delta h|br-br|f-f|br-f|bonds broken|bonds formed|givet bindingsenthalpierne|given the bond enthalpies"},
        {"Arrhenius Equation",
            "arrhenius|activation energy|ea|k at new temperature|rate constant at|find k|find the rate constant"},
        {"Find Activation Energy",
            "activation energy|find ea|two temperatures|two rate constants|ln(k2/k1)"},
        {"Integrated Rate Law",
            "integrated rate law|[a] at time|concentration after|k and t|first order|second order|zero order|rate constant k"},
        {"Half-Life",
            "half-life|halverings|t1/2|t½|radioactive|decay|how long until half"},
        {"Rate Law k[A]ᵐ[B]ⁿ",
            "rate = k|rate law|hastighedsudtrykket|rate expression|calculate the rate|find the rate"},
        {"Graham's Law",
            "graham|effusion|diffusion|rate of effusion|faster gas|lighter gas|molar mass ratio|container punctured|punkteres|nål|needle|partial pressure after"},
        {"Order from Data",
            "reaction order|order from|experimental data|experiment number|rate data|table of rates|nh4+|no2-|determine x and y|what is x|what is y|k[nh4"},
        {"ICE — Weak Acid",
            "ice table|ice|equilibrium concentration|initial concentration|ka|weak acid equilibrium"},
        {"ICE — Weak Base",
            "ice table|ice|equilibrium|kb|weak base equilibrium"},
        {"ICE — General",
            "equilibrium concentration|kc|reach equilibrium|ligevægtsbetingelse"},
        {"Kc from Concentrations",
            "kc|equilibrium expression|equilibrium concentrations|concentration at equilibrium|calculate kc"},
        {"Kp ↔ Kc",
            "kp|kc|delta n|moles of gas|gas phase equilibrium|convert kp|convert kc"},
        {"Q vs K Direction",
            "reaction quotient|q vs k|q < k|q > k|direction|which direction|le chatelier|trykket|pressure increase|increase pressure|favor formation"},
        {"Ksp ↔ Molar Solubility",
            "ksp|solubility product|molar solubility|opløselighed|molær opløselighed|dissolve|mg/l|g/l|how much dissolves|cu(oh)2|agcl|pbcl2|bacl2"},
        {"Osmosis / Osmometry",
            "osmosis|osmotic pressure|van't hoff|molar mass from boiling|molar mass of unknown|unknown molecule|non-electrolyte|non-volatile|colligative|kb =|boiling point elevation|kogepunktsstigning"},
        {"Selective Precipitation",
            "precipitate first|which salt|both pb|both ag|selective precipitation|lowest concentration of metal|[cl-] =|which will precipitate|precipitation first"},
        {"Molar Mass",
            "molar mass|molekylmasse|formula mass|atomic mass|calculate the molar mass"},
        {"Moles ↔ Mass",
            "moles|mol|grams|gram|number of particles|avogadro|n = m/m|mass from moles"},
        {"Ideal Gas Law",
            "ideal gas|pv = nrt|pressure|volume|temperature|moles of gas|gas law"},
        {"Limiting Reagent",
            "limiting|begrænsende|limiting reagent|limiting reactant|excess|maximum amount|how much|maksimalt kan produceres"},
        {"% Yield",
            "percent yield|% yield|procentvis udbytte|actual yield|theoretical yield"},
        {"Empirical Formula",
            "empirical formula|empirisk formel|molecular formula|percent composition|% composition|mass composition"},
        {"Element Lookup",
            "element|atomic number|group|period|electronegativity|atomic radius|valence electrons|outermost shell|elektroner i sin yderste"},
        {"Colligative Properties",
            "boiling point elevation|freezing point depression|delta tb|delta tf|kb = 0.51|kb =|colligative|unknown molar mass|non-electrolyte dissolved"},
        {"Isotope / % Abundance",
            "isotope|abundance|average atomic mass|mass number|nukleontal|proton|neutron|isotoperne"},
        {"Electron Configuration",
            "electron configuration|elektronkonfiguration|outermost shell|valence electron|4s2|4p5|3d|main group|period|s2p5"},
        {"Photon Energy",
            "photon|wavelength|lambda|nm|frequency|hertz|hz|e = hc|energy of light|emission|absorption|kj/mol|per mole of photons"},
        {"Unit Cell / Crystal",
            "unit cell|crystal|bcc|fcc|simple cubic|density|lattice parameter|a =|angstrom|coating|thin film|number of unit cells"},
        {"VSEPR from Formula",
            "bond angle|bindingsvinkel|molecular geometry|molekylgeometri|shape|vsepr|linear|tetrahedral|bent|trigonal|octahedral|planar|polær|polar|non-polar|h-n-h|cs2|sih4|sf6|n2h4"},
        {"VSEPR Manual",
            "bonding domains|lone pairs|steric number|manual vsepr"},
        {"Reference Tables",
            "reference|table|constants|look up|ka value|ksp value|e° value|reduction potential table"},
        {"Dissolution / Ion Count",
            "ions|dissolution|how many moles of ions|complete dissolution|al2(so4)3|aluminiumsulfat|aluminum sulfate|opløsning|ioner dannes|moles of ions formed|komplet opløsning"},
    };

    private final BiConsumer<String, Integer> nav;
    private JPanel resultsPanel;

    public SuggestPanel(BiConsumer<String, Integer> nav) {
        this.nav = nav;
        setLayout(new BorderLayout());
        setBackground(BG);

        // ── Header ────────────────────────────────────────────────────────────
        JPanel hero = new JPanel();
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBackground(HEADING);
        hero.setBorder(BorderFactory.createEmptyBorder(22, 36, 18, 36));

        JLabel title = new JLabel("Smart Suggest");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Paste an exam question - the program scores all calculators and shows the best matches.");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(new Color(160, 180, 230));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub2 = new JLabel("Works with both English and Danish. Purely offline keyword matching - no internet required.");
        sub2.setFont(new Font("SansSerif", Font.ITALIC, 12));
        sub2.setForeground(new Color(130, 155, 210));
        sub2.setAlignmentX(Component.LEFT_ALIGNMENT);

        hero.add(title);
        hero.add(Box.createVerticalStrut(6));
        hero.add(sub);
        hero.add(Box.createVerticalStrut(3));
        hero.add(sub2);
        add(hero, BorderLayout.NORTH);

        // ── Input area ────────────────────────────────────────────────────────
        JPanel inputCard = new JPanel(new BorderLayout(0, 10));
        inputCard.setBackground(CARD_BG);
        inputCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DIVIDER, 1, true),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)));

        JLabel inputLabel = new JLabel("Question / problem text:");
        inputLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        inputLabel.setForeground(HEADING);

        JTextArea questionArea = new JTextArea(6, 60);
        questionArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);
        questionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 202, 215), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        questionArea.setBackground(new Color(252, 253, 255));

        JButton suggestBtn = new JButton("Find Best Calculators");
        suggestBtn.setBackground(ACCENT);
        suggestBtn.setForeground(Color.WHITE);
        suggestBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        suggestBtn.setBorderPainted(false);
        suggestBtn.setFocusPainted(false);
        suggestBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        suggestBtn.setPreferredSize(new Dimension(200, 36));
        suggestBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { suggestBtn.setBackground(new Color(29, 78, 216)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { suggestBtn.setBackground(ACCENT); }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setBackground(CARD_BG);
        btnRow.add(suggestBtn);

        JButton pasteBtn = new JButton("Paste");
        pasteBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        pasteBtn.setBackground(new Color(240, 241, 245));
        pasteBtn.setForeground(new Color(80, 85, 100));
        pasteBtn.setBorderPainted(false);
        pasteBtn.setFocusPainted(false);
        pasteBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pasteBtn.setPreferredSize(new Dimension(70, 36));
        pasteBtn.addActionListener(e -> {
            try {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                    questionArea.setText(clipboard.getData(DataFlavor.stringFlavor).toString());
                    questionArea.requestFocusInWindow();
                }
            } catch (UnsupportedFlavorException | IOException ex) {
                Toolkit.getDefaultToolkit().beep();
            }
        });

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        clearBtn.setBackground(new Color(240, 241, 245));
        clearBtn.setForeground(new Color(80, 85, 100));
        clearBtn.setBorderPainted(false);
        clearBtn.setFocusPainted(false);
        clearBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearBtn.setPreferredSize(new Dimension(70, 36));
        clearBtn.addActionListener(e -> { questionArea.setText(""); resultsPanel.removeAll(); resultsPanel.revalidate(); });
        btnRow.add(Box.createHorizontalStrut(10));
        btnRow.add(pasteBtn);
        btnRow.add(Box.createHorizontalStrut(10));
        btnRow.add(clearBtn);

        inputCard.add(inputLabel,   BorderLayout.NORTH);
        inputCard.add(questionArea, BorderLayout.CENTER);
        inputCard.add(btnRow,       BorderLayout.SOUTH);

        JPanel inputWrap = new JPanel(new BorderLayout());
        inputWrap.setBackground(BG);
        inputWrap.setBorder(BorderFactory.createEmptyBorder(20, 28, 12, 28));
        inputWrap.add(inputCard);

        // ── Results panel — gets its OWN scroll pane; questionArea stays outside ──
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(BG);
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(0, 28, 28, 28));

        JScrollPane resultsScroll = new JScrollPane(resultsPanel,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        resultsScroll.setBorder(null);
        resultsScroll.getViewport().setBackground(BG);
        resultsScroll.getVerticalScrollBar().setUnitIncrement(16);
        // Prevent the results scroll pane from stealing focus from questionArea
        resultsScroll.setFocusable(false);
        resultsScroll.getViewport().setFocusable(false);

        // Center: inputWrap (plain JPanel, no scroll) at top; results scroll below.
        // questionArea never has a JScrollPane ancestor, so macOS focus works correctly.
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BG);
        center.add(inputWrap,    BorderLayout.NORTH);
        center.add(resultsScroll, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // ── Wire button ───────────────────────────────────────────────────────
        suggestBtn.addActionListener(e -> suggest(questionArea.getText()));
    }

    // ── Scoring ───────────────────────────────────────────────────────────────

    private void suggest(String questionText) {
        if (questionText.isBlank()) return;
        String q = questionText.toLowerCase();

        // Score each calculator entry
        List<int[]> scores = new ArrayList<>(); // [calcIndex, score]
        for (int i = 0; i < ChemApp.ALL_CALCS.length; i++) {
            String label = ChemApp.ALL_CALCS[i][0];
            String desc  = ChemApp.ALL_CALCS[i][1];
            int score = 0;

            for (String word : (label + " " + desc).toLowerCase().split("[^a-zA-Z0-9åæøÅÆØ]+")) {
                if (word.length() > 3 && q.contains(word)) score += 1;
            }

            String[] kRow = keywordsFor(label);
            if (kRow != null) {
                for (String kw : kRow) {
                    kw = kw.toLowerCase().trim();
                    if (kw.isBlank()) continue;
                    if (q.contains(kw)) score += kw.contains(" ") ? 3 : 1;
                }
            }

            if (score > 0) scores.add(new int[]{i, score});
        }
        scores.sort((a, b) -> b[1] - a[1]);
        int maxCalcScore = scores.isEmpty() ? 1 : Math.max(1, scores.get(0)[1]);

        // Score facts
        List<int[]> factScores = FactsPanel.scoreFacts(questionText);
        int maxFactScore = factScores.isEmpty() ? 1 : Math.max(1, factScores.get(0)[1]);

        // Render
        resultsPanel.removeAll();

        boolean anyResult = !scores.isEmpty() || !factScores.isEmpty();
        if (!anyResult) {
            JLabel none = new JLabel("No strong matches found. Try more specific chemistry vocabulary.");
            none.setFont(new Font("SansSerif", Font.ITALIC, 13));
            none.setForeground(new Color(130, 135, 150));
            none.setBorder(BorderFactory.createEmptyBorder(10, 4, 10, 4));
            resultsPanel.add(none);
        } else {
            // ── Calculator results ──────────────────────────────────────────
            if (!scores.isEmpty()) {
                JLabel hdr = new JLabel("Calculators  (" + Math.min(scores.size(), 5) + " of " + scores.size() + " matched)");
                hdr.setFont(new Font("SansSerif", Font.BOLD, 12));
                hdr.setForeground(new Color(100, 108, 140));
                hdr.setBorder(BorderFactory.createEmptyBorder(4, 2, 8, 0));
                hdr.setAlignmentX(Component.LEFT_ALIGNMENT);
                resultsPanel.add(hdr);

                int shown = 0;
                for (int[] pair : scores) {
                    if (shown >= 5) break;
                    int idx      = pair[0];
                    int sc       = pair[1];
                    String label    = ChemApp.ALL_CALCS[idx][0];
                    String desc     = ChemApp.ALL_CALCS[idx][1];
                    String panelKey = ChemApp.ALL_CALCS[idx][2];
                    int tabIdx      = Integer.parseInt(ChemApp.ALL_CALCS[idx][3]);
                    int pct = (int) Math.round(100.0 * sc / maxCalcScore);
                    resultsPanel.add(resultCard(shown + 1, label, desc, pct, panelKey, tabIdx));
                    resultsPanel.add(Box.createVerticalStrut(8));
                    shown++;
                }
            }

            // ── Facts / Conceptual results ──────────────────────────────────
            if (!factScores.isEmpty()) {
                int showFacts = Math.min(factScores.size(), 4);
                resultsPanel.add(Box.createVerticalStrut(8));
                JLabel fhdr = new JLabel("Conceptual Facts  (" + showFacts + " of " + factScores.size() + " matched)");
                fhdr.setFont(new Font("SansSerif", Font.BOLD, 12));
                fhdr.setForeground(new Color(100, 108, 140));
                fhdr.setBorder(BorderFactory.createEmptyBorder(4, 2, 8, 0));
                fhdr.setAlignmentX(Component.LEFT_ALIGNMENT);
                resultsPanel.add(fhdr);

                for (int fi = 0; fi < showFacts; fi++) {
                    int factIdx = factScores.get(fi)[0];
                    int sc      = factScores.get(fi)[1];
                    String[] fact = FactsPanel.FACTS[factIdx];
                    int pct = (int) Math.round(100.0 * sc / maxFactScore);
                    resultsPanel.add(factResultCard(fi + 1, fact, pct));
                    resultsPanel.add(Box.createVerticalStrut(8));
                }
            }
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private JPanel factResultCard(int rank, String[] fact, int pct) {
        JPanel card = new JPanel(new BorderLayout(12, 4));
        card.setBackground(new Color(250, 252, 255));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(167, 243, 208), 1, true),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel rankLbl = new JLabel("#" + rank);
        rankLbl.setFont(new Font("SansSerif", Font.BOLD, 16));
        rankLbl.setForeground(new Color(6, 95, 70));
        rankLbl.setPreferredSize(new Dimension(32, 40));
        rankLbl.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel centre = new JPanel();
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.setBackground(new Color(250, 252, 255));

        JLabel topicLbl = new JLabel("FACT  ·  " + fact[0]);
        topicLbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        topicLbl.setForeground(new Color(6, 95, 70));
        topicLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLbl = new JLabel("<html><b>" + fact[1] + "</b></html>");
        titleLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        titleLbl.setForeground(HEADING);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel ansLbl = new JLabel("<html><i>" + fact[2] + "</i></html>");
        ansLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        ansLbl.setForeground(new Color(6, 95, 70));
        ansLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        centre.add(topicLbl);
        centre.add(Box.createVerticalStrut(2));
        centre.add(titleLbl);
        centre.add(Box.createVerticalStrut(2));
        centre.add(ansLbl);

        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(new Color(250, 252, 255));
        JLabel scoreLbl = new JLabel(pct + "%");
        scoreLbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        scoreLbl.setForeground(new Color(6, 95, 70));
        scoreLbl.setHorizontalAlignment(SwingConstants.RIGHT);

        JButton openBtn = new JButton("Facts →");
        openBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        openBtn.setBackground(new Color(209, 250, 229));
        openBtn.setForeground(new Color(6, 95, 70));
        openBtn.setBorderPainted(false);
        openBtn.setFocusPainted(false);
        openBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        openBtn.addActionListener(e -> nav.accept("facts", -1));

        right.add(scoreLbl, BorderLayout.NORTH);
        right.add(openBtn,  BorderLayout.SOUTH);

        card.add(rankLbl, BorderLayout.WEST);
        card.add(centre,  BorderLayout.CENTER);
        card.add(right,   BorderLayout.EAST);
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { nav.accept("facts", -1); }
        });
        return card;
    }

    private String[] keywordsFor(String label) {
        for (String[] row : KEYWORD_MAP) {
            if (row[0].equals(label)) return row[1].split("\\|");
        }
        return null;
    }

    // ── Result card ───────────────────────────────────────────────────────────

    private JPanel resultCard(int rank, String label, String desc, int pct,
                               String panelKey, int tabIdx) {
        JPanel card = new JPanel(new BorderLayout(12, 4));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DIVIDER, 1, true),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Left: rank badge
        JLabel rankLbl = new JLabel("#" + rank);
        rankLbl.setFont(new Font("SansSerif", Font.BOLD, 18));
        rankLbl.setForeground(rank == 1 ? ACCENT : new Color(150, 158, 180));
        rankLbl.setPreferredSize(new Dimension(36, 40));
        rankLbl.setHorizontalAlignment(SwingConstants.CENTER);

        // Centre: label + desc + bar
        JPanel centre = new JPanel();
        centre.setLayout(new BoxLayout(centre, BoxLayout.Y_AXIS));
        centre.setBackground(CARD_BG);

        JLabel nameLbl = new JLabel(label);
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLbl.setForeground(ACCENT);
        nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLbl = new JLabel(desc);
        descLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descLbl.setForeground(new Color(80, 85, 105));
        descLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Relevance bar
        JPanel barBg = new JPanel(new BorderLayout());
        barBg.setBackground(BAR_BG);
        barBg.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));
        barBg.setPreferredSize(new Dimension(Integer.MAX_VALUE, 5));
        barBg.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel barFg = new JPanel();
        barFg.setBackground(pct >= 80 ? new Color(22, 163, 74) : pct >= 50 ? ACCENT : new Color(147, 170, 230));
        barBg.add(barFg, BorderLayout.WEST);
        // Set bar width proportionally after layout; use preferred width trick
        barFg.setPreferredSize(new Dimension(pct, 5));

        centre.add(nameLbl);
        centre.add(Box.createVerticalStrut(2));
        centre.add(descLbl);
        centre.add(Box.createVerticalStrut(5));
        centre.add(barBg);

        // Right: score + open button
        JPanel right = new JPanel(new BorderLayout(0, 4));
        right.setBackground(CARD_BG);

        JLabel scoreLbl = new JLabel(pct + "%");
        scoreLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        scoreLbl.setForeground(pct >= 80 ? new Color(22, 163, 74) : ACCENT);
        scoreLbl.setHorizontalAlignment(SwingConstants.RIGHT);

        JButton openBtn = new JButton("Open →");
        openBtn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        openBtn.setBackground(new Color(240, 242, 255));
        openBtn.setForeground(ACCENT);
        openBtn.setBorderPainted(false);
        openBtn.setFocusPainted(false);
        openBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        openBtn.addActionListener(e -> nav.accept(panelKey, tabIdx));
        openBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { openBtn.setBackground(new Color(210, 220, 255)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { openBtn.setBackground(new Color(240, 242, 255)); }
        });

        right.add(scoreLbl, BorderLayout.NORTH);
        right.add(openBtn,  BorderLayout.SOUTH);

        card.add(rankLbl, BorderLayout.WEST);
        card.add(centre,  BorderLayout.CENTER);
        card.add(right,   BorderLayout.EAST);

        // Whole card is also clickable
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { nav.accept(panelKey, tabIdx); }
            public void mouseEntered(java.awt.event.MouseEvent e) { card.setBackground(new Color(237, 242, 255)); centre.setBackground(new Color(237, 242, 255)); right.setBackground(new Color(237, 242, 255)); }
            public void mouseExited(java.awt.event.MouseEvent e)  { card.setBackground(CARD_BG); centre.setBackground(CARD_BG); right.setBackground(CARD_BG); }
        });

        return card;
    }
}
