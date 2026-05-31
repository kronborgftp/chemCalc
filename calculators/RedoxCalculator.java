package calculators;

import chemistry.FormulaParser;
import chemistry.PeriodicTable;
import chemistry.Element;

import java.util.*;

/**
 * Redox tools:
 *   1. Assign oxidation states to atoms in a formula or equation.
 *   2. Balance a redox equation via the half-reaction method (acid or base).
 */
public class RedoxCalculator implements Calculator {

    @Override
    public void run(Scanner sc) {
        boolean running = true;
        while (running) {
            System.out.println("\n=== REDOX CALCULATOR ===");
            System.out.println("  1. Assign oxidation states");
            System.out.println("  2. Balance redox half-reaction (acid solution)");
            System.out.println("  3. Balance redox half-reaction (basic solution)");
            System.out.println("  4. Combine two balanced half-reactions");
            System.out.println("  5. Full redox balance walkthrough (acid)");
            System.out.println("  6. Formal charge  (FC = V - L - B/2)");
            System.out.println("  7. Galvanic cell  (E°cell, ΔG°, K, electron flow)");
            System.out.println("  0. Back");
            System.out.print("Choice: ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1" -> oxidationStates(sc);
                case "2" -> balanceHalfReaction(sc, false);
                case "3" -> balanceHalfReaction(sc, true);
                case "4" -> combineHalfReactions(sc);
                case "5" -> fullWalkthrough(sc);
                case "6" -> formalCharge(sc);
                case "7" -> galvanicCell(sc);
                case "0" -> running = false;
                default  -> System.out.println("  Invalid choice.");
            }
        }
    }

    // ── 1. Oxidation state assignment ────────────────────────────────────────

    private void oxidationStates(Scanner sc) {
        System.out.println("\n-- Oxidation State Assignment --");
        System.out.println("  Enter a neutral molecule or an ion with its charge.");
        System.out.println("  Examples:  H2O   |   SO4   charge=-2   |   MnO4   charge=-1");
        System.out.print("  Formula: ");
        String formula = sc.nextLine().trim();
        int charge = 0;
        System.out.print("  Net charge (0 if neutral): ");
        String chStr = sc.nextLine().trim();
        if (!chStr.isEmpty()) {
            try { charge = Integer.parseInt(chStr); } catch (NumberFormatException ignored) {}
        }
        printOxStates(formula, charge);
    }

    /** Returns oxidation state analysis as a String (for GUI use). */
    public String getOxStatesString(String formula, int netCharge) {
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        java.io.PrintStream old = System.out;
        System.setOut(new java.io.PrintStream(buf));
        printOxStates(formula, netCharge);
        System.setOut(old);
        return buf.toString();
    }

    public void printOxStates(String formula, int netCharge) {
        Map<String, Integer> comp = FormulaParser.parse(formula);
        System.out.println("\n  Formula: " + formula + "  (net charge = " + netCharge + ")");

        // Priority rules (evaluated in order):
        // 1. Free element = 0
        // 2. F always -1
        // 3. O = -2 (except peroxides -1, OF2 +2)
        // 4. H = +1 (except metal hydrides -1; default +1 here)
        // 5. Group 1 = +1, Group 2 = +2
        // 6. Unknown: solve from sum = netCharge

        PeriodicTable pt = PeriodicTable.getInstance();
        Map<String, Integer> fixed   = new LinkedHashMap<>();
        Map<String, Integer> unknown = new LinkedHashMap<>();

        for (Map.Entry<String, Integer> entry : comp.entrySet()) {
            String sym = entry.getKey();
            int cnt    = entry.getValue();
            Element el = pt.get(sym);

            if (sym.equals("F"))               { fixed.put(sym, -1); continue; }
            if (sym.equals("O"))               { fixed.put(sym, -2); continue; } // simplified
            if (sym.equals("H"))               { fixed.put(sym, +1); continue; }
            if (el != null && el.group == 1 && el.atomicNumber != 1)
                                               { fixed.put(sym, +1); continue; }
            if (el != null && el.group == 2)   { fixed.put(sym, +2); continue; }
            unknown.put(sym, cnt);
        }

        // Sum of fixed contributions
        int fixedSum = 0;
        for (Map.Entry<String, Integer> e : fixed.entrySet())
            fixedSum += e.getValue() * comp.get(e.getKey());

        System.out.println("  Element   Count   Ox.State");
        System.out.println("  ─────────────────────────────");

        if (unknown.isEmpty()) {
            // All determined — just verify
            for (Map.Entry<String, Integer> e : fixed.entrySet())
                System.out.printf("  %-8s  %-6d  %+d%n", e.getKey(), comp.get(e.getKey()), e.getValue());
            System.out.printf("  Sum check: %d (should equal %d)%n", fixedSum, netCharge);
        } else if (unknown.size() == 1) {
            Map.Entry<String, Integer> entry = unknown.entrySet().iterator().next();
            String sym = entry.getKey();
            int cnt    = entry.getValue();
            int ox = (netCharge - fixedSum) / cnt;
            int rem = (netCharge - fixedSum) % cnt;

            for (Map.Entry<String, Integer> e : fixed.entrySet())
                System.out.printf("  %-8s  %-6d  %+d%n", e.getKey(), comp.get(e.getKey()), e.getValue());

            System.out.printf("  %-8s  %-6d  %+d", sym, cnt, ox);
            if (rem != 0) System.out.printf("  [Warning: remainder %d — check formula]", rem);
            System.out.println();
        } else {
            // Multiple unknowns — show what we know and note the rest is ambiguous
            for (Map.Entry<String, Integer> e : fixed.entrySet())
                System.out.printf("  %-8s  %-6d  %+d%n", e.getKey(), comp.get(e.getKey()), e.getValue());
            System.out.println("  The following elements have ambiguous oxidation states");
            System.out.println("  (need more context / known compound rules):");
            for (String sym : unknown.keySet())
                System.out.printf("  %-8s  %-6d  ?%n", sym, comp.get(sym));
            System.out.printf("  Remaining charge to distribute: %d%n", netCharge - fixedSum);
        }
    }

