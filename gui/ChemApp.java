package gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.function.BiConsumer;

public class ChemApp extends JFrame {

    private final CardLayout cards    = new CardLayout();
    private final JPanel     content  = new JPanel(cards);
    private final Map<String, BaseCalcPanel> panelMap = new LinkedHashMap<>();

    // ── Master calculator list ────────────────────────────────────────────────
    // {label, description, panelKey, tabIndex}
    static final String[][] ALL_CALCS = {
        {"Equation Balancer",        "Balance any equation via Gaussian elimination",            "eq",      "-1"},
        {"pH — Strong Acid",         "pH = −log[H⁺]",                                           "ph",       "0"},
        {"pH — Strong Base",         "pOH = −log[OH⁻],  pH = 14 − pOH",                         "ph",       "1"},
        {"pH — Weak Acid",           "Ka + ICE table → [H⁺] and pH",                             "ph",       "2"},
        {"pH — Weak Base",           "Kb + ICE table → [OH⁻], pOH, pH",                          "ph",       "3"},
        {"Buffer pH",                "Henderson-Hasselbalch: pH = pKa + log([A⁻]/[HA])",         "ph",       "4"},
        {"Neutralisation",           "Titration equivalence point and excess acid/base",          "ph",       "5"},
        {"Buffer + Titrant",         "pH after adding strong acid or base to a buffer",           "ph",       "6"},
        {"Full Redox Balance",        "Paste full skeleton equation → auto-balances in acid or base", "redox",   "0"},
        {"Oxidation States",         "Assign ox. states by rules (F, O, H, group metals)",        "redox",    "1"},
        {"Balance Half-Rxn (Acid)",  "Balance half-reactions in acidic solution",                 "redox",    "2"},
        {"Balance Half-Rxn (Base)",  "Balance half-reactions in basic solution",                  "redox",    "3"},
        {"Combine Half-Reactions",   "Find LCM of electrons and multipliers",                     "redox",    "4"},
        {"Galvanic Cell E°",         "E°cell = E°cathode − E°anode, ΔG°, K",                     "redox",    "5"},
        {"Formal Charge",            "FC = V − L − B/2 for up to 4 atoms",                       "redox",    "6"},
        {"E°cell Calculator",        "E°cell, ΔG°, K from two half-cell E° values",              "electro",  "0"},
        {"Nernst Equation",          "E = E° − (RT/nF)·ln Q  at any temperature",                "electro",  "1"},
        {"ΔG° / K / E° Triangle",   "Three-way converter between ΔG°, K, and E°",                "electro",  "2"},
        {"Faraday's Law",            "Mass deposited and charge in electrolysis",                 "electro",  "3"},
        {"Concentration Cell",       "E for same electrode at different concentrations",          "electro",  "4"},
        {"Ksp from E°",              "Derive Ksp from the cell potential for dissolution",        "electro",  "5"},
        {"ΔG = ΔH − TΔS",           "Spontaneity, crossover T = ΔH/ΔS (boiling point est.)",    "thermo",   "0"},
        {"ΔG° ↔ K",                 "ΔG° = −RT·ln K  (standard free energy vs equilibrium)",   "thermo",   "1"},
        {"Non-standard ΔG",          "ΔG = ΔG° + RT·ln Q  at non-equilibrium conditions",        "thermo",   "2"},
        {"Hess's Law",               "Add scaled reactions to find target ΔH°",                   "thermo",   "3"},
        {"ΔH from ΔHf°",            "ΔH° = Σ ΔHf°(products) − Σ ΔHf°(reactants)",              "thermo",   "4"},
        {"Clausius-Clapeyron",       "ln(P2/P1) = −ΔHvap/R · (1/T2 − 1/T1)",                    "thermo",   "5"},
        {"Bond Enthalpy",            "ΔH ≈ Σ bonds broken − Σ bonds formed",                     "thermo",   "6"},
        {"Arrhenius Equation",       "k = A·exp(−Ea/RT), find k at new temperature",             "kinetics", "0"},
        {"Find Activation Energy",   "Ea from two rate constants at two temperatures",            "kinetics", "1"},
        {"Integrated Rate Law",      "[A]t from k and t for 0th, 1st, 2nd order",                "kinetics", "2"},
        {"Half-Life",                "t½ for 0th, 1st, 2nd order reactions",                     "kinetics", "3"},
        {"Rate Law k[A]ᵐ[B]ⁿ",      "Compute rate given k, concentrations, and orders",         "kinetics", "4"},
        {"Graham's Law",             "Rate ∝ 1/√M — compare effusion and diffusion rates",       "kinetics", "5"},
        {"Order from Data",          "Determine x, y in rate = k[A]ˣ[B]ʸ from experiments",     "kinetics", "6"},
        {"ICE — Weak Acid",          "Ka + ICE table → equilibrium [H⁺]",                        "equil",    "0"},
        {"ICE — Weak Base",          "Kb + ICE table → equilibrium [OH⁻]",                       "equil",    "1"},
        {"ICE — General",            "K + ICE table for any A ⇌ b·B reaction",                   "equil",    "2"},
        {"Kc from Concentrations",   "Kc = [P]^a / [R]^b — equilibrium expression",             "equil",    "3"},
        {"Kp ↔ Kc",                 "Kp = Kc·(RT)^Δn",                                          "equil",    "4"},
        {"Q vs K Direction",         "Is Q < K (forward) or Q > K (reverse)?",                   "equil",    "5"},
        {"Ksp ↔ Molar Solubility",   "s = (Ksp / m^m·n^n)^(1/(m+n))",                           "equil",    "6"},
        {"Osmosis / Osmometry",       "π = i·c·R·T, osmotic pressure and molar mass",            "equil",    "7"},
        {"Selective Precipitation",  "Which salt precipitates first at a given [anion]?",        "equil",    "8"},
        {"Molar Mass",               "M = Σ (element mass × count)",                             "stoich",   "0"},
        {"Moles ↔ Mass",             "n = m/M, N = n·NA, m = n·M",                              "stoich",   "1"},
        {"Ideal Gas Law",            "PV = nRT — solve for any variable",                        "stoich",   "2"},
        {"Limiting Reagent",         "Find limiting reactant and theoretical yield",              "stoich",   "3"},
        {"% Yield",                  "% yield = (actual / theoretical) × 100",                   "stoich",   "4"},
        {"Empirical Formula",        "Find empirical/molecular formula from % composition",       "stoich",   "5"},
        {"Element Lookup",           "Atomic number, mass, group, period, EN for any element",   "stoich",   "6"},
        {"Colligative Properties",   "ΔTb, ΔTf, vapour pressure lowering",                       "stoich",   "7"},
        {"Isotope / % Abundance",    "Average atomic mass from isotope masses and abundances",    "stoich",   "8"},
        {"Electron Configuration",   "Write e⁻ config and find valence electrons",               "stoich",   "9"},
        {"Photon Energy",            "E = hc/λ — wavelength, frequency, energy per mole",        "stoich",  "10"},
        {"Unit Cell / Crystal",      "ρ = Z·M/(NA·a³) — density, lattice param, # unit cells",  "stoich",  "11"},
        {"VSEPR from Formula",       "Auto-detect central atom, lone pairs, geometry, polarity", "vsepr",    "0"},
        {"VSEPR Manual",             "Enter bonding domains and lone pairs → geometry",           "vsepr",    "1"},
        {"VSEPR Reference Table",    "All BP/LP combos: geometry, bond angle, planar?",          "vsepr",    "2"},
        {"Reference Tables",         "Ka/Kb, ΔHf°, Ksp, E°, constants, organic classes",        "ref",      "-1"},
    };

