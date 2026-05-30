package calculators;

import java.util.Scanner;

/**
 * Chemical kinetics:
 *   - Arrhenius equation: k = A·exp(-Ea/RT)
 *   - Find Ea from two (k, T) data points
 *   - Integrated rate laws (0th, 1st, 2nd order)
 *   - Half-life for each order
 *   - Rate from rate law: rate = k[A]^m[B]^n
 */
public class KineticsCalc implements Calculator {

    private static final double R = 8.314; // J/(mol·K)

    @Override
    public void run(Scanner sc) {
        boolean running = true;
        while (running) {
            System.out.println("\n=== KINETICS CALCULATOR ===");
            System.out.println("  1. Arrhenius: find k at new T  (given Ea and A, or two k/T pairs)");
            System.out.println("  2. Activation energy Ea  (from two k/T pairs)");
            System.out.println("  3. Integrated rate law  (find [A] at time t)");
            System.out.println("  4. Half-life");
            System.out.println("  5. Rate from rate law:  rate = k[A]^m[B]^n");
            System.out.println("  6. Determine reaction order from experimental data");
            System.out.println("  7. Graham's law of effusion  (rate ∝ 1/√M)");
            System.out.println("  0. Back");
            System.out.print("Choice: ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1" -> arrhenius(sc);
                case "2" -> findEa(sc);
                case "3" -> integratedRateLaw(sc);
                case "4" -> halfLife(sc);
                case "5" -> rateFromLaw(sc);
                case "6" -> determineOrder(sc);
                case "7" -> grahamsLaw(sc);
                case "0" -> running = false;
                default  -> System.out.println("  Invalid choice.");
            }
        }
    }

    // ── 1. Arrhenius ─────────────────────────────────────────────────────────

    private void arrhenius(Scanner sc) {
        System.out.println("\n-- Arrhenius Equation: k = A·exp(-Ea/RT) --");
        System.out.println("  1. Given A and Ea: find k at T");
        System.out.println("  2. Given k1 at T1: find k2 at T2  (ln(k2/k1) = -Ea/R·(1/T2 - 1/T1))");
        System.out.print("Choice: ");
        String ch = sc.nextLine().trim();

        if (ch.equals("1")) {
            double A  = PHCalculator.readDouble(sc, "Pre-exponential factor A (same units as k): ");
            double Ea = PHCalculator.readDouble(sc, "Activation energy Ea (kJ/mol): ") * 1000;
            double T  = PHCalculator.readDouble(sc, "Temperature T (K): ");
            double k  = A * Math.exp(-Ea / (R * T));
            System.out.printf("%n  k = %.6e (same units as A)%n", k);
            System.out.printf("  Ea/RT = %.4f%n", Ea / (R * T));
        } else {
            double Ea = PHCalculator.readDouble(sc, "Activation energy Ea (kJ/mol): ") * 1000;
            double k1 = PHCalculator.readDouble(sc, "k1: ");
            double T1 = PHCalculator.readDouble(sc, "T1 (K): ");
            double T2 = PHCalculator.readDouble(sc, "T2 (K): ");
            double k2 = k1 * Math.exp(-Ea / R * (1.0 / T2 - 1.0 / T1));
            System.out.printf("%n  k2 = %.6e%n", k2);
            System.out.printf("  ln(k2/k1) = %.4f%n", Math.log(k2 / k1));
        }
    }

    // ── 2. Find Ea ────────────────────────────────────────────────────────────

    private void findEa(Scanner sc) {
        System.out.println("\n-- Activation Energy from Two Rate Constants --");
        System.out.println("  ln(k2/k1) = -Ea/R · (1/T2 - 1/T1)");
        double k1 = PHCalculator.readDouble(sc, "k1: ");
        double T1 = PHCalculator.readDouble(sc, "T1 (K): ");
        double k2 = PHCalculator.readDouble(sc, "k2: ");
        double T2 = PHCalculator.readDouble(sc, "T2 (K): ");

        double Ea = -R * Math.log(k2 / k1) / (1.0 / T2 - 1.0 / T1);
        System.out.printf("%n  Ea = %.4f J/mol  (%.4f kJ/mol)%n", Ea, Ea / 1000);

        // Find A using k = A·exp(-Ea/RT1)
        double A = k1 / Math.exp(-Ea / (R * T1));
        System.out.printf("  Pre-exponential factor A = %.6e%n", A);
    }

