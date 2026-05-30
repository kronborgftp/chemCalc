package gui;

import chemistry.FormulaParser;
import chemistry.PeriodicTable;
import chemistry.Element;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class StoichiometryPanel extends BaseCalcPanel {

    public StoichiometryPanel() {
        super("Stoichiometry");
    }

    @Override
    protected void buildUI() {
        inputPanel.setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(TAB_FONT);
        tabs.addTab("Molar Mass",       molarMassTab());
        tabs.addTab("Moles ↔ Mass",     molesMassTab());
        tabs.addTab("Ideal Gas",        idealGasTab());
        tabs.addTab("Limiting Reagent", limitingTab());
        tabs.addTab("% Yield",          yieldTab());
        tabs.addTab("Empirical Formula",empiricalTab());
        tabs.addTab("Element Lookup",   lookupTab());
        tabs.addTab("Colligative",      colligativeTab());
        tabs.addTab("Isotope",          isotopeTab());
        tabs.addTab("Electron Config",  electronConfigTab());
        inputPanel.add(tabs, BorderLayout.CENTER);
    }

    // ── Molar mass ────────────────────────────────────────────────────────────

    private JPanel molarMassTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("Handles nested parentheses: Ca(OH)2, Fe2(SO4)3, Al2(SO4)3"), g);
        g.gridwidth = 1;
        JTextField fF = addRow(p, g, 1, "Formula:");
        ((JTextField)fF).setFont(MONO_FONT);
        JButton btn = calcButton("Calculate M");
        btn.addActionListener(e -> {
            String formula = fF.getText().trim();
            if (formula.isEmpty()) { output("Enter a formula."); return; }
            Map<String, Integer> comp = FormulaParser.parse(formula);
            PeriodicTable pt = PeriodicTable.getInstance();
            StringBuilder sb = new StringBuilder(String.format("Molar mass of %s\n", formula));
            sb.append("─".repeat(40)).append("\n");
            sb.append(String.format("%-6s  %-6s  %-12s  %s%n", "Elem", "Count", "Atomic mass", "Contribution"));
            sb.append("─".repeat(40)).append("\n");
            double total = 0;
            for (Map.Entry<String, Integer> entry : comp.entrySet()) {
                Element el = pt.get(entry.getKey());
                if (el == null) { sb.append("  [?] Unknown: ").append(entry.getKey()).append("\n"); continue; }
                double c = el.atomicMass * entry.getValue();
                total += c;
                sb.append(String.format("%-6s  %-6d  %-12.4f  %.4f%n",
                        entry.getKey(), entry.getValue(), el.atomicMass, c));
            }
            sb.append("─".repeat(40)).append("\n");
            sb.append(String.format("M = %.4f g/mol%n", total));
            output(sb.toString());
        });
        fF.addActionListener(e -> btn.doClick());
        addCalcRow(p, g, 2, btn);
        return p;
    }

    // ── Moles ↔ mass ─────────────────────────────────────────────────────────

    private JPanel molesMassTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("n = m / M"), g);
        g.gridwidth = 1;

        String[] modes = {"mass (g)  →  moles", "moles  →  mass (g)"};
        JComboBox<String> mode = new JComboBox<>(modes);
        g.gridy = 1; g.gridwidth = 2;
        p.add(mode, g);
        g.gridwidth = 1;

        JTextField fF  = addRow(p, g, 2, "Formula:");
        JTextField valF = addRow(p, g, 3, "Value (g or mol):");

        calcBtn(p, g, 4, "Convert", () -> {
            String formula = fF.getText().trim();
            double M = FormulaParser.molarMass(formula);
            double v = parse(valF);
            if (mode.getSelectedIndex() == 0) {
                output(String.format("Mass → Moles\n──────────────────────\nM(%s) = %.4f g/mol\n" +
                    "n = m/M = %.4f / %.4f = %.6f mol", formula, M, v, M, v / M));
            } else {
                output(String.format("Moles → Mass\n──────────────────────\nM(%s) = %.4f g/mol\n" +
                    "m = n·M = %.4f × %.4f = %.6f g", formula, M, v, M, v * M));
            }
        });
        return p;
    }

    // ── Ideal gas ─────────────────────────────────────────────────────────────

    private JPanel idealGasTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();
        double R = 0.08206;
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("PV = nRT  &nbsp; R = 0.08206 L·atm/(mol·K)  &nbsp; Solve for one unknown."), g);
        g.gridwidth = 1;

        String[] vars = {"P (atm)", "V (L)", "n (mol)", "T (K)"};
        JComboBox<String> solve = new JComboBox<>(vars);
        g.gridy = 1; g.gridwidth = 2;
        p.add(solve, g);
        g.gridwidth = 1;

        JTextField pF = addRow(p, g, 2, "P (atm)  — leave blank if solving for P:");
        JTextField vF = addRow(p, g, 3, "V (L)    — leave blank if solving for V:");
        JTextField nF = addRow(p, g, 4, "n (mol)  — leave blank if solving for n:");
        JTextField tF = addRow(p, g, 5, "T (K)    — leave blank if solving for T:");

        calcBtn(p, g, 6, "Solve PV = nRT", () -> {
            int sel = solve.getSelectedIndex();
            String res;
            switch (sel) {
                case 0 -> { double v = parse(vF), n = parse(nF), T = parse(tF); res = String.format("P = nRT/V = %.4f atm", n * R * T / v); }
                case 1 -> { double P = parse(pF), n = parse(nF), T = parse(tF); res = String.format("V = nRT/P = %.4f L", n * R * T / P); }
                case 2 -> { double P = parse(pF), v = parse(vF), T = parse(tF); res = String.format("n = PV/RT = %.4f mol", P * v / (R * T)); }
                default -> { double P = parse(pF), v = parse(vF), n = parse(nF);
                    double T = P * v / (n * R);
                    res = String.format("T = PV/(nR) = %.4f K  (%.2f°C)", T, T - 273.15); }
            }
            output("Ideal Gas Law  PV = nRT\n─────────────────────\n" + res);
        });
        return p;
    }

    // ── Limiting reagent ──────────────────────────────────────────────────────

    private JPanel limitingTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();
        g.gridx = 0; g.gridy = 0; g.gridwidth = 4;
        p.add(hint("Enter up to 3 reactants, their stoich. coefficients, and available masses."), g);
        g.gridwidth = 1;

        int N = 3;
        JTextField[] formF = new JTextField[N], coeffF = new JTextField[N], massF = new JTextField[N];
        for (int i = 0; i < N; i++) {
            int row = i + 1;
            g.gridx = 0; g.gridy = row; g.weightx = 0; g.fill = GridBagConstraints.NONE;
            p.add(lbl("R" + (i+1) + " formula:"), g);
            formF[i] = field(); formF[i].setFont(MONO_FONT);
            g.gridx = 1; g.weightx = 0.5; g.fill = GridBagConstraints.HORIZONTAL;
            p.add(formF[i], g);
            g.gridx = 2; g.weightx = 0; g.fill = GridBagConstraints.NONE;
            p.add(lbl("  coeff:"), g);
            coeffF[i] = field(); coeffF[i].setPreferredSize(new Dimension(50, 28));
            g.gridx = 3; g.weightx = 0.2; g.fill = GridBagConstraints.HORIZONTAL;
            p.add(coeffF[i], g);
            g.gridx = 4; g.weightx = 0; g.fill = GridBagConstraints.NONE;
            p.add(lbl("  mass(g):"), g);
            massF[i] = field(); massF[i].setPreferredSize(new Dimension(70, 28));
            g.gridx = 5; g.weightx = 0.3; g.fill = GridBagConstraints.HORIZONTAL;
            p.add(massF[i], g);
            g.weightx = 0; g.fill = GridBagConstraints.NONE;
        }

        g.gridy = N + 1; g.gridx = 0; g.gridwidth = 2;
        p.add(lbl("Product formula:"), g); g.gridwidth = 1;
        JTextField prodF  = field(); prodF.setFont(MONO_FONT);
        JTextField pcoefF = field(); pcoefF.setPreferredSize(new Dimension(50, 28));
        g.gridx = 2; g.gridy = N + 1; g.weightx = 0.5; g.fill = GridBagConstraints.HORIZONTAL;
        p.add(prodF, g);
        g.gridx = 3; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        p.add(lbl("  coeff:"), g);
        g.gridx = 4; g.weightx = 0.2; g.fill = GridBagConstraints.HORIZONTAL;
        p.add(pcoefF, g);
        g.weightx = 0; g.fill = GridBagConstraints.NONE;

        JTextField[] _f = formF, _co = coeffF, _m = massF;
        calcBtn(p, g, N + 2, "Find Limiting Reagent", () -> {
            double minRatio = Double.MAX_VALUE;
            int limIdx = 0;
            StringBuilder sb = new StringBuilder("Limiting Reagent\n─────────────────────────────────\n");
            for (int i = 0; i < N; i++) {
                if (_f[i].getText().trim().isEmpty()) continue;
                double M   = FormulaParser.molarMass(_f[i].getText().trim());
                double co  = parse(_co[i]);
                double m   = parse(_m[i]);
                double mol = m / M;
                double ratio = mol / co;
                sb.append(String.format("%-12s: %.4f g / %.4f g·mol⁻¹ = %.4f mol  (÷ coeff %.1f) = %.4f mol-rxn%n",
                        _f[i].getText().trim(), m, M, mol, co, ratio));
                if (ratio < minRatio) { minRatio = ratio; limIdx = i; }
            }
            sb.append(String.format("%nLimiting reagent: %s%nExtent ξ = %.4f mol%n",
                    _f[limIdx].getText().trim(), minRatio));
            if (!prodF.getText().trim().isEmpty()) {
                double pm   = FormulaParser.molarMass(prodF.getText().trim());
                double pco  = parse(pcoefF);
                double thMol = minRatio * pco;
                double thMass = thMol * pm;
                sb.append(String.format("%nTheoretical yield: %.4f mol = %.4f g of %s",
                        thMol, thMass, prodF.getText().trim()));
            }
            output(sb.toString());
        });
        return p;
    }

    // ── % yield ───────────────────────────────────────────────────────────────

    private JPanel yieldTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();
        JTextField actF  = addRow(p, g, 0, "Actual yield (g):");
        JTextField theoF = addRow(p, g, 1, "Theoretical yield (g):");
        calcBtn(p, g, 2, "Calculate % Yield", () ->
            output(String.format("%% Yield = (actual / theoretical) × 100\n" +
                "─────────────────────────────────\n%% yield = (%.4f / %.4f) × 100 = %.2f%%",
                parse(actF), parse(theoF), parse(actF) / parse(theoF) * 100)));
        return p;
    }

    // ── Empirical formula ─────────────────────────────────────────────────────

    private JPanel empiricalTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();
        g.gridx = 0; g.gridy = 0; g.gridwidth = 4;
        p.add(hint("Enter up to 5 elements and their mass percentages (must sum to ~100%)."), g);
        g.gridwidth = 1;

        int N = 5;
        JTextField[] symF = new JTextField[N], pctF = new JTextField[N];
        for (int i = 0; i < N; i++) {
            g.gridx = 0; g.gridy = i + 1; g.weightx = 0; g.fill = GridBagConstraints.NONE;
            p.add(lbl("Elem " + (i+1) + ":"), g);
            symF[i] = field(); symF[i].setFont(MONO_FONT); symF[i].setPreferredSize(new Dimension(60, 28));
            g.gridx = 1; g.weightx = 0.3; g.fill = GridBagConstraints.HORIZONTAL;
            p.add(symF[i], g);
            g.gridx = 2; g.weightx = 0; g.fill = GridBagConstraints.NONE;
            p.add(lbl("  mass %:"), g);
            pctF[i] = field(); pctF[i].setPreferredSize(new Dimension(80, 28));
            g.gridx = 3; g.weightx = 0.7; g.fill = GridBagConstraints.HORIZONTAL;
            p.add(pctF[i], g);
            g.weightx = 0; g.fill = GridBagConstraints.NONE;
        }

        JTextField[] _s = symF, _p = pctF;
        calcBtn(p, g, N + 1, "Find Empirical Formula", () -> {
            double[] ratios = new double[N];
            String[] syms   = new String[N];
            int count = 0;
            for (int i = 0; i < N; i++) {
                if (_s[i].getText().trim().isEmpty()) continue;
                syms[count]   = _s[i].getText().trim();
                double pct    = parse(_p[i]);
                double M      = PeriodicTable.getInstance().getMolarMass(syms[count]);
                if (M == 0) { output("Unknown element: " + syms[count]); return; }
                ratios[count] = pct / M;
                count++;
            }
            double min = Double.MAX_VALUE;
            for (int i = 0; i < count; i++) if (ratios[i] < min) min = ratios[i];
            StringBuilder sb = new StringBuilder("Empirical Formula\n─────────────────────\nNormalised ratios:\n");
            StringBuilder formula = new StringBuilder();
            for (int i = 0; i < count; i++) {
                double norm = ratios[i] / min;
                long sub = Math.round(norm);
                sb.append(String.format("  %s: %.3f → %d%n", syms[i], norm, sub));
                formula.append(syms[i]).append(sub == 1 ? "" : sub);
            }
            sb.append("\nEmpirical formula: ").append(formula);
            output(sb.toString());
        });
        return p;
    }

    // ── Element lookup ────────────────────────────────────────────────────────

    private JPanel lookupTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();
        JTextField qF = addRow(p, g, 0, "Symbol or element name:");
        JButton btn = calcButton("Look Up");
        btn.addActionListener(e -> {
            String q = qF.getText().trim();
            PeriodicTable pt = PeriodicTable.getInstance();
            Element el = pt.get(q);
            if (el == null)
                for (Element ee : pt.getAll())
                    if (ee.name.equalsIgnoreCase(q)) { el = ee; break; }
            if (el == null) { output("Element not found: " + q); return; }
            output(String.format(
                "%s — %s\n─────────────────────────────\n" +
                "Atomic number:     %d\n" +
                "Molar mass:        %.4f g/mol\n" +
                "Electronegativity: %.2f (Pauling)\n" +
                "Period: %d   Group: %d",
                el.symbol, el.name, el.atomicNumber, el.atomicMass,
                el.electronegativity, el.period, el.group));
        });
        qF.addActionListener(ev -> btn.doClick());
        addCalcRow(p, g, 1, btn);
        return p;
    }

    // ── Colligative Properties ────────────────────────────────────────────────

    private JPanel colligativeTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("ΔTb = i·Kb·m  |  ΔTf = i·Kf·m  |  m = mol solute / kg solvent  |  water: Kb=0.512, Kf=1.86"), g);
        g.gridwidth = 1;

        String[] modes = {"ΔTb — boiling point elevation", "ΔTf — freezing point depression",
                "Molar mass from ΔTb or ΔTf", "Osmotic pressure  π = iMRT"};
        JComboBox<String> mode = new JComboBox<>(modes);
        g.gridy = 1; g.gridwidth = 2; p.add(mode, g); g.gridwidth = 1;

        JTextField kF    = addRow(p, g, 2, "Kb or Kf of solvent (°C·kg/mol):");
        JTextField dtF   = addRow(p, g, 3, "Observed ΔT (°C)  [blank if solving for ΔT]:");
        JTextField msF   = addRow(p, g, 4, "Mass of solute (g):");
        JTextField mmF   = addRow(p, g, 5, "Molar mass of solute (g/mol)  [blank if unknown]:");
        JTextField msvF  = addRow(p, g, 6, "Mass of solvent (g):");
        JTextField iF    = addRow(p, g, 7, "van't Hoff factor i (1 for non-electrolyte):");
        JTextField molF  = addRow(p, g, 8, "Molarity M (mol/L)  [osmotic pressure only]:");
        JTextField tF    = addRow(p, g, 9, "T (K)  [osmotic pressure only]:");
        iF.setText("1");

        calcBtn(p, g, 10, "Calculate", () -> {
            int sel = mode.getSelectedIndex();
            double i = parse(iF);
            if (sel == 3) {
                double M = parse(molF), T = parse(tF);
                double pi = i * M * 0.08206 * T;
                output(String.format("Osmotic Pressure\n──────────────────\nπ = iMRT = %.4f × %.4f × 0.08206 × %.2f\n  = %.4f atm  (= %.2f kPa)",
                        i, M, T, pi, pi * 101.325));
                return;
            }
            double K    = parse(kF);
            double msSol = parse(msF);
            double kgSolv = parse(msvF) / 1000.0;
            if (sel == 2) {
                double dT = parse(dtF);
                double molarM = (i * K * msSol) / (dT * kgSolv);
                output(String.format("Molar Mass from ΔT\n──────────────────────\n" +
                        "M = (i·K·mass) / (ΔT·kg_solvent)\n  = (%.2f × %.3f × %.4f) / (%.4f × %.4f)\n  = %.4f g/mol",
                        i, K, msSol, dT, kgSolv, molarM));
            } else {
                double mm = parse(mmF);
                double molality = (msSol / mm) / kgSolv;
                double dT = i * K * molality;
                String label = sel == 0 ? "ΔTb" : "ΔTf";
                output(String.format("%s Calculation\n──────────────────────\n" +
                        "molality m = %.6f mol/kg\n%s = i·K·m = %.4f °C\n" +
                        "New %s = normal %s %s %.4f °C",
                        label, molality, label, dT, label.contains("b") ? "b.p." : "f.p.",
                        label.contains("b") ? "b.p." : "f.p.",
                        label.contains("b") ? "+" : "−", dT));
            }
        });
        return p;
    }

    // ── Isotope Calculator ────────────────────────────────────────────────────

    private JPanel isotopeTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("Protons = Z  |  Neutrons = A − Z  |  Electrons = Z − charge"), g);
        g.gridwidth = 1;

        int N = 3;
        JTextField[] symF = new JTextField[N], aF = new JTextField[N], chF = new JTextField[N];
        for (int i = 0; i < N; i++) {
            int row = i * 3 + 1;
            symF[i] = addRow(p, g, row,     "Isotope " + (i+1) + " symbol:");
            aF[i]   = addRow(p, g, row + 1, "  Mass number A:");
            chF[i]  = addRow(p, g, row + 2, "  Ion charge (0 for neutral):");
            chF[i].setText("0");
        }

        JTextField[] _s = symF, _a = aF, _c = chF;
        calcBtn(p, g, N * 3 + 1, "Calculate", () -> {
            chemistry.PeriodicTable pt = chemistry.PeriodicTable.getInstance();
            StringBuilder sb = new StringBuilder("Isotope Calculator\n──────────────────────────────\n");
            int[] neutrons = new int[N];
            String[] syms = new String[N];
            boolean any = false;
            for (int i = 0; i < N; i++) {
                String sym = _s[i].getText().trim();
                if (sym.isEmpty()) { neutrons[i] = -1; continue; }
                chemistry.Element el = pt.get(sym);
                if (el == null) { sb.append(sym).append(": not found\n"); continue; }
                int A = (int) Double.parseDouble(_a[i].getText().trim());
                int charge = (int) Double.parseDouble(_c[i].getText().trim());
                int Z = el.atomicNumber, Nn = A - Z, electrons = Z - charge;
                neutrons[i] = Nn; syms[i] = sym;
                sb.append(String.format("%s-%d:  Z=%d protons,  N=%d neutrons,  %d electrons%n",
                        sym, A, Z, Nn, electrons));
                any = true;
            }
            if (any && N > 1) {
                sb.append("\nRelationship:\n");
                boolean sameN = true, sameA = true;
                for (int i = 1; i < N; i++) { if (neutrons[i] != neutrons[0]) sameN = false; }
                if (sameN) sb.append("  Same neutron count → ISOTONES\n");
                else sb.append("  Different neutrons\n");
            }
            output(sb.toString());
        });
        return p;
    }

    // ── Electron Config ───────────────────────────────────────────────────────

    private JPanel electronConfigTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("Enter outermost subshell config, e.g.  4s2 4p5  →  Period 4, Group 17 (halogen, Br)"), g);
        g.gridwidth = 1;

        JTextField cfF = addRow(p, g, 1, "Outermost config (e.g. '3s2 3p3'):");

        calcBtn(p, g, 2, "Identify Group & Period", () -> {
            String config = cfF.getText().trim().toLowerCase();
            String[] tokens = config.split("\\s+");
            int period = 0, sE = 0, pE = 0, dE = 0, pPer = 0;
            for (String tok : tokens) {
                java.util.regex.Matcher m = java.util.regex.Pattern
                        .compile("(\\d)(s|p|d|f)(\\d+)").matcher(tok);
                if (!m.matches()) continue;
                int n = Integer.parseInt(m.group(1));
                String l = m.group(2);
                int e = Integer.parseInt(m.group(3));
                if (n > period) period = n;
                switch (l) {
                    case "s" -> sE = e;
                    case "p" -> { pE = e; pPer = n; }
                    case "d" -> dE = e;
                }
            }
            if (period == 0) { output("Could not parse config. Use format: 3s2 3p5"); return; }
            int group;
            String block;
            if (pE > 0 && pPer == period) { group = 10 + sE + pE; block = "p"; }
            else if (dE > 0)              { group = sE + dE;       block = "d"; }
            else                          { group = sE;            block = "s"; }
            String groupName = switch (group) {
                case 1  -> "alkali metals (or H)";
                case 2  -> "alkaline earth metals";
                case 13 -> "boron group";
                case 14 -> "carbon group";
                case 15 -> "nitrogen group / pnictogens";
                case 16 -> "chalcogens";
                case 17 -> "halogens";
                case 18 -> "noble gases";
                default -> "transition metals";
            };
            chemistry.PeriodicTable pt = chemistry.PeriodicTable.getInstance();
            String element = "—";
            for (chemistry.Element el : pt.getAll())
                if (el.period == period && el.group == group) { element = el.symbol + " (" + el.name + ")"; break; }
            output(String.format("Electron Configuration Analysis\n──────────────────────────────\n" +
                    "Period = %d\nGroup  = %d  (%s-block)\nGroup name: %s\nElement: %s",
                    period, group, block, groupName, element));
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
