package calculators;

import java.util.Scanner;

/**
 * Thermodynamics calculations:
 *   - ΔG = ΔH - T·ΔS  (spontaneity)
 *   - ΔG° = -RT ln K  (equilibrium connection)
 *   - Non-standard ΔG = ΔG° + RT ln Q
 *   - Hess's law (combine reactions)
 *   - Clausius-Clapeyron (phase transitions)
 */
public class ThermodynamicsCalc implements Calculator {

    private static final double R = 8.314; // J/(mol·K)

    @Override
    public void run(Scanner sc) {
        boolean running = true;
        while (running) {
            System.out.println("\n=== THERMODYNAMICS CALCULATOR ===");
            System.out.println("  1. ΔG = ΔH - T·ΔS  (Gibbs free energy & spontaneity)");
            System.out.println("  2. ΔG° <-> K  (equilibrium constant from ΔG°)");
            System.out.println("  3. Non-standard ΔG = ΔG° + RT·ln Q");
            System.out.println("  4. Hess's Law  (combine reaction enthalpies)");
            System.out.println("  5. Standard enthalpy from ΔHf° values");
            System.out.println("  6. Clausius-Clapeyron (vapour pressure / boiling point)");
            System.out.println("  7. Reference: spontaneity table");
            System.out.println("  0. Back");
            System.out.print("Choice: ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1" -> gibbsFromHAndS(sc);
                case "2" -> gibbsAndK(sc);
                case "3" -> nonStandardG(sc);
                case "4" -> hessLaw(sc);
                case "5" -> enthalpyFromFormation(sc);
                case "6" -> clausiusClapeyron(sc);
                case "7" -> spontaneityTable();
                case "0" -> running = false;
                default  -> System.out.println("  Invalid choice.");
            }
        }
    }

    // ── 1. ΔG = ΔH - TΔS ─────────────────────────────────────────────────────

    private void gibbsFromHAndS(Scanner sc) {
        System.out.println("\n-- Gibbs Free Energy: ΔG = ΔH - T·ΔS --");
        double dH = PHCalculator.readDouble(sc, "ΔH (kJ/mol, can be negative): ");
        double dS = PHCalculator.readDouble(sc, "ΔS (J/(mol·K), can be negative): ");
        double T  = PHCalculator.readDouble(sc, "T (Kelvin): ");

        dH *= 1000; // convert kJ -> J
        double dG = dH - T * dS;

        System.out.printf("%n  ΔH  = %+.2f J/mol%n", dH);
        System.out.printf("  T·ΔS = %+.2f J/mol%n", T * dS);
        System.out.printf("  ΔG  = %+.2f J/mol  (%+.4f kJ/mol)%n", dG, dG / 1000);

        spontaneityComment(dG, dH, dS, T);
    }

    // ── 2. ΔG° <-> K ─────────────────────────────────────────────────────────

    private void gibbsAndK(Scanner sc) {
        System.out.println("\n-- ΔG° <-> Equilibrium Constant K --");
        System.out.println("  ΔG° = -RT ln K     K = e^(-ΔG°/RT)");
        System.out.println("  1. ΔG° -> K");
        System.out.println("  2. K -> ΔG°");
        System.out.print("Choice: ");
        String ch = sc.nextLine().trim();
        double T = PHCalculator.readDouble(sc, "Temperature T (K): ");

        if (ch.equals("1")) {
            double dG0 = PHCalculator.readDouble(sc, "ΔG° (kJ/mol): ") * 1000;
            double K   = Math.exp(-dG0 / (R * T));
            System.out.printf("%n  K = exp(-ΔG°/RT) = exp(%+.4f) = %.6e%n", -dG0 / (R * T), K);
            if (K > 1)      System.out.println("  K > 1: products favoured at equilibrium.");
            else if (K < 1) System.out.println("  K < 1: reactants favoured at equilibrium.");
            else             System.out.println("  K ≈ 1: neither strongly favoured.");
        } else {
            double K   = PHCalculator.readDouble(sc, "K (equilibrium constant): ");
            double dG0 = -R * T * Math.log(K);
            System.out.printf("%n  ΔG° = -RT ln K = %.4f J/mol  (%.4f kJ/mol)%n", dG0, dG0 / 1000);
        }
    }