    // ── 3. Integrated rate laws ───────────────────────────────────────────────

    private void integratedRateLaw(Scanner sc) {
        System.out.println("\n-- Integrated Rate Laws --");
        System.out.println("  0th order:  [A] = [A]₀ - k·t");
        System.out.println("  1st order:  [A] = [A]₀·e^(-k·t)   (ln[A] = ln[A]₀ - k·t)");
        System.out.println("  2nd order:  1/[A] = 1/[A]₀ + k·t");
        System.out.println();
        System.out.println("  Solve for:");
        System.out.println("  1. [A] at time t   (given [A]₀, k, t)");
        System.out.println("  2. time t          (given [A]₀, [A], k)");
        System.out.println("  3. k               (given [A]₀, [A], t)");
        System.out.print("Solve for: ");
        String solve = sc.nextLine().trim();

        System.out.println("  Order (0, 1, or 2): ");
        int order = (int) PHCalculator.readDouble(sc, "Order: ");

        switch (solve) {
            case "1" -> {
                double A0 = PHCalculator.readDouble(sc, "[A]₀ (mol/L): ");
                double k  = PHCalculator.readDouble(sc, "k: ");
                double t  = PHCalculator.readDouble(sc, "t: ");
                double A  = calcA(order, A0, k, t);
                System.out.printf("%n  [A] = %.6e mol/L%n", A);
            }
            case "2" -> {
                double A0 = PHCalculator.readDouble(sc, "[A]₀ (mol/L): ");
                double A  = PHCalculator.readDouble(sc, "[A]  (mol/L): ");
                double k  = PHCalculator.readDouble(sc, "k: ");
                double t  = calcT(order, A0, A, k);
                System.out.printf("%n  t = %.6e%n", t);
            }
            case "3" -> {
                double A0 = PHCalculator.readDouble(sc, "[A]₀ (mol/L): ");
                double A  = PHCalculator.readDouble(sc, "[A]  (mol/L): ");
                double t  = PHCalculator.readDouble(sc, "t: ");
                double k  = calcK(order, A0, A, t);
                System.out.printf("%n  k = %.6e%n", k);
            }
            default -> System.out.println("  Invalid choice.");
        }
    }

    public static double calcA(int order, double A0, double k, double t) {
        return switch (order) {
            case 0 -> A0 - k * t;
            case 1 -> A0 * Math.exp(-k * t);
            case 2 -> 1.0 / (1.0 / A0 + k * t);
            default -> Double.NaN;
        };
    }

    public static double calcT(int order, double A0, double A, double k) {
        return switch (order) {
            case 0 -> (A0 - A) / k;
            case 1 -> Math.log(A0 / A) / k;
            case 2 -> (1.0 / A - 1.0 / A0) / k;
            default -> Double.NaN;
        };
    }

    public static double calcK(int order, double A0, double A, double t) {
        return switch (order) {
            case 0 -> (A0 - A) / t;
            case 1 -> Math.log(A0 / A) / t;
            case 2 -> (1.0 / A - 1.0 / A0) / t;
            default -> Double.NaN;
        };
    }

    // ── 4. Half-life ──────────────────────────────────────────────────────────

    private void halfLife(Scanner sc) {
        System.out.println("\n-- Half-Life --");
        System.out.println("  0th order: t½ = [A]₀ / (2k)");
        System.out.println("  1st order: t½ = ln(2) / k  [independent of [A]₀]");
        System.out.println("  2nd order: t½ = 1 / (k·[A]₀)");
        int order = (int) PHCalculator.readDouble(sc, "Order (0, 1, 2): ");
        double k  = PHCalculator.readDouble(sc, "k: ");
        double A0 = 1.0;
        if (order != 1) A0 = PHCalculator.readDouble(sc, "[A]₀ (mol/L): ");

        double t12 = switch (order) {
            case 0 -> A0 / (2 * k);
            case 1 -> Math.log(2) / k;
            case 2 -> 1.0 / (k * A0);
            default -> Double.NaN;
        };

        System.out.printf("%n  t½ = %.6e (same time units as k)%n", t12);

        if (order == 1) {
            System.out.println("\n  First-order decay table:");
            System.out.println("  n half-lives    Fraction remaining");
            double frac = 1.0;
            for (int i = 0; i <= 5; i++) {
                System.out.printf("  %-15d %.4f  (%.2f%%)%n", i, frac, frac * 100);
                frac /= 2;
            }
        }
    }

