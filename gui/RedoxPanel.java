package gui;

import calculators.RedoxCalculator;

import javax.swing.*;
import java.awt.*;

public class RedoxPanel extends BaseCalcPanel {

    private final RedoxCalculator redox = new RedoxCalculator();

    public RedoxPanel() {
        super("Redox Calculator");
    }

    @Override
    protected void buildUI() {
        inputPanel.setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(TAB_FONT);
        mainTabs = tabs;
        tabs.addTab("Full Equation",            fullRedoxTab());
        tabs.addTab("Oxidation States",         oxStatesTab());
        tabs.addTab("Balance Half-Rxn (Acid)",  halfRxnTab(false));
        tabs.addTab("Balance Half-Rxn (Basic)", halfRxnTab(true));
        tabs.addTab("Combine Half-Reactions",   combineTab());
        tabs.addTab("Galvanic Cell",            galvanicCellTab());
        tabs.addTab("Formal Charge",            formalChargeTab());
        inputPanel.add(tabs, BorderLayout.CENTER);
    }

    // ── Full redox balance ────────────────────────────────────────────────────

    private JPanel fullRedoxTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("Enter the skeleton equation with ALL key species but NO H₂O, H⁺, or OH⁻ — the balancer adds them automatically.<br>" +
                   "State symbols (aq), (s), (g), (l) are stripped. Include ionic charges where relevant (e.g. Fe2+, MnO4-).<br>" +
                   "Use spaces around + between species: <b>MnO4- + Fe2+</b> not <b>MnO4-+Fe2+</b>."), g);
        g.gridwidth = 1;

        JTextField rxnF = addRow(p, g, 1, "Skeleton equation:");
        rxnF.setFont(MONO_FONT);
        rxnF.setPreferredSize(new Dimension(380, 28));

        g.gridy = 2; g.gridx = 0; g.gridwidth = 2;
        p.add(hint("<b>Examples (acidic):</b><br>" +
                "&nbsp; H2SO4 + HI -> I2 + SO2 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; (sum = 7)<br>" +
                "&nbsp; MnO4- + Fe2+ -> Mn2+ + Fe3+ &nbsp;&nbsp; (1 MnO4⁻ : 5 Fe²⁺)<br>" +
                "&nbsp; Cr2O7 + I- -> Cr3+ + I2<br>" +
                "<b>Examples (basic):</b><br>" +
                "&nbsp; MnO4- + C2O4 -> MnO2 + CO2"), g);
        g.gridwidth = 1;

        String[] modes = {"Acidic solution  (adds H⁺ and H₂O)", "Basic solution  (adds OH⁻ and H₂O)"};
        JComboBox<String> modeBox = new JComboBox<>(modes);
        g.gridy = 3; g.gridwidth = 2; p.add(modeBox, g); g.gridwidth = 1;

        JButton btn = calcButton("Balance");
        btn.addActionListener(e -> {
            String rxn = rxnF.getText().trim();
            if (rxn.isEmpty()) { output("Enter a skeleton equation first."); return; }
            try {
                output(redox.balanceFullRedox(rxn, modeBox.getSelectedIndex() == 1));
            } catch (Exception ex) { output("Error: " + ex.getMessage()); }
        });
        rxnF.addActionListener(e -> btn.doClick());
        addCalcRow(p, g, 4, btn);
        return p;
    }

    // ── Oxidation states ──────────────────────────────────────────────────────

    private JPanel oxStatesTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("Rules applied: F=−1, O=−2, H=+1, Group I=+1, Group II=+2, sum = net charge"), g);
        g.gridwidth = 1;

        JTextField formF   = addRow(p, g, 1, "Formula:");
        JTextField chargeF = addRow(p, g, 2, "Net charge (0 = neutral):");
        chargeF.setText("0");

        g.gridy = 3; g.gridx = 0; g.gridwidth = 2;
        p.add(hint("Examples: &nbsp; H2O (0) &nbsp; | &nbsp; SO4 (−2) &nbsp; | &nbsp; MnO4 (−1) &nbsp; | &nbsp; Fe2O3 (0)"), g);
        g.gridwidth = 1;

        JButton btn = calcButton("Assign Ox. States");
        btn.addActionListener(e -> {
            try {
                String formula = formF.getText().trim();
                int charge = chargeF.getText().trim().isEmpty() ? 0
                        : Integer.parseInt(chargeF.getText().trim());
                output(redox.getOxStatesString(formula, charge).strip());
            } catch (NumberFormatException ex) {
                output("Error: charge must be an integer (e.g. -2, 0, +3).");
            }
        });
        formF.addActionListener(e -> btn.doClick());
        addCalcRow(p, g, 4, btn);
        return p;
    }

    // ── Half-reaction balancer ─────────────────────────────────────────────────

    private JPanel halfRxnTab(boolean basic) {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();
        String mode = basic ? "basic" : "acid";

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("Enter only the species — the program adds H₂O, H⁺" +
                (basic ? "/OH⁻" : "") + ", and e⁻ automatically."), g);
        g.gridwidth = 1;

        JTextField rxnF = addRow(p, g, 1, "Half-reaction:");
        ((JTextField) rxnF).setFont(MONO_FONT);
        ((JTextField) rxnF).setPreferredSize(new Dimension(340, 28));

        g.gridy = 2; g.gridx = 0; g.gridwidth = 2;
        p.add(hint("Examples: &nbsp; <b>MnO4 -> Mn2+</b> &nbsp; | &nbsp; " +
                "<b>Fe2+ -> Fe3+</b> &nbsp; | &nbsp; <b>Cr2O7 -> Cr3+</b>"), g);
        g.gridwidth = 1;

        JButton btn = calcButton("Balance (" + mode + ")");
        btn.addActionListener(e -> {
            String rxn = rxnF.getText().trim();
            if (rxn.isEmpty()) { output("Enter a half-reaction first."); return; }
            output(redox.balanceHalf(rxn, basic));
        });
        rxnF.addActionListener(e -> btn.doClick());
        addCalcRow(p, g, 3, btn);
        return p;
    }

    // ── Combine half-reactions ─────────────────────────────────────────────────

    private JPanel combineTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("Paste two already-balanced half-reactions. The program finds the LCM of electrons " +
                "and tells you the multipliers."), g);
        g.gridwidth = 1;

        g.gridy = 1; g.gridx = 0;
        p.add(lbl("Reduction half-reaction:"), g);
        JTextField redF = monoField();
        g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        p.add(redF, g);
        g.weightx = 0; g.fill = GridBagConstraints.NONE;

        g.gridy = 2; g.gridx = 0;
        p.add(lbl("Oxidation half-reaction:"), g);
        JTextField oxF = monoField();
        g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        p.add(oxF, g);
        g.weightx = 0; g.fill = GridBagConstraints.NONE;

        JButton btn = calcButton("Find Multipliers");
        btn.addActionListener(e -> {
            String red = redF.getText().trim();
            String ox  = oxF.getText().trim();
            if (red.isEmpty() || ox.isEmpty()) { output("Enter both half-reactions."); return; }

            int eRed = countElectrons(red, true);
            int eOx  = countElectrons(ox, false);

            if (eRed <= 0 || eOx <= 0) {
                output("Could not detect e⁻ in one of the reactions.\n" +
                       "Make sure the balanced half-reactions contain 'e-'.");
                return;
            }
            long lcm  = lcm(eRed, eOx);
            long mRed = lcm / eRed;
            long mOx  = lcm / eOx;
            output(String.format(
                "Electrons:\n  Reduction: %de⁻   Oxidation: %de⁻   LCM = %d\n\n" +
                "Multiply reduction  × %d\nMultiply oxidation  × %d\n\n" +
                "Then add and cancel %d e⁻ from both sides.\n\n" +
                "  %d × [ %s ]\n  %d × [ %s ]",
                eRed, eOx, lcm, mRed, mOx, lcm, mRed, red, mOx, ox));
        });
        addCalcRow(p, g, 3, btn);
        return p;
    }

    // ── Galvanic Cell ────────────────────────────────────────────────────────

    private JPanel galvanicCellTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("E°cell = E°cathode − E°anode  |  Higher E° reduction = cathode"), g);
        g.gridwidth = 1;

        JTextField name1F = addRow(p, g, 1, "Half-cell 1 name:");
        JTextField e1F    = addRow(p, g, 2, "E° reduction cell 1 (V):");
        JTextField name2F = addRow(p, g, 3, "Half-cell 2 name:");
        JTextField e2F    = addRow(p, g, 4, "E° reduction cell 2 (V):");
        JTextField nF     = addRow(p, g, 5, "Electrons transferred n:");

        JButton btn = calcButton("Calculate E°cell");
        btn.addActionListener(e -> {
            try {
                String n1 = name1F.getText().trim().isEmpty() ? "Cell 1" : name1F.getText().trim();
                String n2 = name2F.getText().trim().isEmpty() ? "Cell 2" : name2F.getText().trim();
                double e1 = parse(e1F), e2 = parse(e2F);
                int n = (int) parse(nF);
                String cathode, anode;
                double eCath, eAnod;
                if (e1 >= e2) { cathode = n1; eCath = e1; anode = n2; eAnod = e2; }
                else          { cathode = n2; eCath = e2; anode = n1; eAnod = e1; }
                double eCell = eCath - eAnod;
                double dG0   = -n * 96485.0 * eCell;
                double K     = Math.exp(-dG0 / (8.314 * 298.15));
                output(String.format(
                        "Galvanic Cell\n────────────────────────────────\n" +
                        "Cathode (reduction): %s  E° = %.4f V\n" +
                        "Anode   (oxidation): %s  E° = %.4f V\n\n" +
                        "E°cell = %.4f − %.4f = %.4f V\n%s\n\n" +
                        "ΔG° = −nFE° = %.2f kJ/mol\nK   = %.4e\n\n" +
                        "Electrons flow:  anode → (external circuit) → cathode",
                        cathode, eCath, anode, eAnod,
                        eCath, eAnod, eCell,
                        eCell > 0 ? "Spontaneous (E°cell > 0)" : "Non-spontaneous (E°cell < 0)",
                        dG0 / 1000, K));
            } catch (NumberFormatException ex) { output("Error: enter valid numbers."); }
        });
        addCalcRow(p, g, 6, btn);
        return p;
    }

    // ── Formal Charge ────────────────────────────────────────────────────────

    private JPanel formalChargeTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("FC = V − L − B/2   |   V = valence e⁻,  L = lone-pair e⁻,  B = bonding e⁻ (2 per bond)"), g);
        g.gridwidth = 1;

        int N = 4;
        JTextField[] vF = new JTextField[N], lF = new JTextField[N], bF = new JTextField[N];
        for (int i = 0; i < N; i++) {
            g.gridx = 0; g.gridy = i + 1; g.weightx = 0; g.fill = GridBagConstraints.NONE;
            p.add(lbl("Atom " + (i+1) + "  V:"), g);
            vF[i] = field(); vF[i].setPreferredSize(new Dimension(55, 28));
            g.gridx = 1; g.weightx = 0.3; g.fill = GridBagConstraints.HORIZONTAL;
            p.add(vF[i], g);
            g.gridx = 2; g.weightx = 0; g.fill = GridBagConstraints.NONE;
            p.add(lbl("  L:"), g);
            lF[i] = field(); lF[i].setPreferredSize(new Dimension(55, 28));
            g.gridx = 3; g.weightx = 0.3; g.fill = GridBagConstraints.HORIZONTAL;
            p.add(lF[i], g);
            g.gridx = 4; g.weightx = 0; g.fill = GridBagConstraints.NONE;
            p.add(lbl("  B:"), g);
            bF[i] = field(); bF[i].setPreferredSize(new Dimension(55, 28));
            g.gridx = 5; g.weightx = 0.3; g.fill = GridBagConstraints.HORIZONTAL;
            p.add(bF[i], g);
        }

        JTextField[] _v = vF, _l = lF, _b = bF;
        JButton btn = calcButton("Calculate Formal Charges");
        btn.addActionListener(e -> {
            try {
                StringBuilder sb = new StringBuilder("Formal Charge  FC = V − L − B/2\n──────────────────────────────\n");
                for (int i = 0; i < N; i++) {
                    if (_v[i].getText().trim().isEmpty()) continue;
                    double V = parse(_v[i]), L = parse(_l[i]), B = parse(_b[i]);
                    double fc = V - L - B / 2.0;
                    sb.append(String.format("Atom %d:  FC = %.0f − %.0f − %.0f/2 = %+.1f  (%s)%n",
                            i + 1, V, L, B, fc,
                            fc == 0 ? "neutral" : fc > 0 ? "positive" : "negative"));
                }
                output(sb.toString());
            } catch (NumberFormatException ex) { output("Error: enter valid numbers."); }
        });
        g.gridx = 0; g.gridy = N + 1; g.gridwidth = 6;
        g.anchor = GridBagConstraints.CENTER; g.fill = GridBagConstraints.NONE;
        p.add(btn, g);
        g.gridwidth = 1; g.anchor = GridBagConstraints.WEST;
        return p;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private int countElectrons(String rxn, boolean onLHS) {
        String[] sides = rxn.split("->");
        if (sides.length < 2) return 0;
        String target = onLHS ? sides[0] : sides[1];
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("([0-9]*)\\s*e-").matcher(target);
        if (m.find()) {
            String n = m.group(1);
            return n.isEmpty() ? 1 : Integer.parseInt(n);
        }
        return 0;
    }

    private long gcd(long a, long b) { return b == 0 ? a : gcd(b, a % b); }
    private long lcm(long a, long b) { return a / gcd(a, b) * b; }
}
