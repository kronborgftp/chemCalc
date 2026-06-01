package chemistry;

import java.util.*;

/**
 * IUPAC nomenclature for inorganic compounds.
 * Handles: common names, binary ionic (Stock), binary molecular (Greek prefix),
 * polyatomic ionic, oxyacids, and binary acids.
 */
public class Nomenclature {

    // ── Common names ──────────────────────────────────────────────────────────
    private static final Map<String, String> COMMON = new LinkedHashMap<>();
    static {
        COMMON.put("H2O",    "water");
        COMMON.put("NH3",    "ammonia");
        COMMON.put("H2O2",   "hydrogen peroxide");
        COMMON.put("CH4",    "methane");
        COMMON.put("C2H6",   "ethane");
        COMMON.put("C3H8",   "propane");
        COMMON.put("C2H4",   "ethylene (ethene)");
        COMMON.put("C2H2",   "acetylene (ethyne)");
        COMMON.put("C6H6",   "benzene");
        COMMON.put("C6H12O6","glucose");
        COMMON.put("C12H22O11","sucrose");
        COMMON.put("C2H5OH", "ethanol");
        COMMON.put("CH3OH",  "methanol");
    }

    // ── Oxyacids and binary acids ─────────────────────────────────────────────
    private static final Map<String, String> ACIDS = new LinkedHashMap<>();
    static {
        ACIDS.put("HF",      "hydrofluoric acid");
        ACIDS.put("HCl",     "hydrochloric acid");
        ACIDS.put("HBr",     "hydrobromic acid");
        ACIDS.put("HI",      "hydroiodic acid");
        ACIDS.put("H2S",     "hydrosulfuric acid");
        ACIDS.put("HCN",     "hydrocyanic acid");
        ACIDS.put("HNO3",    "nitric acid");
        ACIDS.put("HNO2",    "nitrous acid");
        ACIDS.put("H2SO4",   "sulfuric acid");
        ACIDS.put("H2SO3",   "sulfurous acid");
        ACIDS.put("H3PO4",   "phosphoric acid");
        ACIDS.put("H3PO3",   "phosphorous acid");
        ACIDS.put("H2CO3",   "carbonic acid");
        ACIDS.put("HClO4",   "perchloric acid");
        ACIDS.put("HClO3",   "chloric acid");
        ACIDS.put("HClO2",   "chlorous acid");
        ACIDS.put("HClO",    "hypochlorous acid");
        ACIDS.put("H2CrO4",  "chromic acid");
        ACIDS.put("H2Cr2O7", "dichromic acid");
        ACIDS.put("H3AsO4",  "arsenic acid");
        ACIDS.put("HMnO4",   "permanganic acid");
        ACIDS.put("CH3COOH", "acetic acid");
        ACIDS.put("CH3CO2H", "acetic acid");
        ACIDS.put("HCOOH",   "formic acid");
    }

    // ── Polyatomic anions: formula -> {charge, name} ──────────────────────────
    // Order matters: try more specific (longer) anions first.
    private static final List<String[]> POLYATOMIC_ANIONS = new ArrayList<>();
    static {
        // {formula, name, charge-as-string}
        add("Cr2O7",  "dichromate",           "-2");
        add("MnO4",   "permanganate",         "-1");
        add("HPO4",   "hydrogen phosphate",   "-2");
        add("H2PO4",  "dihydrogen phosphate", "-1");
        add("CH3COO", "acetate",              "-1");
        add("HCO3",   "hydrogen carbonate",   "-1");
        add("HSO4",   "hydrogen sulfate",     "-1");
        add("HSO3",   "hydrogen sulfite",     "-1");
        add("CrO4",   "chromate",             "-2");
        add("SO4",    "sulfate",              "-2");
        add("SO3",    "sulfite",              "-2");
        add("CO3",    "carbonate",            "-2");
        add("PO4",    "phosphate",            "-3");
        add("PO3",    "phosphite",            "-3");
        add("AsO4",   "arsenate",             "-3");
        add("NO3",    "nitrate",              "-1");
        add("NO2",    "nitrite",              "-1");
        add("ClO4",   "perchlorate",          "-1");
        add("ClO3",   "chlorate",             "-1");
        add("ClO2",   "chlorite",             "-1");
        add("ClO",    "hypochlorite",         "-1");
        add("S2O3",   "thiosulfate",          "-2");
        add("C2O4",   "oxalate",              "-2");
        add("OH",     "hydroxide",            "-1");
        add("CN",     "cyanide",              "-1");
        add("SCN",    "thiocyanate",          "-1");
        add("O2",     "peroxide",             "-2");
        add("NH2",    "amide",                "-1");
    }
    private static void add(String f, String n, String c) {
        POLYATOMIC_ANIONS.add(new String[]{f, n, c});
    }

