package gui;

import calculators.KineticsCalc;

import javax.swing.*;
import java.awt.*;

public class KineticsPanel extends BaseCalcPanel {

    private static final double R = 8.314;

    public KineticsPanel() {
        super("Kinetics");
    }

    @Override
    protected void buildUI() {
        inputPanel.setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(TAB_FONT);
        mainTabs = tabs;
        tabs.addTab("Arrhenius",         arrheniusTab());
        tabs.addTab("Find Eₐ",           findEaTab());
        tabs.addTab("Integrated Rate Law", integratedTab());
        tabs.addTab("Half-life",          halfLifeTab());
        tabs.addTab("Rate = k[A]ᵐ[B]ⁿ",  rateTab());
        tabs.addTab("Graham's Law",       grahamsTab());
        tabs.addTab("Order from Data",    reactionOrderTab());
        inputPanel.add(tabs, BorderLayout.CENTER);
    }

    // ── Arrhenius ─────────────────────────────────────────────────────────────

    private JPanel arrheniusTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("k = A·exp(−Eₐ/RT)"), g);
        g.gridwidth = 1;

        String[] modes = {"Given A and Eₐ → find k at T", "Given k₁ at T₁ → find k₂ at T₂"};
        JComboBox<String> mode = new JComboBox<>(modes);
        g.gridy = 1; g.gridwidth = 2;
        p.add(mode, g);
        g.gridwidth = 1;

        JTextField aF  = addRow(p, g, 2, "A (pre-exponential factor):");
        JTextField eaF = addRow(p, g, 3, "Eₐ (kJ/mol):");
        JTextField k1F = addRow(p, g, 4, "k₁:");
        JTextField t1F = addRow(p, g, 5, "T₁ (K):");
        JTextField t2F = addRow(p, g, 6, "T₂ (K):");

        mode.addActionListener(e -> {
            boolean useA = mode.getSelectedIndex() == 0;
            aF.setEnabled(useA); k1F.setEnabled(!useA);
        });
        aF.setEnabled(true); k1F.setEnabled(false);

        calcBtn(p, g, 7, "Calculate", () -> {
            if (mode.getSelectedIndex() == 0) {
                double A  = parse(aF);
                double Ea = parse(eaF) * 1000;
                double T  = parse(t2F);
                double k  = A * Math.exp(-Ea / (R * T));
                output(String.format("Arrhenius: k = A·exp(−Eₐ/RT)\n──────────────────────────\n" +
                    "A    = %.4e\nEₐ   = %.4f kJ/mol\nT    = %.2f K\nEₐ/RT = %.4f\nk    = %.6e",
                    A, Ea / 1000, T, Ea / (R * T), k));
            } else {
                double Ea = parse(eaF) * 1000;
                double k1 = parse(k1F), T1 = parse(t1F), T2 = parse(t2F);
                double k2 = k1 * Math.exp(-Ea / R * (1.0 / T2 - 1.0 / T1));
                output(String.format("Arrhenius: k₂ from k₁\n──────────────────────────\n" +
                    "k₁ = %.4e  at T₁ = %.2f K\nk₂ = %.4e  at T₂ = %.2f K\nln(k₂/k₁) = %.4f",
                    k1, T1, k2, T2, Math.log(k2 / k1)));
            }
        });
        return p;
    }

    // ── Find Eₐ ───────────────────────────────────────────────────────────────

    private JPanel findEaTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("ln(k₂/k₁) = −Eₐ/R · (1/T₂ − 1/T₁)"), g);
        g.gridwidth = 1;

        JTextField k1F = addRow(p, g, 1, "k₁:");
        JTextField t1F = addRow(p, g, 2, "T₁ (K):");
        JTextField k2F = addRow(p, g, 3, "k₂:");
        JTextField t2F = addRow(p, g, 4, "T₂ (K):");

        calcBtn(p, g, 5, "Find Eₐ", () -> {
            double k1 = parse(k1F), T1 = parse(t1F), k2 = parse(k2F), T2 = parse(t2F);
            double Ea = -R * Math.log(k2 / k1) / (1.0 / T2 - 1.0 / T1);
            double A  = k1 / Math.exp(-Ea / (R * T1));
            output(String.format("Activation Energy from Two Data Points\n──────────────────────────────────────\n" +
                "Eₐ = −R · ln(k₂/k₁) / (1/T₂ − 1/T₁)\n" +
                "Eₐ = %.4f J/mol  (%.4f kJ/mol)\n" +
                "A  = k₁/exp(−Eₐ/RT₁) = %.4e",
                Ea, Ea / 1000, A));
        });
        return p;
    }

    // ── Integrated rate law ───────────────────────────────────────────────────

    private JPanel integratedTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint(
            "0th: [A] = [A]₀ − kt &nbsp;&nbsp; " +
            "1st: [A] = [A]₀·e<sup>−kt</sup> &nbsp;&nbsp; " +
            "2nd: 1/[A] = 1/[A]₀ + kt"), g);
        g.gridwidth = 1;

        String[] orders  = {"0", "1", "2"};
        String[] solveFor = {"[A] (given [A]₀, k, t)", "t  (given [A]₀, [A], k)", "k  (given [A]₀, [A], t)"};
        JComboBox<String> orderCB = new JComboBox<>(orders);
        JComboBox<String> solveCB = new JComboBox<>(solveFor);

        g.gridy = 1; g.gridwidth = 2;
        p.add(orderCB, g);
        g.gridy = 2;
        p.add(solveCB, g);
        g.gridwidth = 1;

        JTextField a0F = addRow(p, g, 3, "[A]₀ (mol/L):");
        JTextField aF  = addRow(p, g, 4, "[A]  (mol/L)   (if needed):");
        JTextField kF  = addRow(p, g, 5, "k              (if needed):");
        JTextField tF  = addRow(p, g, 6, "t              (if needed):");

        calcBtn(p, g, 7, "Calculate", () -> {
            int ord  = orderCB.getSelectedIndex();  // 0,1,2
            int solv = solveCB.getSelectedIndex();  // 0=[A], 1=t, 2=k
            double A0 = parse(a0F);
            switch (solv) {
                case 0 -> {
                    double k = parse(kF), t = parse(tF);
                    double A = KineticsCalc.calcA(ord, A0, k, t);
                    output(String.format("Order %d — find [A]\n─────────────────────\n[A] = %.6e mol/L", ord, A));
                }
                case 1 -> {
                    double A = parse(aF), k = parse(kF);
                    double t = KineticsCalc.calcT(ord, A0, A, k);
                    output(String.format("Order %d — find t\n─────────────────────\nt = %.6e (same units as k)", ord, t));
                }
                case 2 -> {
                    double A = parse(aF), t = parse(tF);
                    double k = KineticsCalc.calcK(ord, A0, A, t);
                    output(String.format("Order %d — find k\n─────────────────────\nk = %.6e", ord, k));
                }
            }
        });
        return p;
    }

    // ── Half-life ─────────────────────────────────────────────────────────────

    private JPanel halfLifeTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint(
            "0th: t½ = [A]₀/(2k)  &nbsp; " +
            "1st: t½ = ln2/k  &nbsp; " +
            "2nd: t½ = 1/(k·[A]₀)"), g);
        g.gridwidth = 1;

        String[] orders = {"0th order", "1st order", "2nd order"};
        JComboBox<String> orderCB = new JComboBox<>(orders);
        g.gridy = 1; g.gridwidth = 2;
        p.add(orderCB, g);
        g.gridwidth = 1;

        JTextField kF  = addRow(p, g, 2, "k:");
        JTextField a0F = addRow(p, g, 3, "[A]₀ (mol/L)  (not needed for 1st order):");

        calcBtn(p, g, 4, "Calculate t½", () -> {
            int ord = orderCB.getSelectedIndex();
            double k   = parse(kF);
            double A0  = a0F.getText().trim().isEmpty() ? 1 : parse(a0F);
            double t12 = switch (ord) {
                case 0 -> A0 / (2 * k);
                case 1 -> Math.log(2) / k;
                case 2 -> 1.0 / (k * A0);
                default -> Double.NaN;
            };
            StringBuilder sb = new StringBuilder(String.format(
                "%s half-life\n─────────────────────\nt½ = %.6e\n", orders[ord], t12));
            if (ord == 1) {
                sb.append("\nDecay table:\n  n half-lives   Fraction remaining\n");
                double frac = 1.0;
                for (int i = 0; i <= 6; i++) {
                    sb.append(String.format("  %-14d %.4f  (%.2f%%)\n", i, frac, frac * 100));
                    frac /= 2;
                }
            }
            output(sb.toString());
        });
        return p;
    }

    // ── Rate from rate law ─────────────────────────────────────────────────────

    private JPanel rateTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("rate = k·[A]<sup>m</sup>·[B]<sup>n</sup>·... &nbsp; Enter up to 3 reactants."), g);
        g.gridwidth = 1;

        JTextField kF = addRow(p, g, 1, "Rate constant k:");
        String[] letters = {"A", "B", "C"};
        JTextField[] concF  = new JTextField[3];
        JTextField[] orderF = new JTextField[3];
        for (int i = 0; i < 3; i++) {
            concF[i]  = addRow(p, g, i + 2, "[" + letters[i] + "] (mol/L):");
            g.gridx = 0; g.gridy = i + 2;
            // order field in col 3
            g.gridx = 2; g.weightx = 0; g.fill = GridBagConstraints.NONE;
            p.add(lbl("  order:"), g);
            orderF[i] = field(); orderF[i].setText("1"); orderF[i].setPreferredSize(new Dimension(55, 28));
            g.gridx = 3; g.weightx = 0.5; g.fill = GridBagConstraints.HORIZONTAL;
            p.add(orderF[i], g);
            g.weightx = 0; g.fill = GridBagConstraints.NONE;
        }

        JTextField[] _c = concF, _o = orderF;
        calcBtn(p, g, 5, "Calculate rate", () -> {
            double k    = parse(kF);
            double rate = k;
            StringBuilder sb = new StringBuilder(String.format(
                "rate = k·[A]ᵐ·[B]ⁿ·[C]ᵖ\n──────────────────────\nk = %.4e\n", k));
            for (int i = 0; i < 3; i++) {
                if (_c[i].getText().trim().isEmpty()) continue;
                double conc = parse(_c[i]);
                double ord  = parse(_o[i]);
                rate *= Math.pow(conc, ord);
                sb.append(String.format("[%s]^%.1f = (%.4e)^%.1f = %.4e\n",
                        letters[i], ord, conc, ord, Math.pow(conc, ord)));
            }
            sb.append(String.format("\nrate = %.6e mol/(L·s)", rate));
            output(sb.toString());
        });
        return p;
    }

    // ── Graham's Law ─────────────────────────────────────────────────────────

    private JPanel grahamsTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("rate₁/rate₂ = √(M₂/M₁)  —  lighter gas effuses faster"), g);
        g.gridwidth = 1;

        String[] modes = {"Find rate₁/rate₂  (given M₁ and M₂)", "Find unknown molar mass  (given ratio and M₁)"};
        JComboBox<String> mode = new JComboBox<>(modes);
        g.gridy = 1; g.gridwidth = 2;
        p.add(mode, g);
        g.gridwidth = 1;

        JTextField m1F    = addRow(p, g, 2, "Molar mass M₁ (g/mol):");
        JTextField m2F    = addRow(p, g, 3, "Molar mass M₂ (g/mol)  [leave blank if unknown]:");
        JTextField ratioF = addRow(p, g, 4, "rate₁/rate₂  [leave blank if unknown]:");

        calcBtn(p, g, 5, "Calculate", () -> {
            if (mode.getSelectedIndex() == 0) {
                double M1 = parse(m1F), M2 = parse(m2F);
                double ratio = Math.sqrt(M2 / M1);
                output(String.format("Graham's Law\n─────────────────────────\n" +
                        "M₁ = %.4f g/mol\nM₂ = %.4f g/mol\n\n" +
                        "rate₁/rate₂ = √(M₂/M₁) = √(%.4f) = %.4f\n\n%s effuses faster.",
                        M1, M2, M2 / M1, ratio,
                        ratio > 1 ? "Gas 1 (M₁)" : "Gas 2 (M₂)"));
            } else {
                double M1 = parse(m1F), r = parse(ratioF);
                double M2 = M1 * r * r;
                output(String.format("Graham's Law → Unknown Molar Mass\n─────────────────────────\n" +
                        "M₁ = %.4f g/mol\nrate₁/rate₂ = %.4f\n\n" +
                        "M₂ = M₁ × (rate₁/rate₂)² = %.4f × %.4f = %.4f g/mol",
                        M1, r, M1, r * r, M2));
            }
        });
        return p;
    }

    // ── Reaction order from experimental rate data ────────────────────────────

    private JPanel reactionOrderTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 4;
        p.add(hint("Enter rate data. Keep [B] constant in the pair used for [A] order, and vice versa."), g);
        g.gridwidth = 1;

        g.gridx = 1; g.gridy = 1; g.weightx = 0.33; g.fill = GridBagConstraints.HORIZONTAL;
        p.add(lbl("[A] (mol/L)"), g);
        g.gridx = 2; p.add(lbl("[B] (mol/L)"), g);
        g.gridx = 3; p.add(lbl("Rate"), g);
        g.weightx = 0; g.fill = GridBagConstraints.NONE;

        int N = 3;
        JTextField[] aF = new JTextField[N], bF = new JTextField[N], rF = new JTextField[N];
        for (int i = 0; i < N; i++) {
            g.gridx = 0; g.gridy = i + 2; g.weightx = 0; g.fill = GridBagConstraints.NONE;
            p.add(lbl("Exp " + (i + 1)), g);
            aF[i] = field();
            g.gridx = 1; g.weightx = 0.33; g.fill = GridBagConstraints.HORIZONTAL; p.add(aF[i], g);
            bF[i] = field();
            g.gridx = 2; p.add(bF[i], g);
            rF[i] = field();
            g.gridx = 3; p.add(rF[i], g);
            g.weightx = 0; g.fill = GridBagConstraints.NONE;
        }

        String[] pairOpts = {"Exp 1 & 2", "Exp 1 & 3", "Exp 2 & 3"};
        JComboBox<String> pairA = new JComboBox<>(pairOpts);
        JComboBox<String> pairB = new JComboBox<>(pairOpts);
        pairB.setSelectedIndex(1);

        g.gridx = 0; g.gridy = 5; g.gridwidth = 2; g.fill = GridBagConstraints.NONE;
        p.add(lbl("Pair for order of [A]:"), g);
        g.gridx = 2; g.gridwidth = 2; g.fill = GridBagConstraints.HORIZONTAL; p.add(pairA, g);
        g.gridx = 0; g.gridy = 6; g.gridwidth = 2; g.fill = GridBagConstraints.NONE;
        p.add(lbl("Pair for order of [B]:"), g);
        g.gridx = 2; g.gridwidth = 2; g.fill = GridBagConstraints.HORIZONTAL; p.add(pairB, g);
        g.gridwidth = 1; g.fill = GridBagConstraints.NONE;

        JTextField[] _a = aF, _b = bF, _r = rF;
        calcBtn(p, g, 7, "Determine Orders", () -> {
            double[][] d = new double[N][3];
            boolean[] ok = new boolean[N];
            for (int i = 0; i < N; i++) {
                if (!_a[i].getText().trim().isEmpty() && !_r[i].getText().trim().isEmpty()) {
                    d[i][0] = parse(_a[i]);
                    d[i][1] = _b[i].getText().trim().isEmpty() ? Double.NaN : parse(_b[i]);
                    d[i][2] = parse(_r[i]);
                    ok[i] = true;
                }
            }
            int[][] pairs = {{0, 1}, {0, 2}, {1, 2}};
            int[] pA = pairs[pairA.getSelectedIndex()];
            int[] pB = pairs[pairB.getSelectedIndex()];
            if (!ok[pA[0]] || !ok[pA[1]]) { output("Selected experiments for [A] order are incomplete."); return; }

            StringBuilder sb = new StringBuilder(
                    "Reaction Order from Rate Data\nrate = k·[A]^x·[B]^y\n────────────────────────────────\n");

            double rRatioA = d[pA[1]][2] / d[pA[0]][2];
            double cRatioA = d[pA[1]][0] / d[pA[0]][0];
            double rawX    = Math.log(rRatioA) / Math.log(cRatioA);
            int x = (int) Math.round(rawX);
            sb.append(String.format("Order of [A] — Exp %d & %d:\n", pA[0]+1, pA[1]+1));
            sb.append(String.format("  rate%d/rate%d = %.4e/%.4e = %.4f%n", pA[1]+1, pA[0]+1, d[pA[1]][2], d[pA[0]][2], rRatioA));
            sb.append(String.format("  [A]%d/[A]%d  = %.4f/%.4f = %.4f%n", pA[1]+1, pA[0]+1, d[pA[1]][0], d[pA[0]][0], cRatioA));
            sb.append(String.format("  x = ln(%.4f)/ln(%.4f) = %.4f  →  x = %d%n%n", rRatioA, cRatioA, rawX, x));

            int y = 0;
            boolean hasB = ok[pB[0]] && ok[pB[1]]
                    && !Double.isNaN(d[pB[0]][1]) && !Double.isNaN(d[pB[1]][1]);
            if (hasB) {
                double rRatioB = d[pB[1]][2] / d[pB[0]][2];
                double cRatioB = d[pB[1]][1] / d[pB[0]][1];
                double rawY    = Math.log(rRatioB) / Math.log(cRatioB);
                y = (int) Math.round(rawY);
                sb.append(String.format("Order of [B] — Exp %d & %d:\n", pB[0]+1, pB[1]+1));
                sb.append(String.format("  rate ratio = %.4f%n  [B] ratio  = %.4f%n", rRatioB, cRatioB));
                sb.append(String.format("  y = ln(%.4f)/ln(%.4f) = %.4f  →  y = %d%n%n", rRatioB, cRatioB, rawY, y));
            }

            final int fx = x, fy = y;
            for (int i = 0; i < N; i++) {
                if (ok[i]) {
                    double bVal = Double.isNaN(d[i][1]) ? 1.0 : d[i][1];
                    double k = d[i][2] / (Math.pow(d[i][0], fx) * Math.pow(bVal, fy));
                    sb.append(String.format("k from Exp %d:%n  k = %.4e / (%.4f^%d × %.4f^%d) = %.6e%n%n",
                            i+1, d[i][2], d[i][0], fx, bVal, fy, k));
                    break;
                }
            }
            sb.append(String.format("Rate law:  rate = k·[A]^%d·[B]^%d%nOverall order = %d", fx, fy, fx + fy));
            output(sb.toString());
        });
        return p;
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private void calcBtn(JPanel p, GridBagConstraints g, int row, String label, Runnable action) {
        JButton btn = calcButton(label);
        btn.addActionListener(e -> {
            try { action.run(); }
            catch (NumberFormatException ex) { output("Error: enter valid numbers."); }
        });
        addCalcRow(p, g, row, btn);
    }
}
