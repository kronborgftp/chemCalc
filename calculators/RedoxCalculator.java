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
                    .compile("\\^?([0-9]*)([+-])$").matcher(tok);
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