    // ── Metals set ────────────────────────────────────────────────────────────
    private static final Set<String> METALS = new HashSet<>(Arrays.asList(
        "Li","Be","Na","Mg","Al","K","Ca","Sc","Ti","V","Cr","Mn","Fe","Co","Ni","Cu",
        "Zn","Ga","Rb","Sr","Y","Zr","Nb","Mo","Tc","Ru","Rh","Pd","Ag","Cd","In","Sn",
        "Cs","Ba","La","Ce","Pr","Nd","Hf","Ta","W","Re","Os","Ir","Pt","Au","Hg",
        "Tl","Pb","Bi"
    ));

    // Metals that have fixed, unambiguous charges (no Roman numeral needed)
    private static final Map<String, Integer> FIXED_CHARGE = new HashMap<>();
    static {
        FIXED_CHARGE.put("Li",1); FIXED_CHARGE.put("Na",1); FIXED_CHARGE.put("K",1);
        FIXED_CHARGE.put("Rb",1); FIXED_CHARGE.put("Cs",1); FIXED_CHARGE.put("Ag",1);
        FIXED_CHARGE.put("Be",2); FIXED_CHARGE.put("Mg",2); FIXED_CHARGE.put("Ca",2);
        FIXED_CHARGE.put("Sr",2); FIXED_CHARGE.put("Ba",2); FIXED_CHARGE.put("Zn",2);
        FIXED_CHARGE.put("Cd",2); FIXED_CHARGE.put("Al",3);
    }

    // Simple nonmetal anion -ide names and standard charges
    private static final Map<String, String> NONMETAL_IDE = new LinkedHashMap<>();
    private static final Map<String, Integer> NONMETAL_CHARGE = new HashMap<>();
    static {
        NONMETAL_IDE.put("F",  "fluoride");   NONMETAL_CHARGE.put("F",  -1);
        NONMETAL_IDE.put("Cl", "chloride");   NONMETAL_CHARGE.put("Cl", -1);
        NONMETAL_IDE.put("Br", "bromide");    NONMETAL_CHARGE.put("Br", -1);
        NONMETAL_IDE.put("I",  "iodide");     NONMETAL_CHARGE.put("I",  -1);
        NONMETAL_IDE.put("At", "astatide");   NONMETAL_CHARGE.put("At", -1);
        NONMETAL_IDE.put("O",  "oxide");      NONMETAL_CHARGE.put("O",  -2);
        NONMETAL_IDE.put("S",  "sulfide");    NONMETAL_CHARGE.put("S",  -2);
        NONMETAL_IDE.put("Se", "selenide");   NONMETAL_CHARGE.put("Se", -2);
        NONMETAL_IDE.put("Te", "telluride");  NONMETAL_CHARGE.put("Te", -2);
        NONMETAL_IDE.put("N",  "nitride");    NONMETAL_CHARGE.put("N",  -3);
        NONMETAL_IDE.put("P",  "phosphide");  NONMETAL_CHARGE.put("P",  -3);
        NONMETAL_IDE.put("As", "arsenide");   NONMETAL_CHARGE.put("As", -3);
        NONMETAL_IDE.put("C",  "carbide");    NONMETAL_CHARGE.put("C",  -4);
        NONMETAL_IDE.put("H",  "hydride");    NONMETAL_CHARGE.put("H",  -1);
    }

    // Greek prefixes for molecular compounds (index = count)
    private static final String[] GREEK = {
        "", "mono", "di", "tri", "tetra", "penta", "hexa", "hepta", "octa", "nona", "deca"
    };

    // ── Public API ─────────────────────────────────────────────────────────────

