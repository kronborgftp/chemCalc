package ui;

import calculators.*;

import java.util.Scanner;

public class MenuSystem {

    private final Scanner sc = new Scanner(System.in);

    // Lazy-initialise calculators so startup is instant
    private EquationBalancer  equationBalancer;
    private PHCalculator      phCalculator;
    private RedoxCalculator   redoxCalculator;
    private ThermodynamicsCalc thermodynamicsCalc;
    private KineticsCalc      kineticsCalc;
    private EquilibriumCalc   equilibriumCalc;
    private StoichiometryCalc stoichiometryCalc;
    private VESPRCalc         vesprCalc;

    public void start() {
        clearScreen();
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║        DTU CHEMISTRY EXAM TOOLKIT  v1.0              ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = sc.nextLine().trim();
            clearScreen();
            switch (choice) {
                case "1" -> {
                    if (equationBalancer == null) equationBalancer = new EquationBalancer();
                    equationBalancer.run(sc);
                }
                case "2" -> {
                    if (phCalculator == null) phCalculator = new PHCalculator();
                    phCalculator.run(sc);
                }
                case "3" -> {
                    if (redoxCalculator == null) redoxCalculator = new RedoxCalculator();
                    redoxCalculator.run(sc);
                }
                case "4" -> {
                    if (thermodynamicsCalc == null) thermodynamicsCalc = new ThermodynamicsCalc();
                    thermodynamicsCalc.run(sc);
                }
                case "5" -> {
                    if (kineticsCalc == null) kineticsCalc = new KineticsCalc();
                    kineticsCalc.run(sc);
                }
                case "6" -> {
                    if (equilibriumCalc == null) equilibriumCalc = new EquilibriumCalc();
                    equilibriumCalc.run(sc);
                }
                case "7" -> {
                    if (stoichiometryCalc == null) stoichiometryCalc = new StoichiometryCalc();
                    stoichiometryCalc.run(sc);
                }
                case "8" -> {
                    if (vesprCalc == null) vesprCalc = new VESPRCalc();
                    vesprCalc.run(sc);
                }
                case "9" -> showReference();
                case "0" -> {
                    System.out.println("Good luck on your exam!");
                    running = false;
                }
                default -> System.out.println("  Invalid choice — press Enter and try again.");
            }
        }
        sc.close();
    }

    private void printMainMenu() {
        System.out.println("\n┌──────────────────────────────────────────────────────┐");
        System.out.println("│  MAIN MENU                                           │");
        System.out.println("├──────────────────────────────────────────────────────┤");
        System.out.println("│  1. Equation Balancer          (stoichiometry)       │");
        System.out.println("│  2. pH / Acid-Base Calculator                        │");
        System.out.println("│  3. Redox Calculator           (ox. states + balance)│");
        System.out.println("│  4. Thermodynamics             (ΔG, ΔH, ΔS, Hess)   │");
        System.out.println("│  5. Kinetics                   (Arrhenius, rate laws)│");
        System.out.println("│  6. Equilibrium                (ICE table, Kc, Kp)  │");
        System.out.println("│  7. Stoichiometry              (moles, yield, gas)   │");
        System.out.println("│  8. VSEPR / Molecular Geometry (shapes, angles)      │");
        System.out.println("│  9. Reference Tables                                 │");
        System.out.println("│  0. Exit                                             │");
        System.out.println("└──────────────────────────────────────────────────────┘");
        System.out.print("Choice: ");
    }

    private void showReference() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== REFERENCE TABLES ===");
            System.out.println("  1. Physical constants");
            System.out.println("  2. Common Ka/Kb values");
            System.out.println("  3. Common ΔHf° values");
            System.out.println("  4. Common Ksp values");
            System.out.println("  5. Electrochemical series (standard reduction potentials)");
            System.out.println("  6. Organic compound classes");
            System.out.println("  7. Element cycles (Carbon, Nitrogen)");
            System.out.println("  0. Back");
            System.out.print("Choice: ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1" -> refConstants();
                case "2" -> refKaKb();
                case "3" -> refDHf();
                case "4" -> refKsp();
                case "5" -> refRedox();
                case "6" -> refOrganic();
                case "7" -> refCycles();
                case "0" -> running = false;
                default  -> System.out.println("  Invalid.");
            }
        }
    }

    private void refConstants() {
        System.out.println("\n── Physical Constants ──────────────────────────────────");
        System.out.println("  R  = 8.314    J/(mol·K)");
        System.out.println("  R  = 0.08206  L·atm/(mol·K)");
        System.out.println("  F  = 96485    C/mol  (Faraday constant)");
        System.out.println("  NA = 6.022×10²³ mol⁻¹  (Avogadro)");
        System.out.println("  kB = 1.381×10⁻²³ J/K  (Boltzmann)");
        System.out.println("  h  = 6.626×10⁻³⁴ J·s  (Planck)");
        System.out.println("  c  = 2.998×10⁸  m/s  (speed of light)");
        System.out.println("  Kw = 1.0×10⁻¹⁴  (water autoionization at 25°C)");
        System.out.println("  1 atm = 101325 Pa = 760 mmHg = 760 Torr");
        System.out.println("  STP: 0°C, 1 atm,  molar volume = 22.4 L/mol");
        System.out.println("  SATP: 25°C, 1 bar, molar volume = 24.8 L/mol");
    }

    private void refKaKb() {
        System.out.println("\n── Common Ka and Kb Values (25°C) ─────────────────────");
        System.out.println("  Acid              Ka            pKa");
        System.out.println("  ──────────────────────────────────────────────────────");
        System.out.println("  HF                6.8×10⁻⁴      3.17");
        System.out.println("  CH3COOH (acetic)  1.8×10⁻⁵      4.74");
        System.out.println("  HNO2              4.5×10⁻⁴      3.35");
        System.out.println("  H2CO3 (Ka1)       4.3×10⁻⁷      6.37");
        System.out.println("  HCO3⁻  (Ka2)      4.7×10⁻¹¹    10.33");
        System.out.println("  H3PO4  (Ka1)      7.5×10⁻³      2.12");
        System.out.println("  H2PO4⁻ (Ka2)      6.2×10⁻⁸      7.21");
        System.out.println("  HPO4²⁻ (Ka3)      4.8×10⁻¹³    12.32");
        System.out.println("  HSO4⁻  (Ka2)      1.2×10⁻²      1.92");
        System.out.println("  NH4⁺              5.6×10⁻¹⁰     9.25");
        System.out.println("  HCN               6.2×10⁻¹⁰     9.21");
        System.out.println("  H2S    (Ka1)       1×10⁻⁷        7.0");
        System.out.println("\n  Base              Kb            pKb");
        System.out.println("  ──────────────────────────────────────────────────────");
        System.out.println("  NH3               1.8×10⁻⁵      4.74");
        System.out.println("  CH3NH2            4.4×10⁻⁴      3.36");
        System.out.println("  Pyridine          1.7×10⁻⁹      8.77");
        System.out.println("  Aniline (C6H5NH2) 4.3×10⁻¹⁰     9.37");
    }

    private void refDHf() {
        System.out.println("\n── Standard Enthalpies of Formation ΔHf° (kJ/mol, 25°C) ──");
        System.out.println("  Species           ΔHf° (kJ/mol)");
        System.out.println("  ─────────────────────────────────");
        System.out.println("  H2O(l)            -285.8");
        System.out.println("  H2O(g)            -241.8");
        System.out.println("  CO2(g)            -393.5");
        System.out.println("  CO(g)             -110.5");
        System.out.println("  CH4(g)             -74.8");
        System.out.println("  C2H6(g)            -84.7");
        System.out.println("  C6H6(l)            +49.0");
        System.out.println("  NH3(g)             -46.1");
        System.out.println("  NO(g)              +90.3");
        System.out.println("  NO2(g)             +33.2");
        System.out.println("  HCl(g)             -92.3");
        System.out.println("  HF(g)             -271.1");
        System.out.println("  SO2(g)            -296.8");
        System.out.println("  SO3(g)            -395.7");
        System.out.println("  NaCl(s)           -411.1");
        System.out.println("  CaCO3(s)          -1207.6");
        System.out.println("  Fe2O3(s)          -824.2");
        System.out.println("  All elements in standard state = 0 by definition");
    }

    private void refKsp() {
        System.out.println("\n── Common Ksp Values (25°C) ────────────────────────────");
        System.out.println("  Compound          Formula        Ksp");
        System.out.println("  ────────────────────────────────────────────────────────");
        System.out.println("  Silver chloride   AgCl           1.8×10⁻¹⁰");
        System.out.println("  Silver bromide    AgBr           5.0×10⁻¹³");
        System.out.println("  Silver iodide     AgI            8.5×10⁻¹⁷");
        System.out.println("  Barium sulfate    BaSO4          1.1×10⁻¹⁰");
        System.out.println("  Calcium carbonate CaCO3          3.4×10⁻⁹");
        System.out.println("  Calcium fluoride  CaF2           3.5×10⁻¹¹");
        System.out.println("  Lead(II) iodide   PbI2           8.5×10⁻⁹");
        System.out.println("  Iron(II) hydroxide Fe(OH)2       4.9×10⁻¹⁷");
        System.out.println("  Iron(III) hydroxide Fe(OH)3      2.8×10⁻³⁹");
        System.out.println("  Zinc sulfide      ZnS            2×10⁻²⁵");
        System.out.println("  Mercury(I) chloride Hg2Cl2       1.4×10⁻¹⁸");
    }

    private void refRedox() {
        System.out.println("\n── Standard Reduction Potentials E° (V, 25°C, 1M) ─────");
        System.out.println("  Half-reaction                          E° (V)");
        System.out.println("  ─────────────────────────────────────────────────────");
        System.out.println("  F2 + 2e⁻  -> 2F⁻                      +2.87");
        System.out.println("  MnO4⁻ + 8H⁺ + 5e⁻ -> Mn²⁺ + 4H2O    +1.51");
        System.out.println("  Cl2 + 2e⁻  -> 2Cl⁻                    +1.36");
        System.out.println("  Cr2O7²⁻ + 14H⁺ + 6e⁻ -> 2Cr³⁺ + 7H2O +1.33");
        System.out.println("  O2 + 4H⁺ + 4e⁻ -> 2H2O                +1.23");
        System.out.println("  Br2 + 2e⁻  -> 2Br⁻                    +1.07");
        System.out.println("  NO3⁻ + 4H⁺ + 3e⁻ -> NO + 2H2O        +0.96");
        System.out.println("  Ag⁺  + e⁻  -> Ag                      +0.80");
        System.out.println("  Fe³⁺ + e⁻  -> Fe²⁺                    +0.77");
        System.out.println("  I2 + 2e⁻   -> 2I⁻                     +0.54");
        System.out.println("  O2 + 2H2O + 4e⁻ -> 4OH⁻               +0.40");
        System.out.println("  Cu²⁺ + 2e⁻ -> Cu                      +0.34");
        System.out.println("  2H⁺ + 2e⁻  -> H2                       0.00  (reference)");
        System.out.println("  Fe²⁺ + 2e⁻ -> Fe                      -0.44");
        System.out.println("  Zn²⁺ + 2e⁻ -> Zn                      -0.76");
        System.out.println("  Al³⁺ + 3e⁻ -> Al                      -1.66");
        System.out.println("  Na⁺  + e⁻  -> Na                      -2.71");
        System.out.println("  Li⁺  + e⁻  -> Li                      -3.04");
        System.out.println("\n  E°cell = E°cathode - E°anode");
        System.out.println("  ΔG° = -nFE°cell   (n = electrons transferred, F = 96485 C/mol)");
    }

    private void refOrganic() {
        System.out.println("\n── Organic Compound Classes ────────────────────────────");
        System.out.println("  Class           Functional Group       Example");
        System.out.println("  ─────────────────────────────────────────────────────────");
        System.out.println("  Alkane          C-C (single bonds)     CH4, C2H6");
        System.out.println("  Alkene          C=C (double bond)      C2H4 (ethene)");
        System.out.println("  Alkyne          C≡C (triple bond)      C2H2 (ethyne)");
        System.out.println("  Aromatic        benzene ring           C6H6");
        System.out.println("  Alcohol         -OH                    CH3OH (methanol)");
        System.out.println("  Ether           -O-                    CH3OCH3");
        System.out.println("  Aldehyde        -CHO                   HCHO (formaldehyde)");
        System.out.println("  Ketone          -C(=O)-                CH3COCH3 (acetone)");
        System.out.println("  Carboxylic acid -COOH                  CH3COOH (acetic acid)");
        System.out.println("  Ester           -COO-                  CH3COOC2H5");
        System.out.println("  Amine           -NH2                   CH3NH2 (methylamine)");
        System.out.println("  Amide           -CONH-                 CH3CONH2");
        System.out.println("  Halide          -X (X=F,Cl,Br,I)      CH3Cl");
        System.out.println("\n  Naming (IUPAC): meth-1C  eth-2C  prop-3C  but-4C");
        System.out.println("                   pent-5C  hex-6C  hept-7C  oct-8C");
    }

    private void refCycles() {
        System.out.println("\n── Element Cycles ──────────────────────────────────────");
        System.out.println("\n  CARBON CYCLE key processes:");
        System.out.println("  Photosynthesis:  6CO2 + 6H2O -> C6H12O6 + 6O2");
        System.out.println("  Respiration:     C6H12O6 + 6O2 -> 6CO2 + 6H2O");
        System.out.println("  Combustion:      CxHy + O2 -> CO2 + H2O");
        System.out.println("  Ocean dissolution: CO2 + H2O <=> H2CO3 <=> H+ + HCO3-");
        System.out.println("  CaCO3 dissolution: CaCO3 + CO2 + H2O -> Ca2+ + 2HCO3-");
        System.out.println("  Residence: atm CO2 ~3-5 years; ocean deep ~1000 yr");
        System.out.println();
        System.out.println("  NITROGEN CYCLE key processes:");
        System.out.println("  N2 fixation (Haber): N2 + 3H2 -> 2NH3  (industrial)");
        System.out.println("  Biological fixation: N2 -> NH3 (Rhizobium bacteria)");
        System.out.println("  Nitrification:       NH3 -> NO2- -> NO3-  (bacteria)");
        System.out.println("  Denitrification:     NO3- -> N2  (anaerobic bacteria)");
        System.out.println("  Assimilation:        NO3- / NH4+ -> organic N (plants)");
        System.out.println("  Ammonification:      organic N -> NH4+  (decomposers)");
        System.out.println("  Lightning fixation:  N2 + O2 -> 2NO (then -> HNO3)");
    }

    private void clearScreen() {
        // Works on most terminals; harmless if not supported
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