    // ── 3. Non-standard ΔG ───────────────────────────────────────────────────

    private void nonStandardG(Scanner sc) {
        System.out.println("\n-- Non-Standard ΔG = ΔG° + RT·ln Q --");
        double dG0 = PHCalculator.readDouble(sc, "ΔG° (kJ/mol): ") * 1000;
        double Q   = PHCalculator.readDouble(sc, "Reaction quotient Q: ");
        double T   = PHCalculator.readDouble(sc, "Temperature T (K): ");

        double RTlnQ = R * T * Math.log(Q);
        double dG    = dG0 + RTlnQ;

        System.out.printf("%n  ΔG°     = %+.4f kJ/mol%n", dG0 / 1000);
        System.out.printf("  RT·ln Q = %+.4f kJ/mol  (Q = %.4e)%n", RTlnQ / 1000, Q);
        System.out.printf("  ΔG      = %+.4f kJ/mol%n", dG / 1000);

        if (dG < 0)       System.out.println("  Reaction proceeds spontaneously forward.");
        else if (dG > 0)  System.out.println("  Reaction proceeds spontaneously in reverse.");
        else               System.out.println("  System is at equilibrium (Q = K).");
    }

    // ── 4. Hess's Law ─────────────────────────────────────────────────────────

    private void hessLaw(Scanner sc) {
        System.out.println("\n-- Hess's Law: ΔH_total = Σ (multiplier × ΔH_i) --");
        System.out.println("  You can reverse a reaction (multiplier < 0).");
        int n = (int) PHCalculator.readDouble(sc, "Number of reactions to combine: ");
        double total = 0;
        for (int i = 1; i <= n; i++) {
            double dH   = PHCalculator.readDouble(sc, "ΔH of reaction " + i + " (kJ/mol): ");
            double mult = PHCalculator.readDouble(sc, "Multiplier (e.g. 1, -1, 2, 0.5): ");
            double contrib = mult * dH;
            System.out.printf("  Contribution %d: %+.4f × %+.4f = %+.4f kJ/mol%n", i, mult, dH, contrib);
            total += contrib;
        }
        System.out.printf("%n  ΔH_total = %+.4f kJ/mol%n", total);
    }

    // ── 5. Enthalpy from formation enthalpies ─────────────────────────────────

    private void enthalpyFromFormation(Scanner sc) {
        System.out.println("\n-- ΔH°rxn = Σ ΔHf°(products) - Σ ΔHf°(reactants) --");
        System.out.println("  Enter each species and its standard enthalpy of formation.");

        double sumProducts  = enterSpecies(sc, "product");
        double sumReactants = enterSpecies(sc, "reactant");
        double dH = sumProducts - sumReactants;

        System.out.printf("%n  Σ ΔHf°(products)  = %+.4f kJ/mol%n", sumProducts);
        System.out.printf("  Σ ΔHf°(reactants) = %+.4f kJ/mol%n", sumReactants);
        System.out.printf("  ΔH°rxn             = %+.4f kJ/mol%n", dH);
        if (dH < 0) System.out.println("  Exothermic reaction.");
        else         System.out.println("  Endothermic reaction.");
    }

    private double enterSpecies(Scanner sc, String side) {
        int n = (int) PHCalculator.readDouble(sc, "Number of " + side + "s: ");
        double sum = 0;
        for (int i = 1; i <= n; i++) {
            double coeff = PHCalculator.readDouble(sc, "  Stoichiometric coefficient of " + side + " " + i + ": ");
            double hf    = PHCalculator.readDouble(sc, "  ΔHf° of " + side + " " + i + " (kJ/mol): ");
            sum += coeff * hf;
        }
        return sum;
    }

    // ── 6. Clausius-Clapeyron ─────────────────────────────────────────────────

