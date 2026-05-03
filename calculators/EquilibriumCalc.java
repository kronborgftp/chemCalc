package calculators;

import java.util.Scanner;

/**
 * Chemical equilibrium:
 *   - ICE table solver (quadratic and small-x approximation)
 *   - Kc from equilibrium concentrations
 *   - Kp <-> Kc conversion
 *   - Reaction quotient Q and direction prediction
 *   - Le Chatelier shift direction
 */
public class EquilibriumCalc implements Calculator {

    private static final double R = 0.08206; // L·atm/(mol·K) for Kp calculations

    @Override
    public void run(Scanner sc) {
        boolean running = true;
        while (running) {
            System.out.println("\n=== EQUILIBRIUM CALCULATOR ===");
            System.out.println("  1. ICE table — find equilibrium concentrations from Ka/Kb");
            System.out.println("  2. Kc from measured equilibrium concentrations");
            System.out.println("  3. Kp <-> Kc  conversion");
            System.out.println("  4. Reaction quotient Q — which direction does reaction proceed?");
            System.out.println("  5. Percent dissociation / ionisation");
            System.out.println("  6. Solubility product Ksp <-> molar solubility");
            System.out.println("  0. Back");
            System.out.print("Choice: ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1" -> iceTable(sc);
                case "2" -> kcFromConcentrations(sc);
                case "3" -> kpKcConvert(sc);
                case "4" -> reactionQuotient(sc);
                case "5" -> percentDissociation(sc);
                case "6" -> kspSolubility(sc);
                case "0" -> running = false;
                default  -> System.out.println("  Invalid choice.");
            }
        }
    }

    // ── 1. ICE table ──────────────────────────────────────────────────────────

    private void iceTable(Scanner sc) {
        System.out.println("\n-- ICE Table (A <=> B + C, one unknown x) --");
        System.out.println("  Covers:  HA <=> H+ + A-   (Ka)");
        System.out.println("           B + H2O <=> BH+ + OH-  (Kb)");
        System.out.println("           General: aA <=> bB + cC  (K)");
        System.out.println();
        System.out.println("  Reaction type:");
        System.out.println("  1. Weak acid HA  (Ka given)");
        System.out.println("  2. Weak base B   (Kb given)");
        System.out.println("  3. General equilibrium A <=> products  (K and stoichiometry given)");
        System.out.print("Type: ");
        String type = sc.nextLine().trim();

        switch (type) {
            case "1" -> iceWeakAcid(sc);
            case "2" -> iceWeakBase(sc);
            case "3" -> iceGeneral(sc);
            default  -> System.out.println("  Invalid.");
        }
    }

    private void iceWeakAcid(Scanner sc) {
        System.out.println("\n  HA  <=>  H+  +  A-");
        System.out.println("  I:  C      0     0");
        System.out.println("  C: -x     +x    +x");
        System.out.println("  E: C-x     x     x");
        System.out.println("  Ka = x² / (C - x)  =>  x² + Ka·x - Ka·C = 0");

        double Ka = PHCalculator.readDouble(sc, "Ka: ");
        double C  = PHCalculator.readDouble(sc, "Initial [HA] (mol/L): ");

        double x = PHCalculator.solveWeakAcidQuadratic(Ka, C);
        printICEAcid(Ka, C, x);
    }

    private void iceWeakBase(Scanner sc) {
        System.out.println("\n  B  +  H2O  <=>  BH+  +  OH-");
        double Kb = PHCalculator.readDouble(sc, "Kb: ");
        double C  = PHCalculator.readDouble(sc, "Initial [B] (mol/L): ");

        double x   = PHCalculator.solveWeakAcidQuadratic(Kb, C);
        double pOH = -Math.log10(x);
        double pH  = 14 - pOH;

        System.out.printf("%n  x = [OH-] = %.4e mol/L%n", x);
        System.out.printf("  [B] at equil.  = %.4e mol/L%n", C - x);
        System.out.printf("  pOH = %.4f   pH = %.4f%n", pOH, pH);
        System.out.printf("  %% ionised = %.2f%%%n", (x / C) * 100);
        fivePercentCheck(x, C);
    }