    // ── 2 & 3. Balance a single half-reaction ────────────────────────────────
    // Input format:  "MnO4 -> Mn2+"   or   "Fe2+ -> Fe3+"
    // The method adds H2O, H+, and e- to balance.

    private void balanceHalfReaction(Scanner sc, boolean basic) {
        System.out.println("\n-- Balance Half-Reaction (" + (basic ? "Basic" : "Acid") + " Solution) --");
        System.out.println("  Format: LHS -> RHS   (omit H2O, H+, e-)");
        System.out.println("  Example (acid): MnO4 -> Mn2+    |   Fe2+ -> Fe3+");
        System.out.print("  Enter half-reaction: ");
        String line = sc.nextLine().trim();

        String balanced = balanceHalf(line, basic);
        System.out.println("\n  Balanced: " + balanced);
    }

    /**
     * Balances a half-reaction:
     *  1. Balance all atoms except H and O.
     *  2. Balance O by adding H2O.
     *  3. Balance H by adding H+ (or H2O/OH- for basic).
     *  4. Balance charge by adding e-.
     */
    public String balanceHalf(String reaction, boolean basic) {
        String[] parts = reaction.split("->", 2);
        if (parts.length < 2) return "Error: missing ->";

        // Parse LHS and RHS species, stripping charges for atom counting
        String lhsRaw = parts[0].trim();
        String rhsRaw = parts[1].trim();

        // Atom counts (ignore charge symbols for parsing)
        Map<String, Integer> lhsAtoms = atomsInSide(lhsRaw);
        Map<String, Integer> rhsAtoms = atomsInSide(rhsRaw);

        // Charges on LHS and RHS (parse from ion notation like 2+ or 3-)
        int lhsCharge = parseSideCharge(lhsRaw);
        int rhsCharge = parseSideCharge(rhsRaw);

        // ── Step 1: atoms other than H and O should already match.
        //            (We trust user input; flag if non-H/O atoms don't balance.)
        for (String el : lhsAtoms.keySet()) {
            if (el.equals("H") || el.equals("O")) continue;
            int lCnt = lhsAtoms.getOrDefault(el, 0);
            int rCnt = rhsAtoms.getOrDefault(el, 0);
            if (lCnt != rCnt)
                return "Error: " + el + " atoms not balanced (" + lCnt + " vs " + rCnt
                        + "). Balance all non-H/O atoms first.";
        }

        // ── Step 2: balance oxygen with H2O
        int oLeft  = lhsAtoms.getOrDefault("O", 0);
        int oRight = rhsAtoms.getOrDefault("O", 0);
        int h2oNeeded = oLeft - oRight; // positive -> add to RHS; negative -> add to LHS
        // h2oNeeded > 0: add h2oNeeded H2O to RHS
        // h2oNeeded < 0: add -h2oNeeded H2O to LHS

        int hInH2O_lhs = 0, hInH2O_rhs = 0;
        if (h2oNeeded > 0)       hInH2O_rhs = 2 * h2oNeeded;
        else if (h2oNeeded < 0)  hInH2O_lhs = -2 * h2oNeeded;

        // ── Step 3: balance hydrogen with H+
        int hLeft  = lhsAtoms.getOrDefault("H", 0) + hInH2O_lhs;
        int hRight = rhsAtoms.getOrDefault("H", 0) + hInH2O_rhs;
        int hPlusNeeded = hRight - hLeft; // positive -> add to LHS; negative -> add to RHS
        // hPlusNeeded > 0: add to LHS; < 0: add to RHS

        int hPlusLhs = Math.max(0,  hPlusNeeded);
        int hPlusRhs = Math.max(0, -hPlusNeeded);

        // ── Step 4: balance charge with electrons (e-)
        // Total charge LHS = lhsCharge + (h+) on LHS
        int chargeLhs = lhsCharge + hPlusLhs;
        int chargeRhs = rhsCharge + hPlusRhs;

        int eLhs = 0, eRhs = 0;
        int chargeDiff = chargeLhs - chargeRhs;
        if (chargeDiff > 0)       eRhs = chargeDiff;   // electrons on RHS (oxidation)
        else if (chargeDiff < 0)  eLhs = -chargeDiff;  // electrons on LHS (reduction)

        // ── Build output string ───────────────────────────────────────────────
        StringBuilder lhs = new StringBuilder(lhsRaw);
        StringBuilder rhs = new StringBuilder(rhsRaw);

        if (!basic) {
            // Acid solution
            if (hPlusLhs > 0) lhs.append(" + ").append(coeff(hPlusLhs)).append("H+");
            if (hPlusRhs > 0) rhs.append(" + ").append(coeff(hPlusRhs)).append("H+");
        } else {
            // Basic solution: use OH- instead of H+
            // The trick: add H+ as above, then add equal OH- to both sides to neutralize.
            // Net: H+ + OH- -> H2O
            if (hPlusLhs > 0) {
                lhs.append(" + ").append(coeff(hPlusLhs)).append("OH-");
                rhs.append(" + ").append(coeff(hPlusLhs)).append("H2O");
            }
            if (hPlusRhs > 0) {
                rhs.append(" + ").append(coeff(hPlusRhs)).append("OH-");
                lhs.append(" + ").append(coeff(hPlusRhs)).append("H2O");
            }
        }

        if (h2oNeeded > 0) rhs.append(" + ").append(coeff(h2oNeeded)).append("H2O");
        if (h2oNeeded < 0) lhs.append(" + ").append(coeff(-h2oNeeded)).append("H2O");

        if (eLhs > 0) lhs.insert(0, coeff(eLhs) + "e-  +  ");
        if (eRhs > 0) rhs.append(" + ").append(coeff(eRhs)).append("e-");

        return lhs + "  ->  " + rhs;
    }

