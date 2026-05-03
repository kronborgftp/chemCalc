# DTU Chemistry Exam Toolkit

A Java program (GUI + CLI) covering all major calculation topics from the DTU 4th Semester Chemistry curriculum. Run it locally during your exam — no internet required.

## Getting Started

**Requirements:** Java 17+ installed.

```bash
./run.sh          # launches GUI (default)
java Main --cli   # terminal / CLI mode
```

Or compile and run manually:

```bash
javac chemistry/*.java calculators/*.java ui/*.java gui/*.java Main.java
java Main          # GUI
java Main --cli    # CLI
```

## Interface

The default interface is a **Swing GUI** with a dark sidebar for navigation and a tab-based layout for each calculator. Every panel has labelled input fields, a calculate button, and a dark terminal-style output area. Press **Enter** in any text field to trigger the calculation.

The original **CLI** is still fully functional via `java Main --cli`.

## Features

### 1. Equation Balancer
Balance any chemical equation using Gaussian elimination (exact rational arithmetic).
```
Input:  Fe2O3 + CO -> Fe + CO2
Output: Fe2O3 + 3CO  ->  2Fe + 3CO2
```

### 2. pH / Acid-Base Calculator
- Strong acids and bases
- Weak acids and bases (exact quadratic + 5% rule check)
- Buffers via Henderson-Hasselbalch
- Polyprotic acids (Ka1, Ka2, Ka3 + amphoteric species pH)
- Acid-base neutralisation (mixing strong acid + strong base)
- Ka ↔ Kb ↔ pKa ↔ pKb conversions

### 3. Redox Calculator
- Oxidation state assignment from formula and net charge
- Balance half-reactions in acid solution (auto-adds H₂O, H⁺, e⁻)
- Balance half-reactions in basic solution (converts to OH⁻ / H₂O)
- Combine two balanced half-reactions (equalize electrons)
- Guided full walkthrough from unbalanced equation

### 4. Thermodynamics
- ΔG = ΔH − T·ΔS with spontaneity analysis and crossover temperature
- ΔG° ↔ K (equilibrium constant)
- Non-standard ΔG = ΔG° + RT·ln Q
- Hess's Law (combine any number of reactions)
- ΔH°rxn from standard enthalpies of formation ΔHf°
- Clausius-Clapeyron (vapour pressure / boiling point)

### 5. Kinetics
- Arrhenius: `k = A·exp(−Ea/RT)` — find k, Ea, or A
- Activation energy from two (k, T) data points
- Integrated rate laws for 0th, 1st, and 2nd order — solve for [A], t, or k
- Half-life for each reaction order (with decay table for 1st order)
- Rate = k·[A]^m·[B]^n
- Determine reaction order from experimental data pairs

### 6. Equilibrium
- ICE table solver for weak acids, weak bases, and general equilibria
- Kc from measured equilibrium concentrations
- Kp ↔ Kc conversion: `Kp = Kc·(RT)^Δn`
- Reaction quotient Q — predict direction of reaction
- Percent dissociation / ionisation
- Ksp ↔ molar solubility for any salt MmXn

### 7. Stoichiometry
- Molar mass from formula with element breakdown
- Moles ↔ mass (`n = m/M`)
- Ideal gas law (`PV = nRT`) — solve for P, V, n, or T
- Limiting reagent and theoretical yield
- Percent yield
- Empirical formula from % mass composition
- Molecular formula from empirical formula + molar mass

### 8. Reference Tables
Built-in tables you can look up during the exam:
- Physical constants (R, F, NA, h, c, Kw)
- Common Ka/Kb values (acetic acid, NH₃, H₂CO₃, H₃PO₄, ...)
- Standard enthalpies of formation ΔHf°
- Common Ksp values
- Electrochemical series (standard reduction potentials E°)
- Organic compound classes and functional groups
- Carbon cycle and nitrogen cycle key reactions
- Periodic table trends (atomic radius, IE, electronegativity, metallic character)

## Formula Input Format

| Input | Meaning |
|-------|---------|
| `H2O` | Water |
| `Ca(OH)2` | Calcium hydroxide |
| `Fe2(SO4)3` | Iron(III) sulfate |
| `MnO4` | Permanganate (add charge separately) |
| `e-` | Electron (for redox half-reactions) |

## Project Structure

```
Program/
├── Main.java                     Entry point (GUI default, --cli for terminal)
├── run.sh                        Compile + run script
├── chemistry/
│   ├── Element.java              Element data class
│   ├── PeriodicTable.java        Singleton with all elements (H–Rn)
│   └── FormulaParser.java        Formula parser (handles parentheses, charges)
├── calculators/                  Pure calculation logic (no UI dependency)
│   ├── Calculator.java           Interface
│   ├── EquationBalancer.java     Gaussian elimination balancer
│   ├── PHCalculator.java         pH and acid-base
│   ├── RedoxCalculator.java      Oxidation states and redox
│   ├── ThermodynamicsCalc.java   ΔG, ΔH, ΔS, Hess, Clausius-Clapeyron
│   ├── KineticsCalc.java         Arrhenius, rate laws, half-life
│   ├── EquilibriumCalc.java      ICE tables, Kc, Kp, Ksp
│   └── StoichiometryCalc.java    Moles, yield, ideal gas, empirical formula
├── gui/                          Swing GUI (JFrame)
│   ├── ChemApp.java              Main window — sidebar + CardLayout
│   ├── BaseCalcPanel.java        Abstract base panel (input card + output area)
│   ├── HomePanel.java            Welcome / feature overview screen
│   ├── EquationBalancerPanel.java
│   ├── PHPanel.java
│   ├── RedoxPanel.java
│   ├── ThermodynamicsPanel.java
│   ├── KineticsPanel.java
│   ├── EquilibriumPanel.java
│   ├── StoichiometryPanel.java
│   └── ReferencePanel.java       8 scrollable reference tables
└── ui/
    └── MenuSystem.java           CLI menu navigation
```
