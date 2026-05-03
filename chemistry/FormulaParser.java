package chemistry;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Parses chemical formulas like H2O, Ca(OH)2, Fe2(SO4)3, Al2(SO4)3.
 * Also handles electron notation "e-" in redox half-reactions.
 */
public class FormulaParser {

    public static Map<String, Integer> parse(String formula) {
        formula = formula.trim();
        // Normalize electron: e- -> treat as element "e" with count 1
        formula = formula.replace("e-", "e1").replace("e+", "e1");
        // Strip ionic charge like ^2+ or ^2- or ^+ or ^-
        formula = formula.replaceAll("\\^[0-9]*[+-]", "").replaceAll("\\^[+-][0-9]*", "");
        Map<String, Integer> result = new LinkedHashMap<>();
        int[] pos = {0};
        parseGroup(formula, pos, 1, result);
        return result;
    }

    private static void parseGroup(String formula, int[] pos, int multiplier, Map<String, Integer> result) {
        while (pos[0] < formula.length()) {
            char c = formula.charAt(pos[0]);
            if (c == '(') {
                pos[0]++;
                Map<String, Integer> inner = new LinkedHashMap<>();
                parseGroup(formula, pos, 1, inner);
                int subscript = readNumber(formula, pos);
                if (subscript == 0) subscript = 1;
                for (Map.Entry<String, Integer> e : inner.entrySet())
                    result.merge(e.getKey(), e.getValue() * subscript * multiplier, Integer::sum);
            } else if (c == '[') {
                pos[0]++;
                Map<String, Integer> inner = new LinkedHashMap<>();
                parseGroup(formula, pos, 1, inner);
                int subscript = readNumber(formula, pos);
                if (subscript == 0) subscript = 1;
                for (Map.Entry<String, Integer> e : inner.entrySet())
                    result.merge(e.getKey(), e.getValue() * subscript * multiplier, Integer::sum);
            } else if (c == ')' || c == ']') {
                pos[0]++;
                return;
            } else if (Character.isUpperCase(c)) {
                StringBuilder sym = new StringBuilder().append(c);
                pos[0]++;
                while (pos[0] < formula.length() && Character.isLowerCase(formula.charAt(pos[0])))
                    sym.append(formula.charAt(pos[0]++));
                int subscript = readNumber(formula, pos);
                if (subscript == 0) subscript = 1;
                result.merge(sym.toString(), subscript * multiplier, Integer::sum);
            } else {
                pos[0]++; // skip dots, spaces, etc.
            }
        }
    }

    private static int readNumber(String formula, int[] pos) {
        int start = pos[0];
        while (pos[0] < formula.length() && Character.isDigit(formula.charAt(pos[0])))
            pos[0]++;
        return pos[0] == start ? 0 : Integer.parseInt(formula.substring(start, pos[0]));
    }

    /** Calculates the molar mass of a chemical formula. Returns 0 and prints a warning for unknown elements. */
    public static double molarMass(String formula) {
        Map<String, Integer> composition = parse(formula);
        PeriodicTable pt = PeriodicTable.getInstance();
        double mass = 0;
        for (Map.Entry<String, Integer> entry : composition.entrySet()) {
            Element el = pt.get(entry.getKey());
            if (el == null) {
                System.out.println("  [Warning] Unknown element: " + entry.getKey());
                continue;
            }
            mass += el.atomicMass * entry.getValue();
        }
        return mass;
    }

    /** Formats a composition map back to a readable string, e.g. {H:2, O:1} -> "H2O". */
    public static String formatComposition(Map<String, Integer> comp) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> e : comp.entrySet()) {
            sb.append(e.getKey());
            if (e.getValue() > 1) sb.append(e.getValue());
        }
        return sb.toString();
    }
}