    // ── 4. Combine two half-reactions ────────────────────────────────────────

    private void combineHalfReactions(Scanner sc) {
        System.out.println("\n-- Combine Two Half-Reactions --");
        System.out.println("  Enter each balanced half-reaction, e.g.:");
        System.out.println("  Reduction:  5e-  +  8H+  +  MnO4-  ->  Mn2+  +  4H2O");
        System.out.println("  Oxidation:  Fe2+  ->  Fe3+  +  e-");
        System.out.print("  Reduction half-reaction: ");
        String red = sc.nextLine().trim();
        System.out.print("  Oxidation half-reaction: ");
        String ox  = sc.nextLine().trim();

        int eRed = countElectrons(red, true);
        int eOx  = countElectrons(ox, false);

        if (eRed <= 0 || eOx <= 0) {
            System.out.println("  Could not parse electron counts. Check that e- appears in each equation.");
            return;
        }

        long lcm = lcm(eRed, eOx);
        long multRed = lcm / eRed;
        long multOx  = lcm / eOx;

        System.out.printf("%n  Multiply reduction by %d and oxidation by %d to equalize %d e-%n",
                multRed, multOx, lcm);
        System.out.println("  Cancel " + lcm + " e- from each side.");
        System.out.println("\n  Combined equation (simplify manually if needed):");
        System.out.println("  " + multRed + "x [" + red + "]");
        System.out.println("  " + multOx  + "x [" + ox  + "]");
    }

    // ── 5. Full walkthrough ──────────────────────────────────────────────────

