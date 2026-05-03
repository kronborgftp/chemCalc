package calculators;

import chemistry.FormulaParser;

import java.util.*;

/**
 * Balances chemical equations using Gaussian elimination (null-space method).
 * Input format: "H2 + O2 -> H2O"  or  "Fe + HCl -> FeCl2 + H2"
 */
public class EquationBalancer implements Calculator {

    // ── Exact rational arithmetic to avoid floating-point drift ──────────────

    private static class Frac {
        long n, d;

        Frac(long n, long d) {
            if (d == 0) throw new ArithmeticException("Zero denominator");
            long g = gcd(Math.abs(n), Math.abs(d));
            long s = d < 0 ? -1 : 1;
            this.n = s * n / g;
            this.d = s * d / g;
        }

        static long gcd(long a, long b) { return b == 0 ? a : gcd(b, a % b); }
        static long lcm(long a, long b) { return a == 0 || b == 0 ? 0 : a / gcd(a, b) * b; }

        Frac add(Frac o) { return new Frac(n * o.d + o.n * d, d * o.d); }
        Frac sub(Frac o) { return new Frac(n * o.d - o.n * d, d * o.d); }
        Frac mul(Frac o) { return new Frac(n * o.n, d * o.d); }
        Frac div(Frac o) { return new Frac(n * o.d, d * o.n); }
        boolean isZero() { return n == 0; }

        static final Frac ZERO = new Frac(0, 1);
        static final Frac ONE  = new Frac(1, 1);
    }

    // ── Public API ────────────────────────────────────────────────────────────

    @Override
    public void run(Scanner sc) {
        System.out.println("\n=== EQUATION BALANCER ===");
        System.out.println("Format:  H2 + O2 -> H2O   |   Fe2O3 + CO -> Fe + CO2");
        System.out.println("Use -> or = as the arrow.  Separate compounds with +");
        System.out.print("\nEnter equation: ");
        String line = sc.nextLine().trim();
        String result = balance(line);
        System.out.println("\nBalanced: " + result);
    }

