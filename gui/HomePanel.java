package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.BiConsumer;

public class HomePanel extends JPanel {

    private static final Color BG       = new Color(245, 246, 250);
    private static final Color CARD_BG  = Color.WHITE;
    private static final Color ACCENT   = new Color(37, 99, 235);
    private static final Color HEADING  = new Color(18, 32, 68);
    private static final Color DESC_FG  = new Color(70, 75, 95);
    private static final Color DIVIDER  = new Color(220, 222, 232);
    private static final Color HOVER_BG = new Color(237, 242, 255);

    // Groups: {groupName, panelKey, {label, description, tabIndex}, ...}
    private static final Object[][] GROUPS = {
        {"Equation Balancer", "eq", new Object[][]{
            {"Equation Balancer", "Balance any equation via Gaussian elimination", -1},
        }},
        {"pH / Acid-Base", "ph", new Object[][]{
            {"Strong Acid",       "pH = −log[H⁺]",                                             0},
            {"Strong Base",       "pOH = −log[OH⁻],  pH = 14 − pOH",                          1},
            {"Weak Acid",         "Ka + ICE table → [H⁺] and pH",                              2},
            {"Weak Base",         "Kb + ICE table → [OH⁻], pOH, pH",                           3},
            {"Buffer pH",         "Henderson-Hasselbalch: pH = pKa + log([A⁻]/[HA])",          4},
            {"Neutralisation",    "Titration equivalence point and excess acid/base",           5},
            {"Buffer + Titrant",  "pH after adding strong acid or base to a buffer",            6},
        }},
        {"Redox", "redox", new Object[][]{
            {"Full Redox Balance",       "Paste skeleton equation → balances in acid or base",  0},
            {"Oxidation States",        "Assign ox. states by rules (F, O, H, group metals)",  1},
            {"Balance Half-Rxn (Acid)", "Balance half-reactions in acidic solution",            2},
            {"Balance Half-Rxn (Base)", "Balance half-reactions in basic solution",             3},
            {"Combine Half-Reactions",  "Find LCM of electrons and multipliers",                4},
            {"Galvanic Cell E°",        "E°cell = E°cathode − E°anode, ΔG°, K",               5},
            {"Formal Charge",           "FC = V − L − B/2 for up to 4 atoms",                  6},
        }},
        {"Electrochemistry", "electro", new Object[][]{
            {"E°cell Calculator",      "E°cell, ΔG°, K from two half-cell E° values",          0},
            {"Nernst Equation",        "E = E° − (RT/nF)·ln Q  at any temperature",            1},
            {"ΔG° / K / E° Triangle",  "Three-way converter between ΔG°, K, and E°",           2},
            {"Faraday's Law",          "Mass deposited and charge in electrolysis",             3},
            {"Concentration Cell",     "E for same electrode at different concentrations",      4},
            {"Ksp from E°",            "Derive Ksp from cell potential for dissolution",        5},
        }},
        {"Thermodynamics", "thermo", new Object[][]{
            {"ΔG = ΔH − TΔS",     "Spontaneity, crossover T = ΔH/ΔS (boiling pt est.)",       0},
            {"ΔG° ↔ K",           "ΔG° = −RT·ln K",                                           1},
            {"Non-standard ΔG",    "ΔG = ΔG° + RT·ln Q  at non-equilibrium conditions",        2},
            {"Hess's Law",         "Add scaled reactions to find target ΔH°",                   3},
            {"ΔH from ΔHf°",      "ΔH° = Σ ΔHf°(products) − Σ ΔHf°(reactants)",              4},
            {"Clausius-Clapeyron", "ln(P2/P1) = −ΔHvap/R · (1/T2 − 1/T1)",                   5},
            {"Bond Enthalpy",      "ΔH ≈ Σ bonds broken − Σ bonds formed",                     6},
        }},
        {"Kinetics", "kinetics", new Object[][]{
            {"Arrhenius Equation",    "k = A·exp(−Ea/RT), find k at new temperature",          0},
            {"Find Activation Energy","Ea from two rate constants at two temperatures",         1},
            {"Integrated Rate Law",   "[A]t from k and t for 0th, 1st, 2nd order",             2},
            {"Half-Life",             "t½ for 0th, 1st, 2nd order reactions",                  3},
            {"Rate Law k[A]ᵐ[B]ⁿ",   "Compute rate given k, concentrations, and orders",      4},
            {"Graham's Law",          "Rate ∝ 1/√M — compare effusion and diffusion",          5},
            {"Order from Data",       "Determine x, y in rate = k[A]ˣ[B]ʸ from experiments",  6},
        }},
        {"Equilibrium", "equil", new Object[][]{
            {"ICE — Weak Acid",        "Ka + ICE table → equilibrium [H⁺]",                   0},
            {"ICE — Weak Base",        "Kb + ICE table → equilibrium [OH⁻]",                  1},
            {"ICE — General",          "K + ICE table for any A ⇌ b·B reaction",               2},
            {"Kc from Concentrations", "Kc = [P]^a / [R]^b — equilibrium expression",         3},
            {"Kp ↔ Kc",               "Kp = Kc·(RT)^Δn",                                      4},
            {"Q vs K Direction",       "Is Q < K (forward) or Q > K (reverse)?",               5},
            {"Ksp ↔ Molar Solubility", "s = (Ksp / m^m·n^n)^(1/(m+n))",                       6},
            {"Osmosis / Osmometry",    "π = i·c·R·T, osmotic pressure and molar mass",         7},
            {"Selective Precipitation","Which salt precipitates first at a given [anion]?",    8},
        }},
        {"Stoichiometry", "stoich", new Object[][]{
            {"Molar Mass",           "M = Σ (element mass × count)",                           0},
            {"Moles ↔ Mass",         "n = m/M, N = n·NA, m = n·M",                            1},
            {"Ideal Gas Law",        "PV = nRT — solve for any variable",                      2},
            {"Limiting Reagent",     "Find limiting reactant and theoretical yield",            3},
            {"% Yield",              "% yield = (actual / theoretical) × 100",                 4},
            {"Empirical Formula",    "Find empirical/molecular formula from % composition",    5},
            {"Element Lookup",       "Atomic number, mass, group, period, EN",                 6},
            {"Colligative Properties","ΔTb, ΔTf, vapour pressure lowering",                   7},
            {"Isotope / % Abundance","Average atomic mass from isotope masses and abundances", 8},
            {"Electron Configuration","Write e⁻ config and find valence electrons",            9},
            {"Photon Energy",        "E = hc/λ — wavelength, frequency, energy per mole",    10},
            {"Unit Cell / Crystal",  "ρ = Z·M/(NA·a³) — density, lattice param, # cells",   11},
        }},
        {"VSEPR / Geometry", "vsepr", new Object[][]{
            {"From Formula",      "Auto-detect central atom, lone pairs, geometry, polarity",  0},
            {"Manual",            "Enter bonding domains and lone pairs → geometry",            1},
            {"Reference Table",   "All BP/LP combos: geometry, bond angle, planar?",           2},
        }},
        {"Reference Tables", "ref", new Object[][]{
            {"Reference Tables",  "Ka/Kb, ΔHf°, Ksp, E°, constants, organic classes",        -1},
        }},
    };