    private void clausiusClapeyron(Scanner sc) {
        System.out.println("\n-- Clausius-Clapeyron: ln(P2/P1) = -ΔHvap/R · (1/T2 - 1/T1) --");
        System.out.println("  1. Find P2 given P1, T1, T2, ΔHvap");
        System.out.println("  2. Find T2 given P1, P2, T1, ΔHvap");
        System.out.println("  3. Find ΔHvap from (P1,T1) and (P2,T2)");
        System.out.print("Choice: ");
        String ch = sc.nextLine().trim();

        switch (ch) {
            case "1" -> {
                double dHvap = PHCalculator.readDouble(sc, "ΔHvap (kJ/mol): ") * 1000;
                double P1    = PHCalculator.readDouble(sc, "P1 (any unit): ");
                double T1    = PHCalculator.readDouble(sc, "T1 (K): ");
                double T2    = PHCalculator.readDouble(sc, "T2 (K): ");
                double P2 = P1 * Math.exp(-dHvap / R * (1.0 / T2 - 1.0 / T1));
                System.out.printf("  P2 = %.4f (same units as P1)%n", P2);
            }
            case "2" -> {
                double dHvap = PHCalculator.readDouble(sc, "ΔHvap (kJ/mol): ") * 1000;
                double P1    = PHCalculator.readDouble(sc, "P1: ");
                double T1    = PHCalculator.readDouble(sc, "T1 (K): ");
                double P2    = PHCalculator.readDouble(sc, "P2 (same units as P1): ");
                double invT2 = 1.0 / T1 - R / dHvap * Math.log(P2 / P1);
                System.out.printf("  T2 = %.2f K  (%.2f °C)%n", 1.0 / invT2, 1.0 / invT2 - 273.15);
            }
            case "3" -> {
                double P1 = PHCalculator.readDouble(sc, "P1: ");
                double T1 = PHCalculator.readDouble(sc, "T1 (K): ");
                double P2 = PHCalculator.readDouble(sc, "P2: ");
                double T2 = PHCalculator.readDouble(sc, "T2 (K): ");
                double dHvap = -R * Math.log(P2 / P1) / (1.0 / T2 - 1.0 / T1);
                System.out.printf("  ΔHvap = %.4f J/mol  (%.4f kJ/mol)%n", dHvap, dHvap / 1000);
            }
            default -> System.out.println("  Invalid choice.");
        }
    }

    // ── 7. Reference table ────────────────────────────────────────────────────

    private void spontaneityTable() {
        System.out.println("\n-- Spontaneity Summary (ΔG = ΔH - TΔS) --");
        System.out.println("  ΔH    ΔS    Spontaneous?");
        System.out.println("  ─────────────────────────────────────────────");
        System.out.println("  -     +     Always  (ΔG always negative)");
        System.out.println("  +     -     Never   (ΔG always positive)");
        System.out.println("  -     -     Only at low T  (when |ΔH| > T|ΔS|)");
        System.out.println("  +     +     Only at high T (when T|ΔS| > ΔH)");
        System.out.println("\n  Crossover temperature T = ΔH/ΔS (where ΔG = 0)");
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private void spontaneityComment(double dG, double dH, double dS, double T) {
        System.out.println();
        if (dG < 0)      System.out.println("  -> Spontaneous (ΔG < 0)");
        else if (dG > 0) System.out.println("  -> Non-spontaneous (ΔG > 0)");
        else              System.out.println("  -> Equilibrium (ΔG = 0)");

        double crossover = dH / dS;
        if (dH != 0 && dS != 0) {
            System.out.printf("  Crossover temperature (ΔG=0): T = ΔH/ΔS = %.2f K (%.2f °C)%n",
                    crossover, crossover - 273.15);
            if (dH < 0 && dS < 0)
                System.out.println("  Spontaneous below T = " + String.format("%.0f", crossover) + " K");
            else if (dH > 0 && dS > 0)
                System.out.println("  Spontaneous above T = " + String.format("%.0f", crossover) + " K");
        }
    }
}
