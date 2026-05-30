package gui;

import chemistry.Element;
import chemistry.FormulaParser;
import chemistry.PeriodicTable;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class VESPRPanel extends BaseCalcPanel {

    public VESPRPanel() { super("VSEPR / Molecular Geometry"); }

    @Override
    protected void buildUI() {
        inputPanel.setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(TAB_FONT);
        mainTabs = tabs;
        tabs.addTab("From Formula", formulaTab());
        tabs.addTab("Manual",       manualTab());
        tabs.addTab("Reference",    referenceTab());
        inputPanel.add(tabs, BorderLayout.CENTER);
    }

    // ── From Formula ──────────────────────────────────────────────────────────

    private JPanel formulaTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("Central atom auto-detected (lowest electronegativity, non-H). " +
                "For N₂H₄, H₂O₂ etc. fill in 'Atoms bonded to ONE central atom'."), g);
        g.gridwidth = 1;

        JTextField fmF     = addRow(p, g, 1, "Formula (e.g. H2O, NH3, SF6, N2H4):");
        JTextField chF     = addRow(p, g, 2, "Net charge (0 for neutral):");
        JTextField centF   = addRow(p, g, 3, "Central atom override (blank = auto):");
        JTextField bondedF = addRow(p, g, 4, "Atoms bonded to ONE central atom (blank = auto):");
        chF.setText("0");

        calcBtn(p, g, 5, "Analyse", () -> {
            String formula = fmF.getText().trim();
            int charge = chF.getText().trim().isEmpty() ? 0 : (int) calculators.PHCalculator.parseExpression(chF.getText().trim());
            Map<String, Integer> comp = FormulaParser.parse(formula);
            if (comp.isEmpty()) { output("Could not parse formula."); return; }

            PeriodicTable pt = PeriodicTable.getInstance();
            String centralSym = centF.getText().trim().isEmpty() ? identifyCentral(comp) : centF.getText().trim();
            Element central = pt.get(centralSym);
            if (central == null) { output("Element not found: " + centralSym); return; }
            int veC = valenceElectrons(central);
            if (veC < 0) { output("Transition metal — use Manual tab instead."); return; }

            int nCentral = comp.getOrDefault(centralSym, 1);
            Map<String, Integer> periph = new LinkedHashMap<>(comp);
            periph.put(centralSym, nCentral - 1);
            if (periph.getOrDefault(centralSym, 0) <= 0) periph.remove(centralSym);

            int bp, lp;
            String bondedTxt = bondedF.getText().trim();
            if (!bondedTxt.isEmpty()) {
                bp = (int) calculators.PHCalculator.parseExpression(bondedTxt);
                lp = Math.max(0, (veC - bp) / 2);
            } else if (nCentral > 1) {
                output("'" + centralSym + "' appears " + nCentral + " times.\nFill in 'Atoms bonded to ONE central atom'.");
                return;
            } else {
                int totalVE = veC;
                for (Map.Entry<String, Integer> e : periph.entrySet()) {
                    Element el = pt.get(e.getKey());
                    if (el != null) totalVE += valenceElectrons(el) * e.getValue();
                }
                totalVE -= charge;
                int nPeriph = periph.values().stream().mapToInt(Integer::intValue).sum();
                int remaining = totalVE - 2 * nPeriph;
                int neededByPeriph = periph.entrySet().stream()
                        .filter(e -> !e.getKey().equals("H"))
                        .mapToInt(e -> 6 * e.getValue()).sum();
                remaining -= Math.min(neededByPeriph, remaining);
                lp = Math.max(0, remaining / 2);
                bp = nPeriph;
                int centralE = 2 * bp + 2 * lp;
                if (centralE < 8 && central.period <= 2 && lp == 0)
                    ; // multiple bonds present — bp stays same, lp=0 is correct
            }

            output(buildResult(centralSym, bp, lp, periph, "Formula: " + formula));
        });
        return p;
    }

    // ── Manual ────────────────────────────────────────────────────────────────

    private JPanel manualTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("Enter the number of bonding domains and lone pairs on the central atom directly."), g);
        g.gridwidth = 1;

        JTextField bpF = addRow(p, g, 1, "Bonding domains (atoms bonded to central):");
        JTextField lpF = addRow(p, g, 2, "Lone pairs on central atom:");

        calcBtn(p, g, 3, "Show Geometry", () -> {
            int bp = (int) calculators.PHCalculator.parseExpression(bpF.getText().trim());
            int lp = (int) calculators.PHCalculator.parseExpression(lpF.getText().trim());
            output(buildResult("?", bp, lp, Collections.emptyMap(), "Manual input"));
        });
        return p;
    }

    // ── Reference table ───────────────────────────────────────────────────────

    private JPanel referenceTab() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CARD_BG);
        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ta.setBackground(new Color(248, 249, 252));

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-4s %-4s %-22s %-22s %-32s %s%n",
                "BP", "LP", "Electron Geometry", "Molecular Geometry", "Bond Angle(s)", "Planar"));
        sb.append("─".repeat(100)).append("\n");
        int[][] cases = {{2,0},{3,0},{2,1},{4,0},{3,1},{2,2},{5,0},{4,1},{3,2},{2,3},{6,0},{5,1},{4,2}};
        for (int[] c : cases) {
            sb.append(String.format("%-4d %-4d %-22s %-22s %-32s %s%n",
                    c[0], c[1],
                    electronGeometry(c[0] + c[1]),
                    molecularGeometry(c[0], c[1]),
                    bondAngle(c[0], c[1]),
                    isPlanar(c[0], c[1]) ? "yes" : "no"));
        }
        sb.append("\nBP = bonding domains  LP = lone pairs on central atom");
        ta.setText(sb.toString());
        p.add(new JScrollPane(ta), BorderLayout.CENTER);
        return p;
    }

    // ── Result builder ────────────────────────────────────────────────────────

    private String buildResult(String central, int bp, int lp, Map<String, Integer> periph, String header) {
        int sn = bp + lp;
        return String.format(
                "%s\n─────────────────────────────────────\n" +
                "Central atom:       %s\n" +
                "Bonding domains:    %d\n" +
                "Lone pairs:         %d\n" +
                "Steric number:      %d\n\n" +
                "Electron geometry:  %s\n" +
                "Molecular geometry: %s\n" +
                "Bond angle(s):      %s\n" +
                "Planar:             %s\n" +
                "Polarity:           %s",
                header, central, bp, lp, sn,
                electronGeometry(sn),
                molecularGeometry(bp, lp),
                bondAngle(bp, lp),
                isPlanar(bp, lp) ? "yes" : "no",
                polarity(bp, lp, periph));
    }

    // ── VSEPR logic ───────────────────────────────────────────────────────────

    private String electronGeometry(int sn) {
        return switch (sn) {
            case 2 -> "linear";
            case 3 -> "trigonal planar";
            case 4 -> "tetrahedral";
            case 5 -> "trigonal bipyramidal";
            case 6 -> "octahedral";
            default -> "unknown";
        };
    }

    private String molecularGeometry(int bp, int lp) {
        return switch (bp * 10 + lp) {
            case 20 -> "linear";
            case 30 -> "trigonal planar";
            case 21 -> "bent (~120°)";
            case 40 -> "tetrahedral";
            case 31 -> "trigonal pyramidal";
            case 22 -> "bent (~104.5°)";
            case 50 -> "trigonal bipyramidal";
            case 41 -> "seesaw";
            case 32 -> "T-shaped";
            case 23 -> "linear";
            case 60 -> "octahedral";
            case 51 -> "square pyramidal";
            case 42 -> "square planar";
            default -> "unknown";
        };
    }

    private String bondAngle(int bp, int lp) {
        int sn = bp + lp;
        if (sn == 2) return "180°";
        if (sn == 3 && lp == 0) return "120°";
        if (sn == 3 && lp == 1) return "~118°";
        if (sn == 4 && lp == 0) return "109.5°";
        if (sn == 4 && lp == 1) return "~107°";
        if (sn == 4 && lp == 2) return "~104.5°";
        if (sn == 5 && lp == 0) return "90° (ax-eq) and 120° (eq-eq)";
        if (sn == 5 && lp == 1) return "~90° and ~120°";
        if (sn == 5 && lp == 2) return "~90°";
        if (sn == 5 && lp == 3) return "180°";
        if (sn == 6 && lp == 0) return "90°";
        if (sn == 6 && lp == 1) return "~90°";
        if (sn == 6 && lp == 2) return "90°";
        return "—";
    }

    private boolean isPlanar(int bp, int lp) {
        int sn = bp + lp;
        if (sn <= 3) return true;
        if (sn == 4 && lp == 2) return true;
        if (sn == 6 && lp == 2) return true;
        return false;
    }

    private String polarity(int bp, int lp, Map<String, Integer> periph) {
        boolean allSame = periph.size() <= 1;
        if (lp == 0 && allSame) return "nonpolar (symmetric, identical ligands)";
        if (bp == 2 && lp == 3) return "nonpolar (linear — dipoles cancel)";
        if (bp == 4 && lp == 2 && allSame) return "nonpolar (square planar — dipoles cancel)";
        if (lp > 0) return "polar (lone pair → asymmetric electron distribution)";
        return "polar (different peripheral atoms → net dipole)";
    }

    private int valenceElectrons(Element e) {
        int g = e.group;
        if (g == 1)  return 1;
        if (g == 2)  return 2;
        if (g == 13) return 3;
        if (g == 14) return 4;
        if (g == 15) return 5;
        if (g == 16) return 6;
        if (g == 17) return 7;
        if (g == 18) return 8;
        return -1;
    }

    private String identifyCentral(Map<String, Integer> comp) {
        String central = null;
        double lowestEN = Double.MAX_VALUE;
        for (String sym : comp.keySet()) {
            if (sym.equals("H")) continue;
            Element e = PeriodicTable.getInstance().get(sym);
            if (e != null && e.electronegativity < lowestEN) {
                lowestEN = e.electronegativity;
                central = sym;
            }
        }
        return central != null ? central : comp.keySet().iterator().next();
    }

    private void calcBtn(JPanel p, GridBagConstraints g, int row, String label, Runnable action) {
        JButton btn = calcButton(label);
        btn.addActionListener(e -> {
            try { action.run(); }
            catch (Exception ex) { output("Error: " + ex.getMessage()); }
        });
        addCalcRow(p, g, row, btn);
    }
}