    public HomePanel(BiConsumer<String, Integer> nav) {
        setLayout(new BorderLayout());
        setBackground(BG);

        // ── Hero header ───────────────────────────────────────────────────────
        JPanel hero = new JPanel();
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBackground(HEADING);
        hero.setBorder(BorderFactory.createEmptyBorder(28, 40, 22, 40));

        JLabel mainTitle = new JLabel("DTU Chemistry Exam Toolkit");
        mainTitle.setFont(new Font("SansSerif", Font.BOLD, 26));
        mainTitle.setForeground(Color.WHITE);
        mainTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(ChemApp.ALL_CALCS.length + " calculators  •  Use the search bar on the left to jump directly to any calculator");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(160, 180, 230));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tip = new JLabel("Tip: press Enter in any field to trigger the calculation  •  All inputs accept scientific notation (e.g. 1.8e-5) and 10^x");
        tip.setFont(new Font("SansSerif", Font.ITALIC, 12));
        tip.setForeground(new Color(130, 155, 210));
        tip.setAlignmentX(Component.LEFT_ALIGNMENT);

        hero.add(mainTitle);
        hero.add(Box.createVerticalStrut(6));
        hero.add(subtitle);
        hero.add(Box.createVerticalStrut(4));
        hero.add(tip);
        add(hero, BorderLayout.NORTH);

