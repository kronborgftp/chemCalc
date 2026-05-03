package gui;

import javax.swing.*;
import java.awt.*;

public class ReferencePanel extends JPanel {

    public ReferencePanel() {
        setLayout(new BorderLayout(0, 10));
        setBackground(new Color(245, 246, 250));
        setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));

        JLabel header = new JLabel("Reference Tables");
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setForeground(new Color(20, 25, 40));
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabs.addTab("Constants",          textPane(constants()));
        tabs.addTab("Ka / Kb",            textPane(kaKb()));
        tabs.addTab("ΔHf°",              textPane(dhf()));
        tabs.addTab("Ksp",                textPane(ksp()));
        tabs.addTab("Reduction Potentials", textPane(redox()));
        tabs.addTab("Organic Classes",    textPane(organic()));
        tabs.addTab("Element Cycles",     textPane(cycles()));
        tabs.addTab("Periodic Trends",    textPane(periodicTrends()));

        add(tabs, BorderLayout.CENTER);
    }

    private JScrollPane textPane(String content) {
        JTextArea area = new JTextArea(content);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setBackground(new Color(18, 22, 30));
        area.setForeground(new Color(100, 210, 130));
        area.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        area.setLineWrap(false);
        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(BorderFactory.createLineBorder(new Color(215, 218, 228), 1, true));
        return sp;
    }

    // ── Content ───────────────────────────────────────────────────────────────

    private String constants() {
        return """
Physical Constants
═══════════════════════════════════════════
R  = 8.314      J/(mol·K)
R  = 0.08206    L·atm/(mol·K)
R  = 8.314×10⁻³ kJ/(mol·K)
F  = 96485      C/mol   (Faraday)
NA = 6.022×10²³ mol⁻¹  (Avogadro)
kB = 1.381×10⁻²³ J/K   (Boltzmann)
h  = 6.626×10⁻³⁴ J·s   (Planck)
c  = 2.998×10⁸  m/s
Kw = 1.0×10⁻¹⁴  at 25°C  (pH + pOH = 14)

Unit Conversions
───────────────────────────────────────────
1 atm = 101325 Pa = 760 mmHg = 760 Torr
1 bar = 100000 Pa
0°C   = 273.15 K
STP:  T=0°C,  P=1 atm,  V_m = 22.414 L/mol
SATP: T=25°C, P=1 bar,  V_m = 24.790 L/mol

Electrochemistry
───────────────────────────────────────────
ΔG° = -nFE°       ΔG° in J/mol
E°cell = E°cathode - E°anode
""";
    }

    private String kaKb() {
        return """
Common Ka and Kb Values at 25°C
═══════════════════════════════════════════
Acid                     Ka            pKa
───────────────────────────────────────────
HClO4  (strong)          ~∞             —
HI     (strong)          ~∞             —
HBr    (strong)          ~∞             —
HCl    (strong)          ~∞             —
H2SO4  (Ka1, strong)     ~∞             —
HNO3   (strong)          ~∞             —
HSO4⁻  (Ka2)            1.2×10⁻²      1.92
HF                       6.8×10⁻⁴      3.17
HNO2                     4.5×10⁻⁴      3.35
CH3COOH (acetic)         1.8×10⁻⁵      4.74
H2CO3  (Ka1)             4.3×10⁻⁷      6.37
H2S    (Ka1)             1.0×10⁻⁷      7.00
NH4⁺                     5.6×10⁻¹⁰     9.25
HCN                      6.2×10⁻¹⁰     9.21
HCO3⁻  (Ka2)             4.7×10⁻¹¹    10.33
H2O                      1.0×10⁻¹⁴    14.00
H3PO4  (Ka1)             7.5×10⁻³      2.12
H2PO4⁻ (Ka2)             6.2×10⁻⁸      7.21
HPO4²⁻ (Ka3)             4.8×10⁻¹³    12.32
H2SO3  (Ka1)             1.5×10⁻²      1.82
HSO3⁻  (Ka2)             6.3×10⁻⁸      7.20

Base                     Kb            pKb
───────────────────────────────────────────
NaOH   (strong)          ~∞             —
KOH    (strong)          ~∞             —
NH3                      1.8×10⁻⁵      4.74
CH3NH2 (methylamine)     4.4×10⁻⁴      3.36
(CH3)2NH (dimethylamine) 5.1×10⁻⁴      3.29
Pyridine (C5H5N)         1.7×10⁻⁹      8.77
Aniline (C6H5NH2)        4.3×10⁻¹⁰     9.37

Note: Ka × Kb = Kw = 1×10⁻¹⁴  (conjugate pair)
""";
    }

    private String dhf() {
        return """
Standard Enthalpies of Formation ΔHf° (kJ/mol, 25°C)
Elements in standard state: ΔHf° = 0 by definition
═══════════════════════════════════════════════════════
Species              ΔHf° (kJ/mol)
───────────────────────────────────────────────────────
H2O(l)               -285.8
H2O(g)               -241.8
CO2(g)               -393.5
CO(g)                -110.5
CH4(g)                -74.8
C2H6(g) (ethane)      -84.7
C2H4(g) (ethylene)    +52.5
C2H2(g) (acetylene)  +226.7
C6H6(l) (benzene)     +49.0
CH3OH(l) (methanol)  -238.7
C2H5OH(l) (ethanol)  -277.7
NH3(g)                -46.1
N2O(g)                +82.1
NO(g)                 +90.3
NO2(g)                +33.2
N2O4(g)               +9.2
HCl(g)                -92.3
HBr(g)                -36.3
HF(g)                -271.1
HI(g)                 +26.5
SO2(g)               -296.8
SO3(g)               -395.7
H2SO4(l)             -814.0
NaCl(s)              -411.1
NaOH(s)              -425.6
CaCO3(s) (calcite)  -1207.6
CaO(s)               -635.1
Ca(OH)2(s)           -986.1
Fe2O3(s)             -824.2
Fe3O4(s)            -1118.4
Al2O3(s)            -1675.7
SiO2(s)              -910.9
MgO(s)               -601.2
Mg(OH)2(s)           -924.5
""";
    }

    private String ksp() {
        return """
Common Ksp Values at 25°C
═════════════════════════════════════════════════════
Compound                 Formula        Ksp
─────────────────────────────────────────────────────
Silver chloride          AgCl           1.8×10⁻¹⁰
Silver bromide           AgBr           5.0×10⁻¹³
Silver iodide            AgI            8.5×10⁻¹⁷
Silver sulfate           Ag2SO4         1.2×10⁻⁵
Barium sulfate           BaSO4          1.1×10⁻¹⁰
Barium carbonate         BaCO3          2.6×10⁻⁹
Calcium carbonate        CaCO3          3.4×10⁻⁹
Calcium fluoride         CaF2           3.5×10⁻¹¹
Calcium sulfate          CaSO4          4.9×10⁻⁵
Calcium hydroxide        Ca(OH)2        5.5×10⁻⁶
Lead(II) iodide          PbI2           8.5×10⁻⁹
Lead(II) sulfate         PbSO4          1.6×10⁻⁸
Lead(II) sulfide         PbS            9.0×10⁻²⁹
Iron(II) hydroxide       Fe(OH)2        4.9×10⁻¹⁷
Iron(III) hydroxide      Fe(OH)3        2.8×10⁻³⁹
Magnesium hydroxide      Mg(OH)2        5.6×10⁻¹²
Copper(II) sulfide       CuS            6×10⁻³⁷
Zinc hydroxide           Zn(OH)2        3×10⁻¹⁷
Zinc sulfide             ZnS            2×10⁻²⁵
Manganese(II) hydroxide  Mn(OH)2        2×10⁻¹³
Aluminium hydroxide      Al(OH)3        3×10⁻³⁴
Mercury(I) chloride      Hg2Cl2         1.4×10⁻¹⁸
""";
    }

    private String redox() {
        return """
Standard Reduction Potentials E° (V, 25°C, 1M, 1 atm)
More positive = stronger oxidising agent (reduced more readily)
═══════════════════════════════════════════════════════════════
Half-reaction                                E° (V)
───────────────────────────────────────────────────────────────
F2 + 2e⁻  →  2F⁻                           +2.87
S2O8²⁻ + 2e⁻  →  2SO4²⁻                    +2.01
H2O2 + 2H⁺ + 2e⁻  →  2H2O                  +1.77
MnO4⁻ + 8H⁺ + 5e⁻  →  Mn²⁺ + 4H2O        +1.51
Cl2 + 2e⁻  →  2Cl⁻                         +1.36
Cr2O7²⁻ + 14H⁺ + 6e⁻  →  2Cr³⁺ + 7H2O    +1.33
O2 + 4H⁺ + 4e⁻  →  2H2O                   +1.23
Br2 + 2e⁻  →  2Br⁻                         +1.07
NO3⁻ + 4H⁺ + 3e⁻  →  NO + 2H2O            +0.96
Ag⁺ + e⁻  →  Ag                             +0.80
Fe³⁺ + e⁻  →  Fe²⁺                          +0.77
I2 + 2e⁻  →  2I⁻                            +0.54
O2 + 2H2O + 4e⁻  →  4OH⁻                   +0.40
Cu²⁺ + 2e⁻  →  Cu                           +0.34
Cu²⁺ + e⁻  →  Cu⁺                           +0.16
Sn⁴⁺ + 2e⁻  →  Sn²⁺                        +0.15
2H⁺ + 2e⁻  →  H2                             0.00  (SHE reference)
Fe³⁺ + 3e⁻  →  Fe                           -0.04
Pb²⁺ + 2e⁻  →  Pb                           -0.13
Sn²⁺ + 2e⁻  →  Sn                           -0.14
Ni²⁺ + 2e⁻  →  Ni                           -0.25
Co²⁺ + 2e⁻  →  Co                           -0.28
Fe²⁺ + 2e⁻  →  Fe                           -0.44
Cr³⁺ + 3e⁻  →  Cr                           -0.74
Zn²⁺ + 2e⁻  →  Zn                           -0.76
Mn²⁺ + 2e⁻  →  Mn                           -1.18
Al³⁺ + 3e⁻  →  Al                           -1.66
Mg²⁺ + 2e⁻  →  Mg                           -2.37
Na⁺ + e⁻   →  Na                            -2.71
Ca²⁺ + 2e⁻  →  Ca                           -2.87
K⁺ + e⁻    →  K                             -2.93
Li⁺ + e⁻   →  Li                            -3.04

E°cell = E°cathode − E°anode   (positive = spontaneous)
ΔG° = −nFE°cell   (F = 96485 C/mol)
ln K = nFE°/(RT)
""";
    }

    private String organic() {
        return """
Organic Compound Classes
═══════════════════════════════════════════════════════════════
Class           Functional Group        General Formula  Example
───────────────────────────────────────────────────────────────
Alkane          C−C  (only singles)     CnH2n+2          CH4, C3H8
Alkene          C=C  (one double)       CnH2n            C2H4 (ethene)
Alkyne          C≡C  (one triple)       CnH2n-2          C2H2 (ethyne)
Aromatic        benzene ring            CnHn (cyclic)    C6H6
Alcohol         −OH                     R−OH             CH3OH
Ether           −O−                     R−O−R'           CH3OCH3
Aldehyde        −CHO                    R−CHO            HCHO, CH3CHO
Ketone          −C(=O)−                 R−CO−R'          CH3COCH3
Carboxylic acid −COOH                   R−COOH           CH3COOH
Ester           −COO−                   R−COO−R'         CH3COOC2H5
Amine           −NH2  (primary)         R−NH2            CH3NH2
Amide           −CO−NH−                 R−CONH2          CH3CONH2
Nitrile         −C≡N                    R−CN             CH3CN
Halide          −X  (F, Cl, Br, I)      R−X              CH3Cl

IUPAC Prefixes (carbon chain length)
───────────────────────────────────────
1C: meth-    2C: eth-     3C: prop-
4C: but-     5C: pent-    6C: hex-
7C: hept-    8C: oct-     9C: non-    10C: dec-

Suffix: -ane (alkane), -ene (alkene), -yne (alkyne),
        -ol (alcohol), -al (aldehyde), -one (ketone),
        -oic acid (carboxylic), -yl …ate (ester)

Isomerism
───────────────────────────────────────
Structural (constitutional): same formula, different connectivity
Stereoisomers: same connectivity, different spatial arrangement
  - Geometric (cis/trans or E/Z): restricted rotation (C=C)
  - Optical (enantiomers): non-superimposable mirror images (chiral centres)
""";
    }

    private String cycles() {
        return """
Biogeochemical Cycles
═══════════════════════════════════════════════════════════════

CARBON CYCLE
───────────────────────────────────────────────────────────────
Photosynthesis:      6CO2 + 6H2O  →  C6H12O6 + 6O2
Respiration:         C6H12O6 + 6O2  →  6CO2 + 6H2O
Combustion:          CxHy + O2  →  CO2 + H2O
Dissolution (ocean): CO2 + H2O  ⇌  H2CO3  ⇌  H⁺ + HCO3⁻
Carbonate eq.:       CaCO3 + CO2 + H2O  ⇌  Ca²⁺ + 2HCO3⁻
Methane oxidation:   CH4 + 2O2  →  CO2 + 2H2O (microbes)

Carbon reservoirs:
  Atmosphere: ~800 Gt C (CO2 ≈ 420 ppm, rising ~2 ppm/yr)
  Ocean (surface): ~900 Gt C,  deep ocean: ~38 000 Gt C
  Terrestrial biosphere: ~560 Gt C
  Fossil fuels: ~5000–10 000 Gt C

Residence times:
  Atmosphere CO2: ~3–5 yr (exchange), ~100s yr (perturbation)
  Deep ocean: ~1000 yr

NITROGEN CYCLE
───────────────────────────────────────────────────────────────
N2 fixation (Haber-Bosch): N2 + 3H2  →  2NH3  (industrial, 400°C, Fe cat.)
Biological N2 fixation: N2 + 8H⁺ + 8e⁻  →  2NH3 + H2 (Rhizobium, Azotobacter)
Nitrification:
  2NH4⁺ + 3O2  →  2NO2⁻ + 4H⁺ + 2H2O  (Nitrosomonas)
  2NO2⁻  + O2  →  2NO3⁻                 (Nitrobacter)
Denitrification: 2NO3⁻ + 10e⁻ + 12H⁺  →  N2 + 6H2O  (Pseudomonas)
Assimilation:    NO3⁻ / NH4⁺  →  organic N  (plants, uptake)
Ammonification:  organic N  →  NH4⁺  (decomposers, bacteria)
Lightning:       N2 + O2  →  2NO  →  HNO3 (acid rain precursor)
Anammox:         NH4⁺ + NO2⁻  →  N2 + 2H2O  (anaerobic)

Nitrogen species and oxidation states:
  N2 (0)  NH3/NH4⁺ (−3)  N2O (+1)  NO (+2)
  NO2⁻ (+3)  NO2 (+4)  NO3⁻ (+5)

PHOSPHORUS CYCLE  (no major atmospheric component)
───────────────────────────────────────────────────────────────
Weathering:     Ca3(PO4)2(s)  →  PO4³⁻ (dissolved)
Uptake:         PO4³⁻ + organisms  →  organic P
Mineralisation: organic P  →  PO4³⁻ (decomposers)
""";
    }

    private String periodicTrends() {
        return """
Periodic Table Trends
═══════════════════════════════════════════════════════════════

Atomic Radius
─────────────────────────────────────────
  Across period (→):   DECREASES  (more protons, same shell)
  Down group    (↓):   INCREASES  (new electron shell added)

Ionisation Energy (1st IE)
─────────────────────────────────────────
  Across period (→):   INCREASES  (harder to remove e⁻, more nuclear charge)
  Down group    (↓):   DECREASES  (outer e⁻ farther, more shielded)
  Anomalies:   Be>B  (2s vs 2p),   N>O  (half-filled 2p stable)

Electronegativity (Pauling)
─────────────────────────────────────────
  Across period (→):   INCREASES
  Down group    (↓):   DECREASES
  Highest: F (3.98)   Lowest (excl. noble gases): Cs (0.79)

Electron Affinity
─────────────────────────────────────────
  Across period (→):   generally INCREASES (more negative)
  Down group    (↓):   generally DECREASES
  Exception: halogens row 2 < row 3 (fluorine less than chlorine due to small size)

Metallic Character
─────────────────────────────────────────
  Across period (→):   DECREASES
  Down group    (↓):   INCREASES

Key Group Properties
─────────────────────────────────────────
  Group 1  (alkali metals):     very reactive, +1 ion
  Group 2  (alkaline earths):   reactive, +2 ion
  Group 17 (halogens):          very reactive, −1 ion, oxidising agents
  Group 18 (noble gases):       inert, full valence shell
  Transition metals:             variable oxidation states, coloured compounds

Oxide Character
─────────────────────────────────────────
  Metal oxides:     basic  (e.g. Na2O + H2O → NaOH)
  Non-metal oxides: acidic (e.g. SO3 + H2O → H2SO4)
  Amphoteric oxides: Al2O3, ZnO (react with both acid and base)
""";
    }
}
