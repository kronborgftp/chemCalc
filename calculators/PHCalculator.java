package calculators;

import java.util.Scanner;

/**
 * pH calculations: strong/weak acids & bases, buffers (Henderson-Hasselbalch),
 * polyprotic acids, and acid-base neutralisation mixtures.
 */
public class PHCalculator implements Calculator {

    private static final double Kw = 1e-14;   // at 25 °C

    @Override
    public void run(Scanner sc) {
        boolean running = true;
        while (running) {
            System.out.println("\n=== pH / ACID-BASE CALCULATOR ===");
            System.out.println("  1. Strong acid");
            System.out.println("  2. Strong base");
            System.out.println("  3. Weak acid (given Ka)");
            System.out.println("  4. Weak base (given Kb)");
            System.out.println("  5. Buffer — Henderson-Hasselbalch");
            System.out.println("  6. Polyprotic acid (up to 3 Ka values)");
            System.out.println("  7. Acid-base neutralisation (mix strong acid + strong base)");
            System.out.println("  8. Convert between Ka, Kb, pKa, pKb");
            System.out.println("  0. Back");
            System.out.print("Choice: ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1" -> strongAcid(sc);
                case "2" -> strongBase(sc);
                case "3" -> weakAcid(sc);
                case "4" -> weakBase(sc);
                case "5" -> buffer(sc);
                case "6" -> polyproticAcid(sc);
                case "7" -> neutralisation(sc);
                case "8" -> convertK(sc);
                case "0" -> running = false;
                default  -> System.out.println("  Invalid choice.");
            }
        }
    }

    // ── Strong acid ──────────────────────────────────────────────────────────

    private void strongAcid(Scanner sc) {
        System.out.println("\n-- Strong Acid --");
        double C = readDouble(sc, "Enter concentration [H+] (mol/L): ");
        double pH = -Math.log10(C);
        double pOH = 14 - pH;
        System.out.printf("  pH   = %.4f%n", pH);
        System.out.printf("  pOH  = %.4f%n", pOH);
        System.out.printf("  [H+] = %.4e mol/L%n", C);
        System.out.printf("  [OH-]= %.4e mol/L%n", Kw / C);
    }

    // ── Strong base ──────────────────────────────────────────────────────────

    private void strongBase(Scanner sc) {
        System.out.println("\n-- Strong Base --");
        double C = readDouble(sc, "Enter concentration [OH-] (mol/L): ");
        double pOH = -Math.log10(C);
        double pH  = 14 - pOH;
        System.out.printf("  pOH  = %.4f%n", pOH);
        System.out.printf("  pH   = %.4f%n", pH);
        System.out.printf("  [OH-]= %.4e mol/L%n", C);
        System.out.printf("  [H+] = %.4e mol/L%n", Kw / C);
    }

    // ── Weak acid ────────────────────────────────────────────────────────────

    private void weakAcid(Scanner sc) {
        System.out.println("\n-- Weak Acid  HA  <=>  H+  +  A- --");
        double Ka = readDouble(sc, "Enter Ka: ");
        double C  = readDouble(sc, "Enter concentration C (mol/L): ");

        double x = solveWeakAcidQuadratic(Ka, C);
        double pH = -Math.log10(x);
        double percent = (x / C) * 100;

        System.out.printf("  [H+]      = %.4e mol/L%n", x);
        System.out.printf("  pH        = %.4f%n", pH);
        System.out.printf("  pKa       = %.4f%n", -Math.log10(Ka));
        System.out.printf("  %% ionised = %.2f%%%n", percent);

        if (x / C < 0.05)
            System.out.printf("  (5%% rule holds: approx. x = sqrt(Ka*C) = %.4e)%n",
                    Math.sqrt(Ka * C));
    }

    // ── Weak base ────────────────────────────────────────────────────────────

    private void weakBase(Scanner sc) {
        System.out.println("\n-- Weak Base  B  +  H2O  <=>  BH+  +  OH- --");
        double Kb = readDouble(sc, "Enter Kb: ");
        double C  = readDouble(sc, "Enter concentration C (mol/L): ");

        double x   = solveWeakAcidQuadratic(Kb, C);
        double pOH = -Math.log10(x);
        double pH  = 14 - pOH;

        System.out.printf("  [OH-]     = %.4e mol/L%n", x);
        System.out.printf("  pOH       = %.4f%n", pOH);
        System.out.printf("  pH        = %.4f%n", pH);
        System.out.printf("  pKb       = %.4f%n", -Math.log10(Kb));
        System.out.printf("  %% ionised = %.2f%%%n", (x / C) * 100);
    }

    // ── Buffer ───────────────────────────────────────────────────────────────

    private void buffer(Scanner sc) {
        System.out.println("\n-- Buffer  (Henderson-Hasselbalch) --");
        System.out.println("  pH = pKa + log([A-]/[HA])");
        double Ka  = readDouble(sc, "Enter Ka of the weak acid: ");
        double cA  = readDouble(sc, "Enter [A-]  (conjugate base, mol/L): ");
        double cHA = readDouble(sc, "Enter [HA]  (weak acid,      mol/L): ");

        double pKa = -Math.log10(Ka);
        double pH  = pKa + Math.log10(cA / cHA);

        System.out.printf("  pKa = %.4f%n", pKa);
        System.out.printf("  pH  = %.4f%n", pH);
        System.out.printf("  Buffer ratio [A-]/[HA] = %.4f%n", cA / cHA);

        // Buffer capacity indicator
        double ratio = cA / cHA;
        if (ratio < 0.1 || ratio > 10)
            System.out.println("  [Warning] Ratio outside 1:10–10:1 — poor buffer capacity.");
        else
            System.out.println("  Buffer capacity: adequate (ratio within optimal range).");
    }