    private void fullWalkthrough(Scanner sc) {
        System.out.println("\n-- Full Redox Walkthrough (Acid Solution) --");
        System.out.println("  You supply two unbalanced half-reactions separately.");
        System.out.println("  The program balances each, equates electrons, and combines.\n");

        System.out.println("  Step 1: Reduction half-reaction");
        System.out.println("  (species being reduced, i.e. gaining electrons)");
        System.out.print("  Enter reduction half (e.g. MnO4 -> Mn2+): ");
        String redRaw = sc.nextLine().trim();

        System.out.println("\n  Step 2: Oxidation half-reaction");
        System.out.println("  (species being oxidized, i.e. losing electrons)");
        System.out.print("  Enter oxidation half (e.g. Fe2+ -> Fe3+): ");
        String oxRaw = sc.nextLine().trim();

        String redBal = balanceHalf(redRaw, false);
        String oxBal  = balanceHalf(oxRaw, false);

        System.out.println("\n  Balanced reduction:  " + redBal);
        System.out.println("  Balanced oxidation:  " + oxBal);

        int eRed = countElectrons(redBal, true);
        int eOx  = countElectrons(oxBal, false);

        if (eRed <= 0 || eOx <= 0) {
            System.out.println("\n  Could not automatically count electrons. Combine manually.");
            return;
        }

        long lcm = lcm(eRed, eOx);
        long mR  = lcm / eRed;
        long mO  = lcm / eOx;

        System.out.printf("%n  Electrons: reduction has %de-, oxidation has %de-%n", eRed, eOx);
        System.out.printf("  Multiply reduction x%d, oxidation x%d (equalize %de-)%n", mR, mO, lcm);
        System.out.println("\n  Final overall equation = " + mR + "×(reduction) + " + mO + "×(oxidation)");
        System.out.println("  Cancel water, H+, e- that appear on both sides.");
    }

    // ── 7. Galvanic cell ─────────────────────────────────────────────────────

    private void galvanicCell(Scanner sc) {
        System.out.println("\n-- Galvanic Cell Calculator --");
        System.out.println("  E°cell = E°cathode - E°anode");
        System.out.println("  The half-cell with the HIGHER reduction potential is the cathode (reduction).");
        System.out.println("  The half-cell with the LOWER  reduction potential is the anode  (oxidation).");
        System.out.println();
        System.out.print("  Name/description of half-cell 1: ");
        String name1 = sc.nextLine().trim();
        double e1 = PHCalculator.readDouble(sc, "  E° (reduction) for half-cell 1 (V): ");
        System.out.print("  Name/description of half-cell 2: ");
        String name2 = sc.nextLine().trim();
        double e2 = PHCalculator.readDouble(sc, "  E° (reduction) for half-cell 2 (V): ");
        int n = (int) PHCalculator.readDouble(sc, "  Electrons transferred (n): ");

        String cathode, anode;
        double eCathode, eAnode;
        if (e1 >= e2) {
            cathode = name1; eCathode = e1;
            anode   = name2; eAnode   = e2;
        } else {
            cathode = name2; eCathode = e2;
            anode   = name1; eAnode   = e1;
        }

        double eCell = eCathode - eAnode;
        double dG0   = -n * 96485 * eCell;
        double K     = Math.exp(-dG0 / (8.314 * 298.15));

        System.out.println("\n  ── Result ──────────────────────────────────────────");
        System.out.printf("  Cathode (reduction): %s  E° = +%.4f V%n", cathode, eCathode);
        System.out.printf("  Anode   (oxidation): %s  E° = +%.4f V%n", anode,   eAnode);
        System.out.printf("  E°cell = E°cathode - E°anode = %.4f - %.4f = %.4f V%n",
                eCathode, eAnode, eCell);
        if (eCell > 0) System.out.println("  E°cell > 0 → reaction is spontaneous.");
        else            System.out.println("  E°cell < 0 → reaction is non-spontaneous as written.");
        System.out.printf("  ΔG° = -nFE°cell = -(%.0f)(96485)(%.4f) = %.2f J/mol  (%.2f kJ/mol)%n",
                (double) n, eCell, dG0, dG0 / 1000);
        System.out.printf("  K   = e^(-ΔG°/RT) at 25°C = %.4e%n", K);
        System.out.println("\n  Electrons flow externally from ANODE → CATHODE.");
        System.out.println("  Current flows externally from CATHODE → ANODE.");
    }

    // ── 6. Formal charge ─────────────────────────────────────────────────────