    private void iceGeneral(Scanner sc) {
        System.out.println("\n  General:  aA  <=>  products");
        System.out.println("  Assumption: single reactant A, stoichiometry  A <=> b·B  (common form).");
        System.out.println("  For more complex cases, set up the algebra manually.");
        double K  = PHCalculator.readDouble(sc, "Equilibrium constant K: ");
        double C0 = PHCalculator.readDouble(sc, "Initial concentration [A]₀ (mol/L): ");
        double b  = PHCalculator.readDouble(sc, "Stoichiometric coefficient of product: ");

        // K = (b*x)^b / (C0 - x)  — only solvable in closed form for b=1,2
        if (Math.abs(b - 1) < 1e-9) {
            // K = x / (C0 - x)  => x = K*C0/(1+K)
            double x = K * C0 / (1 + K);
            System.out.printf("%n  x = %.6e mol/L%n", x);
            System.out.printf("  [A]_eq = %.6e mol/L%n", C0 - x);
            System.out.printf("  [B]_eq = %.6e mol/L%n", x);
        } else if (Math.abs(b - 2) < 1e-9) {
            // K = (2x)^2 / (C0 - x) = 4x^2/(C0-x)
            // 4x^2 + K*x - K*C0 = 0
            double x = (-K + Math.sqrt(K * K + 16 * K * C0)) / 8.0;
            System.out.printf("%n  x = %.6e mol/L%n", x);
            System.out.printf("  [A]_eq = %.6e mol/L%n", C0 - x);
            System.out.printf("  [B]_eq = %.6e mol/L%n", 2 * x);
        } else {
            // Small-x approximation: x ≈ (K * C0^a)^(1/(a+b))
            System.out.println("  Non-standard stoichiometry: using small-x approximation.");
            System.out.println("  K ≈ (b·x)^b / C0  =>  x ≈ (K·C0)^(1/b) / b");
            double x = Math.pow(K * C0, 1.0 / b) / b;
            System.out.printf("  x ≈ %.6e mol/L  (verify x/C0 < 5%% for validity)%n", x);
            fivePercentCheck(x, C0);
        }
    }

    // ── 2. Kc from concentrations ─────────────────────────────────────────────

    private void kcFromConcentrations(Scanner sc) {
        System.out.println("\n-- Kc from Equilibrium Concentrations --");
        System.out.println("  Kc = [products]^coeffs / [reactants]^coeffs");
        System.out.println("  Enter each species, its equilibrium concentration, and coefficient.");
        System.out.println("  (Omit pure solids and liquids — their activity = 1)");

        double numerator   = computeKContrib(sc, "product");
        double denominator = computeKContrib(sc, "reactant");
        double Kc = numerator / denominator;

        System.out.printf("%n  Kc = %.6e%n", Kc);
        System.out.printf("  ln Kc = %.4f%n", Math.log(Kc));
        System.out.printf("  pK    = %.4f%n", -Math.log10(Kc));
    }

    private double computeKContrib(Scanner sc, String side) {
        int n = (int) PHCalculator.readDouble(sc, "Number of " + side + "s: ");
        double product = 1.0;
        for (int i = 1; i <= n; i++) {
            double conc  = PHCalculator.readDouble(sc, "  [" + side + " " + i + "] (mol/L): ");
            double coeff = PHCalculator.readDouble(sc, "  Stoichiometric coefficient: ");
            product *= Math.pow(conc, coeff);
        }
        return product;
    }

    // ── 3. Kp <-> Kc ─────────────────────────────────────────────────────────

    private void kpKcConvert(Scanner sc) {
        System.out.println("\n-- Kp <-> Kc Conversion --");
        System.out.println("  Kp = Kc · (RT)^Δn    where Δn = moles gaseous products - moles gaseous reactants");
        System.out.println("  R = 0.08206 L·atm/(mol·K)");
        System.out.println("  1. Kc -> Kp");
        System.out.println("  2. Kp -> Kc");
        System.out.print("Choice: ");
        String ch = sc.nextLine().trim();
        double T  = PHCalculator.readDouble(sc, "Temperature T (K): ");
        double dn = PHCalculator.readDouble(sc, "Δn (gas) = moles_products - moles_reactants: ");
        double RT = R * T;

        if (ch.equals("1")) {
            double Kc = PHCalculator.readDouble(sc, "Kc: ");
            double Kp = Kc * Math.pow(RT, dn);
            System.out.printf("%n  Kp = Kc·(RT)^Δn = %.4e · (%.4f)^%.1f = %.6e%n", Kc, RT, dn, Kp);
        } else {
            double Kp = PHCalculator.readDouble(sc, "Kp: ");
            double Kc = Kp / Math.pow(RT, dn);
            System.out.printf("%n  Kc = Kp/(RT)^Δn = %.4e / (%.4f)^%.1f = %.6e%n", Kp, RT, dn, Kc);
        }
    }

    // ── 4. Reaction quotient Q ───────────────────────────────────────────────

