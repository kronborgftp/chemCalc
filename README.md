# DTU Chemistry Exam Toolkit

A Java Swing GUI covering every major calculation topic from the DTU 26020/26021 Chemistry curriculum. Built for use during the written exam — no internet required.

60+ calculators across 10 topic areas, a full-text search bar, a clickable directory, and a **Smart Suggest** feature that reads a pasted exam question and ranks the most relevant calculators.

## Getting Started

**Requirements:** Java 17+

```bash
./run.sh        # compile and launch GUI
```

Or manually:

```bash
javac chemistry/*.java calculators/*.java ui/*.java gui/*.java Main.java -d out
java -cp out Main
```

## Navigation

**Sidebar search bar** — type any keyword ("buffer", "ksp", "nernst", "hess", "bond angle") and matching calculators appear as clickable results that jump directly to the right panel and tab.

**Home** — scrollable directory of all 60+ calculators, grouped by topic. Click any row to open it.

**Smart Suggest** — paste the full exam question text (Danish or English). The program scores every calculator against the question vocabulary and shows the top matches ranked by relevance with an "Open →" button.

Press **Enter** in any input field to trigger the calculation. All numeric fields accept scientific notation (`1.8e-5`) and power notation (`10^-5`).

---

## Calculators

### Equation Balancer
Balance any chemical equation using Gaussian elimination (exact rational arithmetic).

### pH / Acid-Base
| Tab | What it solves |
|-----|---------------|
| Strong Acid | pH = −log\[H⁺\] |
| Strong Base | pOH = −log\[OH⁻\], pH = 14 − pOH |
| Weak Acid | Ka + ICE table → \[H⁺\], pH, % ionised |
| Weak Base | Kb + ICE table → \[OH⁻\], pOH, pH |
| Buffer | Henderson-Hasselbalch: pH = pKa + log(\[A⁻\]/\[HA\]) |
| Neutralisation | Mix strong acid + strong base → excess / equivalence point |
| Buffer + Titrant | pH after adding strong acid or base to a buffer (mole-based) |

### Redox
| Tab | What it solves |
|-----|---------------|
| Oxidation States | Assign ox. states from formula and net charge |
| Balance Half-Rxn (Acid) | Auto-adds H₂O, H⁺, e⁻ in acidic solution |
| Balance Half-Rxn (Base) | Converts to OH⁻/H₂O in basic solution |
| Combine Half-Reactions | Find LCM of electrons, compute multipliers |
| Galvanic Cell E° | E°cell = E°cathode − E°anode, ΔG°, K |
| Formal Charge | FC = V − L − B/2 for up to 4 atoms |

### Electrochemistry
| Tab | What it solves |
|-----|---------------|
| E°cell | E°cell, ΔG°, K from two E° reduction values |
| Nernst Equation | E = E° − (RT/nF)·ln Q at any temperature |
| ΔG° / K / E° | Three-way converter between ΔG°, K, and E° |
| Faraday's Law | Mass deposited and charge in electrolysis |
| Concentration Cell | E for same electrode at different concentrations |
| Ksp from E° | Derive Ksp from the dissolution cell potential |

### Thermodynamics
| Tab | What it solves |
|-----|---------------|
| ΔG = ΔH − TΔS | Spontaneity, crossover T = ΔH/ΔS (boiling point estimate) |
| ΔG° ↔ K | ΔG° = −RT·ln K |
| Non-standard ΔG | ΔG = ΔG° + RT·ln Q |
| Hess's Law | Add scaled reactions to find target ΔH° |
| ΔH from ΔHf° | ΔH° = Σ ΔHf°(products) − Σ ΔHf°(reactants) |
| Clausius-Clapeyron | ln(P₂/P₁) = −ΔHvap/R·(1/T₂ − 1/T₁) |
| Bond Enthalpy | ΔH ≈ Σ bonds broken − Σ bonds formed |

### Kinetics
| Tab | What it solves |
|-----|---------------|
| Arrhenius | k = A·exp(−Ea/RT) — find k at new T |
| Find Ea | Activation energy from two (k, T) pairs |
| Integrated Rate Law | \[A\]t from k and t for 0th, 1st, 2nd order |
| Half-Life | t½ for 0th, 1st, 2nd order |
| Rate = k\[A\]ᵐ\[B\]ⁿ | Compute rate given k, concentrations, orders |
| Graham's Law | Rate ∝ 1/√M — compare effusion / diffusion |
| Order from Data | Determine x, y in rate = k\[A\]ˣ\[B\]ʸ from experiment table |