    private void formalCharge(Scanner sc) {
        System.out.println("\n-- Formal Charge --");
        System.out.println("  FC = V - L - B/2");
        System.out.println("  V = valence electrons of the free atom");
        System.out.println("  L = lone pair (non-bonding) electrons on the atom");
        System.out.println("  B = bonding electrons shared with the atom (both shared electrons)");
        System.out.println("  (Each single bond contributes 2 to B, double bond 4, triple bond 6)");
        System.out.println();
        System.out.print("  Number of atoms to evaluate: ");
        int n = (int) PHCalculator.readDouble(sc, "");
        for (int i = 1; i <= n; i++) {
            System.out.println("  --- Atom " + i + " ---");
            double V  = PHCalculator.readDouble(sc, "  Valence electrons V (group number for main-group): ");
            double L  = PHCalculator.readDouble(sc, "  Lone-pair electrons L on this atom: ");
            double B  = PHCalculator.readDouble(sc, "  Bonding electrons B (2 per bond to this atom): ");
            double FC = V - L - B / 2.0;
            System.out.printf("  FC = %.0f - %.0f - %.0f/2 = %+.1f%n", V, L, B, FC);
            if (FC == 0)       System.out.println("  (neutral atom in this structure)");
            else if (FC > 0)   System.out.println("  (positive formal charge — fewer electrons than free atom)");
            else               System.out.println("  (negative formal charge — more electrons than free atom)");
        }
    }

    // ── Full redox equation balancer ────────────────────────────────────────

