package chemistry;

/**
 * General-purpose chemistry utilities that do not belong to a specific calculator.
 * Kept in the chemistry package so that both the calculator layer and the GUI base
 * class can depend on it without creating cross-layer coupling.
 */
public final class ChemUtils {

    private ChemUtils() {}

    /**
     * Parses a numeric string, additionally accepting:
     * <ul>
     *   <li>Standard scientific notation: {@code 1.5e-3}, {@code 6.022E23}</li>
     *   <li>Power-of-ten shorthand: {@code 10^-5}, {@code 10^(-13)}</li>
     * </ul>
     */
    public static double parseExpression(String s) {
        s = s.trim().toLowerCase().replace(" ", "");
        if (s.startsWith("10^")) {
            String exp = s.substring(3);
            if (exp.startsWith("(") && exp.endsWith(")"))
                exp = exp.substring(1, exp.length() - 1);
            return Math.pow(10, Double.parseDouble(exp));
        }
        return Double.parseDouble(s);
    }
}
