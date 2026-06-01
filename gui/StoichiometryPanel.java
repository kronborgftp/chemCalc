package gui;

import chemistry.FormulaParser;
import chemistry.Nomenclature;
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
        mainTabs = tabs;
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
        tabs.addTab("Photon Energy",    photonEnergyTab());
        tabs.addTab("Unit Cell",        crystalUnitCellTab());
        tabs.addTab("Dissolution",      dissolutionTab());
        tabs.addTab("Ionic Formula",    ionicFormulaTab());
        tabs.addTab("Name Formula",     nameFormulaTab());
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
        g.gridx = 0; g.gridy = 0; g.gridwidth = 6;
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

        // ── Example loader ────────────────────────────────────────────────────
        // {r1formula, r1coeff, r1mass, r2formula, r2coeff, r2mass, r3formula, r3coeff, r3mass,
        //  productFormula, productCoeff, label}
        String[][] examples = {
            {"H2","2","10",    "O2","1","16",   "","","",  "H2O","2",    "2H₂+O₂→2H₂O"},
            {"C6H12O6","1","1000","","","",      "","","",  "C2H5OH","2", "Fermentation"},
            {"N2","1","28",    "H2","3","10",   "","","",  "NH3","2",    "N₂+3H₂→2NH₃"},
            {"Al","2","10",    "Br2","3","50",  "","","",  "AlBr3","2",  "2Al+3Br₂→2AlBr₃"},
        };

        JPanel exRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 0));
        exRow.setBackground(CARD_BG);
        exRow.add(lbl("Examples:"));
        for (String[] ex : examples) {
            JButton btn = new JButton(ex[11]);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
            btn.setBackground(new Color(240, 242, 255));
            btn.setForeground(new Color(37, 99, 235));
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                for (int i = 0; i < N; i++) {
                    formF[i].setText(ex[i * 3]);
                    coeffF[i].setText(ex[i * 3 + 1]);
                    massF[i].setText(ex[i * 3 + 2]);
                }
                prodF.setText(ex[9]);
                pcoefF.setText(ex[10]);
            });
            exRow.add(btn);
        }

        g.gridx = 0; g.gridy = N + 2; g.gridwidth = 6;
        p.add(exRow, g);
        g.gridwidth = 1;

        JTextField[] _f = formF, _co = coeffF, _m = massF;
        calcBtn(p, g, N + 3, "Find Limiting Reagent", () -> {
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

    // ── Photon Energy ─────────────────────────────────────────────────────────

    private JPanel photonEnergyTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("E = hc/λ  |  h = 6.626×10⁻³⁴ J·s  |  c = 2.998×10⁸ m/s  |  NA = 6.022×10²³"), g);
        g.gridwidth = 1;

        String[] modes = {"λ (nm)  →  energy per photon & per mole",
                          "Energy (kJ/mol)  →  λ (nm)",
                          "Frequency ν (Hz)  →  energy"};
        JComboBox<String> mode = new JComboBox<>(modes);
        g.gridy = 1; g.gridwidth = 2; p.add(mode, g); g.gridwidth = 1;

        JTextField lamF  = addRow(p, g, 2, "Wavelength λ (nm):");
        JTextField eF    = addRow(p, g, 3, "Energy (kJ/mol)  [blank if solving for E]:");
        JTextField freqF = addRow(p, g, 4, "Frequency ν (Hz)  [for frequency mode]:");

        final double h  = 6.626e-34;
        final double c  = 2.998e8;
        final double NA = 6.022e23;

        calcBtn(p, g, 5, "Calculate", () -> {
            switch (mode.getSelectedIndex()) {
                case 0 -> {
                    double lambda   = parse(lamF) * 1e-9;
                    double ePhoton  = h * c / lambda;
                    double eMol     = ePhoton * NA;
                    output(String.format(
                        "Photon Energy from Wavelength\n──────────────────────────────\n" +
                        "λ           = %.2f nm  (%.4e m)\n\n" +
                        "E (photon)  = hc/λ = %.4e J\n" +
                        "E (per mol) = E × NA = %.4f kJ/mol\n" +
                        "ν = c/λ     = %.4e Hz",
                        parse(lamF), lambda, ePhoton, eMol / 1000, c / lambda));
                }
                case 1 -> {
                    double eMol    = parse(eF) * 1000;
                    double ePhoton = eMol / NA;
                    double lambda  = h * c / ePhoton;
                    output(String.format(
                        "Wavelength from Energy\n──────────────────────────────\n" +
                        "E           = %.4f kJ/mol  (%.4e J/photon)\n\n" +
                        "λ = hc/E    = %.2f nm\n" +
                        "ν = c/λ     = %.4e Hz",
                        parse(eF), ePhoton, lambda / 1e-9, c / lambda));
                }
                case 2 -> {
                    double freq    = parse(freqF);
                    double ePhoton = h * freq;
                    double eMol    = ePhoton * NA;
                    double lambda  = c / freq;
                    output(String.format(
                        "Energy from Frequency\n──────────────────────────────\n" +
                        "ν           = %.4e Hz\n\n" +
                        "E (photon)  = hν = %.4e J\n" +
                        "E (per mol) = %.4f kJ/mol\n" +
                        "λ = c/ν     = %.2f nm",
                        freq, ePhoton, eMol / 1000, lambda / 1e-9));
                }
            }
        });
        return p;
    }

    // ── Crystal Unit Cell ─────────────────────────────────────────────────────

    private JPanel crystalUnitCellTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("ρ = Z·M / (NA·a³)  |  sc: Z=1  bcc: Z=2  fcc: Z=4  |  a in pm, ρ in g/cm³"), g);
        g.gridwidth = 1;

        String[] structures = {"bcc  (body-centred cubic, Z = 2)",
                               "fcc  (face-centred cubic,  Z = 4)",
                               "sc   (simple cubic,        Z = 1)"};
        JComboBox<String> structCB = new JComboBox<>(structures);
        g.gridy = 1; g.gridwidth = 2; p.add(structCB, g); g.gridwidth = 1;

        String[] modes = {"ρ  →  lattice parameter a",
                          "a  →  density ρ",
                          "# unit cells in area × thickness"};
        JComboBox<String> mode = new JComboBox<>(modes);
        g.gridy = 2; g.gridwidth = 2; p.add(mode, g); g.gridwidth = 1;

        JTextField mF    = addRow(p, g, 3, "Molar mass M (g/mol):");
        JTextField rhoF  = addRow(p, g, 4, "Density ρ (g/cm³)  [blank if solving for ρ]:");
        JTextField aF    = addRow(p, g, 5, "Lattice parameter a (pm)  [blank if solving for a]:");
        JTextField areaF = addRow(p, g, 6, "Area (cm²)  [for unit cell count]:");
        JTextField thkF  = addRow(p, g, 7, "Thickness (µm)  [for unit cell count]:");

        final double NA = 6.022e23;

        calcBtn(p, g, 8, "Calculate", () -> {
            int Z = switch (structCB.getSelectedIndex()) { case 0 -> 2; case 1 -> 4; default -> 1; };
            String struct = (String) structCB.getSelectedItem();
            double M = parse(mF);

            switch (mode.getSelectedIndex()) {
                case 0 -> {
                    double rho = parse(rhoF);
                    double a3  = Z * M / (NA * rho);       // cm³
                    double a   = Math.pow(a3, 1.0 / 3);    // cm
                    double aPm = a * 1e10;                  // cm → pm
                    output(String.format(
                        "Lattice Parameter from Density\n──────────────────────────────\n" +
                        "Structure: %s   Z = %d\nM = %.4f g/mol   ρ = %.4f g/cm³\n\n" +
                        "a³ = Z·M / (NA·ρ) = %.4e cm³\na  = %.4e cm  =  %.2f pm",
                        struct, Z, M, rho, a3, a, aPm));
                }
                case 1 -> {
                    double aCm = parse(aF) * 1e-10;         // pm → cm
                    double rho = Z * M / (NA * Math.pow(aCm, 3));
                    output(String.format(
                        "Density from Lattice Parameter\n──────────────────────────────\n" +
                        "Structure: %s   Z = %d\nM = %.4f g/mol   a = %.2f pm\n\n" +
                        "ρ = Z·M / (NA·a³) = %.4f g/cm³",
                        struct, Z, M, parse(aF), rho));
                }
                case 2 -> {
                    double rho    = parse(rhoF);
                    double a3     = Z * M / (NA * rho);
                    double a      = Math.pow(a3, 1.0 / 3);
                    double area   = parse(areaF);
                    double thkCm  = parse(thkF) * 1e-4;    // µm → cm
                    double vol    = area * thkCm;
                    double nCells = vol / a3;
                    output(String.format(
                        "Unit Cells in Volume\n──────────────────────────────\n" +
                        "Structure: %s   Z = %d\nM = %.4f g/mol   ρ = %.4f g/cm³\n\n" +
                        "a = %.2f pm   a³ = %.4e cm³\n" +
                        "Area = %.4f cm²   Thickness = %.2f µm = %.4e cm\n" +
                        "Volume = %.4e cm³\n\n" +
                        "N = V / a³ = %.4e / %.4e\n  = %.4e unit cells",
                        struct, Z, M, rho, a * 1e10, a3,
                        area, parse(thkF), thkCm, vol, vol, a3, nCells));
                }
            }
        });
        return p;
    }

    // ── Dissolution / Ion Count ───────────────────────────────────────────────

    private JPanel dissolutionTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("Al₂(SO₄)₃ → 2 Al³⁺ + 3 SO₄²⁻  =  5 mol ions per mol compound"), g);
        g.gridwidth = 1;

        g.gridy = 1; g.gridwidth = 2;
        p.add(new JLabel("<html><b>Enter each ion produced upon complete dissolution:</b></html>"), g);
        g.gridwidth = 1;

        JSpinner nIons = new JSpinner(new SpinnerNumberModel(2, 1, 8, 1));
        g.gridy = 2;
        p.add(new JLabel("Number of distinct ion types:"), g);
        g.gridx = 1; p.add(nIons, g); g.gridx = 0;

        JPanel ionGrid = new JPanel(new GridBagLayout());
        ionGrid.setBackground(p.getBackground());
        GridBagConstraints ig = new GridBagConstraints();
        ig.insets = new Insets(3, 6, 3, 6);
        ig.fill   = GridBagConstraints.HORIZONTAL;

        ig.gridy = 0;
        ig.gridx = 0; ionGrid.add(new JLabel("Ion formula"), ig);
        ig.gridx = 1; ionGrid.add(new JLabel("Coefficient"), ig);

        int MAX = 8;
        JTextField[] ionF  = new JTextField[MAX];
        JSpinner[]   coeff = new JSpinner[MAX];
        for (int i = 0; i < MAX; i++) {
            ig.gridy = i + 1;
            ionF[i]  = new JTextField(8);
            coeff[i] = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
            ig.gridx = 0; ionGrid.add(ionF[i],  ig);
            ig.gridx = 1; ionGrid.add(coeff[i], ig);
        }

        g.gridy = 3; g.gridwidth = 2; p.add(ionGrid, g); g.gridwidth = 1;

        nIons.addChangeListener(e -> {
            int n = (int) nIons.getValue();
            for (int i = 0; i < MAX; i++) {
                ionF[i].setVisible(i < n);
                coeff[i].setVisible(i < n);
            }
            ionGrid.revalidate();
        });
        // trigger initial visibility
        int init = (int) nIons.getValue();
        for (int i = 0; i < MAX; i++) { ionF[i].setVisible(i < init); coeff[i].setVisible(i < init); }

        calcBtn(p, g, 4, "Count Ions", () -> {
            int n = (int) nIons.getValue();
            StringBuilder sb = new StringBuilder("Dissolution  —  Ion Count\n" + "─".repeat(40) + "\n");
            int total = 0;
            for (int i = 0; i < n; i++) {
                String formula = ionF[i].getText().trim();
                int c = (int) coeff[i].getValue();
                if (formula.isEmpty()) formula = "(ion " + (i + 1) + ")";
                sb.append(String.format("  %d  ×  %s%n", c, formula));
                total += c;
            }
            sb.append("─".repeat(40)).append("\n");
            sb.append(String.format("Total ions per formula unit = %d%n", total));
            sb.append(String.format("%nFor 1 mol compound → %.0f mol ions%n", (double) total));
            output(sb.toString());
        });
        return p;
    }

    // ── Ionic formula builder ─────────────────────────────────────────────────

    // Each row: { "alias1|alias2|...", "Display Name", "formula", "±charge", "polyatomic?" }
    // Aliases are pipe-separated inside ONE string — do NOT use commas to separate them
    // because Java would treat them as separate array elements, breaking row[2..4].
    private static final String[][] CATION_DB = {
        {"hydrogen",                         "Hydrogen",      "H",     "+1", "false"},
        {"lithium",                          "Lithium",       "Li",    "+1", "false"},
        {"sodium|natrium",                   "Sodium",        "Na",    "+1", "false"},
        {"potassium|kalium",                 "Potassium",     "K",     "+1", "false"},
        {"silver|sølv",                      "Silver",        "Ag",    "+1", "false"},
        {"ammonium",                         "Ammonium",      "NH4",   "+1", "true"},
        {"copper(i)|copper i|cu+",           "Copper(I)",     "Cu",    "+1", "false"},
        {"magnesium",                        "Magnesium",     "Mg",    "+2", "false"},
        {"calcium",                          "Calcium",       "Ca",    "+2", "false"},
        {"strontium",                        "Strontium",     "Sr",    "+2", "false"},
        {"barium",                           "Barium",        "Ba",    "+2", "false"},
        {"zinc|zink",                        "Zinc",          "Zn",    "+2", "false"},
        {"iron(ii)|iron ii|ferrous|fe2+",    "Iron(II)",      "Fe",    "+2", "false"},
        {"copper(ii)|copper ii|cupric|cu2+", "Copper(II)",    "Cu",    "+2", "false"},
        {"lead(ii)|lead ii|pb2+|bly",        "Lead(II)",      "Pb",    "+2", "false"},
        {"nickel",                           "Nickel",        "Ni",    "+2", "false"},
        {"cobalt(ii)|cobalt ii",             "Cobalt(II)",    "Co",    "+2", "false"},
        {"manganese(ii)|manganese ii",       "Manganese(II)", "Mn",    "+2", "false"},
        {"mercury(ii)|mercury ii",           "Mercury(II)",   "Hg",    "+2", "false"},
        {"tin(ii)|tin ii",                   "Tin(II)",       "Sn",    "+2", "false"},
        {"aluminum|aluminium|al3+",          "Aluminum",      "Al",    "+3", "false"},
        {"iron(iii)|iron iii|ferric|fe3+",   "Iron(III)",     "Fe",    "+3", "false"},
        {"chromium(iii)|chromium iii",       "Chromium(III)", "Cr",    "+3", "false"},
    };

    private static final String[][] ANION_DB = {
        {"fluoride|fluorid",                        "Fluoride",            "F",      "-1", "false"},
        {"chloride|chlorid",                        "Chloride",            "Cl",     "-1", "false"},
        {"bromide|bromid",                          "Bromide",             "Br",     "-1", "false"},
        {"iodide|iodid",                            "Iodide",              "I",      "-1", "false"},
        {"hydroxide|hydroxid",                      "Hydroxide",           "OH",     "-1", "true"},
        {"nitrate|nitrat",                          "Nitrate",             "NO3",    "-1", "true"},
        {"nitrite|nitrit",                          "Nitrite",             "NO2",    "-1", "true"},
        {"cyanide|cyanid",                          "Cyanide",             "CN",     "-1", "true"},
        {"permanganate|permanganat",                "Permanganate",        "MnO4",   "-1", "true"},
        {"acetate|acetat|ethanoate",                "Acetate",             "CH3COO", "-1", "true"},
        {"hydrogen carbonate|bicarbonate|hydrogencarbonat|bikarbonat",
                                                    "Hydrogen carbonate",  "HCO3",   "-1", "true"},
        {"hydrogen sulfate|bisulfate|hydrogensulfat","Hydrogen sulfate",   "HSO4",   "-1", "true"},
        {"dihydrogen phosphate|dihydrogenphosphat", "Dihydrogen phosphate","H2PO4",  "-1", "true"},
        {"perchlorate|perchlorat",                  "Perchlorate",         "ClO4",   "-1", "true"},
        {"chlorate|chlorat",                        "Chlorate",            "ClO3",   "-1", "true"},
        {"thiocyanate|thiocyanat",                  "Thiocyanate",         "SCN",    "-1", "true"},
        {"oxide|oxid",                              "Oxide",               "O",      "-2", "false"},
        {"sulfide|sulfid",                          "Sulfide",             "S",      "-2", "false"},
        {"sulfate|sulfat",                          "Sulfate",             "SO4",    "-2", "true"},
        {"sulfite|sulfit",                          "Sulfite",             "SO3",    "-2", "true"},
        {"carbonate|carbonat",                      "Carbonate",           "CO3",    "-2", "true"},
        {"chromate|chromat",                        "Chromate",            "CrO4",   "-2", "true"},
        {"dichromate|dichromat",                    "Dichromate",          "Cr2O7",  "-2", "true"},
        {"oxalate|oxalat",                          "Oxalate",             "C2O4",   "-2", "true"},
        {"hydrogen phosphate|hydrogenphosphat",     "Hydrogen phosphate",  "HPO4",   "-2", "true"},
        {"thiosulfate|thiosulfat",                  "Thiosulfate",         "S2O3",   "-2", "true"},
        {"phosphate|phosphat",                      "Phosphate",           "PO4",    "-3", "true"},
        {"phosphite|phosphit",                      "Phosphite",           "PO3",    "-3", "true"},
        {"arsenate|arsenat",                        "Arsenate",            "AsO4",   "-3", "true"},
    };

    private JPanel ionicFormulaTab() {
        JPanel outer = new JPanel(new BorderLayout(0, 8));
        outer.setBackground(CARD_BG);
        outer.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        // ── Input form ────────────────────────────────────────────────────────
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(CARD_BG);
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        form.add(hint("Type ion names (English or Danish). Examples: ammonium, nitrate, sulfate, hydroxide"), g);
        g.gridwidth = 1;

        JTextField catF = addRow(form, g, 1, "Cation (positive ion):");
        JTextField anF  = addRow(form, g, 2, "Anion (negative ion):");

        calcBtn(form, g, 3, "Build Formula", () -> {
            String[] cat = findIon(catF.getText().trim().toLowerCase(), CATION_DB);
            String[] an  = findIon(anF.getText().trim().toLowerCase(), ANION_DB);
            if (cat == null) { output("Cation not found: " + catF.getText().trim()
                    + "\nSee the reference table below."); return; }
            if (an == null)  { output("Anion not found: " + anF.getText().trim()
                    + "\nSee the reference table below."); return; }

            int cCharge = Integer.parseInt(cat[3]);
            int aCharge = -Integer.parseInt(an[3]); // store as negative, use abs
            int gcd = gcd(cCharge, aCharge);
            int numCat = aCharge / gcd;
            int numAn  = cCharge / gcd;

            boolean catPoly = Boolean.parseBoolean(cat[4]);
            boolean anPoly  = Boolean.parseBoolean(an[4]);

            String catPart = (numCat > 1 && catPoly) ? "(" + cat[2] + ")" + numCat
                           : (numCat > 1)            ? cat[2] + numCat
                           :                           cat[2];
            String anPart  = (numAn  > 1 && anPoly)  ? "(" + an[2]  + ")" + numAn
                           : (numAn  > 1)             ? an[2]  + numAn
                           :                            an[2];

            String formula = catPart + anPart;

            StringBuilder sb = new StringBuilder("Ionic Formula Builder\n");
            sb.append("─".repeat(44)).append("\n");
            sb.append(String.format("Cation:  %-24s  %s  (charge %+d)%n", cat[1], cat[2] + superscript(cCharge), cCharge));
            sb.append(String.format("Anion:   %-24s  %s  (charge %+d)%n", an[1],  an[2]  + superscript(-aCharge), -aCharge));
            sb.append("\n");
            sb.append(String.format("Ratio:  %d cation : %d anion%n", numCat, numAn));
            sb.append(String.format("Check:  %d × (%+d) + %d × (%+d) = %d  ✓%n",
                    numCat, cCharge, numAn, -aCharge, numCat * cCharge + numAn * (-aCharge)));
            sb.append("─".repeat(44)).append("\n");
            sb.append("Formula:  ").append(formula).append("\n");
            output(sb.toString());
        });

        outer.add(form, BorderLayout.NORTH);

        // ── Reference table ───────────────────────────────────────────────────
        JTextArea ref = new JTextArea(ionReference());
        ref.setEditable(false);
        ref.setFont(MONO_FONT);
        ref.setBackground(new Color(18, 20, 12));
        ref.setForeground(new Color(108, 200, 90));
        ref.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        ref.setLineWrap(false);
        JScrollPane refScroll = new JScrollPane(ref,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        refScroll.getVerticalScrollBar().setUnitIncrement(16);
        refScroll.getBorder();
        outer.add(refScroll, BorderLayout.CENTER);

        return outer;
    }

    private String[] findIon(String input, String[][] db) {
        input = input.trim().toLowerCase();
        // Exact match on any alias (aliases are pipe-separated within row[0])
        for (String[] row : db) {
            for (String alias : row[0].split("\\|")) {
                if (alias.trim().equals(input)) return row;
            }
        }
        // Partial match fallback (e.g. "hydrogenphosphate" vs "hydrogen phosphate")
        String compact = input.replaceAll("\\s+", "");
        for (String[] row : db) {
            for (String alias : row[0].split("\\|")) {
                String ca = alias.trim().replaceAll("\\s+", "");
                if (ca.equals(compact) || ca.contains(compact) || compact.contains(ca))
                    return row;
            }
        }
        return null;
    }

    private int gcd(int a, int b) { return b == 0 ? a : gcd(b, a % b); }

    private String superscript(int charge) {
        if (charge ==  1) return "⁺";
        if (charge == -1) return "⁻";
        if (charge ==  2) return "²⁺";
        if (charge == -2) return "²⁻";
        if (charge ==  3) return "³⁺";
        if (charge == -3) return "³⁻";
        return (charge > 0 ? "+" : "") + charge;
    }

    private String ionReference() {
        return """
Common Ions — Quick Reference
═══════════════════════════════════════════════════════════════════
CATIONS (positive)            Formula  Charge  Type name to use
───────────────────────────────────────────────────────────────────
Ammonium                      NH4+     +1      ammonium
Sodium / Natrium              Na+      +1      sodium
Potassium / Kalium            K+       +1      potassium
Silver / Sølv                 Ag+      +1      silver
Lithium                       Li+      +1      lithium
Hydrogen                      H+       +1      hydrogen
Magnesium                     Mg2+     +2      magnesium
Calcium                       Ca2+     +2      calcium
Barium                        Ba2+     +2      barium
Strontium                     Sr2+     +2      strontium
Zinc / Zink                   Zn2+     +2      zinc
Iron(II) / Ferrous            Fe2+     +2      iron(ii)
Copper(II) / Cupric           Cu2+     +2      copper(ii)
Lead(II) / Bly                Pb2+     +2      lead(ii)
Nickel                        Ni2+     +2      nickel
Mercury(II)                   Hg2+     +2      mercury(ii)
Manganese(II)                 Mn2+     +2      manganese(ii)
Aluminum / Aluminium          Al3+     +3      aluminum
Iron(III) / Ferric            Fe3+     +3      iron(iii)
Chromium(III)                 Cr3+     +3      chromium(iii)

ANIONS (negative)             Formula  Charge  Type name to use
───────────────────────────────────────────────────────────────────
Fluoride / Fluorid            F-       -1      fluoride
Chloride / Chlorid            Cl-      -1      chloride
Bromide / Bromid              Br-      -1      bromide
Iodide / Iodid                I-       -1      iodide
Hydroxide / Hydroxid          OH-      -1      hydroxide
Nitrate / Nitrat              NO3-     -1      nitrate
Nitrite / Nitrit              NO2-     -1      nitrite
Acetate / Acetat              CH3COO-  -1      acetate
Permanganate                  MnO4-    -1      permanganate
Hydrogen carbonate            HCO3-    -1      hydrogen carbonate
Hydrogen sulfate              HSO4-    -1      hydrogen sulfate
Dihydrogen phosphate          H2PO4-   -1      dihydrogen phosphate
Perchlorate / Perchlorat      ClO4-    -1      perchlorate
Oxide / Oxid                  O2-      -2      oxide
Sulfide / Sulfid              S2-      -2      sulfide
Sulfate / Sulfat              SO42-    -2      sulfate
Sulfite / Sulfit              SO32-    -2      sulfite
Carbonate / Carbonat          CO32-    -2      carbonate
Chromate / Chromat            CrO42-   -2      chromate
Dichromate / Dichromat        Cr2O72-  -2      dichromate
Oxalate / Oxalat              C2O42-   -2      oxalate
Hydrogen phosphate            HPO42-   -2      hydrogen phosphate
Thiosulfate / Thiosulfat      S2O32-   -2      thiosulfate
Phosphate / Phosphat          PO43-    -3      phosphate
Arsenate / Arsenat            AsO43-   -3      arsenate
""";
    }

    // ── Name Formula ──────────────────────────────────────────────────────────

    private JPanel nameFormulaTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("IUPAC nomenclature: ionic (Stock), binary molecular (Greek prefix), acids, common names."), g);
        g.gridwidth = 1;

        JTextField fF = addRow(p, g, 1, "Formula:");
        fF.setFont(MONO_FONT);

        // Quick example buttons
        String[] examples = {"NaCl", "Fe2(SO4)3", "Ca(NO3)2", "CO2", "N2O4", "Al2O3",
                             "H2SO4", "NH4Cl", "(NH4)2SO4", "CuO", "FeCl3", "H2O"};
        JPanel exRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 0));
        exRow.setBackground(CARD_BG);
        exRow.add(lbl("Examples:"));
        for (String ex : examples) {
            JButton btn = new JButton(ex);
            btn.setFont(new Font("Monospaced", Font.PLAIN, 11));
            btn.setBackground(new Color(240, 242, 255));
            btn.setForeground(new Color(37, 99, 235));
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> fF.setText(ex));
            exRow.add(btn);
        }
        g.gridx = 0; g.gridy = 2; g.gridwidth = 2;
        p.add(exRow, g);
        g.gridwidth = 1;

        JButton btn = calcButton("Get Name");
        btn.addActionListener(e -> {
            String formula = fF.getText().trim();
            if (formula.isEmpty()) { output("Enter a formula."); return; }
            String result = Nomenclature.nameWithExplanation(formula);
            String[] parts = result.split("\n", 2);
            String name  = parts[0];
            String rule  = parts.length > 1 ? parts[1] : "";
            StringBuilder sb = new StringBuilder();
            sb.append("Formula:  ").append(formula).append("\n");
            sb.append("─".repeat(44)).append("\n");
            sb.append("Name:     ").append(name).append("\n");
            if (!rule.isEmpty()) sb.append("Rule:     ").append(rule).append("\n");
            output(sb.toString());
        });
        fF.addActionListener(e -> btn.doClick());
        addCalcRow(p, g, 3, btn);
        return p;
    }

    // ── Helper ────────────────────────────────────────────────────────────────

}