### Equilibrium
| Tab | What it solves |
|-----|---------------|
| ICE — Weak Acid | Ka + ICE → equilibrium \[H⁺\] |
| ICE — Weak Base | Kb + ICE → equilibrium \[OH⁻\] |
| ICE — General | K + ICE for any A ⇌ b·B |
| Kc from Concentrations | Kc = \[P\]^a / \[R\]^b |
| Kp ↔ Kc | Kp = Kc·(RT)^Δn |
| Q vs K Direction | Q < K → forward, Q > K → reverse |
| Ksp ↔ Molar Solubility | s = (Ksp / mᵐ·nⁿ)^(1/(m+n)) |
| Osmosis / Osmometry | π = i·c·R·T — osmotic pressure and molar mass from osmometry |
| Selective Precipitation | Which salt precipitates first at a given \[anion\]? |

### Stoichiometry
| Tab | What it solves |
|-----|---------------|
| Molar Mass | M = Σ (element mass × count) |
| Moles ↔ Mass | n = m/M, N = n·NA |
| Ideal Gas Law | PV = nRT — solve for any variable |
| Limiting Reagent | Find limiting reactant, theoretical yield |
| % Yield | % yield = (actual / theoretical) × 100 |
| Empirical Formula | Empirical/molecular formula from % composition |
| Element Lookup | Atomic number, mass, group, period, EN for any element |
| Colligative Properties | ΔTb, ΔTf, vapour pressure lowering |
| Isotope / % Abundance | Average atomic mass from isotope masses and abundances |
| Electron Configuration | Write e⁻ config and find valence electrons |
| Photon Energy | E = hc/λ — wavelength ↔ energy (J and kJ/mol) |
| Unit Cell / Crystal | ρ = Z·M/(NA·a³) — density, lattice parameter, # unit cells |

### VSEPR / Geometry
| Tab | What it solves |
|-----|---------------|
| From Formula | Auto-detect central atom, lone pairs, geometry, polarity |
| Manual | Enter bonding domains and lone pairs directly |
| Reference Table | All BP/LP combos: electron geometry, molecular geometry, bond angles |

### Reference Tables
Built-in read-only tables:
- Physical constants (R, F, NA, h, c, Kw)
- Ka / Kb values for common acids and bases
- Standard enthalpies of formation ΔHf°
- Common Ksp values
- Electrochemical series (standard reduction potentials E°)
- Organic compound classes and functional groups
- Carbon cycle and nitrogen cycle key reactions
- Periodic table trends

---

## Input Format

| Input | Meaning |
|-------|---------|
| `1.8e-5` | 1.8 × 10⁻⁵ |
| `10^-5` | 10⁻⁵ |
| `H2O` | Water |
| `Ca(OH)2` | Calcium hydroxide |
| `Fe2(SO4)3` | Iron(III) sulfate |
| `e-` | Electron (redox half-reactions) |

---

## Project Structure

```
Program/
├── Main.java                       Entry point (GUI)
├── run.sh                          Compile + run script
├── chemistry/
│   ├── Element.java                Element data class
│   ├── PeriodicTable.java          Singleton with all elements (H–Rn)
│   └── FormulaParser.java          Formula parser (handles parentheses, charges)
├── calculators/                    Pure calculation logic (no UI dependency)
│   ├── PHCalculator.java           pH, acid-base, expression parser (10^x)
│   ├── RedoxCalculator.java        Oxidation states and half-reaction balancing
│   ├── ThermodynamicsCalc.java     ΔG, ΔH, ΔS, Hess's Law, Clausius-Clapeyron
│   ├── KineticsCalc.java           Arrhenius, rate laws, half-life
│   ├── EquilibriumCalc.java        ICE tables, Kc, Kp, Ksp
│   ├── StoichiometryCalc.java      Moles, yield, ideal gas, empirical formula
│   └── EquationBalancer.java       Gaussian elimination balancer
└── gui/                            Swing GUI (JFrame + CardLayout)
    ├── ChemApp.java                Main window — sidebar, search, CardLayout
    ├── BaseCalcPanel.java          Abstract base: input card + output area + goToTab()
    ├── HomePanel.java              Clickable directory of all calculators
    ├── SuggestPanel.java           Smart Suggest — paste question → ranked matches
    ├── EquationBalancerPanel.java
    ├── PHPanel.java
    ├── RedoxPanel.java
    ├── ElectrochemistryPanel.java
    ├── ThermodynamicsPanel.java
    ├── KineticsPanel.java
    ├── EquilibriumPanel.java
    ├── StoichiometryPanel.java
    ├── VESPRPanel.java
    └── ReferencePanel.java         8 scrollable reference tables
```