    // ── 5. Rate from rate law ─────────────────────────────────────────────────

    private void rateFromLaw(Scanner sc) {
        System.out.println("\n-- Rate = k·[A]^m·[B]^n --");
        double k = PHCalculator.readDouble(sc, "Rate constant k: ");
        int nSpec = (int) PHCalculator.readDouble(sc, "Number of reactants in rate law: ");
        double rate = k;
        for (int i = 1; i <= nSpec; i++) {
            double conc  = PHCalculator.readDouble(sc, "  [" + (char)('A' + i - 1) + "] (mol/L): ");
            double order = PHCalculator.readDouble(sc, "  Order for this reactant: ");
            rate *= Math.pow(conc, order);
        }
        System.out.printf("%n  rate = %.6e mol/(L·s)%n", rate);
    }

    // ── 7. Graham's law ───────────────────────────────────────────────────────

    private void grahamsLaw(Scanner sc) {
        System.out.println("\n-- Graham's Law of Effusion --");
        System.out.println("  rate₁/rate₂ = √(M₂/M₁)");
        System.out.println("  Lighter gases effuse faster.");
        System.out.println("  1. Find rate ratio given molar masses");
        System.out.println("  2. Find unknown molar mass given rate ratio and one molar mass");
        System.out.print("Choice: ");
        String ch = sc.nextLine().trim();
        if (ch.equals("1")) {
            double M1 = PHCalculator.readDouble(sc, "Molar mass M₁ (g/mol): ");
            double M2 = PHCalculator.readDouble(sc, "Molar mass M₂ (g/mol): ");
            double ratio = Math.sqrt(M2 / M1);
            System.out.printf("%n  rate₁/rate₂ = √(M₂/M₁) = √(%.4f/%.4f) = %.4f%n", M2, M1, ratio);
            System.out.printf("  Gas 1 effuses %.4f× %s than Gas 2.%n",
                    ratio > 1 ? ratio : 1.0/ratio,
                    ratio > 1 ? "faster" : "slower");
        } else {
            System.out.println("  Known: M₁ and rate₁/rate₂. Solve for M₂.");
            System.out.println("  M₂ = M₁ · (rate₁/rate₂)²");
            double M1    = PHCalculator.readDouble(sc, "Known molar mass M₁ (g/mol): ");
            double ratio = PHCalculator.readDouble(sc, "rate₁/rate₂: ");
            double M2    = M1 * ratio * ratio;
            System.out.printf("%n  M₂ = %.4f g/mol%n", M2);
        }
    }

    // ── 6. Determine order from data ──────────────────────────────────────────

    private void determineOrder(Scanner sc) {
        System.out.println("\n-- Determine Reaction Order from [A] vs t Data --");
        System.out.println("  Enter two data points ([A]₁, t₁) and ([A]₂, t₂).");
        System.out.println("  The program tests 0th, 1st, and 2nd order fits.\n");
        double A1 = PHCalculator.readDouble(sc, "[A]₁ (mol/L): ");
        double t1 = PHCalculator.readDouble(sc, "t₁: ");
        double A2 = PHCalculator.readDouble(sc, "[A]₂ (mol/L): ");
        double t2 = PHCalculator.readDouble(sc, "t₂: ");

        double dt = t2 - t1;
        System.out.println("\n  Order  k (implied)      Fit check");
        System.out.println("  ──────────────────────────────────────────────────");
        double k0 = (A1 - A2) / dt;
        double k1 = Math.log(A1 / A2) / dt;
        double k2 = (1.0 / A2 - 1.0 / A1) / dt;
        System.out.printf("  0th    k = %+.4e    [A]=A1-k*dt -> %.4e%n", k0, A1 - k0 * dt);
        System.out.printf("  1st    k = %+.4e    [A]=A1*exp(-k*dt) -> %.4e%n", k1, A1 * Math.exp(-k1 * dt));
        System.out.printf("  2nd    k = %+.4e    [A]=1/(1/A1+k*dt) -> %.4e%n", k2, 1.0 / (1.0 / A1 + k2 * dt));
        System.out.println("  (Compare each predicted [A]₂ to the measured " + A2 + " to identify the correct order)");
    }
}