    // ── Polyprotic acid ──────────────────────────────────────────────────────

    private void polyproticAcid(Scanner sc) {
        System.out.println("\n-- Polyprotic Acid --");
        int steps = (int) readDouble(sc, "Number of ionisation steps (1-3): ");
        double C = readDouble(sc, "Initial concentration (mol/L): ");
        double[] Ka = new double[steps];
        for (int i = 0; i < steps; i++)
            Ka[i] = readDouble(sc, "  Ka" + (i + 1) + " = ");

        // Step 1 dominates pH when Ka1 >> Ka2
        double x1 = solveWeakAcidQuadratic(Ka[0], C);
        double pH  = -Math.log10(x1);
        System.out.printf("%n  Step 1: [H+] = %.4e  pH = %.4f%n", x1, pH);

        for (int i = 1; i < steps; i++) {
            System.out.printf("  Step %d: Ka%d = %.2e  (contributes ~%.2e mol/L H+ additional)%n",
                    i + 1, i + 1, Ka[i], Ka[i]);
        }
        System.out.printf("  Dominant pH (from Ka1) = %.4f%n", pH);
        System.out.println("  Note: For polyprotic acids Ka1 >> Ka2 >> Ka3, so pH is dominated by Ka1.");

        // Amphoteric intermediate species pH
        if (steps >= 2) {
            double pHAmphoteric = 0.5 * (-Math.log10(Ka[0]) + (-Math.log10(Ka[1])));
            System.out.printf("  pH of intermediate (amphoteric) species  = (pKa1+pKa2)/2 = %.4f%n",
                    pHAmphoteric);
        }
    }

    // ── Neutralisation ───────────────────────────────────────────────────────

    private void neutralisation(Scanner sc) {
        System.out.println("\n-- Acid-Base Neutralisation (Strong acid + Strong base) --");
        double Va = readDouble(sc, "Volume of acid (L): ");
        double Ca = readDouble(sc, "Concentration of acid (mol/L): ");
        double Vb = readDouble(sc, "Volume of base (mol/L): ");
        double Cb = readDouble(sc, "Concentration of base (mol/L): ");

        double molAcid = Va * Ca;
        double molBase = Vb * Cb;
        double Vtot    = Va + Vb;

        System.out.printf("%n  mol H+  = %.4f%n", molAcid);
        System.out.printf("  mol OH- = %.4f%n", molBase);

        double pH;
        if (Math.abs(molAcid - molBase) < 1e-12) {
            pH = 7.00;
            System.out.println("  Equivalence point reached.");
        } else if (molAcid > molBase) {
            double excess = molAcid - molBase;
            double cH = excess / Vtot;
            pH = -Math.log10(cH);
            System.out.printf("  Excess H+  = %.4e mol/L%n", cH);
        } else {
            double excess = molBase - molAcid;
            double cOH = excess / Vtot;
            pH = 14 + Math.log10(cOH);
            System.out.printf("  Excess OH- = %.4e mol/L%n", cOH);
        }
        System.out.printf("  pH = %.4f%n", pH);
    }

    // ── Ka/Kb conversions ────────────────────────────────────────────────────

    private void convertK(Scanner sc) {
        System.out.println("\n-- Ka / Kb / pKa / pKb Conversions --");
        System.out.println("  Kw = Ka * Kb = 1e-14 at 25°C");
        System.out.println("  1. Ka -> pKa and Kb");
        System.out.println("  2. Kb -> pKb and Ka");
        System.out.println("  3. pKa -> Ka");
        System.out.println("  4. pKb -> Kb");
        System.out.print("Choice: ");
        String ch = sc.nextLine().trim();
        switch (ch) {
            case "1" -> {
                double Ka = readDouble(sc, "Ka = ");
                System.out.printf("  pKa = %.4f,  Kb = %.4e%n", -Math.log10(Ka), Kw / Ka);
            }
            case "2" -> {
                double Kb = readDouble(sc, "Kb = ");
                System.out.printf("  pKb = %.4f,  Ka = %.4e%n", -Math.log10(Kb), Kw / Kb);
            }
            case "3" -> {
                double pKa = readDouble(sc, "pKa = ");
                System.out.printf("  Ka = %.4e%n", Math.pow(10, -pKa));
            }
            case "4" -> {
                double pKb = readDouble(sc, "pKb = ");
                System.out.printf("  Kb = %.4e%n", Math.pow(10, -pKb));
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /** Solves x^2 + K*x - K*C = 0 (standard weak acid/base quadratic). Returns positive root. */
    static double solveWeakAcidQuadratic(double K, double C) {
        // x^2 + K*x - K*C = 0
        double discriminant = K * K + 4 * K * C;
        return (-K + Math.sqrt(discriminant)) / 2.0;
    }

    static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print("  " + prompt);
            String line = sc.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number, try again.");
            }
        }
    }
}