    private void reactionQuotient(Scanner sc) {
        System.out.println("\n-- Reaction Quotient Q --");
        System.out.println("  Q uses current (non-equilibrium) concentrations; same formula as Kc.");
        double K = PHCalculator.readDouble(sc, "Equilibrium constant K: ");

        System.out.println("  Enter CURRENT concentrations:");
        double numerator   = computeKContrib(sc, "product");
        double denominator = computeKContrib(sc, "reactant");
        double Q = numerator / denominator;

        System.out.printf("%n  Q = %.6e   K = %.6e%n", Q, K);
        if (Q < K)       System.out.println("  Q < K: reaction proceeds FORWARD (makes more products).");
        else if (Q > K)  System.out.println("  Q > K: reaction proceeds REVERSE (makes more reactants).");
        else              System.out.println("  Q = K: system is at equilibrium.");
    }

    // ── 5. Percent dissociation ───────────────────────────────────────────────

    private void percentDissociation(Scanner sc) {
        System.out.println("\n-- Percent Dissociation / Ionisation --");
        System.out.println("  % = ([dissociated] / [initial]) × 100");
        System.out.println("  1. Weak acid (Ka known)");
        System.out.println("  2. Enter [dissociated] and [initial] directly");
        System.out.print("Choice: ");
        String ch = sc.nextLine().trim();

        if (ch.equals("1")) {
            double Ka = PHCalculator.readDouble(sc, "Ka: ");
            double C  = PHCalculator.readDouble(sc, "Initial concentration (mol/L): ");
            double x  = PHCalculator.solveWeakAcidQuadratic(Ka, C);
            System.out.printf("  x = %.4e mol/L%n", x);
            System.out.printf("  %% dissociated = %.2f%%%n", (x / C) * 100);
            fivePercentCheck(x, C);
        } else {
            double dis  = PHCalculator.readDouble(sc, "[dissociated] (mol/L): ");
            double init = PHCalculator.readDouble(sc, "[initial]     (mol/L): ");
            System.out.printf("  %% = %.2f%%%n", (dis / init) * 100);
        }
    }

    // ── 6. Ksp <-> molar solubility ───────────────────────────────────────────

    private void kspSolubility(Scanner sc) {
        System.out.println("\n-- Ksp and Molar Solubility --");
        System.out.println("  For salt  MmXn  ->  m·M^(n+)  +  n·X^(m-)");
        System.out.println("  Ksp = (m·s)^m · (n·s)^n  = m^m · n^n · s^(m+n)");
        System.out.println("  1. Ksp -> molar solubility s");
        System.out.println("  2. s   -> Ksp");
        System.out.print("Choice: ");
        String ch = sc.nextLine().trim();
        double m = PHCalculator.readDouble(sc, "m (stoich. coeff. of cation): ");
        double n = PHCalculator.readDouble(sc, "n (stoich. coeff. of anion):  ");
        double exponent = m + n;
        double prefactor = Math.pow(m, m) * Math.pow(n, n);

        if (ch.equals("1")) {
            double Ksp = PHCalculator.readDouble(sc, "Ksp: ");
            double s   = Math.pow(Ksp / prefactor, 1.0 / exponent);
            System.out.printf("%n  s = (Ksp / (m^m · n^n))^(1/(m+n)) = %.6e mol/L%n", s);
        } else {
            double s   = PHCalculator.readDouble(sc, "Molar solubility s (mol/L): ");
            double Ksp = prefactor * Math.pow(s, exponent);
            System.out.printf("%n  Ksp = %.6e%n", Ksp);
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void printICEAcid(double Ka, double C, double x) {
        double pH     = -Math.log10(x);
        double pct    = (x / C) * 100;
        System.out.printf("%n  ICE table for HA <=> H+ + A-:%n");
        System.out.printf("  %-10s  %-12s  %-12s  %-12s%n", "", "[HA]", "[H+]", "[A-]");
        System.out.printf("  %-10s  %-12.4e  %-12s  %-12s%n",  "I:", C, "0", "0");
        System.out.printf("  %-10s  %-12s  %-12s  %-12s%n",    "C:", "-x", "+x", "+x");
        System.out.printf("  %-10s  %-12.4e  %-12.4e  %-12.4e%n", "E:", C - x, x, x);
        System.out.printf("%n  Ka check: x²/(C-x) = %.4e  (Ka = %.4e)%n",
                (x * x) / (C - x), Ka);
        System.out.printf("  pH = %.4f%n", pH);
        System.out.printf("  %% ionised = %.2f%%%n", pct);
        fivePercentCheck(x, C);
    }

    private void fivePercentCheck(double x, double C) {
        double pct = (x / C) * 100;
        if (pct < 5)
            System.out.printf("  5%% rule holds (%.2f%% < 5%%) — approx. valid.%n", pct);
        else
            System.out.printf("  [!] 5%% rule fails (%.2f%% >= 5%%) — quadratic result is exact.%n", pct);
    }
}
