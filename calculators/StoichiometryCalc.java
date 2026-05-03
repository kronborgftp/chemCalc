package calculators;

import chemistry.FormulaParser;
import chemistry.PeriodicTable;
import chemistry.Element;

import java.util.*;

/**
 * Stoichiometry toolkit:
 *   - Molar mass from formula
 *   - Moles <-> mass <-> volume (ideal gas at STP / custom T,P)
 *   - Limiting reagent + theoretical yield
 *   - Percent yield
 *   - Empirical / molecular formula from % composition
 */
public class StoichiometryCalc implements Calculator {

    @Override
    public void run(Scanner sc) {
        boolean running = true;
        while (running) {
            System.out.println("\n=== STOICHIOMETRY CALCULATOR ===");
            System.out.println("  1. Molar mass of a formula");
            System.out.println("  2. Moles <-> mass  (n = m/M)");
            System.out.println("  3. Ideal gas: moles <-> volume  (PV = nRT)");
            System.out.println("  4. Limiting reagent & theoretical yield");
            System.out.println("  5. Percent yield");
            System.out.println("  6. Empirical formula from % mass composition");
            System.out.println("  7. Molecular formula from empirical formula + molar mass");
            System.out.println("  8. Periodic table lookup");
            System.out.println("  0. Back");
            System.out.print("Choice: ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1" -> molarMass(sc);
                case "2" -> molesMass(sc);
                case "3" -> idealGas(sc);
                case "4" -> limitingReagent(sc);
                case "5" -> percentYield(sc);
                case "6" -> empiricalFormula(sc);
                case "7" -> molecularFormula(sc);
                case "8" -> periodicLookup(sc);
                case "0" -> running = false;
                default  -> System.out.println("  Invalid choice.");
            }
        }
    }

    // ── 1. Molar mass ─────────────────────────────────────────────────────────

    private void molarMass(Scanner sc) {
        System.out.println("\n-- Molar Mass --");
        System.out.print("  Formula (e.g. H2O, Ca(OH)2, Fe2(SO4)3): ");
        String formula = sc.nextLine().trim();
        Map<String, Integer> comp = FormulaParser.parse(formula);
        PeriodicTable pt = PeriodicTable.getInstance();

        System.out.println();
        System.out.printf("  %-6s  %-8s  %-12s  %-12s%n", "Elem", "Count", "Atomic mass", "Contribution");
        System.out.println("  ─────────────────────────────────────────────────");
        double total = 0;
        for (Map.Entry<String, Integer> e : comp.entrySet()) {
            Element el = pt.get(e.getKey());
            if (el == null) { System.out.println("  [?] Unknown: " + e.getKey()); continue; }
            double contrib = el.atomicMass * e.getValue();
            total += contrib;
            System.out.printf("  %-6s  %-8d  %-12.4f  %.4f%n",
                    e.getKey(), e.getValue(), el.atomicMass, contrib);
        }
        System.out.println("  ─────────────────────────────────────────────────");
        System.out.printf("  M(%s) = %.4f g/mol%n", formula, total);
    }

    // ── 2. Moles <-> mass ─────────────────────────────────────────────────────

    private void molesMass(Scanner sc) {
        System.out.println("\n-- Moles <-> Mass:  n = m / M --");
        System.out.print("  Formula: ");
        String formula = sc.nextLine().trim();
        double M = FormulaParser.molarMass(formula);
        System.out.printf("  M(%s) = %.4f g/mol%n%n", formula, M);
        System.out.println("  1. mass -> moles  (n = m/M)");
        System.out.println("  2. moles -> mass  (m = n·M)");
        System.out.print("Choice: ");
        String ch = sc.nextLine().trim();
        if (ch.equals("1")) {
            double m = PHCalculator.readDouble(sc, "Mass m (g): ");
            System.out.printf("  n = %.4f / %.4f = %.6f mol%n", m, M, m / M);
        } else {
            double n = PHCalculator.readDouble(sc, "Moles n (mol): ");
            System.out.printf("  m = %.4f × %.4f = %.6f g%n", n, M, n * M);
        }
    }

    // ── 3. Ideal gas ──────────────────────────────────────────────────────────