        // ── Scrollable directory ──────────────────────────────────────────────
        JPanel directory = new JPanel();
        directory.setLayout(new BoxLayout(directory, BoxLayout.Y_AXIS));
        directory.setBackground(BG);
        directory.setBorder(BorderFactory.createEmptyBorder(18, 28, 28, 28));

        for (Object[] group : GROUPS) {
            String groupName = (String) group[0];
            String panelKey  = (String) group[1];
            Object[][] calcs = (Object[][]) group[2];

            directory.add(groupHeader(groupName));
            directory.add(Box.createVerticalStrut(6));

            JPanel card = new JPanel();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setBackground(CARD_BG);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DIVIDER, 1, true),
                BorderFactory.createEmptyBorder(4, 0, 4, 0)));
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

            for (int i = 0; i < calcs.length; i++) {
                Object[] calc = calcs[i];
                String label = (String) calc[0];
                String desc  = (String) calc[1];
                int    tab   = (Integer) calc[2];
                card.add(calcRow(label, desc, panelKey, tab, nav));
                if (i < calcs.length - 1) {
                    JSeparator sep = new JSeparator();
                    sep.setForeground(DIVIDER);
                    sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                    card.add(sep);
                }
            }
            directory.add(card);
            directory.add(Box.createVerticalStrut(18));
        }

        JScrollPane scroll = new JScrollPane(directory,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
    }

    private JLabel groupHeader(String text) {
        JLabel lbl = new JLabel(text.toUpperCase());
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(new Color(100, 110, 140));
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel calcRow(String label, String desc, String panelKey, int tabIdx,
                           BiConsumer<String, Integer> nav) {
        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setBackground(CARD_BG);
        row.setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        nameLabel.setForeground(ACCENT);

        JLabel descLabel = new JLabel(desc);
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descLabel.setForeground(DESC_FG);

        JLabel arrow = new JLabel("→");
        arrow.setFont(new Font("SansSerif", Font.PLAIN, 14));
        arrow.setForeground(new Color(180, 190, 210));

        row.add(nameLabel, BorderLayout.WEST);
        row.add(descLabel, BorderLayout.CENTER);
        row.add(arrow,     BorderLayout.EAST);

        MouseAdapter hover = new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                row.setBackground(HOVER_BG);
                nameLabel.setBackground(HOVER_BG);
                descLabel.setBackground(HOVER_BG);
                arrow.setBackground(HOVER_BG);
                for (Component c : row.getComponents()) c.setBackground(HOVER_BG);
            }
            public void mouseExited(MouseEvent e) {
                row.setBackground(CARD_BG);
                for (Component c : row.getComponents()) c.setBackground(CARD_BG);
            }
            public void mouseClicked(MouseEvent e) {
                nav.accept(panelKey, tabIdx);
            }
        };
        row.addMouseListener(hover);
        nameLabel.addMouseListener(hover);
        descLabel.addMouseListener(hover);
        arrow.addMouseListener(hover);

        return row;
    }
}