    /**
     * Balances a complete skeleton redox equation using the oxidation-number method.
     *
     * Input:  "H2SO4 + HI -> I2 + SO2"   (no H2O, H+, OH- or e-)
     * State symbols (aq), (s), (g), (l) are stripped automatically.
     * Returns a formatted multi-line result string for the GUI.
     */
    public String balanceFullRedox(String equation, boolean basic) {
        // ── 1. Parse ─────────────────────────────────────────────────────────
        String eq = equation.replace("→", "->").replace("⇌", "->").replace("=", "->");
        String[] halves = eq.split("->", 2);
        if (halves.length < 2) return "Error: use  ->  to separate reactants from products.";

        List<String> rSp = parseSpeciesList(halves[0]);
        List<String> pSp = parseSpeciesList(halves[1]);
        if (rSp.isEmpty() || pSp.isEmpty()) return "Error: could not parse species.";

        // ── 2. Oxidation states for each species ──────────────────────────────
        Map<String, Map<String, Integer>> rOx = new LinkedHashMap<>();
        Map<String, Map<String, Integer>> pOx = new LinkedHashMap<>();
        for (String s : rSp) rOx.put(s, getOxStatesNumeric(cleanFormula(s), parseIonCharge(s)));
        for (String s : pSp) pOx.put(s, getOxStatesNumeric(cleanFormula(s), parseIonCharge(s)));

        // ── 3. Detect which element is oxidized / reduced ─────────────────────
        Map<String, Integer> oxR = new LinkedHashMap<>(), oxP = new LinkedHashMap<>();
        for (Map<String, Integer> m : rOx.values())
            m.forEach((el, v) -> { if (!el.equals("H") && !el.equals("O")) oxR.putIfAbsent(el, v); });
        for (Map<String, Integer> m : pOx.values())
            m.forEach((el, v) -> { if (!el.equals("H") && !el.equals("O")) oxP.putIfAbsent(el, v); });

        String redEl = null, oxEl = null;
        int oxChange = 0, redChange = 0;
        for (String el : oxR.keySet()) {
            if (!oxP.containsKey(el)) continue;
            int before = oxR.get(el), after = oxP.get(el);
            if (after < before) { redEl = el; redChange = before - after; }  // gained e-
            if (after > before) { oxEl  = el; oxChange  = after - before; }  // lost  e-
        }
        if (redEl == null || oxEl == null)
            return "Could not detect an oxidation state change.\n" +
                   "Ensure the equation has one oxidized and one reduced species.\n" +
                   "Tip: include ionic charges if needed (e.g. Fe2+, MnO4-).";

        final String RED_EL = redEl, OX_EL = oxEl;

        // ── 4. Identify species containing each changed element ───────────────
        String redReact = rSp.stream()
            .filter(s -> atomCount(s, RED_EL) > 0).findFirst().orElse(null);
        String redProd  = pSp.stream()
            .filter(s -> atomCount(s, RED_EL) > 0).findFirst().orElse(null);
        String oxReact  = rSp.stream()
            .filter(s -> atomCount(s, OX_EL) > 0).findFirst().orElse(null);
        String oxProd   = pSp.stream()
            .filter(s -> atomCount(s, OX_EL) > 0).findFirst().orElse(null);

        if (redReact == null || redProd == null || oxReact == null || oxProd == null)
            return "Could not match all species to half-reactions.\n" +
                   "Make sure both sides of the arrow contain species with the changed elements.";

        // ── 5. Oxidation-number cross-multiplication ───────────────────────────
        // e- per formula unit of each key species (using monatomic-ion-corrected counts)
        int nRedR = atomCount(redReact, RED_EL);
        int nRedP = atomCount(redProd,  RED_EL);
        int nOxR  = atomCount(oxReact,  OX_EL);
        int nOxP  = atomCount(oxProd,   OX_EL);

        int ePerRedR = redChange * nRedR;   // e- gained per formula unit of reducing reactant
        int ePerOxR  = oxChange  * nOxR;    // e- lost  per formula unit of oxidising reactant

        long lcmE = lcm(ePerRedR, ePerOxR);
        int cRedR = (int)(lcmE / ePerRedR); // coefficient of the reducing reactant
        int cOxR  = (int)(lcmE / ePerOxR);  // coefficient of the oxidising reactant

        // Products: scale to match the changed element count
        int cRedP = cRedR * nRedR / nRedP;
        int cOxP  = cOxR  * nOxR  / nOxP;

        // ── 6. Remaining species get coefficient 1 (spectators) ───────────────
        Map<String, Integer> coeffs = new LinkedHashMap<>();
        for (String s : rSp) coeffs.put(s, 1);
        for (String s : pSp) coeffs.put(s, 1);
        coeffs.put(redReact, cRedR);
        coeffs.put(redProd,  cRedP);
        coeffs.put(oxReact,  cOxR);
        coeffs.put(oxProd,   cOxP);

        // ── 7. Count H, O on each side ────────────────────────────────────────
        int hL = 0, oL = 0, hR = 0, oR = 0;
        for (String s : rSp) {
            int c = coeffs.getOrDefault(s, 1);
            Map<String, Integer> comp = FormulaParser.parse(cleanFormula(s));
            hL += c * comp.getOrDefault("H", 0);
            oL += c * comp.getOrDefault("O", 0);
        }
        for (String s : pSp) {
            int c = coeffs.getOrDefault(s, 1);
            Map<String, Integer> comp = FormulaParser.parse(cleanFormula(s));
            hR += c * comp.getOrDefault("H", 0);
            oR += c * comp.getOrDefault("O", 0);
        }

        // ── 8. Balance O with H2O, then H with H+ or OH- ─────────────────────
        int h2oL = 0, h2oR = 0, hpL = 0, hpR = 0, ohR = 0, ohL = 0;
        int oDiff = oL - oR;
        if (oDiff > 0) { h2oR = oDiff; hR += 2 * oDiff; }   // add H2O to RHS
        if (oDiff < 0) { h2oL = -oDiff; hL += -2 * oDiff; } // add H2O to LHS

        int hDiff = hL - hR;
        if (!basic) {
            if (hDiff > 0) hpR = hDiff;   // add H+ to RHS
            else if (hDiff < 0) hpL = -hDiff;
        } else {
            // Basic: use OH- instead of H+
            if (hDiff > 0) { ohR = hDiff; h2oL += hDiff; }   // OH- on RHS, H2O on LHS
            if (hDiff < 0) { ohL = -hDiff; h2oR += -hDiff; } // OH- on LHS, H2O on RHS
        }

        // ── 9. Build output ───────────────────────────────────────────────────
        StringBuilder lhs = new StringBuilder();
        boolean firstL = true;
        for (String s : rSp) {
            if (!firstL) lhs.append(" + ");
            int c = coeffs.getOrDefault(s, 1);
            if (c != 1) lhs.append(c);
            lhs.append(s.trim());
            firstL = false;
        }
        if (h2oL > 0) lhs.append(" + ").append(h2oL == 1 ? "" : h2oL).append("H₂O");
        if (hpL  > 0) lhs.append(" + ").append(hpL  == 1 ? "" : hpL ).append("H⁺");
        if (ohL  > 0) lhs.append(" + ").append(ohL  == 1 ? "" : ohL ).append("OH⁻");

        StringBuilder rhs = new StringBuilder();
        boolean firstR = true;
        for (String s : pSp) {
            if (!firstR) rhs.append(" + ");
            int c = coeffs.getOrDefault(s, 1);
            if (c != 1) rhs.append(c);
            rhs.append(s.trim());
            firstR = false;
        }
        if (h2oR > 0) rhs.append(" + ").append(h2oR == 1 ? "" : h2oR).append("H₂O");
        if (hpR  > 0) rhs.append(" + ").append(hpR  == 1 ? "" : hpR ).append("H⁺");
        if (ohR  > 0) rhs.append(" + ").append(ohR  == 1 ? "" : ohR ).append("OH⁻");

        String solution = basic ? "Basic" : "Acidic";

        // sum of all integer coefficients (including H2O, H+/OH-)
        int sumCoeffs = 0;
        for (int c : coeffs.values()) sumCoeffs += c;
        sumCoeffs += h2oL + h2oR + hpL + hpR + ohL + ohR;

        return String.format(
            "Full Redox Balance  (%s solution)\n" +
            "────────────────────────────────────────────────────\n" +
            "Changed elements:\n" +
            "  %s  →  %s  (REDUCTION, gains %de⁻ per atom)\n" +
            "  %s  →  %s  (OXIDATION, loses %de⁻ per atom)\n\n" +
            "Electrons transferred:  %d e⁻  (LCM of %d and %d)\n\n" +
            "Balanced equation:\n  %s  →  %s\n\n" +
            "Sum of all coefficients:  %d",
            solution,
            RED_EL + "(" + oxR.get(RED_EL) + "→" + oxP.get(RED_EL) + ")", redEl, redChange,
            OX_EL  + "(" + oxR.get(OX_EL)  + "→" + oxP.get(OX_EL)  + ")", oxEl,  oxChange,
            (int) lcmE, ePerRedR, ePerOxR,
            lhs, rhs,
            sumCoeffs);
    }