    /**
     * Returns the IUPAC (or common) name for the given formula, plus a one-line
     * explanation of which rule was applied.
     */
    public static String nameWithExplanation(String formula) {
        formula = formula.trim();

        // 1. Common names
        if (COMMON.containsKey(formula))
            return COMMON.get(formula) + "\n[Common name]";

        // 2. Acids (lookup table)
        if (ACIDS.containsKey(formula))
            return ACIDS.get(formula) + "\n[Acid – lookup]";

        // 3. Parse composition
        Map<String, Integer> comp = FormulaParser.parse(formula);
        if (comp.isEmpty()) return "Could not parse formula.";
        List<String> elems = new ArrayList<>(comp.keySet());

        // 4. Single element
        if (elems.size() == 1) {
            String sym = elems.get(0);
            Element el = PeriodicTable.getInstance().get(sym);
            String n = el != null ? el.name : sym;
            int cnt = comp.get(sym);
            String name = cnt == 1 ? n : greekPrefix(cnt) + n.toLowerCase();
            return name + "\n[Elemental substance]";
        }

        // 5. Ammonium compounds  (NH4... or (NH4)...)
        boolean isNH4 = formula.startsWith("(NH4)") || formula.startsWith("NH4");
        if (isNH4) {
            String r = nameAmmonium(formula, comp);
            if (r != null) return r + "\n[Ionic – ammonium compound]";
        }

        // 6. Metal cation → ionic compound
        if (METALS.contains(elems.get(0))) {
            String r = nameIonic(elems.get(0), comp, elems);
            if (r != null) return r + "\n[Ionic compound – Stock nomenclature]";
        }

        // 7. Binary molecular (two nonmetals)
        if (elems.size() == 2 && !METALS.contains(elems.get(0))) {
            return binaryMolecular(elems, comp) + "\n[Binary molecular – Greek prefixes]";
        }

        return "Cannot determine name automatically.\nElements: " + elems;
    }

    // ── Ammonium compounds ────────────────────────────────────────────────────

    private static String nameAmmonium(String formula, Map<String, Integer> comp) {
        int numNH4 = comp.getOrDefault("N", 0);
        Map<String, Integer> rest = new LinkedHashMap<>(comp);
        merge(rest, "N", -numNH4);
        merge(rest, "H", -4 * numNH4);
        if (rest.isEmpty()) return "ammonium (ion)";
        String anion = nameAnionComp(rest);
        return anion != null ? "ammonium " + anion : null;
    }

    // ── Ionic compounds ───────────────────────────────────────────────────────

    private static String nameIonic(String cationSym, Map<String, Integer> comp, List<String> elems) {
        PeriodicTable pt = PeriodicTable.getInstance();
        Element el = pt.get(cationSym);
        String cationBase = el != null ? el.name.toLowerCase() : cationSym;
        int numCat = comp.get(cationSym);

        // Build anion composition (everything except the cation element)
        Map<String, Integer> anionComp = new LinkedHashMap<>(comp);
        anionComp.remove(cationSym);
        if (anionComp.isEmpty()) return cationBase; // pure metal

        String anionName = nameAnionComp(anionComp);
        if (anionName == null) return null;

        // Determine charge on cation (needed for Roman numeral if variable)
        int anionTotalCharge = anionTotalCharge(anionComp);
        if (anionTotalCharge == 0) return null; // can't balance
        int cationCharge = -anionTotalCharge / numCat;

        String name = cationBase;
        if (!FIXED_CHARGE.containsKey(cationSym) && cationCharge > 0) {
            name += "(" + toRoman(cationCharge) + ")";
        }
        return name + " " + anionName;
    }

    /**
     * Given a composition map that represents the anion part,
     * returns the anion name or null if unrecognised.
     */
    private static String nameAnionComp(Map<String, Integer> anionComp) {
        // Try polyatomic anions: find how many units fit
        for (String[] entry : POLYATOMIC_ANIONS) {
            String af = entry[0];
            String an = entry[1];
            Map<String, Integer> aComp = FormulaParser.parse(af);

            // Check if anionComp = numAn * aComp
            int numAn = -1;
            boolean ok = true;
            for (Map.Entry<String, Integer> e : aComp.entrySet()) {
                Integer avail = anionComp.get(e.getKey());
                if (avail == null || avail % e.getValue() != 0) { ok = false; break; }
                int ratio = avail / e.getValue();
                if (numAn == -1) numAn = ratio;
                else if (numAn != ratio) { ok = false; break; }
            }
            if (!ok || numAn <= 0) continue;

            // Ensure no leftover elements
            Map<String, Integer> leftover = new LinkedHashMap<>(anionComp);
            for (Map.Entry<String, Integer> e : aComp.entrySet())
                merge(leftover, e.getKey(), -e.getValue() * numAn);
            if (!leftover.isEmpty()) continue;

            return an;
        }

        // Try simple (monatomic) anion
        if (anionComp.size() == 1) {
            String sym = anionComp.keySet().iterator().next();
            return NONMETAL_IDE.get(sym);
        }
        return null;
    }

