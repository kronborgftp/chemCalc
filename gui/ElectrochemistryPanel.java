package gui;

import javax.swing.*;
import java.awt.*;

public class ElectrochemistryPanel extends BaseCalcPanel {

    private static final double F_CONST = 96485.0; // C/mol
    private static final double R       = 8.314;   // J/(mol·K)

    public ElectrochemistryPanel() {
        super("Electrochemistry");
    }

    @Override
    protected void buildUI() {
        inputPanel.setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(TAB_FONT);
        mainTabs = tabs;
        tabs.addTab("E°cell",          ecellTab());
        tabs.addTab("Nernst Equation", nernstTab());
        tabs.addTab("ΔG° / K / E°",   triangleTab());
        tabs.addTab("Faraday's Law",   faradayTab());
        tabs.addTab("Concentration Cell", concCellTab());
        tabs.addTab("Ksp from E°",        kspFromETab());
        inputPanel.add(tabs, BorderLayout.CENTER);
    }

    // ── Tab 1: E°cell ─────────────────────────────────────────────────────────

    private JPanel ecellTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("E°cell = E°cathode − E°anode  &nbsp; (both as standard reduction potentials)"), g);
        g.gridwidth = 1;

        JTextField catF = addRow(p, g, 1, "E°cathode — reduction potential (V):");
        JTextField anoF = addRow(p, g, 2, "E°anode  — reduction potential (V):");
        JTextField nF   = addRow(p, g, 3, "n (electrons transferred):");

        g.gridy = 4; g.gridx = 0; g.gridwidth = 2;
        p.add(hint("Example: Zn/Cu cell → cathode Cu²⁺/Cu = +0.34 V, anode Zn²⁺/Zn = −0.76 V, n = 2"), g);
        g.gridwidth = 1;

        calcBtn(p, g, 5, "Calculate E°cell", () -> {
            double ec   = parse(catF);
            double ea   = parse(anoF);
            double n    = parse(nF);
            double Ecell = ec - ea;
            double T    = 298.15;
            double dG0  = -n * F_CONST * Ecell;
            double K    = Math.exp(n * F_CONST * Ecell / (R * T));
            String spont = Ecell > 1e-9 ? "Spontaneous  (E°cell > 0)"
                         : Ecell < -1e-9 ? "Non-spontaneous  (E°cell < 0)"
                         : "Equilibrium  (E°cell = 0)";
            output(String.format(
                "Galvanic Cell  E°cell = E°cathode − E°anode\n" +
                "──────────────────────────────────────────\n" +
                "E°cathode = %+.4f V  (reduction)\n" +
                "E°anode   = %+.4f V  (reduction)\n" +
                "E°cell    = %+.4f − (%+.4f) = %+.4f V\n\n" +
                "%s\n\n" +
                "At T = 298.15 K, n = %.0f:\n" +
                "ΔG°  = −nFE° = %+.2f kJ/mol\n" +
                "K    = exp(nFE°/RT) = %.4e\n" +
                "ln K = nFE°/RT = %.4f",
                ec, ea, ec, ea, Ecell, spont, n, dG0 / 1000, K, Math.log(K)));
        });
        return p;
    }

    // ── Tab 2: Nernst equation ────────────────────────────────────────────────

    private JPanel nernstTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("E = E° − (RT/nF)·ln Q  &nbsp;  At 25°C: E = E° − (0.05916/n)·log₁₀ Q"), g);
        g.gridwidth = 1;

        String[] modes = {"Find E   (given E°, Q)", "Find Q   (given E°, E)", "Find E°  (given E, Q)"};
        JComboBox<String> mode = new JComboBox<>(modes);
        g.gridy = 1; g.gridwidth = 2;
        p.add(mode, g);
        g.gridwidth = 1;

        JTextField e0F = addRow(p, g, 2, "E° (V):");
        JTextField nF  = addRow(p, g, 3, "n (electrons transferred):");
        JTextField tF  = addRow(p, g, 4, "T (K):");
        JTextField qF  = addRow(p, g, 5, "Q  (leave blank if solving for Q):");
        JTextField eF  = addRow(p, g, 6, "E (V)  (leave blank if solving for E):");
        tF.setText("298.15");

        calcBtn(p, g, 7, "Calculate", () -> {
            double e0   = parse(e0F);
            double n    = parse(nF);
            double T    = parse(tF);
            double RTnF = R * T / (n * F_CONST);
            switch (mode.getSelectedIndex()) {
                case 0 -> {
                    double Q = parse(qF);
                    double E = e0 - RTnF * Math.log(Q);
                    output(String.format(
                        "Nernst: E = E° − (RT/nF)·ln Q\n" +
                        "──────────────────────────────────────\n" +
                        "E°       = %+.4f V\nn        = %.0f\nT        = %.2f K\n" +
                        "RT/(nF)  = %.5f V\nQ        = %.4e\nln Q     = %.4f\n\n" +
                        "E = %.4f − %.5f × %.4f\nE = %+.4f V",
                        e0, n, T, RTnF, Q, Math.log(Q), e0, RTnF, Math.log(Q), E));
                }
                case 1 -> {
                    double E   = parse(eF);
                    double lnQ = (e0 - E) / RTnF;
                    double Q   = Math.exp(lnQ);
                    output(String.format(
                        "Nernst → Find Q\n" +
                        "──────────────────────────────────────\n" +
                        "E  = %+.4f V\nE° = %+.4f V\n" +
                        "ln Q = (E° − E)·nF/RT = %.4f\n" +
                        "Q    = %.4e",
                        E, e0, lnQ, Q));
                }
                case 2 -> {
                    double Q  = parse(qF);
                    double E  = parse(eF);
                    double e0calc = E + RTnF * Math.log(Q);
                    output(String.format(
                        "Nernst → Find E°\n" +
                        "──────────────────────────────────────\n" +
                        "E  = %+.4f V\nQ  = %.4e\n" +
                        "E° = E + (RT/nF)·ln Q = %+.4f V",
                        E, Q, e0calc));
                }
            }
        });
        return p;
    }

    // ── Tab 3: ΔG° / K / E° triangle ─────────────────────────────────────────

    private JPanel triangleTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("ΔG° = −nFE°  &nbsp;  ΔG° = −RT·ln K  &nbsp;  E° = RT·ln K / (nF)"), g);
        g.gridwidth = 1;

        String[] modes = {"E°  →  ΔG° and K", "ΔG°  →  E° and K", "K  →  E° and ΔG°"};
        JComboBox<String> mode = new JComboBox<>(modes);
        g.gridy = 1; g.gridwidth = 2;
        p.add(mode, g);
        g.gridwidth = 1;

        JTextField nF   = addRow(p, g, 2, "n (electrons transferred):");
        JTextField tF   = addRow(p, g, 3, "T (K):");
        JTextField valF = addRow(p, g, 4, "Value:  E° (V)  |  ΔG° (kJ/mol)  |  K:");
        tF.setText("298.15");

        calcBtn(p, g, 5, "Calculate", () -> {
            double n   = parse(nF);
            double T   = parse(tF);
            double val = parse(valF);
            switch (mode.getSelectedIndex()) {
                case 0 -> {
                    double dG0 = -n * F_CONST * val;
                    double K   = Math.exp(-dG0 / (R * T));
                    output(String.format(
                        "E° → ΔG° and K\n──────────────────────────────\n" +
                        "E°   = %+.4f V\nn    = %.0f\nT    = %.2f K\n\n" +
                        "ΔG°  = −nFE° = %+.4f kJ/mol\nK    = exp(nFE°/RT) = %.4e\nln K = %.4f",
                        val, n, T, dG0 / 1000, K, Math.log(K)));
                }
                case 1 -> {
                    double dG0 = val * 1000;
                    double e0  = -dG0 / (n * F_CONST);
                    double K   = Math.exp(-dG0 / (R * T));
                    output(String.format(
                        "ΔG° → E° and K\n──────────────────────────────\n" +
                        "ΔG°  = %+.4f kJ/mol\nn    = %.0f\nT    = %.2f K\n\n" +
                        "E°   = −ΔG°/(nF) = %+.4f V\nK    = exp(−ΔG°/RT) = %.4e\nln K = %.4f",
                        val, n, T, e0, K, Math.log(K)));
                }
                case 2 -> {
                    double dG0 = -R * T * Math.log(val);
                    double e0  = -dG0 / (n * F_CONST);
                    output(String.format(
                        "K → ΔG° and E°\n──────────────────────────────\n" +
                        "K    = %.4e\nn    = %.0f\nT    = %.2f K\n\n" +
                        "ΔG°  = −RT·ln K = %+.4f kJ/mol\nE°   = −ΔG°/(nF) = %+.4f V",
                        val, n, T, dG0 / 1000, e0));
                }
            }
        });
        return p;
    }

    // ── Tab 4: Faraday's law of electrolysis ──────────────────────────────────

    private JPanel faradayTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("m = M·I·t / (n·F)  &nbsp;  Q = I·t  &nbsp;  mol deposited = Q / (n·F)"), g);
        g.gridwidth = 1;

        String[] modes = {"Find mass deposited (m)", "Find time (t)", "Find current (I)"};
        JComboBox<String> mode = new JComboBox<>(modes);
        g.gridy = 1; g.gridwidth = 2;
        p.add(mode, g);
        g.gridwidth = 1;

        JTextField mF = addRow(p, g, 2, "Molar mass M (g/mol):");
        JTextField nF = addRow(p, g, 3, "n (electrons per ion,  e.g. Cu²⁺ = 2):");
        JTextField iF = addRow(p, g, 4, "Current I (A)  [blank if solving for I]:");
        JTextField tF = addRow(p, g, 5, "Time t (s)    [blank if solving for t]:");
        JTextField dF = addRow(p, g, 6, "Mass deposited m (g) [blank if solving for m]:");

        calcBtn(p, g, 7, "Calculate", () -> {
            double M  = parse(mF);
            double n  = parse(nF);
            switch (mode.getSelectedIndex()) {
                case 0 -> {
                    double I = parse(iF), t = parse(tF);
                    double Q   = I * t;
                    double mol = Q / (n * F_CONST);
                    double dep = mol * M;
                    output(String.format(
                        "Faraday's Law — mass deposited\n" +
                        "──────────────────────────────────\n" +
                        "Q   = I·t = %.4f × %.4f = %.4f C\n" +
                        "mol = Q / (n·F) = %.4f / (%.0f × 96485) = %.6f mol\n" +
                        "m   = mol·M = %.6f × %.4f = %.4f g",
                        I, t, Q, Q, n, mol, mol, M, dep));
                }
                case 1 -> {
                    double I = parse(iF), dep = parse(dF);
                    double mol = dep / M;
                    double Q   = mol * n * F_CONST;
                    double t   = Q / I;
                    output(String.format(
                        "Faraday's Law — time required\n" +
                        "──────────────────────────────────\n" +
                        "mol = m/M = %.4f / %.4f = %.6f mol\n" +
                        "Q   = mol·n·F = %.6f × %.0f × 96485 = %.4f C\n" +
                        "t   = Q/I = %.4f / %.4f = %.2f s  (%.2f min)",
                        dep, M, mol, mol, n, Q, Q, I, t, t / 60));
                }
                case 2 -> {
                    double t = parse(tF), dep = parse(dF);
                    double mol = dep / M;
                    double Q   = mol * n * F_CONST;
                    double I   = Q / t;
                    output(String.format(
                        "Faraday's Law — current required\n" +
                        "──────────────────────────────────\n" +
                        "mol = m/M = %.4f / %.4f = %.6f mol\n" +
                        "Q   = mol·n·F = %.6f × %.0f × 96485 = %.4f C\n" +
                        "I   = Q/t = %.4f / %.4f = %.4f A",
                        dep, M, mol, mol, n, Q, Q, t, I));
                }
            }
        });
        return p;
    }

    // ── Tab 5: Concentration cell ─────────────────────────────────────────────

    private JPanel concCellTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("Concentration cell: E° = 0  →  E = −(RT/nF)·ln(c_anode / c_cathode)"), g);
        g.gridwidth = 1;

        JTextField nF  = addRow(p, g, 1, "n (electrons transferred):");
        JTextField tF  = addRow(p, g, 2, "T (K):");
        JTextField c1F = addRow(p, g, 3, "c_cathode — higher concentration (mol/L):");
        JTextField c2F = addRow(p, g, 4, "c_anode  — lower  concentration (mol/L):");
        tF.setText("298.15");

        g.gridy = 5; g.gridx = 0; g.gridwidth = 2;
        p.add(hint("Example: Cu cell with 1.0 M (cathode) and 0.010 M (anode), n = 2"), g);
        g.gridwidth = 1;

        calcBtn(p, g, 6, "Calculate Cell Potential", () -> {
            double n   = parse(nF);
            double T   = parse(tF);
            double cc  = parse(c1F);
            double ca  = parse(c2F);
            double Q   = ca / cc;
            double E   = -(R * T / (n * F_CONST)) * Math.log(Q);
            double dG  = -n * F_CONST * E;
            output(String.format(
                "Concentration Cell  (E° = 0)\n" +
                "──────────────────────────────────\n" +
                "c_cathode = %.4f mol/L\n" +
                "c_anode   = %.4f mol/L\n" +
                "Q = c_anode/c_cathode = %.4e\n\n" +
                "E = −(RT/nF)·ln Q\n  = −%.5f × %.4f\n  = %+.4f V\n\n" +
                "ΔG = −nFE = %+.2f kJ/mol\n" +
                "Direction: electrons flow from anode (%s M) to cathode (%s M)",
                cc, ca, Q, R * T / (n * F_CONST), Math.log(Q), E,
                dG / 1000,
                String.format("%.4f", ca), String.format("%.4f", cc)));
        });
        return p;
    }

    // ── Tab 6: Ksp from standard reduction potentials ────────────────────────
    // Lecture slide p.27: use two half-reactions sharing the same metal to
    // construct an E°cell for the dissolution MX(s) → M^n+ + X^n-,
    // then Ksp = exp(nFE°cell / RT).

    private JPanel kspFromETab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("<b>Method:</b> combine two half-reactions that share the same metal to get the dissolution reaction."), g);
        g.gridy = 1;
        p.add(hint("Rxn 1 (simple ion):    M^n⁺ + ne⁻  →  M(s)   &nbsp; E°red(1)"), g);
        g.gridy = 2;
        p.add(hint("Rxn 2 (insoluble salt): MX(s) + ne⁻  →  M(s) + X^n⁻  &nbsp; E°red(2)"), g);
        g.gridy = 3;
        p.add(hint("Dissolution: MX(s)  ⇌  M^n⁺ + X^n⁻  &nbsp; E°cell = E°red(2) − E°red(1)"), g);
        g.gridwidth = 1;

        JTextField e1F = addRow(p, g, 4, "E°red(1) — simple metal ion (V):");
        JTextField e2F = addRow(p, g, 5, "E°red(2) — insoluble salt (V):");
        JTextField nF  = addRow(p, g, 6, "n (electrons in each half-reaction):");
        JTextField tF  = addRow(p, g, 7, "T (K):");
        tF.setText("298.15");

        g.gridy = 8; g.gridx = 0; g.gridwidth = 2;
        p.add(hint("Example (AgCl): E°red(1) = 0.80 V  (Ag⁺ + e⁻ → Ag),  " +
                   "E°red(2) = 0.215 V  (AgCl + e⁻ → Ag + Cl⁻),  n = 1"), g);
        g.gridwidth = 1;

        calcBtn(p, g, 9, "Calculate Ksp", () -> {
            double e1  = parse(e1F);
            double e2  = parse(e2F);
            double n   = parse(nF);
            double T   = parse(tF);
            double Ecell = e2 - e1;
            double lnKsp = n * F_CONST * Ecell / (R * T);
            double Ksp   = Math.exp(lnKsp);
            double dG0   = -n * F_CONST * Ecell;
            String spont = Ecell > 0 ? "Spontaneous dissolution (unusual — very soluble)"
                         : "Non-spontaneous dissolution (sparingly soluble — as expected for Ksp << 1)";
            output(String.format(
                "Ksp from Standard Reduction Potentials\n" +
                "──────────────────────────────────────────\n" +
                "Dissolution:  MX(s)  ⇌  M^n⁺(aq)  +  X^n⁻(aq)\n\n" +
                "E°red(1) = %+.4f V   (M^n⁺ + ne⁻ → M)\n" +
                "E°red(2) = %+.4f V   (MX + ne⁻ → M + X^n⁻)\n" +
                "n        = %.0f\nT        = %.2f K\n\n" +
                "E°cell(dissolution) = E°red(2) − E°red(1)\n" +
                "                    = %+.4f − (%+.4f) = %+.4f V\n\n" +
                "ln Ksp = nFE°cell / RT = %.4f\n" +
                "Ksp    = exp(%.4f) = %.4e\n\n" +
                "ΔG°    = −nFE° = %+.2f kJ/mol\n\n%s",
                e1, e2, n, T, e2, e1, Ecell, lnKsp, lnKsp, Ksp, dG0 / 1000, spont));
        });
        return p;
    }

    // ── Helper ────────────────────────────────────────────────────────────────

}