    public ChemApp() {
        super("DTU Chemistry Exam Toolkit  v1.1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 800);
        setMinimumSize(new Dimension(980, 640));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ── Title bar ─────────────────────────────────────────────────────────
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(18, 32, 68));
        titleBar.setPreferredSize(new Dimension(0, 44));
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 18));
        JLabel title = new JLabel("DTU Chemistry Exam Toolkit");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        JLabel version = new JLabel("v1.1  —  " + ALL_CALCS.length + " calculators");
        version.setForeground(new Color(150, 170, 220));
        version.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleBar.add(title, BorderLayout.WEST);
        titleBar.add(version, BorderLayout.EAST);
        add(titleBar, BorderLayout.NORTH);

        // ── Content area ──────────────────────────────────────────────────────
        content.setBackground(new Color(245, 246, 250));

        BiConsumer<String, Integer> nav = this::navigate;

        content.add(new HomePanel(nav),    "home");
        content.add(new SuggestPanel(nav), "suggest");
        register(new EquationBalancerPanel(), "eq");
        register(new PHPanel(),               "ph");
        register(new RedoxPanel(),            "redox");
        register(new ElectrochemistryPanel(), "electro");
        register(new ThermodynamicsPanel(),   "thermo");
        register(new KineticsPanel(),         "kinetics");
        register(new EquilibriumPanel(),      "equil");
        register(new StoichiometryPanel(),    "stoich");
        register(new VESPRPanel(),            "vsepr");
        content.add(new ReferencePanel(),     "ref");
        add(content, BorderLayout.CENTER);

        // ── Sidebar ───────────────────────────────────────────────────────────
        add(buildSidebar(nav), BorderLayout.WEST);

        setVisible(true);
    }

    private void register(BaseCalcPanel panel, String key) {
        panelMap.put(key, panel);
        content.add(panel, key);
    }

    void navigate(String panelKey, int tabIdx) {
        cards.show(content, panelKey);
        if (tabIdx >= 0) {
            BaseCalcPanel p = panelMap.get(panelKey);
            if (p != null) p.goToTab(tabIdx);
        }
    }

    // ── Sidebar construction ──────────────────────────────────────────────────

    private JPanel buildSidebar(BiConsumer<String, Integer> nav) {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(new Color(24, 28, 42));
        sidebar.setPreferredSize(new Dimension(218, 0));

        // ── Search field ──────────────────────────────────────────────────────
        JTextField search = new JTextField("Search calculators...");
        search.setFont(new Font("SansSerif", Font.PLAIN, 12));
        search.setBackground(new Color(36, 41, 60));
        search.setForeground(new Color(130, 140, 165));
        search.setCaretColor(new Color(120, 160, 255));
        search.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(55, 65, 100), 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setBackground(new Color(24, 28, 42));
        searchWrap.setBorder(BorderFactory.createEmptyBorder(10, 8, 6, 8));
        searchWrap.add(search, BorderLayout.CENTER);

        // ── Nav list ─────────────────────────────────────────────────────────
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(24, 28, 42));
        navPanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 12, 8));

        String[][] items = {
            {"Home",             "home"},
            {"Smart Suggest",    "suggest"},
            {"Equation Balancer","eq"},
            {"pH / Acid-Base",   "ph"},
            {"Redox",            "redox"},
            {"Electrochemistry", "electro"},
            {"Thermodynamics",   "thermo"},
            {"Kinetics",         "kinetics"},
            {"Equilibrium",      "equil"},
            {"Stoichiometry",    "stoich"},
            {"VSEPR / Geometry", "vsepr"},
            {"Reference Tables", "ref"},
        };
        for (String[] it : items) {
            navPanel.add(navBtn(it[0], it[1], -1, nav));
            navPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        }
        navPanel.add(Box.createVerticalGlue());

        JScrollPane navScroll = scrollWrap(navPanel);

        // ── Results list (shown while searching) ──────────────────────────────
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(new Color(24, 28, 42));
        resultsPanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 8, 8));

        JScrollPane resultsScroll = scrollWrap(resultsPanel);

        // CardLayout to swap nav ↔ results
        JPanel switcher = new JPanel(new CardLayout());
        switcher.setBackground(new Color(24, 28, 42));
        switcher.add(navScroll,     "nav");
        switcher.add(resultsScroll, "results");
        CardLayout switchCards = (CardLayout) switcher.getLayout();

        // Focus placeholder behaviour
        search.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (search.getText().equals("Search calculators...")) {
                    search.setText("");
                    search.setForeground(new Color(220, 225, 240));
                }
            }
            public void focusLost(FocusEvent e) {
                if (search.getText().isEmpty()) {
                    search.setText("Search calculators...");
                    search.setForeground(new Color(130, 140, 165));
                    switchCards.show(switcher, "nav");
                }
            }
        });

        search.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refresh(); }
            public void removeUpdate(DocumentEvent e) { refresh(); }
            public void changedUpdate(DocumentEvent e) { refresh(); }

            void refresh() {
                String q = search.getText().trim().toLowerCase();
                if (q.isEmpty() || q.equals("search calculators...")) {
                    switchCards.show(switcher, "nav");
                    return;
                }
                resultsPanel.removeAll();
                boolean any = false;
                for (String[] entry : ALL_CALCS) {
                    String searchable = (entry[0] + " " + entry[1]).toLowerCase();
                    if (searchable.contains(q)) {
                        resultsPanel.add(resultBtn(entry[0], entry[1], entry[2],
                                                   Integer.parseInt(entry[3]), nav));
                        resultsPanel.add(Box.createRigidArea(new Dimension(0, 3)));
                        any = true;
                    }
                }
                if (!any) {
                    JLabel none = new JLabel("No match for \"" + q + "\"");
                    none.setForeground(new Color(140, 148, 168));
                    none.setFont(new Font("SansSerif", Font.ITALIC, 12));
                    none.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    resultsPanel.add(none);
                }
                resultsPanel.revalidate();
                resultsPanel.repaint();
                switchCards.show(switcher, "results");
            }
        });

        sidebar.add(searchWrap, BorderLayout.NORTH);
        sidebar.add(switcher,   BorderLayout.CENTER);
        return sidebar;
    }

    private JScrollPane scrollWrap(JComponent c) {
        JScrollPane sp = new JScrollPane(c,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setBorder(null);
        sp.getViewport().setBackground(new Color(24, 28, 42));
        return sp;
    }

    private JButton navBtn(String label, String key, int tab, BiConsumer<String, Integer> nav) {
        JButton btn = new JButton(label);
        styleBtn(btn, 36);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.addActionListener(e -> nav.accept(key, tab));
        return btn;
    }

    private JButton resultBtn(String label, String desc, String key, int tab,
                              BiConsumer<String, Integer> nav) {
        JButton btn = new JButton(
            "<html><b>" + label + "</b><br>" +
            "<font size='-2' color='#8899bb'>" + desc + "</font></html>");
        styleBtn(btn, 52);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.addActionListener(e -> nav.accept(key, tab));
        return btn;
    }

    private void styleBtn(JButton btn, int height) {
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(202, height));
        btn.setPreferredSize(new Dimension(202, height));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBackground(new Color(36, 41, 60));
        btn.setForeground(new Color(210, 215, 230));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 6));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(37, 99, 200));
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(36, 41, 60));
                btn.setForeground(new Color(210, 215, 230));
            }
        });
    }
}
