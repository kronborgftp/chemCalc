package gui;

import calculators.PHCalculator;

import javax.swing.*;
import java.awt.*;

public class PHPanel extends BaseCalcPanel {

    private static final double Kw = 1e-14;

    public PHPanel() {
        super("pH / Acid-Base Calculator");
    }

    @Override
    protected void buildUI() {
        inputPanel.setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(TAB_FONT);
        mainTabs = tabs;
        tabs.addTab("Strong Acid",     strongAcidTab());
        tabs.addTab("Strong Base",     strongBaseTab());
        tabs.addTab("Weak Acid",       weakAcidTab());
        tabs.addTab("Weak Base",       weakBaseTab());
        tabs.addTab("Buffer",          bufferTab());
        tabs.addTab("Neutralisation",   neutralTab());
        tabs.addTab("Buffer + Titrant", bufferTitrantTab());
        inputPanel.add(tabs, BorderLayout.CENTER);
    }

    // ── Strong acid ───────────────────────────────────────────────────────────

    private JPanel strongAcidTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();
        JTextField cF = addRow(p, g, 0, "Concentration [H⁺] (mol/L):");
        g.gridy = 1;
        inputPanel(g, p, 1, () -> {
            double C   = parse(cF);
            double pH  = -Math.log10(C);
            double pOH = 14 - pH;
            output(String.format(
                "Strong Acid\n─────────────────────────\n" +
                "pH    = %.4f\npOH   = %.4f\n[H⁺]  = %.4e mol/L\n[OH⁻] = %.4e mol/L",
                pH, pOH, C, Kw / C));
        });
        return p;
    }

    // ── Strong base ───────────────────────────────────────────────────────────

    private JPanel strongBaseTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();
        JTextField cF = addRow(p, g, 0, "Concentration [OH⁻] (mol/L):");
        inputPanel(g, p, 1, () -> {
            double C   = parse(cF);
            double pOH = -Math.log10(C);
            double pH  = 14 - pOH;
            output(String.format(
                "Strong Base\n─────────────────────────\n" +
                "pOH   = %.4f\npH    = %.4f\n[OH⁻] = %.4e mol/L\n[H⁺]  = %.4e mol/L",
                pOH, pH, C, Kw / C));
        });
        return p;
    }

    // ── Weak acid ─────────────────────────────────────────────────────────────

    private JPanel weakAcidTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();
        JTextField kaF = addRow(p, g, 0, "Ka:");
        JTextField cF  = addRow(p, g, 1, "Initial [HA] (mol/L):");
        inputPanel(g, p, 2, () -> {
            double Ka  = parse(kaF);
            double C   = parse(cF);
            double x   = PHCalculator.solveWeakAcidQuadratic(Ka, C);
            double pH  = -Math.log10(x);
            double pct = x / C * 100;
            String rule = pct < 5 ? "5% rule holds — approx. valid" : "[!] 5% rule fails — quadratic exact";
            output(String.format(
                "Weak Acid   HA  ⇌  H⁺  +  A⁻\n" +
                "────────────────────────────────\n" +
                "[H⁺]       = %.4e mol/L\n" +
                "pH         = %.4f\n" +
                "pKa        = %.4f\n" +
                "%% ionised  = %.2f%%\n%s\n\n" +
                "ICE Table:\n  I: [HA]=%.4e   [H⁺]=0   [A⁻]=0\n" +
                "  E: [HA]=%.4e   [H⁺]=%.4e   [A⁻]=%.4e",
                x, pH, -Math.log10(Ka), pct, rule, C, C - x, x, x));
        });
        return p;
    }

    // ── Weak base ─────────────────────────────────────────────────────────────

    private JPanel weakBaseTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();
        JTextField kbF = addRow(p, g, 0, "Kb:");
        JTextField cF  = addRow(p, g, 1, "Initial [B] (mol/L):");
        inputPanel(g, p, 2, () -> {
            double Kb  = parse(kbF);
            double C   = parse(cF);
            double x   = PHCalculator.solveWeakAcidQuadratic(Kb, C);
            double pOH = -Math.log10(x);
            double pH  = 14 - pOH;
            output(String.format(
                "Weak Base   B  +  H₂O  ⇌  BH⁺  +  OH⁻\n" +
                "──────────────────────────────────────────\n" +
                "[OH⁻]     = %.4e mol/L\n" +
                "pOH       = %.4f\npH        = %.4f\n" +
                "pKb       = %.4f\n%% ionised = %.2f%%",
                x, pOH, pH, -Math.log10(Kb), x / C * 100));
        });
        return p;
    }

    // ── Buffer ────────────────────────────────────────────────────────────────

    private JPanel bufferTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();
        JTextField kaF  = addRow(p, g, 0, "Ka of weak acid:");
        JTextField cAF  = addRow(p, g, 1, "[A⁻] conjugate base (mol/L):");
        JTextField cHAF = addRow(p, g, 2, "[HA] weak acid (mol/L):");
        inputPanel(g, p, 3, () -> {
            double Ka    = parse(kaF);
            double cA    = parse(cAF);
            double cHA   = parse(cHAF);
            double pKa   = -Math.log10(Ka);
            double pH    = pKa + Math.log10(cA / cHA);
            double ratio = cA / cHA;
            String cap   = (ratio >= 0.1 && ratio <= 10)
                    ? "Buffer capacity: adequate (ratio in 1:10–10:1)"
                    : "[Warning] Poor buffer capacity — ratio outside 1:10–10:1";
            output(String.format(
                "Buffer  (Henderson-Hasselbalch)\n" +
                "pH = pKa + log([A⁻]/[HA])\n" +
                "─────────────────────────────────\n" +
                "pKa             = %.4f\n" +
                "[A⁻]/[HA] ratio = %.4f\n" +
                "pH              = %.4f\n\n%s",
                pKa, ratio, pH, cap));
        });
        return p;
    }

    // ── Neutralisation ────────────────────────────────────────────────────────

    private JPanel neutralTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();
        JTextField vaF = addRow(p, g, 0, "Volume of acid (L):");
        JTextField caF = addRow(p, g, 1, "Concentration of acid (mol/L):");
        JTextField vbF = addRow(p, g, 2, "Volume of base (L):");
        JTextField cbF = addRow(p, g, 3, "Concentration of base (mol/L):");
        inputPanel(g, p, 4, () -> {
            double Va     = parse(vaF), Ca = parse(caF);
            double Vb     = parse(vbF), Cb = parse(cbF);
            double molA   = Va * Ca, molB = Vb * Cb, Vtot = Va + Vb;
            double pH;
            String status;
            if (Math.abs(molA - molB) < 1e-12) {
                pH = 7.00; status = "Equivalence point — pH = 7.00";
            } else if (molA > molB) {
                double cH = (molA - molB) / Vtot;
                pH = -Math.log10(cH);
                status = String.format("Excess H⁺  = %.4e mol/L", cH);
            } else {
                double cOH = (molB - molA) / Vtot;
                pH = 14 + Math.log10(cOH);
                status = String.format("Excess OH⁻ = %.4e mol/L", cOH);
            }
            output(String.format(
                "Acid-Base Neutralisation\n─────────────────────────\n" +
                "mol H⁺  = %.4f\nmol OH⁻ = %.4f\n%s\npH = %.4f",
                molA, molB, status, pH));
        });
        return p;
    }

    // ── Buffer + Titrant ──────────────────────────────────────────────────────

    private JPanel bufferTitrantTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("Strong base: HA + OH⁻ → A⁻ + H₂O  |  Strong acid: A⁻ + H⁺ → HA  |  Uses mol ratios — volumes cancel"), g);
        g.gridwidth = 1;

        String[] kModes = {"Ka of weak acid (direct)", "Kb of weak base (Ka = Kw/Kb)"};
        JComboBox<String> kMode = new JComboBox<>(kModes);
        g.gridy = 1; g.gridwidth = 2; p.add(kMode, g); g.gridwidth = 1;

        JTextField kF     = addRow(p, g, 2, "Ka  (or Kb if mode above is Kb):");
        JTextField molHAF = addRow(p, g, 3, "Initial mol HA  (weak acid / or weak base if Kb):");
        JTextField molAF  = addRow(p, g, 4, "Initial mol A⁻  (conjugate base / or conj. acid):");

        String[] titTypes = {"Strong Base (OH⁻ added)", "Strong Acid (H⁺ added)"};
        JComboBox<String> titType = new JComboBox<>(titTypes);
        g.gridy = 5; g.gridwidth = 2; p.add(titType, g); g.gridwidth = 1;

        JTextField titVolF  = addRow(p, g, 6, "Titrant volume (mL):");
        JTextField titConcF = addRow(p, g, 7, "Titrant concentration (mol/L):");

        inputPanel(g, p, 8, () -> {
            double K  = parse(kF);
            double Ka = (kMode.getSelectedIndex() == 1) ? 1e-14 / K : K;
            double molHA  = parse(molHAF);
            double molA   = parse(molAF);
            double molTit = parse(titVolF) / 1000.0 * parse(titConcF);

            double newMolHA, newMolA;
            String titDesc;
            if (titType.getSelectedIndex() == 0) {
                newMolHA = molHA - molTit;  newMolA = molA + molTit;
                titDesc  = "Strong base";
            } else {
                newMolHA = molHA + molTit;  newMolA = molA - molTit;
                titDesc  = "Strong acid";
            }

            if (newMolHA <= 0 || newMolA <= 0) {
                output(String.format(
                    "Buffer capacity exceeded!\n" +
                    "mol HA after = %.4f   mol A⁻ after = %.4f\n" +
                    "One component is fully consumed — buffer no longer holds.",
                    newMolHA, newMolA));
                return;
            }

            double pKa   = -Math.log10(Ka);
            double ratio = newMolA / newMolHA;
            double pH    = pKa + Math.log10(ratio);
            output(String.format(
                "Buffer + Titrant  (Henderson-Hasselbalch)\n" +
                "─────────────────────────────────────────────\n" +
                "%s added:   %.4f mol\n\n" +
                "Before:  mol HA = %.4f   mol A⁻ = %.4f\n" +
                "After:   mol HA = %.4f   mol A⁻ = %.4f\n" +
                "         [A⁻]/[HA] = %.4f\n\n" +
                "pKa = %.4f\n" +
                "pH  = pKa + log([A⁻]/[HA])\n" +
                "    = %.4f + (%.4f)\n" +
                "    = %.4f",
                titDesc, molTit, molHA, molA,
                newMolHA, newMolA, ratio,
                pKa, pKa, Math.log10(ratio), pH));
        });
        return p;
    }

    // ── Helper: add button row ────────────────────────────────────────────────

    private void inputPanel(GridBagConstraints g, JPanel p, int row, Runnable action) {
        JButton btn = calcButton("Calculate pH");
        btn.addActionListener(e -> {
            try { action.run(); }
            catch (NumberFormatException ex) { output("Error: enter valid numbers."); }
        });
        addCalcRow(p, g, row, btn);
    }
}