    /** Numeric oxidation states. Returns a Map<element, oxState> for the given formula. */
    public Map<String, Integer> getOxStatesNumeric(String formula, int netCharge) {
        Map<String, Integer> comp = new LinkedHashMap<>(FormulaParser.parse(formula));

        // FormulaParser reads "Fe2+" as {Fe:2} (treats "2" as a subscript).
        // For simple monatomic ions (one element), when the count equals |charge|, it's the
        // charge magnitude — fix the count to 1.
        int chargeMag = Math.abs(netCharge);
        if (chargeMag > 1 && comp.size() == 1) {
            String el = comp.keySet().iterator().next();
            if (comp.get(el) == chargeMag) comp.put(el, 1);
        }
        PeriodicTable pt = PeriodicTable.getInstance();
        Map<String, Integer> result   = new LinkedHashMap<>();
        Map<String, Integer> unknowns = new LinkedHashMap<>();

        for (Map.Entry<String, Integer> entry : comp.entrySet()) {
            String sym = entry.getKey();
            Element el = pt.get(sym);
            if (sym.equals("F"))                                          { result.put(sym, -1); continue; }
            if (sym.equals("O"))                                          { result.put(sym, -2); continue; }
            if (sym.equals("H"))                                          { result.put(sym, +1); continue; }
            if (el != null && el.group == 1 && el.atomicNumber != 1)     { result.put(sym, +1); continue; }
            if (el != null && el.group == 2)                             { result.put(sym, +2); continue; }
            unknowns.put(sym, entry.getValue());
        }

        int fixedSum = result.entrySet().stream()
                .mapToInt(e -> e.getValue() * comp.getOrDefault(e.getKey(), 0)).sum();

        if (unknowns.size() == 1) {
            Map.Entry<String, Integer> u = unknowns.entrySet().iterator().next();
            result.put(u.getKey(), (netCharge - fixedSum) / u.getValue());
        }
        return result;
    }

    /**
     * Split "H2SO4 + HI" or "MnO4- + Fe2+" into individual species.
     * Splits on "+" only when surrounded by whitespace (so "Fe2+" is kept intact).
     * Falls back to letter-adjacent "+" for compact input like "H2SO4+HI".
     */
    private List<String> parseSpeciesList(String side) {
        // Replace separator "+" (has at least one space on either side) with a safe delimiter
        String norm = side.trim()
                .replaceAll("\\s+\\+\\s+", "|")           // "A + B" → "A|B"
                .replaceAll("(?<=[A-Za-z0-9\\)\\]])\\+(?=[A-Za-z\\(])", "|"); // "HI+I2" → "HI|I2"
        List<String> list = new ArrayList<>();
        for (String tok : norm.split("\\|")) {
            String t = tok.trim();
            if (!t.isEmpty()) list.add(t);
        }
        return list;
    }

    /**
     * Returns the true atom count for an element in a species string,
     * applying the monatomic-ion correction (e.g. "Fe2+" → Fe count = 1, not 2).
     */
    private int atomCount(String species, String element) {
        Map<String, Integer> comp = new LinkedHashMap<>(FormulaParser.parse(cleanFormula(species)));
        int chargeMag = Math.abs(parseIonCharge(species));
        if (chargeMag > 1 && comp.size() == 1) {
            String el = comp.keySet().iterator().next();
            if (comp.get(el) == chargeMag) comp.put(el, 1);
        }
        return comp.getOrDefault(element, 0);
    }

    /** Strip only state symbols — FormulaParser already skips charge signs like + and -. */
    private String cleanFormula(String s) {
        return s.replaceAll("\\(aq\\)|\\(s\\)|\\(g\\)|\\(l\\)", "").trim();
    }