    /** Returns total charge contributed by the anion composition (negative value). */
    private static int anionTotalCharge(Map<String, Integer> anionComp) {
        // Try polyatomic first
        for (String[] entry : POLYATOMIC_ANIONS) {
            String af = entry[0];
            int charge = Integer.parseInt(entry[2]);
            Map<String, Integer> aComp = FormulaParser.parse(af);

            int numAn = -1;
            boolean ok = true;
            for (Map.Entry<String, Integer> e : aComp.entrySet()) {
                Integer avail = anionComp.get(e.getKey());
                if (avail == null || avail % e.getValue() != 0) { ok = false; break; }
                int ratio = avail / e.getValue();
                if (numAn == -1) numAn = ratio;
                else if (numAn != ratio) { ok = false; break; }
            }
            if (!ok || numAn <= 0) continue;

            Map<String, Integer> leftover = new LinkedHashMap<>(anionComp);
            for (Map.Entry<String, Integer> e : aComp.entrySet())
                merge(leftover, e.getKey(), -e.getValue() * numAn);
            if (!leftover.isEmpty()) continue;

            return numAn * charge;
        }
        // Simple monatomic
        if (anionComp.size() == 1) {
            String sym = anionComp.keySet().iterator().next();
            Integer charge = NONMETAL_CHARGE.get(sym);
            if (charge != null) return anionComp.get(sym) * charge;
        }
        return 0;
    }

    // ── Binary molecular ──────────────────────────────────────────────────────

    private static String binaryMolecular(List<String> elems, Map<String, Integer> comp) {
        PeriodicTable pt = PeriodicTable.getInstance();
        String s1 = elems.get(0), s2 = elems.get(1);
        int n1 = comp.get(s1), n2 = comp.get(s2);

        Element e1 = pt.get(s1), e2 = pt.get(s2);
        String name1 = e1 != null ? e1.name.toLowerCase() : s1.toLowerCase();
        String ide2  = NONMETAL_IDE.containsKey(s2)
                        ? NONMETAL_IDE.get(s2)
                        : (e2 != null ? e2.name.toLowerCase() + "ide" : s2.toLowerCase() + "ide");

        // First element: no "mono" prefix
        String first = (n1 == 1 ? "" : greekPrefix(n1)) + name1;
        // Second element: always prefix, with elision
        String second = elide(greekPrefix(n2), ide2);

        return first + " " + second;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static String greekPrefix(int n) {
        return n > 0 && n < GREEK.length ? GREEK[n] : n + "-";
    }

    /** Drops trailing 'a'/'o' from prefix when the stem starts with a vowel. */
    private static String elide(String prefix, String stem) {
        if (prefix.isEmpty() || stem.isEmpty()) return prefix + stem;
        char last = prefix.charAt(prefix.length() - 1);
        char first = stem.charAt(0);
        boolean vowel = "aeiou".indexOf(first) >= 0;
        if (vowel && (last == 'a' || last == 'o'))
            return prefix.substring(0, prefix.length() - 1) + stem;
        return prefix + stem;
    }

    private static String toRoman(int n) {
        return switch (n) {
            case 1 -> "I";   case 2 -> "II";  case 3 -> "III";
            case 4 -> "IV";  case 5 -> "V";   case 6 -> "VI";
            case 7 -> "VII"; case 8 -> "VIII"; default -> String.valueOf(n);
        };
    }

    private static void merge(Map<String, Integer> map, String key, int delta) {
        int val = map.getOrDefault(key, 0) + delta;
        if (val == 0) map.remove(key);
        else map.put(key, val);
    }
}
