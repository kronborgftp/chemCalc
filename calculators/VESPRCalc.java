package calculators;

import chemistry.Element;
import chemistry.FormulaParser;
import chemistry.PeriodicTable;

import java.util.*;

/**
 * VSEPR / Molecular Geometry:
 *   - Auto-determines lone pairs and geometry from a formula (AXn molecules)
 *   - For multi-central-atom molecules (N2H4, H2O2, C2H4), asks the user
 *     how many atoms bond to ONE central atom, then uses the atom-perspective method.
 *   - Manual mode: enter bonding domains + lone pairs directly.
 *   - Reference table of all VSEPR geometries.
 */
public class VESPRCalc implements Calculator {

    private final PeriodicTable pt = PeriodicTable.getInstance();

    @Override
    public void run(Scanner sc) {
        boolean running = true;
        while (running) {
            System.out.println("\n=== VSEPR / MOLECULAR GEOMETRY ===");
            System.out.println("  1. Analyze molecule from formula");
            System.out.println("  2. Manual: enter bonding domains + lone pairs");
            System.out.println("  3. Reference table (all geometries)");
            System.out.println("  0. Back");
            System.out.print("Choice: ");
            String ch = sc.nextLine().trim();
            switch (ch) {
                case "1" -> analyzeFromFormula(sc);
                case "2" -> manualGeometry(sc);
                case "3" -> referenceTable();
                case "0" -> running = false;
                default  -> System.out.println("  Invalid choice.");
            }
        }
    }

    // ── 1. Analyze from formula ───────────────────────────────────────────────

    private void analyzeFromFormula(Scanner sc) {
        System.out.println("\n-- VSEPR from Formula --");
        System.out.print("  Formula (e.g. H2O, NH3, SF6, CO2, N2H4): ");
        String formula = sc.nextLine().trim();

        System.out.print("  Net charge (0 for neutral, -1 for anion, etc.): ");
        String chStr = sc.nextLine().trim();
        int charge = chStr.isEmpty() ? 0 : (int) PHCalculator.parseExpression(chStr);

        Map<String, Integer> comp = FormulaParser.parse(formula);
        if (comp.isEmpty()) { System.out.println("  Could not parse formula."); return; }

        // Identify central atom (lowest EN among non-H atoms)
        String centralSym = identifyCentral(comp);
        System.out.printf("  Central atom detected: %s  (Enter to accept, or type another): ", centralSym);
        String ov = sc.nextLine().trim();
        if (!ov.isEmpty()) centralSym = ov;

        Element central = pt.get(centralSym);
        if (central == null) { System.out.println("  Element not found: " + centralSym); return; }
        int veC = valenceElectrons(central);
        if (veC < 0) {
            System.out.println("  Transition metal — use manual mode (option 2).");
            return;
        }

        int nCentral = comp.getOrDefault(centralSym, 1);

        // Build peripheral map (everything except the central atoms)
        Map<String, Integer> periph = new LinkedHashMap<>(comp);
        periph.put(centralSym, nCentral - 1);
        if (periph.get(centralSym) <= 0) periph.remove(centralSym);

        int bondingDomains;
        int lonePairs;

        if (nCentral > 1) {
            // Multi-central-atom molecule: ask how many atoms bond to ONE central atom
            System.out.printf("%n  '%s' appears %d times — analyzing ONE %s atom.%n",
                    centralSym, nCentral, centralSym);
            System.out.println("  (e.g. N2H4: each N bonds to 2 H + 1 N = 3 atoms)");
            bondingDomains = (int) PHCalculator.readDouble(sc,
                    "  How many atoms are bonded to ONE " + centralSym + " atom? ");
            lonePairs = Math.max(0, (veC - bondingDomains) / 2);
            System.out.printf("  Lone pairs on central %s = (%d - %d) / 2 = %d%n",
                    centralSym, veC, bondingDomains, lonePairs);
        } else {
            // Single central atom: full total-VE Lewis structure algorithm
            int totalVE = veC;
            for (Map.Entry<String, Integer> e : periph.entrySet()) {
                Element el = pt.get(e.getKey());
                if (el != null) totalVE += valenceElectrons(el) * e.getValue();
            }
            totalVE -= charge;

            int nPeriph = 0;
            for (int v : periph.values()) nPeriph += v;

            // Single bonds to all peripheral atoms
            int remaining = totalVE - 2 * nPeriph;

            // Complete octets on non-H peripheral atoms
            int neededByPeriph = 0;
            for (Map.Entry<String, Integer> e : periph.entrySet())
                if (!e.getKey().equals("H")) neededByPeriph += 6 * e.getValue();

            remaining -= Math.min(neededByPeriph, remaining);

            // Remaining electrons → lone pairs on central
            lonePairs = Math.max(0, remaining / 2);

            // Check if central atom is octet-deficient and needs multiple bonds
            int centralE = 2 * nPeriph + 2 * lonePairs;
            if (centralE < 8 && central.period <= 2 && lonePairs == 0) {
                // Multiple bonds reduce bonding to same n_periph domains, just note it
                System.out.println("  (Multiple bonds needed to satisfy octet on " + centralSym + ")");
            }

            bondingDomains = nPeriph;

            System.out.println("\n  Total valence electrons: " + totalVE);
            System.out.println("  Bonding domains: " + bondingDomains);
            System.out.println("  Lone pairs on central atom: " + lonePairs);
        }

        // Override confirmation
        System.out.printf("%n  Confirm lone pairs = %d  (Enter to accept, or type a number): ", lonePairs);
        String lpOv = sc.nextLine().trim();
        if (!lpOv.isEmpty()) lonePairs = Integer.parseInt(lpOv);

        printGeometry(bondingDomains, lonePairs, periph, centralSym);
    }

