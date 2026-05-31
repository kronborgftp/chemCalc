package gui;

import chemistry.FormulaParser;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class OrgChemPanel extends BaseCalcPanel {

    public OrgChemPanel() { super("Organic Chemistry"); }

    @Override
    protected void buildUI() {
        inputPanel.setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(TAB_FONT);
        mainTabs = tabs;
        tabs.addTab("Degree of Polymerisation", polyTab());
        tabs.addTab("Combustion Analysis",       combustionTab());
        tabs.addTab("Functional Groups",         textPane(functionalGroups()));
        tabs.addTab("Chirality",                 textPane(chirality()));
        inputPanel.add(tabs, BorderLayout.CENTER);
    }

    // ── Tab 1: Degree of polymerisation ──────────────────────────────────────

    private JPanel polyTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("n = M_polymer / M_repeat.  Give either a formula (auto MW) or a manual M_repeat."), g);
        g.gridwidth = 1;

        JTextField repeatF  = addRow(p, g, 1, "Repeat-unit formula (e.g. C10H8O4):");
        JTextField manualMF = addRow(p, g, 2, "M_repeat (g/mol)  — if no formula:");
        JTextField polyMF   = addRow(p, g, 3, "Polymer MW (g/mol):");
        JTextField polyKgF  = addRow(p, g, 4, "Polymer MW (kg/mol) — if not in g/mol:");

        repeatF.setFont(MONO_FONT);

        calcBtn(p, g, 5, "Calculate n", () -> {
            double Mrepeat;
            String formula = repeatF.getText().trim();
            String manualM = manualMF.getText().trim();
            if (!formula.isEmpty()) {
                Mrepeat = FormulaParser.molarMass(formula);
                if (Mrepeat <= 0) { output("Could not parse formula."); return; }
            } else if (!manualM.isEmpty()) {
                Mrepeat = parse(manualMF);
            } else {
                output("Enter either a repeat-unit formula or M_repeat."); return;
            }

            double Mpoly;
            if (!polyMF.getText().trim().isEmpty()) {
                Mpoly = parse(polyMF);
            } else if (!polyKgF.getText().trim().isEmpty()) {
                Mpoly = parse(polyKgF) * 1000;
            } else {
                output("Enter polymer MW (g/mol or kg/mol)."); return;
            }

            double n = Mpoly / Mrepeat;

            StringBuilder sb = new StringBuilder("Degree of Polymerisation\n");
            sb.append("─".repeat(40)).append("\n");
            if (!formula.isEmpty())
                sb.append(String.format("Repeat unit: %s%n  M_repeat = %.2f g/mol%n%n", formula, Mrepeat));
            else
                sb.append(String.format("M_repeat = %.2f g/mol%n%n", Mrepeat));
            sb.append(String.format("M_polymer = %.0f g/mol%n", Mpoly));
            sb.append("─".repeat(40)).append("\n");
            sb.append(String.format("n = %.0f / %.2f = %.1f  (≈ %d)%n", Mpoly, Mrepeat, n, Math.round(n)));
            output(sb.toString());
        });
        return p;
    }

    // ── Tab 2: Combustion analysis ────────────────────────────────────────────

    private JPanel combustionTab() {
        JPanel p = tabPanel();
        GridBagConstraints g = gbc();

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        p.add(hint("Burn CₓHᵧOᵤNᵥ in excess O₂. Collect CO₂ and H₂O to find the empirical formula."), g);
        g.gridwidth = 1;

        JTextField sampleF = addRow(p, g, 1, "Sample mass (g):");
        JTextField co2F    = addRow(p, g, 2, "CO₂ collected (g):");
        JTextField h2oF    = addRow(p, g, 3, "H₂O collected (g):");
        JTextField n2F     = addRow(p, g, 4, "N₂ collected (g, 0 if absent):");
        n2F.setText("0");

        calcBtn(p, g, 5, "Find Empirical Formula", () -> {
            double mSample = parse(sampleF);
            double mCO2   = parse(co2F);
            double mH2O   = parse(h2oF);
            double mN2    = parse(n2F);

            if (mSample <= 0 || mCO2 < 0 || mH2O < 0) {
                output("Enter positive values for sample, CO₂ and H₂O masses."); return;
            }

            double molC = mCO2 / 44.01;
            double molH = mH2O / 18.02 * 2;
            double molN = mN2  / 28.02 * 2;

            double massC = molC * 12.011;
            double massH = molH * 1.008;
            double massN = molN * 14.007;
            double massO = mSample - massC - massH - massN;

            if (massO < -0.01) { output("Mass of O is negative — check inputs."); return; }
            double molO = Math.max(0, massO / 16.00);

            double[] mols  = {molC, molH, molO, molN};
            double[] masses = {massC, massH, massO, massN};
            String[] syms  = {"C", "H", "O", "N"};
            double minMol  = Arrays.stream(mols).filter(v -> v > 1e-6).min().orElse(1);
            double[] ratios = Arrays.stream(mols).map(v -> v / minMol).toArray();

            int mult = 1;
            outer:
            for (int m = 1; m <= 8; m++) {
                boolean ok = true;
                for (double r : ratios)
                    if (Math.abs(r * m - Math.round(r * m)) > 0.08) { ok = false; break; }
                if (ok) { mult = m; break outer; }
            }

            StringBuilder sb = new StringBuilder("Combustion Analysis\n");
            sb.append("─".repeat(44)).append("\n");
            sb.append(String.format("%-4s  %-10s  %-10s  %-8s  ×%d%n", "Elem","mass(g)","mol","ratio",mult));
            sb.append("─".repeat(44)).append("\n");
            for (int i = 0; i < 4; i++) {
                if (mols[i] < 1e-6) continue;
                sb.append(String.format("%-4s  %-10.4f  %-10.6f  %-8.4f  %d%n",
                        syms[i], masses[i], mols[i], ratios[i], Math.round(ratios[i] * mult)));
            }
            sb.append("─".repeat(44)).append("\n");
            sb.append("Empirical formula: ");
            for (int i = 0; i < 4; i++) {
                long count = Math.round(ratios[i] * mult);
                if (count == 0) continue;
                sb.append(syms[i]);
                if (count > 1) sb.append(count);
            }
            sb.append("\n");
            output(sb.toString());
        });
        return p;
    }

    // ── Reference tab builder ─────────────────────────────────────────────────
    // Returns a JScrollPane that scrolls its own content independently.
    // No outer inputScroll exists (removed from BaseCalcPanel), so there is no
    // nesting issue — the wheel goes directly to this scroll pane.

    private JScrollPane textPane(String content) {
        JTextArea area = new JTextArea(content);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setBackground(Theme.OUTPUT_BG);
        area.setForeground(Theme.OUTPUT_FG);
        area.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        area.setLineWrap(false);
        JScrollPane sp = new JScrollPane(area,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(18);
        sp.getHorizontalScrollBar().setUnitIncrement(18);
        return sp;
    }

    // ── Tab 3: Functional groups reference ───────────────────────────────────

    private String functionalGroups() {
        return """
Functional Groups — Exam Quick Reference
═══════════════════════════════════════════════════════════════════════════
Group             Structure           How to identify
───────────────────────────────────────────────────────────────────────────
Alcohol           R−OH                −OH bonded directly to C (not to C=O)
Ether             R−O−R'              O bridging two C atoms, no C=O nearby
Aldehyde          R−CHO               C=O at chain END; one H on the carbonyl C
Ketone            R−CO−R'             C=O in chain INTERIOR; C on both sides
Carboxylic acid   R−COOH              C=O AND −OH on the SAME carbon  (acidic)
Ester             R−COO−R'            C=O with an O−C (no OH) on the carbonyl C
Amine             R−NH₂ / NHR / NR₂   N with H or alkyl groups; no adjacent C=O
Amide             R−CO−NH−            C=O directly bonded to N (the peptide bond)
Nitrile           R−C≡N               Triple bond C≡N at chain end
Aromatic          benzene ring         Flat ring, alternating bonds, 6 π electrons

KEY DISTINCTIONS  (what is bonded to the C=O carbon?)
───────────────────────────────────────────────────────────────────────────
  C=O + −OH       →  Carboxylic acid
  C=O + −O−C      →  Ester          (no H on the oxygen)
  C=O + −N        →  Amide
  C=O at end + H  →  Aldehyde
  C=O flanked by C→  Ketone
  −O− with no C=O →  Ether
  −OH with no C=O →  Alcohol

IUPAC SUFFIXES
───────────────────────────────────────────────────────────────────────────
  -ane  alkane      -ol    alcohol       -al    aldehyde
  -ene  alkene      -one   ketone        -oic acid  carboxylic acid
  -yne  alkyne      -amine amine         -amide amide      -yl…ate ester

CARBON-CHAIN PREFIXES
───────────────────────────────────────────────────────────────────────────
  1C meth-   2C eth-   3C prop-   4C but-   5C pent-
  6C hex-    7C hept-  8C oct-    9C non-   10C dec-
""";
    }

    // ── Tab 4: Chirality reference ────────────────────────────────────────────

    private String chirality() {
        return """
Chirality — Exam Quick Reference
═══════════════════════════════════════════════════════════════════════════

CHIRAL CENTRE (stereogenic carbon)
───────────────────────────────────────────────────────────────────────────
A carbon atom is chiral if it is bonded to FOUR DIFFERENT substituents.
  • Must be sp³ (tetrahedral) — no double or triple bond on that carbon
  • All four attached groups must differ from each other
  • Two identical groups on the same carbon → NOT chiral

Quick symmetry test:
  Plane of symmetry exists  → achiral (meso or symmetric molecule)
  No plane of symmetry      → chiral (optically active)

COMMON EXAM TRAPS
───────────────────────────────────────────────────────────────────────────
  ✗  −CH₂− has two H's                → NOT a chiral centre
  ✓  −CH(A)(B)(C)− all different      → IS a chiral centre
  ✗  Internal mirror plane in molecule → meso (achiral overall)
  ✗  sp² carbon (C=C or C=O)          → planar, CANNOT be chiral

STEREOISOMERS OVERVIEW
───────────────────────────────────────────────────────────────────────────
  Enantiomers    Non-superimposable mirror images.
                 Same physical properties; rotate polarised light oppositely.
                 Labelled R / S via CIP priority rules.

  Diastereomers  Stereoisomers that are NOT mirror images.
                 Different physical/chemical properties.
                 Includes cis/trans (geometric) isomers.

  Meso compound  Has chiral centres but an internal symmetry plane.
                 → Achiral overall (optically inactive).

R / S ASSIGNMENT (CIP rules)
───────────────────────────────────────────────────────────────────────────
  1. Rank the four substituents by atomic number (highest priority = 1).
  2. Orient so the LOWEST-priority group points away from you.
  3. Trace 1 → 2 → 3:
       Clockwise      →  R  (Rectus)
       Anticlockwise  →  S  (Sinister)

  Tie-break: follow the chain outward to the first point of difference.
  Lone pairs count as atomic number 0 (lowest possible).

OPTICAL ACTIVITY
───────────────────────────────────────────────────────────────────────────
  (+) / d  →  dextrorotatory  (rotates plane-polarised light clockwise)
  (−) / l  →  levorotatory   (rotates anticlockwise)
  Racemic mixture: 50 % (+) + 50 % (−) → no net optical rotation
""";
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

}