    public String balance(String equation) {
        // ── 1. Split reactants / products ────────────────────────────────────
        String arrow = equation.contains("->") ? "->" : equation.contains("=") ? "=" : null;
        if (arrow == null) return "Error: missing arrow (-> or =)";

        String[] sides = equation.split(arrow.equals("->") ? "->" : "=", 2);
        if (sides.length < 2) return "Error: could not split equation";

        List<String> compounds = new ArrayList<>();
        List<Integer> signs    = new ArrayList<>();  // +1 reactant, -1 product

        for (String s : sides[0].split("\\+")) {
            String t = s.trim();
            if (!t.isEmpty()) { compounds.add(t); signs.add(1); }
        }
        int reactantCount = compounds.size();
        for (String s : sides[1].split("\\+")) {
            String t = s.trim();
            if (!t.isEmpty()) { compounds.add(t); signs.add(-1); }
        }

        int n = compounds.size();
        if (n < 2) return "Error: need at least 2 compounds";

        // ── 2. Build element-compound matrix ─────────────────────────────────
        List<Map<String, Integer>> parsed = new ArrayList<>();
        Set<String> elemSet = new LinkedHashSet<>();
        for (String c : compounds) {
            Map<String, Integer> m = FormulaParser.parse(c);
            parsed.add(m);
            elemSet.addAll(m.keySet());
        }
        List<String> elems = new ArrayList<>(elemSet);
        int numElems = elems.size();

        Frac[][] mat = new Frac[numElems][n];
        for (int i = 0; i < numElems; i++)
            for (int j = 0; j < n; j++) {
                int cnt = parsed.get(j).getOrDefault(elems.get(i), 0);
                mat[i][j] = new Frac(cnt * signs.get(j), 1);
            }

        // ── 3. Gauss-Jordan elimination (RREF) ───────────────────────────────
        int[] pivotCol = new int[numElems];
        Arrays.fill(pivotCol, -1);
        int row = 0;

        for (int col = 0; col < n && row < numElems; col++) {
            int pivRow = -1;
            for (int r = row; r < numElems; r++)
                if (!mat[r][col].isZero()) { pivRow = r; break; }
            if (pivRow == -1) continue;

            Frac[] tmp = mat[row]; mat[row] = mat[pivRow]; mat[pivRow] = tmp;

            Frac piv = mat[row][col];
            for (int c = 0; c < n; c++) mat[row][c] = mat[row][c].div(piv);

            for (int r = 0; r < numElems; r++) {
                if (r != row && !mat[r][col].isZero()) {
                    Frac f = mat[r][col];
                    for (int c = 0; c < n; c++)
                        mat[r][c] = mat[r][c].sub(f.mul(mat[row][c]));
                }
            }
            pivotCol[row] = col;
            row++;
        }

        int rank = row;

        // ── 4. Find free variable ─────────────────────────────────────────────
        Set<Integer> pivSet = new HashSet<>();
        for (int r = 0; r < rank; r++) pivSet.add(pivotCol[r]);
        List<Integer> freeVars = new ArrayList<>();
        for (int j = 0; j < n; j++) if (!pivSet.contains(j)) freeVars.add(j);

        if (freeVars.isEmpty())
            return "Cannot balance: equation is over-determined or trivially balanced";

        // ── 5. Set free variable = 1, solve for pivots ───────────────────────
        int freeVar = freeVars.get(0);
        Frac[] sol = new Frac[n];
        for (int j = 0; j < n; j++) sol[j] = Frac.ZERO;
        sol[freeVar] = Frac.ONE;

        for (int r = rank - 1; r >= 0; r--) {
            int pc = pivotCol[r];
            Frac val = Frac.ZERO;
            for (int c = 0; c < n; c++)
                if (c != pc) val = val.add(mat[r][c].mul(sol[c]));
            sol[pc] = new Frac(-val.n, val.d);
        }

        // ── 6. Scale to positive integers ────────────────────────────────────
        // Negate entire solution if any coefficient is negative
        boolean hasNeg = false;
        for (Frac f : sol) if (f.n < 0) { hasNeg = true; break; }
        if (hasNeg)
            for (int j = 0; j < n; j++) sol[j] = new Frac(-sol[j].n, sol[j].d);

        long lcm = 1;
        for (Frac f : sol) lcm = Frac.lcm(lcm, f.d);

        long[] coeffs = new long[n];
        for (int j = 0; j < n; j++) coeffs[j] = sol[j].n * (lcm / sol[j].d);

        // Divide by overall GCD to simplify (e.g., 2,4,2 -> 1,2,1)
        long g = 0;
        for (long c : coeffs) g = Frac.gcd(g, Math.abs(c));
        if (g > 1) for (int j = 0; j < n; j++) coeffs[j] /= g;

        // ── 7. Format output ─────────────────────────────────────────────────
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < reactantCount; j++) {
            if (j > 0) sb.append(" + ");
            if (coeffs[j] != 1) sb.append(coeffs[j]);
            sb.append(compounds.get(j));
        }
        sb.append("  ->  ");
        for (int j = reactantCount; j < n; j++) {
            if (j > reactantCount) sb.append(" + ");
            if (coeffs[j] != 1) sb.append(coeffs[j]);
            sb.append(compounds.get(j));
        }

        // Verify (optional sanity check)
        String check = verify(elems, parsed, signs, coeffs);
        if (!check.isEmpty()) sb.append("\n  [Warning] ").append(check);

        return sb.toString();
    }

    private String verify(List<String> elems, List<Map<String, Integer>> parsed,
                          List<Integer> signs, long[] coeffs) {
        for (String el : elems) {
            long sum = 0;
            for (int j = 0; j < parsed.size(); j++)
                sum += (long) parsed.get(j).getOrDefault(el, 0) * signs.get(j) * coeffs[j];
            if (sum != 0) return "Element " + el + " does not balance (sum=" + sum + ")";
        }
        return "";
    }
}