    private void idealGas(Scanner sc) {
        System.out.println("\n-- Ideal Gas Law:  PV = nRT --");
        System.out.println("  R = 8.314 J/(mol·K) = 0.08206 L·atm/(mol·K)");
        System.out.println("  Solve for:  1=P  2=V  3=n  4=T");
        System.out.print("Solve for: ");
        String solve = sc.nextLine().trim();

        System.out.println("  Units: P in atm, V in L, n in mol, T in K");
        double R = 0.08206;

        switch (solve) {
            case "1" -> {
                double V = PHCalculator.readDouble(sc, "V (L): ");
                double n = PHCalculator.readDouble(sc, "n (mol): ");
                double T = PHCalculator.readDouble(sc, "T (K): ");
                System.out.printf("  P = nRT/V = %.4f atm%n", n * R * T / V);
            }
            case "2" -> {
                double P = PHCalculator.readDouble(sc, "P (atm): ");
                double n = PHCalculator.readDouble(sc, "n (mol): ");
                double T = PHCalculator.readDouble(sc, "T (K): ");
                System.out.printf("  V = nRT/P = %.4f L%n", n * R * T / P);
            }
            case "3" -> {
                double P = PHCalculator.readDouble(sc, "P (atm): ");
                double V = PHCalculator.readDouble(sc, "V (L): ");
                double T = PHCalculator.readDouble(sc, "T (K): ");
                System.out.printf("  n = PV/RT = %.4f mol%n", P * V / (R * T));
            }
            case "4" -> {
                double P = PHCalculator.readDouble(sc, "P (atm): ");
                double V = PHCalculator.readDouble(sc, "V (L): ");
                double n = PHCalculator.readDouble(sc, "n (mol): ");
                System.out.printf("  T = PV/(nR) = %.4f K  (%.2f °C)%n",
                        P * V / (n * R), P * V / (n * R) - 273.15);
            }
            default -> System.out.println("  Invalid.");
        }
    }

    // ── 4. Limiting reagent ───────────────────────────────────────────────────

    private void limitingReagent(Scanner sc) {
        System.out.println("\n-- Limiting Reagent & Theoretical Yield --");
        System.out.println("  Enter the balanced equation stoichiometry and available masses.\n");

        int nReactants = (int) PHCalculator.readDouble(sc, "Number of reactants: ");
        String[] formulas = new String[nReactants];
        double[] coeffs   = new double[nReactants];
        double[] masses   = new double[nReactants];
        double[] moles    = new double[nReactants];
        double[] molarMasses = new double[nReactants];

        for (int i = 0; i < nReactants; i++) {
            System.out.print("  Formula of reactant " + (i + 1) + ": ");
            formulas[i]    = sc.nextLine().trim();
            coeffs[i]      = PHCalculator.readDouble(sc, "  Stoichiometric coefficient: ");
            masses[i]      = PHCalculator.readDouble(sc, "  Available mass (g): ");
            molarMasses[i] = FormulaParser.molarMass(formulas[i]);
            moles[i]       = masses[i] / molarMasses[i];
            System.out.printf("  => %.4f g / %.4f g/mol = %.4f mol%n%n",
                    masses[i], molarMasses[i], moles[i]);
        }

        // Moles of reaction possible from each reactant = moles[i] / coeffs[i]
        double minRatio = Double.MAX_VALUE;
        int limitingIdx = 0;
        for (int i = 0; i < nReactants; i++) {
            double ratio = moles[i] / coeffs[i];
            System.out.printf("  %s: %.4f mol / coeff %.1f = %.4f mol-rxn%n",
                    formulas[i], moles[i], coeffs[i], ratio);
            if (ratio < minRatio) { minRatio = ratio; limitingIdx = i; }
        }

        System.out.println("\n  Limiting reagent: " + formulas[limitingIdx]);
        System.out.printf("  Extent of reaction ξ = %.4f mol%n", minRatio);

        // Product yield
        System.out.print("\n  Formula of product for yield calculation: ");
        String prodFormula = sc.nextLine().trim();
        double prodCoeff   = PHCalculator.readDouble(sc, "  Stoichiometric coefficient of product: ");
        double prodMass_M  = FormulaParser.molarMass(prodFormula);
        double theorMol    = minRatio * prodCoeff;
        double theorMass   = theorMol * prodMass_M;

        System.out.printf("%n  Theoretical yield: %.4f mol = %.4f g of %s%n",
                theorMol, theorMass, prodFormula);
    }

    // ── 5. Percent yield ──────────────────────────────────────────────────────

