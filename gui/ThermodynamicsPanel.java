package gui;

import javax.swing.*;
import java.awt.*;

public class ThermodynamicsPanel extends BaseCalcPanel {

    private static final double R = 8.314;

    public ThermodynamicsPanel() {
        super("Thermodynamics");
    }

    @Override
    protected void buildUI() {
        inputPanel.setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(TAB_FONT);
        tabs.addTab("ΔG = ΔH − TΔS",  gibbsTab());
        tabs.addTab("ΔG° ↔ K",        gibbsKTab());
        tabs.addTab("Non-std ΔG",      nonStdTab());
        tabs.addTab("Hess's Law",      hessTab());
        tabs.addTab("ΔH from ΔHf°",   dhfTab());
        tabs.addTab("Clausius-Clap.",  ccTab());
        inputPanel.add(tabs, BorderLayout.CENTER);
    }

    // ── ΔG = ΔH − TΔS ────────────────────────────────────────────────────────

    private JPanel gibbsTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();
        JTextField dhF = addRow(p, g, 0, "ΔH (kJ/mol):");
        JTextField dsF = addRow(p, g, 1, "ΔS (J/mol·K):");
        JTextField tF  = addRow(p, g, 2, "T (K):");
        calcBtn(p, g, 3, "Calculate ΔG", () -> {
            double dH = parse(dhF) * 1000;
            double dS = parse(dsF);
            double T  = parse(tF);
            double dG = dH - T * dS;
            String sp = dG < 0 ? "Spontaneous (ΔG < 0)" : dG > 0 ? "Non-spontaneous (ΔG > 0)" : "Equilibrium (ΔG = 0)";
            String cross = "";
            if (dH != 0 && dS != 0) {
                double Tx = dH / dS;
                cross = String.format("\nCrossover T = ΔH/ΔS = %.2f K (%.2f°C)", Tx, Tx - 273.15);
            }
            output(String.format("ΔG = ΔH − T·ΔS\n─────────────────────────\n" +
                "ΔH    = %+.4f kJ/mol\nT·ΔS  = %+.4f kJ/mol\nΔG    = %+.4f kJ/mol\n\n%s%s",
                dH / 1000, T * dS / 1000, dG / 1000, sp, cross));
        });
        return p;
    }

    // ── ΔG° ↔ K ──────────────────────────────────────────────────────────────

    private JPanel gibbsKTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        String[] modes = {"ΔG°  →  K", "K  →  ΔG°"};
        JComboBox<String> mode = new JComboBox<>(modes);
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(mode, g);
        g.gridwidth = 1;

        JTextField tF  = addRow(p, g, 1, "T (K):");
        JTextField valF = addRow(p, g, 2, "ΔG° (kJ/mol)  or  K:");

        calcBtn(p, g, 3, "Calculate", () -> {
            double T   = parse(tF);
            double val = parse(valF);
            if (mode.getSelectedIndex() == 0) {
                double dG0 = val * 1000;
                double K   = Math.exp(-dG0 / (R * T));
                output(String.format("ΔG° → K\n─────────────────────\n" +
                    "ΔG° = %+.4f kJ/mol\nT   = %.2f K\n" +
                    "K   = exp(%.4f) = %.6e\n\n%s",
                    val, T, -dG0 / (R * T), K,
                    K > 1 ? "K > 1: products favoured" : K < 1 ? "K < 1: reactants favoured" : "K = 1: neither favoured"));
            } else {
                double dG0 = -R * T * Math.log(val);
                output(String.format("K → ΔG°\n─────────────────────\n" +
                    "K   = %.6e\nT   = %.2f K\nΔG° = −RT·ln K = %+.4f kJ/mol",
                    val, T, dG0 / 1000));
            }
        });
        return p;
    }

    // ── Non-standard ΔG ───────────────────────────────────────────────────────

    private JPanel nonStdTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();
        JTextField dg0F = addRow(p, g, 0, "ΔG° (kJ/mol):");
        JTextField qF   = addRow(p, g, 1, "Q (reaction quotient):");
        JTextField tF   = addRow(p, g, 2, "T (K):");
        calcBtn(p, g, 3, "Calculate ΔG", () -> {
            double dG0   = parse(dg0F) * 1000;
            double Q     = parse(qF);
            double T     = parse(tF);
            double RTlnQ = R * T * Math.log(Q);
            double dG    = dG0 + RTlnQ;
            String dir   = dG < 0 ? "Proceeds forward" : dG > 0 ? "Proceeds reverse" : "At equilibrium (Q = K)";
            output(String.format("ΔG = ΔG° + RT·ln Q\n─────────────────────────\n" +
                "ΔG°     = %+.4f kJ/mol\nRT·ln Q = %+.4f kJ/mol\nΔG      = %+.4f kJ/mol\n\n%s",
                dG0 / 1000, RTlnQ / 1000, dG / 1000, dir));
        });
        return p;
    }

    // ── Hess's Law ─────────────────────────────────────────────────────────────

    private JPanel hessTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("Enter each reaction's ΔH and its multiplier. Use negative multiplier to reverse a reaction."), g);
        g.gridwidth = 1;

        int N = 4;
        JTextField[] dhF   = new JTextField[N];
        JTextField[] multF = new JTextField[N];

        for (int i = 0; i < N; i++) {
            g.gridx = 0; g.gridy = i + 1; g.weightx = 0; g.fill = GridBagConstraints.NONE;
            p.add(lbl("Rxn " + (i+1) + "  ΔH (kJ/mol):"), g);
            dhF[i] = field();
            g.gridx = 1; g.weightx = 0.6; g.fill = GridBagConstraints.HORIZONTAL;
            p.add(dhF[i], g);

            g.gridx = 2; g.weightx = 0; g.fill = GridBagConstraints.NONE;
            p.add(lbl(" × mult:"), g);
            multF[i] = field();
            multF[i].setText("1");
            multF[i].setPreferredSize(new Dimension(60, 28));
            g.gridx = 3; g.weightx = 0.4; g.fill = GridBagConstraints.HORIZONTAL;
            p.add(multF[i], g);
            g.weightx = 0; g.fill = GridBagConstraints.NONE;
        }

        JTextField[] _dh = dhF, _m = multF;
        calcBtn(p, g, N + 1, "Calculate ΔH total", () -> {
            double total = 0;
            StringBuilder sb = new StringBuilder("Hess's Law\n────────────────────────────\n");
            for (int i = 0; i < N; i++) {
                if (_dh[i].getText().trim().isEmpty()) continue;
                double dH   = parse(_dh[i]);
                double mult = _m[i].getText().trim().isEmpty() ? 1 : parse(_m[i]);
                double c    = dH * mult;
                sb.append(String.format("Rxn %d:  %+.4f × %.2f = %+.4f kJ/mol\n", i+1, dH, mult, c));
                total += c;
            }
            sb.append(String.format("─────────────────────\nΔH_total = %+.4f kJ/mol\n%s",
                    total, total < 0 ? "Exothermic" : "Endothermic"));
            output(sb.toString());
        });
        return p;
    }

    // ── ΔH from ΔHf° ─────────────────────────────────────────────────────────

    private JPanel dhfTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("ΔH°rxn = Σ (coeff × ΔHf°)_products  −  Σ (coeff × ΔHf°)_reactants"), g);
        g.gridwidth = 1;

        int N = 3;
        JTextField[][] cf = new JTextField[2][N]; // [0]=products, [1]=reactants
        JTextField[][] hf = new JTextField[2][N];

        for (int side = 0; side < 2; side++) {
            String sideName = side == 0 ? "Product" : "Reactant";
            g.gridx = 0; g.gridy = side * (N + 1) + 1; g.gridwidth = 2;
            p.add(lbl(sideName + "s (coeff  ×  ΔHf°):"), g);
            g.gridwidth = 1;
            for (int i = 0; i < N; i++) {
                int row = side * (N + 1) + i + 2;
                g.gridx = 0; g.gridy = row; g.weightx = 0; g.fill = GridBagConstraints.NONE;
                p.add(lbl("  #" + (i+1) + " coeff:"), g);
                cf[side][i] = field(); cf[side][i].setPreferredSize(new Dimension(60, 28));
                g.gridx = 1; g.weightx = 0.3; g.fill = GridBagConstraints.HORIZONTAL;
                p.add(cf[side][i], g);
                g.gridx = 2; g.weightx = 0; g.fill = GridBagConstraints.NONE;
                p.add(lbl("  ΔHf°:"), g);
                hf[side][i] = field();
                g.gridx = 3; g.weightx = 0.7; g.fill = GridBagConstraints.HORIZONTAL;
                p.add(hf[side][i], g);
                g.weightx = 0; g.fill = GridBagConstraints.NONE;
            }
        }

        JTextField[][] _cf = cf, _hf = hf;
        calcBtn(p, g, 2 * (N + 1) + 1, "Calculate ΔH°rxn", () -> {
            double sumP = 0, sumR = 0;
            for (int i = 0; i < N; i++) {
                if (!_cf[0][i].getText().isEmpty() && !_hf[0][i].getText().isEmpty())
                    sumP += parse(_cf[0][i]) * parse(_hf[0][i]);
                if (!_cf[1][i].getText().isEmpty() && !_hf[1][i].getText().isEmpty())
                    sumR += parse(_cf[1][i]) * parse(_hf[1][i]);
            }
            double dH = sumP - sumR;
            output(String.format("ΔH°rxn from Formation Enthalpies\n──────────────────────────────────\n" +
                "Σ ΔHf°(products)  = %+.4f kJ/mol\nΣ ΔHf°(reactants) = %+.4f kJ/mol\n" +
                "ΔH°rxn             = %+.4f kJ/mol\n\n%s",
                sumP, sumR, dH, dH < 0 ? "Exothermic" : "Endothermic"));
        });
        return p;
    }

    // ── Clausius-Clapeyron ────────────────────────────────────────────────────

    private JPanel ccTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("ln(P₂/P₁) = −ΔH_vap/R · (1/T₂ − 1/T₁)"), g);
        g.gridwidth = 1;

        String[] modes = {"Find P₂", "Find T₂", "Find ΔH_vap"};
        JComboBox<String> mode = new JComboBox<>(modes);
        g.gridy = 1; g.gridwidth = 2;
        p.add(mode, g);
        g.gridwidth = 1;

        JTextField dhF = addRow(p, g, 2, "ΔH_vap (kJ/mol):");
        JTextField p1F = addRow(p, g, 3, "P₁:");
        JTextField t1F = addRow(p, g, 4, "T₁ (K):");
        JTextField p2F = addRow(p, g, 5, "P₂ (leave blank if solving for P₂):");
        JTextField t2F = addRow(p, g, 6, "T₂ (K) (leave blank if solving for T₂):");

        calcBtn(p, g, 7, "Calculate", () -> {
            int sel = mode.getSelectedIndex();
            if (sel == 0) {
                double dH = parse(dhF) * 1000, P1 = parse(p1F), T1 = parse(t1F), T2 = parse(t2F);
                double P2 = P1 * Math.exp(-dH / R * (1.0 / T2 - 1.0 / T1));
                output(String.format("Clausius-Clapeyron → P₂\n──────────────────────\nP₂ = %.4f (same units as P₁)", P2));
            } else if (sel == 1) {
                double dH = parse(dhF) * 1000, P1 = parse(p1F), T1 = parse(t1F), P2 = parse(p2F);
                double invT2 = 1.0 / T1 - R / dH * Math.log(P2 / P1);
                double T2 = 1.0 / invT2;
                output(String.format("Clausius-Clapeyron → T₂\n──────────────────────\nT₂ = %.2f K  (%.2f°C)", T2, T2 - 273.15));
            } else {
                double P1 = parse(p1F), T1 = parse(t1F), P2 = parse(p2F), T2 = parse(t2F);
                double dH = -R * Math.log(P2 / P1) / (1.0 / T2 - 1.0 / T1);
                output(String.format("Clausius-Clapeyron → ΔH_vap\n──────────────────────────\nΔH_vap = %.4f J/mol  (%.4f kJ/mol)", dH, dH / 1000));
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