    /**
     * Parse ionic charge from a species string correctly for both:
     *   "MnO4-"  → charge -1  (the "4" is an oxygen subscript, only "-" is the charge)
     *   "Fe2+"   → charge +2  (single-element ion; "2" is the charge magnitude, not a subscript)
     *   "Cr2O72-"→ charge -2
     * Achieved by scanning the formula left-to-right to find where the formula ends,
     * then parsing what remains as the charge string.
     */
    private int parseIonCharge(String s) {
        String f = s.replaceAll("\\(aq\\)|\\(s\\)|\\(g\\)|\\(l\\)", "").replaceAll("\\^.*$", "").trim();
        int n = f.length(), pos = 0, elementCount = 0, lastSubscript = 1;

        while (pos < n) {
            char c = f.charAt(pos);
            if (Character.isUpperCase(c)) {
                elementCount++;
                pos++;
                while (pos < n && Character.isLowerCase(f.charAt(pos))) pos++;
                int subStart = pos;
                while (pos < n && Character.isDigit(f.charAt(pos))) pos++;
                String sub = f.substring(subStart, pos);
                lastSubscript = sub.isEmpty() ? 1 : Integer.parseInt(sub);
            } else if (c == '(' || c == '[') {
                elementCount = 2;  // treat grouped formulas as multi-element
                pos++;
                int depth = 1;
                while (pos < n && depth > 0) {
                    char x = f.charAt(pos++);
                    if (x == '(' || x == '[') depth++;
                    else if (x == ')' || x == ']') depth--;
                }
                while (pos < n && Character.isDigit(f.charAt(pos))) pos++;
            } else {
                break;  // hit a non-formula character (start of charge)
            }
        }

        String chargeStr = f.substring(pos).trim();
        if (chargeStr.isEmpty()) return 0;

        // Special case: single-element ion (e.g. "Fe2+") where the subscript IS the charge magnitude.
        // FormulaParser consumes the "2" as subscript; we detect it here and return the real charge.
        if (elementCount == 1 && lastSubscript > 1
                && (chargeStr.equals("+") || chargeStr.equals("-"))) {
            return (chargeStr.equals("+") ? 1 : -1) * lastSubscript;
        }

        // General case: chargeStr is "2-", "-", "+", "2+", etc.
        int sign = chargeStr.endsWith("+") ? 1 : -1;
        String magStr = chargeStr.substring(0, chargeStr.length() - 1).trim();
        try {
            return sign * (magStr.isEmpty() ? 1 : Integer.parseInt(magStr));
        } catch (NumberFormatException e) {
            return sign;
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Map<String, Integer> atomsInSide(String side) {
        // Could be "MnO4" or "Mn2+" — strip charge notation and parse
        String clean = side.replaceAll("[0-9]*[+\\-]", "").replace("^", "");
        return FormulaParser.parse(clean);
    }

    private int parseSideCharge(String side) {
        // Looks for patterns like 2+, 3-, +, - at the end of each ion
        int total = 0;
        // Split by spaces/+ to handle multi-species sides like "Fe2+ + H2O"
        String[] tokens = side.split("\\s*\\+\\s*|\\s+");
        for (String tok : tokens) {
            tok = tok.trim();
            if (tok.isEmpty()) continue;
            // find trailing charge e.g. "^2+" or "2+" or "+" or "-"
            java.util.regex.Matcher m = java.util.regex.Pattern
                    .compile("\\^?([0-9]*?)([+-])$").matcher(tok);
            if (m.find()) {
                String numStr = m.group(1);
                String sign   = m.group(2);
                int mag = numStr.isEmpty() ? 1 : Integer.parseInt(numStr);
                total += sign.equals("+") ? mag : -mag;
            }
        }
        return total;
    }

    private int countElectrons(String reaction, boolean isReduction) {
        // For reduction:  e- appears on LHS  =>  "Ne-  +  ..."
        // For oxidation:  e- appears on RHS  =>  "...  +  Ne-"
        String[] sides = reaction.split("->");
        if (sides.length < 2) return 0;
        String target = isReduction ? sides[0] : sides[1];
        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("([0-9]*)\\s*e-").matcher(target);
        if (m.find()) {
            String n = m.group(1);
            return n.isEmpty() ? 1 : Integer.parseInt(n);
        }
        return 0;
    }

    private String coeff(int n) { return n == 1 ? "" : String.valueOf(n); }

    private long gcd(long a, long b) { return b == 0 ? a : gcd(b, a % b); }
    private long lcm(long a, long b) { return a / gcd(a, b) * b; }
}