    private void percentYield(Scanner sc) {
        System.out.println("\n-- Percent Yield --");
        double actual    = PHCalculator.readDouble(sc, "Actual yield (g): ");
        double theoretic = PHCalculator.readDouble(sc, "Theoretical yield (g): ");
        System.out.printf("  %% yield = (%.4f / %.4f) × 100 = %.2f%%%n",
                actual, theoretic, (actual / theoretic) * 100);
    }

    // ── 6. Empirical formula from % composition ───────────────────────────────

    private void empiricalFormula(Scanner sc) {
        System.out.println("\n-- Empirical Formula from % Mass Composition --");
        System.out.println("  Enter each element and its mass percentage. Percentages must sum to ~100%.");
        int n = (int) PHCalculator.readDouble(sc, "Number of elements: ");

        String[] syms  = new String[n];
        double[] ratios = new double[n];
        double sumPct  = 0;

        for (int i = 0; i < n; i++) {
            System.out.print("  Element " + (i + 1) + " symbol: ");
            syms[i] = sc.nextLine().trim();
            double pct = PHCalculator.readDouble(sc, "  Mass % of " + syms[i] + ": ");
            sumPct += pct;
            double M = PeriodicTable.getInstance().getMolarMass(syms[i]);
            if (M == 0) { System.out.println("  [Warning] Unknown element " + syms[i]); M = 1; }
            ratios[i] = pct / M;
        }
        System.out.printf("  Sum of percentages: %.2f%%%n", sumPct);

        // Divide by smallest ratio
        double minRatio = Double.MAX_VALUE;
        for (double r : ratios) if (r < minRatio) minRatio = r;
        double[] normalized = new double[n];
        for (int i = 0; i < n; i++) normalized[i] = ratios[i] / minRatio;

        // Round to nearest integer (or simple fraction)
        System.out.print("\n  Empirical formula: ");
        for (int i = 0; i < n; i++) {
            long subscript = Math.round(normalized[i]);
            System.out.print(syms[i] + (subscript == 1 ? "" : subscript));
        }
        System.out.println();

        System.out.println("  (Raw ratios before rounding: ");
        for (int i = 0; i < n; i++)
            System.out.printf("    %s: %.3f%n", syms[i], normalized[i]);
        System.out.println("  )");
    }

    // ── 7. Molecular formula ──────────────────────────────────────────────────

    private void molecularFormula(Scanner sc) {
        System.out.println("\n-- Molecular Formula from Empirical Formula + Molar Mass --");
        System.out.print("  Empirical formula: ");
        String empFormula = sc.nextLine().trim();
        double empMass  = FormulaParser.molarMass(empFormula);
        double molMass  = PHCalculator.readDouble(sc, "Molar mass of molecular compound (g/mol): ");
        long n = Math.round(molMass / empMass);
        System.out.printf("  n = M/M_emp = %.2f / %.2f ≈ %d%n", molMass, empMass, n);
        System.out.print("  Molecular formula: ");
        Map<String, Integer> empComp = FormulaParser.parse(empFormula);
        for (Map.Entry<String, Integer> e : empComp.entrySet()) {
            long sub = e.getValue() * n;
            System.out.print(e.getKey() + (sub == 1 ? "" : sub));
        }
        System.out.println();
        System.out.printf("  M(molecular) = %.4f g/mol%n", empMass * n);
    }

    // ── 8. Periodic table lookup ──────────────────────────────────────────────

    private void periodicLookup(Scanner sc) {
        System.out.println("\n-- Periodic Table Lookup --");
        System.out.print("  Element symbol or name: ");
        String query = sc.nextLine().trim();

        PeriodicTable pt = PeriodicTable.getInstance();
        Element el = pt.get(query);
        if (el == null) {
            // Try by name (case-insensitive)
            for (Element e : pt.getAll()) {
                if (e.name.equalsIgnoreCase(query)) { el = e; break; }
            }
        }
        if (el == null) {
            System.out.println("  Element not found: " + query);
            return;
        }
        System.out.println("\n  " + el);
        System.out.printf("  Molar mass:         %.4f g/mol%n", el.atomicMass);
        System.out.printf("  Electronegativity:  %.2f (Pauling)%n", el.electronegativity);
        System.out.printf("  Period: %d   Group: %d%n", el.period, el.group);
    }
}
