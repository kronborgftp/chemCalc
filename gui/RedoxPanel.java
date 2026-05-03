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
        tabs.addTab("Oxidation States",         oxStatesTab());
        tabs.addTab("Balance Half-Rxn (Acid)",  halfRxnTab(false));
        tabs.addTab("Balance Half-Rxn (Basic)", halfRxnTab(true));
        tabs.addTab("Combine Half-Reactions",   combineTab());
        inputPanel.add(tabs, BorderLayout.CENTER);
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