    // ── 2. Manual mode ────────────────────────────────────────────────────────

    private void manualGeometry(Scanner sc) {
        System.out.println("\n-- Manual VSEPR --");
        int bp = (int) PHCalculator.readDouble(sc, "Bonding domains (atoms bonded to central): ");
        int lp = (int) PHCalculator.readDouble(sc, "Lone pairs on central atom: ");
        printGeometry(bp, lp, Collections.emptyMap(), "?");
    }

    // ── 3. Reference table ────────────────────────────────────────────────────

    private void referenceTable() {
        System.out.println("\n── VSEPR Reference ──────────────────────────────────────");
        System.out.printf("  %-4s %-4s %-22s %-22s %-30s %s%n",
                "BP", "LP", "Electron Geometry", "Molecular Geometry", "Bond Angle(s)", "Planar");
        System.out.println("  " + "─".repeat(100));
        int[][] cases = {{2,0},{3,0},{2,1},{4,0},{3,1},{2,2},{5,0},{4,1},{3,2},{2,3},{6,0},{5,1},{4,2}};
        for (int[] c : cases) {
            int sn = c[0] + c[1];
            System.out.printf("  %-4d %-4d %-22s %-22s %-30s %s%n",
                    c[0], c[1],
                    electronGeometry(sn),
                    molecularGeometry(c[0], c[1]),
                    bondAngle(c[0], c[1]),
                    isPlanar(c[0], c[1]) ? "yes" : "no");
        }
        System.out.println("\n  BP = bonding domains  LP = lone pairs on central atom");
        System.out.println("  Each lone pair compresses bond angles by ~2-2.5°");
    }

    // ── Output helper ─────────────────────────────────────────────────────────

    private void printGeometry(int bp, int lp, Map<String, Integer> periph, String central) {
        int sn = bp + lp;
        String eGeom  = electronGeometry(sn);
        String mGeom  = molecularGeometry(bp, lp);
        String angles = bondAngle(bp, lp);
        boolean planar = isPlanar(bp, lp);
        String polar  = polarity(bp, lp, periph);

        System.out.println("\n  ─── Results ───────────────────────────────────");
        System.out.println("  Steric number (electron domains): " + sn);
        System.out.println("  Electron geometry:                " + eGeom);
        System.out.println("  Molecular geometry:               " + mGeom);
        System.out.println("  Bond angle(s):                    " + angles);
        System.out.println("  Planar:                           " + (planar ? "yes" : "no"));
        System.out.println("  Polarity:                         " + polar);
    }

    // ── VSEPR lookup tables ───────────────────────────────────────────────────

    private String electronGeometry(int sn) {
        return switch (sn) {
            case 1 -> "—";
            case 2 -> "linear";
            case 3 -> "trigonal planar";
            case 4 -> "tetrahedral";
            case 5 -> "trigonal bipyramidal";
            case 6 -> "octahedral";
            default -> "unknown (sn=" + sn + ")";
        };
    }

    private String molecularGeometry(int bp, int lp) {
        return switch (bp * 10 + lp) {
            case 10 -> "linear (diatomic)";
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
            default -> "unknown (bp=" + bp + ", lp=" + lp + ")";
        };
    }

    private String bondAngle(int bp, int lp) {
        int sn = bp + lp;
        if (sn == 1) return "n/a";
        if (sn == 2) return "180°";
        if (sn == 3 && lp == 0) return "120°";
        if (sn == 3 && lp == 1) return "~118° (<120°)";
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
        // linear and all sn=3 shapes are planar
        if (sn <= 3) return true;
        // bent (sn=4, lp=2) is in one plane
        if (sn == 4 && lp == 2) return true;
        // square planar (sn=6, lp=2) is planar
        if (sn == 6 && lp == 2) return true;
        return false;
    }

    private String polarity(int bp, int lp, Map<String, Integer> periph) {
        boolean allSamePeriph = periph.size() <= 1;
        // Symmetric + identical ligands + no lone pairs → nonpolar
        if (lp == 0 && allSamePeriph) return "nonpolar (symmetric, identical ligands)";
        // Linear geometry with lone pairs on central can still be nonpolar (e.g. XeF2)
        if (bp == 2 && (lp == 0 || lp == 3)) return "nonpolar (linear molecular geometry — dipoles cancel)";
        // Square planar with same ligands is nonpolar
        if (bp == 4 && lp == 2 && allSamePeriph) return "nonpolar (square planar — dipoles cancel)";
        // Any lone pair or different ligands → polar
        if (lp > 0) return "polar (lone pair creates asymmetric electron distribution)";
        return "polar (different peripheral atoms → net dipole)";
    }

    // ── periodic table helpers ────────────────────────────────────────────────

    private String identifyCentral(Map<String, Integer> comp) {
        String central = null;
        double lowestEN = Double.MAX_VALUE;
        for (String sym : comp.keySet()) {
            if (sym.equals("H")) continue;
            Element e = pt.get(sym);
            if (e == null) continue;
            if (e.electronegativity < lowestEN) {
                lowestEN = e.electronegativity;
                central = sym;
            }
        }
        return central != null ? central : comp.keySet().iterator().next();
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
}
