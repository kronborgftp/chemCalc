package gui;

import calculators.PHCalculator;

import javax.swing.*;
import java.awt.*;

public class EquilibriumPanel extends BaseCalcPanel {

    private static final double R_ATM = 0.08206; // L·atm/(mol·K)

    public EquilibriumPanel() {
        super("Equilibrium");
    }

    @Override
    protected void buildUI() {
        inputPanel.setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(TAB_FONT);
        tabs.addTab("ICE — Weak Acid",   iceAcidTab());
        tabs.addTab("ICE — Weak Base",   iceBaseTab());
        tabs.addTab("ICE — General",     iceGenTab());
        tabs.addTab("Kc from [conc]",    kcTab());
        tabs.addTab("Kp ↔ Kc",          kpKcTab());
        tabs.addTab("Q vs K direction",  qTab());
        tabs.addTab("Ksp ↔ solubility",  kspTab());
        tabs.addTab("Osmosis",           osmosisTab());
        inputPanel.add(tabs, BorderLayout.CENTER);
    }

    // ── ICE weak acid ─────────────────────────────────────────────────────────

    private JPanel iceAcidTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("HA  ⇌  H⁺  +  A⁻     Ka = x² / (C − x)"), g);
        g.gridwidth = 1;
        JTextField kaF = addRow(p, g, 1, "Ka:");
        JTextField cF  = addRow(p, g, 2, "Initial [HA] (mol/L):");
        calcBtn(p, g, 3, "Solve ICE Table", () -> {
            double Ka = parse(kaF), C = parse(cF);
            double x  = PHCalculator.solveWeakAcidQuadratic(Ka, C);
            double pH = -Math.log10(x);
            double pct = x / C * 100;
            String rule = pct < 5 ? "5% rule holds" : "[!] 5% rule fails — quadratic value is exact";
            output(String.format(
                "ICE Table:  HA  ⇌  H⁺  +  A⁻\n" +
                "────────────────────────────────────────\n" +
                "         [HA]        [H⁺]       [A⁻]\n" +
                "I:   %.4e      0          0\n" +
                "C:   −x             +x         +x\n" +
                "E:   %.4e   %.4e  %.4e\n\n" +
                "Ka check: x²/(C−x) = %.4e  (Ka = %.4e)\n" +
                "pH         = %.4f\n" +
                "%% ionised  = %.2f%%\n%s",
                C, C - x, x, x, x * x / (C - x), Ka, pH, pct, rule));
        });
        return p;
    }

    // ── ICE weak base ─────────────────────────────────────────────────────────

    private JPanel iceBaseTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("B  +  H₂O  ⇌  BH⁺  +  OH⁻     Kb = x² / (C − x)"), g);
        g.gridwidth = 1;
        JTextField kbF = addRow(p, g, 1, "Kb:");
        JTextField cF  = addRow(p, g, 2, "Initial [B] (mol/L):");
        calcBtn(p, g, 3, "Solve ICE Table", () -> {
            double Kb  = parse(kbF), C = parse(cF);
            double x   = PHCalculator.solveWeakAcidQuadratic(Kb, C);
            double pOH = -Math.log10(x);
            double pH  = 14 - pOH;
            double pct = x / C * 100;
            output(String.format(
                "ICE Table:  B  +  H₂O  ⇌  BH⁺  +  OH⁻\n" +
                "──────────────────────────────────────────\n" +
                "         [B]         [BH⁺]     [OH⁻]\n" +
                "I:   %.4e      0          0\n" +
                "E:   %.4e   %.4e  %.4e\n\n" +
                "pOH = %.4f   pH = %.4f\n%% ionised = %.2f%%\n%s",
                C, C - x, x, x, pOH, pH, pct,
                pct < 5 ? "5% rule holds" : "[!] 5% rule fails"));
        });
        return p;
    }

    // ── ICE general ───────────────────────────────────────────────────────────

    private JPanel iceGenTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("A  ⇌  b·B   &nbsp; K = (b·x)^b / (C − x) &nbsp; solved for b = 1 or 2; " +
                "small-x approx. otherwise."), g);
        g.gridwidth = 1;
        JTextField kF  = addRow(p, g, 1, "K:");
        JTextField cF  = addRow(p, g, 2, "Initial [A] (mol/L):");
        JTextField bF  = addRow(p, g, 3, "Stoich. coeff. of product (b):");
        calcBtn(p, g, 4, "Solve", () -> {
            double K  = parse(kF), C = parse(cF), b = parse(bF);
            double x;
            String method;
            if (Math.abs(b - 1) < 1e-9) {
                x = K * C / (1 + K); method = "exact (b=1)";
            } else if (Math.abs(b - 2) < 1e-9) {
                x = (-K + Math.sqrt(K * K + 16 * K * C)) / 8.0; method = "quadratic (b=2)";
            } else {
                x = Math.pow(K * C, 1.0 / b) / b; method = "small-x approximation";
            }
            output(String.format(
                "ICE: A  ⇌  %.0f·B   (%s)\n──────────────────────────\n" +
                "x     = %.6e mol/L\n[A]_eq = %.6e mol/L\n[B]_eq = %.6e mol/L\n" +
                "x/C   = %.2f%%",
                b, method, x, C - x, b * x, x / C * 100));
        });
        return p;
    }

    // ── Kc from concentrations ────────────────────────────────────────────────

    private JPanel kcTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("Kc = [products]^coeff / [reactants]^coeff  &nbsp; (omit pure solids/liquids)"), g);
        g.gridwidth = 1;

        int N = 3;
        JTextField[][] cF = new JTextField[2][N], coF = new JTextField[2][N];
        for (int side = 0; side < 2; side++) {
            String name = side == 0 ? "Product" : "Reactant";
            g.gridx = 0; g.gridy = side * (N + 1) + 1; g.gridwidth = 2;
            p.add(lbl(name + "s:"), g);
            g.gridwidth = 1;
            for (int i = 0; i < N; i++) {
                int row = side * (N + 1) + i + 2;
                cF[side][i]  = addRow(p, g, row, "  [" + name.charAt(0) + (i+1) + "] (mol/L):");
                g.gridx = 2; g.gridy = row; g.weightx = 0; g.fill = GridBagConstraints.NONE;
                p.add(lbl("  coeff:"), g);
                coF[side][i] = field(); coF[side][i].setText("1");
                coF[side][i].setPreferredSize(new Dimension(55, 28));
                g.gridx = 3; g.weightx = 0.4; g.fill = GridBagConstraints.HORIZONTAL;
                p.add(coF[side][i], g);
                g.weightx = 0; g.fill = GridBagConstraints.NONE;
            }
        }

        JTextField[][] _c = cF, _co = coF;
        calcBtn(p, g, 2 * (N + 1) + 1, "Calculate Kc", () -> {
            double num = 1, den = 1;
            for (int i = 0; i < N; i++) {
                if (!_c[0][i].getText().isEmpty()) num *= Math.pow(parse(_c[0][i]), parse(_co[0][i]));
                if (!_c[1][i].getText().isEmpty()) den *= Math.pow(parse(_c[1][i]), parse(_co[1][i]));
            }
            double Kc = num / den;
            output(String.format("Kc from equilibrium concentrations\n──────────────────────────────────\n" +
                "Kc    = %.6e\npKc   = %.4f\nln Kc = %.4f", Kc, -Math.log10(Kc), Math.log(Kc)));
        });
        return p;
    }

    // ── Kp ↔ Kc ──────────────────────────────────────────────────────────────

    private JPanel kpKcTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("Kp = Kc·(RT)^Δn &nbsp; R = 0.08206 L·atm/(mol·K) &nbsp; Δn = Δ(moles gas)"), g);
        g.gridwidth = 1;

        String[] modes = {"Kc → Kp", "Kp → Kc"};
        JComboBox<String> mode = new JComboBox<>(modes);
        g.gridy = 1; g.gridwidth = 2;
        p.add(mode, g);
        g.gridwidth = 1;

        JTextField tF  = addRow(p, g, 2, "T (K):");
        JTextField dnF = addRow(p, g, 3, "Δn (moles gas products − reactants):");
        JTextField valF = addRow(p, g, 4, "Kc  or  Kp:");
        calcBtn(p, g, 5, "Convert", () -> {
            double T   = parse(tF), dn = parse(dnF), val = parse(valF);
            double RT  = R_ATM * T;
            if (mode.getSelectedIndex() == 0) {
                double Kp = val * Math.pow(RT, dn);
                output(String.format("Kc → Kp\n──────────────────\nKp = Kc·(RT)^Δn = %.4e · (%.4f)^%.1f = %.6e",
                        val, RT, dn, Kp));
            } else {
                double Kc = val / Math.pow(RT, dn);
                output(String.format("Kp → Kc\n──────────────────\nKc = Kp/(RT)^Δn = %.4e / (%.4f)^%.1f = %.6e",
                        val, RT, dn, Kc));
            }
        });
        return p;
    }

    // ── Q vs K ────────────────────────────────────────────────────────────────

    private JPanel qTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("Q uses current (non-equilibrium) concentrations. Compare to K to predict direction."), g);
        g.gridwidth = 1;

        JTextField kF  = addRow(p, g, 1, "K (equilibrium constant):");
        JTextField numF = addRow(p, g, 2, "Product conc. term  (numerator of Q):");
        JTextField denF = addRow(p, g, 3, "Reactant conc. term (denominator of Q):");
        g.gridx = 0; g.gridy = 4; g.gridwidth = 2;
        p.add(hint("Tip: enter each species raised to its coefficient before multiplying, " +
                "e.g. [CO2]^1 × [H2O]^1 = product of those values."), g);
        g.gridwidth = 1;

        calcBtn(p, g, 5, "Compare Q to K", () -> {
            double K = parse(kF), num = parse(numF), den = parse(denF), Q = num / den;
            String dir;
            if (Q < K) dir = "Q < K  →  reaction proceeds FORWARD (makes more products)";
            else if (Q > K) dir = "Q > K  →  reaction proceeds REVERSE (makes more reactants)";
            else dir = "Q = K  →  system is at equilibrium";
            output(String.format("Reaction Quotient\n─────────────────────\nQ = %.6e\nK = %.6e\n\n%s", Q, K, dir));
        });
        return p;
    }

    // ── Ksp ↔ solubility ──────────────────────────────────────────────────────

    private JPanel kspTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("MₘXₙ → m·M^(n+) + n·X^(m−) &nbsp; Ksp = mᵐ · nⁿ · s^(m+n)"), g);
        g.gridwidth = 1;

        String[] modes = {"Ksp → molar solubility s", "s → Ksp"};
        JComboBox<String> mode = new JComboBox<>(modes);
        g.gridy = 1; g.gridwidth = 2;
        p.add(mode, g);
        g.gridwidth = 1;

        JTextField mF   = addRow(p, g, 2, "m (coeff. of cation):");
        JTextField nF   = addRow(p, g, 3, "n (coeff. of anion):");
        JTextField valF = addRow(p, g, 4, "Ksp  or  s (mol/L):");
        calcBtn(p, g, 5, "Calculate", () -> {
            double m = parse(mF), n = parse(nF), val = parse(valF);
            double exp = m + n;
            double pre = Math.pow(m, m) * Math.pow(n, n);
            if (mode.getSelectedIndex() == 0) {
                double s = Math.pow(val / pre, 1.0 / exp);
                output(String.format("Ksp → Molar Solubility\n──────────────────────\n" +
                    "s = (Ksp / (m^m · n^n))^(1/(m+n))\ns = %.6e mol/L", s));
            } else {
                double Ksp = pre * Math.pow(val, exp);
                output(String.format("Molar Solubility → Ksp\n──────────────────────\n" +
                    "Ksp = m^m · n^n · s^(m+n)\nKsp = %.6e", Ksp));
            }
        });
        return p;
    }

    // ── Osmosis ───────────────────────────────────────────────────────────────

    private JPanel osmosisTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("π = i·c·R·T  &nbsp;  R = 0.08206 L·atm/(mol·K)  &nbsp; i = van't Hoff factor"), g);
        g.gridwidth = 1;

        String[] modes = {"π → c  (find concentration)", "c → π  (find osmotic pressure)",
                          "Molar mass from osmometry"};
        JComboBox<String> mode = new JComboBox<>(modes);
        g.gridy = 1; g.gridwidth = 2;
        p.add(mode, g);
        g.gridwidth = 1;

        JTextField iF  = addRow(p, g, 2, "i (van't Hoff factor, 1 = non-electrolyte):");
        JTextField tF  = addRow(p, g, 3, "T (K):");
        JTextField piF = addRow(p, g, 4, "π osmotic pressure (atm)  [blank if solving]:");
        JTextField cF  = addRow(p, g, 5, "c concentration (mol/L)  [blank if solving]:");
        JTextField massF = addRow(p, g, 6, "Solute mass (g)  [only for molar mass mode]:");
        JTextField volF  = addRow(p, g, 7, "Solution volume (L)  [only for molar mass mode]:");
        iF.setText("1");
        tF.setText("298.15");

        g.gridy = 8; g.gridx = 0; g.gridwidth = 2;
        p.add(hint("i = 1 (glucose), i = 2 (NaCl), i = 3 (CaCl₂, MgSO₄), i = 4 (AlCl₃)"), g);
        g.gridwidth = 1;

        calcBtn(p, g, 9, "Calculate", () -> {
            double iVal = parse(iF);
            double T    = parse(tF);
            double R    = R_ATM;
            switch (mode.getSelectedIndex()) {
                case 0 -> {
                    double pi = parse(piF);
                    double c  = pi / (iVal * R * T);
                    output(String.format(
                        "Osmosis: π = i·c·R·T  →  find c\n" +
                        "──────────────────────────────────\n" +
                        "π   = %.4f atm\ni   = %.2f\nT   = %.2f K\n\n" +
                        "c = π / (i·R·T) = %.4f / (%.2f × %.5f × %.2f)\n" +
                        "c = %.6f mol/L",
                        pi, iVal, T, pi, iVal, R, T, c));
                }
                case 1 -> {
                    double c  = parse(cF);
                    double pi = iVal * c * R * T;
                    output(String.format(
                        "Osmosis: π = i·c·R·T  →  find π\n" +
                        "──────────────────────────────────\n" +
                        "c   = %.4f mol/L\ni   = %.2f\nT   = %.2f K\n\n" +
                        "π = i·c·R·T = %.2f × %.4f × %.5f × %.2f\n" +
                        "π = %.4f atm  (= %.2f kPa)",
                        c, iVal, T, iVal, c, R, T, pi, pi * 101.325));
                }
                case 2 -> {
                    double pi   = parse(piF);
                    double mass = parse(massF);
                    double vol  = parse(volF);
                    double c    = pi / (iVal * R * T);
                    double M    = mass / (c * vol);
                    output(String.format(
                        "Osmometry: molar mass from π\n" +
                        "──────────────────────────────────\n" +
                        "π       = %.4f atm\ni       = %.2f\nT       = %.2f K\n" +
                        "mass    = %.4f g\nvolume  = %.4f L\n\n" +
                        "c = π/(i·R·T) = %.6f mol/L\n" +
                        "M = mass/(c·V) = %.4f / (%.6f × %.4f)\n" +
                        "M = %.2f g/mol",
                        pi, iVal, T, mass, vol, c, mass, c, vol, M));
                }
            }
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
