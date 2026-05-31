package gui;

import java.util.*;

/**
 * Curated excerpts from the DTU 26021 lecture notes, linked to FactsPanel entries.
 * All content is derived from the lecture markdown files.
 */
public class LectureData {

    // ── Note entries ──────────────────────────────────────────────────────────
    // {key, lectureRef, sectionTitle, htmlContent}
    public static final String[][] NOTES = {

        {"lec1.isotopes",
         "Lecture 1 — Atoms, Molecules, Stoichiometry (Ch. 2+3+4)",
         "Isotopes, Isobars & Isotones",
         "<b>Isotope notation:</b> the superscript is the <b>mass number A</b> (protons + neutrons) and the subscript is the <b>atomic number Z</b> (protons only). Same Z = same element."
         + "<br><br><b>Definitions:</b>"
         + "<ul>"
         + "<li><b>Isotopes</b>: same Z, different N and A. Same chemical behaviour, different mass. (e.g. ¹H, ²H, ³H)</li>"
         + "<li><b>Isobars</b>: same A, different Z (different elements).</li>"
         + "<li><b>Isotones</b>: same N (neutron count), different Z.</li>"
         + "</ul>"
         + "<b>Worked example:</b> ⁷²Zn (Z=30, A=72, N=42), ⁷⁵As (Z=33, A=75, N=42), ⁷⁴Ge (Z=32, A=74, N=42). "
         + "All have N=42 → they are <b>isotones</b>."},

        {"lec2.dissolution",
         "Lecture 2 — Aqueous Reactions & Gases (Ch. 4+9)",
         "Dissolution & Ion Counting",
         "<b>Strong electrolytes</b> (ionic salts, strong acids, strong bases) dissociate completely in water. "
         + "Each subscript in the ionic formula tells you how many of that ion are produced per formula unit."
         + "<br><br><b>Rule:</b> total ions per formula unit = sum of the subscripts."
         + "<br><br><b>Examples:</b>"
         + "<ul>"
         + "<li>NaCl → Na⁺ + Cl⁻ &nbsp;&nbsp; (2 ions)</li>"
         + "<li>CaCl₂ → Ca²⁺ + 2Cl⁻ &nbsp;&nbsp; (3 ions)</li>"
         + "<li>Al₂(SO₄)₃ → 2 Al³⁺ + 3 SO₄²⁻ &nbsp;&nbsp; (5 ions)</li>"
         + "</ul>"
         + "Water surrounds each ion with a <b>hydration shell</b>: cations attract the O end (δ⁻) of water; anions attract the H ends (δ⁺). "
         + "This solvation energy drives dissolution."},

        {"lec2.grahams",
         "Lecture 2 — Aqueous Reactions & Gases (Ch. 4+9)",
         "Graham's Law of Effusion",
         "<b>Graham's law:</b> the rate of effusion (or diffusion) of a gas is inversely proportional to the square root of its molar mass:"
         + "<br><br>&nbsp;&nbsp;&nbsp;&nbsp;r₁ / r₂ = √(M₂ / M₁)"
         + "<br><br>Lighter gases effuse <b>faster</b>."
         + "<br><br><b>Application to H₂/N₂ mixture:</b> H₂ (M=2) vs N₂ (M=28) → r(H₂)/r(N₂) = √(28/2) = √14 ≈ 3.7. "
         + "H₂ escapes about 3.7× faster through a puncture. After time, the container is enriched in N₂ → "
         + "partial pressure of N₂ <b>becomes larger</b> than that of H₂."
         + "<br><br><b>Lab demo (from lecture):</b> NH₃ (M=17) vs HCl (M=36.5) diffuse toward each other in a tube; "
         + "r(NH₃)/r(HCl) = √(36.5/17) ≈ 1.46 → NH₃ travels farther → white NH₄Cl ring forms nearer the HCl end."},

        {"lec4.IE",
         "Lecture 4 — Electronic Structure & Periodic Table (Ch. 6)",
         "Ionization Energy & Electron Affinity",
         "<b>Ionization energy (IE₁):</b> energy required to remove one electron from a gaseous atom: X(g) → X⁺(g) + e⁻ (always endothermic)."
         + "<br><br><b>Trends:</b>"
         + "<ul>"
         + "<li><b>Across a period (→):</b> IE increases. Z_eff (effective nuclear charge) rises while shielding stays similar → electrons held more tightly.</li>"
         + "<li><b>Down a group (↓):</b> IE decreases. Electrons in higher shells are farther from nucleus and more shielded by inner electrons.</li>"
         + "</ul>"
         + "<b>Exceptions in period 2:</b> IE dips Be→B (B loses a 2p electron, easier than 2s) and N→O "
         + "(O has a paired 2p electron — extra repulsion makes it easier to remove, despite higher Z)."
         + "<br><br><b>Electron affinity (EA):</b> energy change when an electron is added: X(g) + e⁻ → X⁻(g). "
         + "Halogens have the most negative EA (strongly exothermic). Noble gases have positive EA (unfavourable)."
         + "<br><br><b>Electronegativity:</b> χ = (IE + EA)/2 (Mulliken). Fluorine is the most electronegative element."},

        {"lec4.radius",
         "Lecture 4 — Electronic Structure & Periodic Table (Ch. 6)",
         "Atomic & Ionic Radius Trends",
         "<b>Atomic radius across a period (→):</b> decreases. Z_eff increases while shielding is similar → electrons pulled inward. "
         + "Period 2 Z_eff: Li 1.3, Be 1.9, B 2.4, C 3.1, N 3.8, O 4.5, F 5.1, Ne 5.8."
         + "<br><br><b>Atomic radius down a group (↓):</b> increases. New electron shells (higher n) dominate over the nuclear charge increase."
         + "<br><br><b>Ionic radius:</b>"
         + "<ul>"
         + "<li><b>Cations</b> (lost e⁻): smaller than parent atom — same nuclear charge, fewer electrons, less repulsion.</li>"
         + "<li><b>Anions</b> (gained e⁻): larger than parent atom — same nuclear charge, more electrons, more repulsion.</li>"
         + "</ul>"
         + "<b>Isoelectronic series</b> (same number of electrons, different Z): more protons → stronger pull on the same electron cloud → smaller."
         + "<br>Example: Rb⁺ (37p), Sr²⁺ (38p), Br⁻ (35p), Se²⁻ (34p) — all have 36 electrons. "
         + "Sr²⁺ has the most protons → <b>smallest ionic radius</b>."},

        {"lec4.EN",
         "Lecture 4 — Electronic Structure & Periodic Table (Ch. 6)",
         "Electronegativity & Bond Polarity",
         "<b>Electronegativity (χ, Pauling scale):</b> how strongly an atom attracts bonding electrons."
         + "<br><br><b>Trend:</b> increases left→right and bottom→top. Fluorine (χ ≈ 4.0) is the highest."
         + "<br><br><b>Key values:</b> F > O > N ≈ Cl > Br > C > S ≈ I > H."
         + "<br><br><b>Bond character from ΔEN:</b>"
         + "<ul>"
         + "<li>ΔEN = 0 → purely covalent (e.g. Cl₂)</li>"
         + "<li>ΔEN < ~0.4 → nonpolar covalent</li>"
         + "<li>ΔEN 0.4–1.7 → polar covalent (e.g. HCl, H₂O)</li>"
         + "<li>ΔEN > ~1.7 → mostly ionic (e.g. NaCl, LiF)</li>"
         + "</ul>"
         + "<b>Covalent character order</b> (increasing): LiF &lt; CsBr &lt; NaBr &lt; HI &lt; Cl₂. "
         + "LiF has the largest ΔEN → most ionic. Cl₂ ΔEN = 0 → purely covalent."},

        {"lec4.config",
         "Lecture 4 — Electronic Structure & Periodic Table (Ch. 6)",
         "Electron Configuration & Periodic Position",
         "<b>Three filling rules:</b>"
         + "<ol>"
         + "<li><b>Aufbau:</b> fill lowest-energy orbitals first. Order: 1s, 2s, 2p, 3s, 3p, 4s, 3d, 4p, 5s, ...</li>"
         + "<li><b>Pauli exclusion:</b> each orbital holds max 2 electrons with opposite spins.</li>"
         + "<li><b>Hund's rule:</b> within a subshell, fill each orbital singly before pairing (maximise spin).</li>"
         + "</ol>"
         + "<b>Reading group and period from the outermost shell:</b>"
         + "<ul>"
         + "<li>Highest n = period number.</li>"
         + "<li>s-block: group = # of s electrons (1 or 2).</li>"
         + "<li>p-block: group = 10 + s-electrons + p-electrons.</li>"
         + "</ul>"
         + "<b>Example — 4s²4p⁵:</b> highest n = 4 → <b>Period 4</b>. Valence: 2+5 = 7 → <b>Group 17</b> (halogens). Element = Br (Z=35)."
         + "<br><br>Notation: number = n (shell), letter = subshell (s/p/d/f), superscript = electron count."},

        {"lec4.isoelectronic",
         "Lecture 4 — Electronic Structure & Periodic Table (Ch. 6)",
         "Isoelectronic Species",
         "<b>Isoelectronic</b> = same number of electrons. The electron configuration is identical; only the nuclear charge (protons) differs."
         + "<br><br><b>Neon has 10 electrons</b> (1s²2s²2p⁶). Species isoelectronic with Ne:"
         + "<ul>"
         + "<li>Na⁺: 11 − 1 = 10 e⁻ ✓</li>"
         + "<li>Mg²⁺: 12 − 2 = 10 e⁻ ✓</li>"
         + "<li>F⁻: 9 + 1 = 10 e⁻ ✓</li>"
         + "<li>O²⁻: 8 + 2 = 10 e⁻ ✓</li>"
         + "<li>Al³⁺: 13 − 3 = 10 e⁻ ✓</li>"
         + "</ul>"
         + "<b>NOT isoelectronic with Ne:</b>"
         + "<ul>"
         + "<li>S²⁻: 16 + 2 = 18 e⁻ (isoelectronic with Ar, not Ne) ✗</li>"
         + "<li>Cl⁻: 17 + 1 = 18 e⁻ (also Ar-like) ✗</li>"
         + "</ul>"},

        {"lec5.VSEPR",
         "Lecture 5 — Chemical Bonds, Geometries & Hybridization (Ch. 7+8)",
         "VSEPR Geometry",
         "<b>VSEPR (Valence Shell Electron Pair Repulsion):</b> bonding pairs AND lone pairs repel each other → maximise separation around the central atom."
         + "<br><br><b>Steric number (SN)</b> = bonds + lone pairs on central atom:"
         + "<ul>"
         + "<li>SN=2, LP=0 → linear, 180°</li>"
         + "<li>SN=3, LP=0 → trigonal planar, 120°</li>"
         + "<li>SN=3, LP=1 → bent, ~118°</li>"
         + "<li>SN=4, LP=0 → tetrahedral, 109.5°</li>"
         + "<li>SN=4, LP=1 → trigonal pyramidal, ~107°</li>"
         + "<li>SN=4, LP=2 → bent, ~104.5°</li>"
         + "</ul>"
         + "<b>Lone pairs compress bond angles</b> by ~2–2.5° each (higher electron density, more repulsion)."
         + "<br><br><b>Planarity:</b> linear, trigonal planar, and bent are planar. Tetrahedral is 3-D — NOT planar."
         + "<br><br><b>N₂H₄ (H-N-H angle):</b> each N bonds to 2 H + 1 N and has 1 lone pair → SN=4, LP=1 → trigonal pyramidal → H-N-H ≈ 107°."
         + "<br><b>SiH₄:</b> Si has 4 bonds, 0 lone pairs → tetrahedral → NOT planar."},

        {"lec5.lewis",
         "Lecture 5 — Chemical Bonds, Geometries & Hybridization (Ch. 7+8)",
         "Lewis Structures",
         "<b>Strategy for drawing Lewis structures:</b>"
         + "<ol>"
         + "<li>Skeletal structure — central atom usually has lowest electronegativity (not H).</li>"
         + "<li>Count total valence electrons (add 1 per negative charge, subtract 1 per positive).</li>"
         + "<li>Draw single bonds to all peripheral atoms (2 e⁻ each).</li>"
         + "<li>Fill octets on peripheral atoms with remaining lone pairs.</li>"
         + "<li>If central atom is electron-deficient, convert lone pairs on periphery into double/triple bonds.</li>"
         + "</ol>"
         + "<b>Formal charge:</b> FC = V − L − B/2. Best structure minimises |FC| and places negative FC on more electronegative atoms."
         + "<br><br><b>Oxalate C₂O₄²⁻:</b> total VE = 2×4 + 4×6 + 2 = 34. Each C bonds to 2 O + 1 C. Best structure: C–C single bond, each C has one C=O (double bond) and one C–O⁻ (single bond). This minimises formal charges on C."},

        {"lec5.formal_charge",
         "Lecture 5 — Chemical Bonds, Geometries & Hybridization (Ch. 7+8)",
         "Formal Charge",
         "<b>Formal charge (FC):</b> the charge an atom would carry if all bonding electrons were shared equally."
         + "<br><br>&nbsp;&nbsp;&nbsp;&nbsp;<b>FC = V − L − B/2</b>"
         + "<br><br><b>Where:</b>"
         + "<ul>"
         + "<li><b>V</b> = valence electrons of the free atom (= group number for main-group elements)</li>"
         + "<li><b>L</b> = lone-pair (non-bonding) electrons on that atom</li>"
         + "<li><b>B</b> = bonding electrons on that atom (2 per single bond, 4 per double, 6 per triple)</li>"
         + "</ul>"
         + "Sum of all FC in a molecule/ion = overall charge."
         + "<br><br><b>Example — N in NO₃⁻:</b>"
         + "<ul>"
         + "<li>V = 5 (group 15)</li>"
         + "<li>L = 0 lone pairs on N</li>"
         + "<li>B = 1 double bond (4) + 2 single bonds (2+2) = 8</li>"
         + "<li>FC(N) = 5 − 0 − 8/2 = <b>+1</b></li>"
         + "</ul>"
         + "The two singly-bonded O atoms each have FC = −1; the doubly-bonded O has FC = 0. Sum = +1 − 1 − 1 + 0 = −1 ✓"},

        {"lec5.polarity",
         "Lecture 5 — Chemical Bonds, Geometries & Hybridization (Ch. 7+8)",
         "Molecular Polarity & Bond Character",
         "<b>Bond polarity</b> arises from electronegativity differences: a polar bond has δ⁺ on the less electronegative atom and δ⁻ on the more electronegative."
         + "<br><br><b>Molecular polarity</b> depends on BOTH bond polarity AND geometry:"
         + "<ul>"
         + "<li>All bond dipoles cancel by symmetry → <b>nonpolar</b> (e.g. CO₂ linear, BF₃ trigonal planar, CH₄ tetrahedral)</li>"
         + "<li>Dipoles don't fully cancel → <b>polar</b> (e.g. H₂O bent, NH₃ pyramidal)</li>"
         + "</ul>"
         + "<b>CS₂:</b> carbon central, two C=S double bonds, linear (SN=2, no lone pairs on C). "
         + "C and S have different EN, so C=S bonds are polar — but the two dipoles point in exactly opposite directions and cancel. "
         + "CS₂ is <b>linear and nonpolar</b>."
         + "<br><br><b>Covalent character (increasing):</b> LiF &lt; CsBr &lt; NaBr &lt; HI &lt; Cl₂"},

        {"lec5_6.hbond",
         "Lectures 5+6 — Bonds & Intermolecular Forces (Ch. 7+10+11)",
         "Hydrogen Bonding",
         "<b>Hydrogen bond:</b> X–H···Y where X and Y are F, O, or N. The H is covalently bonded to one electronegative atom and attracted to a lone pair on another."
         + "<br><br><b>Strength:</b> ~10–100 kJ/mol — much stronger than van der Waals (<10 kJ/mol), but weaker than covalent bonds (~200–900 kJ/mol)."
         + "<br><br><b>Why HF has an anomalously high boiling point vs HCl, HBr, HI:</b>"
         + "<ul>"
         + "<li>HCl, HBr, HI are held together only by van der Waals (London dispersion) forces — boiling point increases down the series because heavier molecules have stronger dispersion.</li>"
         + "<li>HF is the <i>lightest</i> of the four but has the <i>highest</i> boiling point because F is the most electronegative element → F–H bonds are highly polar → very strong F–H···F hydrogen bonds.</li>"
         + "</ul>"
         + "<b>Other consequences of H-bonding:</b> anomalously high bp of H₂O and NH₃; ice less dense than liquid water "
         + "(H-bonds form an open hexagonal lattice); DNA double helix held together by H-bonds between base pairs."},

        {"lec6.vapor_pressure",
         "Lecture 6 — Intermolecular Forces, Liquids & Solids (Ch. 10+11)",
         "Vapor Pressure & Colligative Properties",
         "<b>Vapor pressure</b> = equilibrium pressure of the gas above a liquid. Higher VP = more volatile = weaker intermolecular forces."
         + "<br><br><b>At room temperature, decreasing VP order:</b>"
         + "<ul>"
         + "<li><b>Ethanol</b> (C₂H₅OH, M=46): moderate H-bonding (one –OH), small molecule → <b>highest VP</b></li>"
         + "<li><b>Water</b> (H₂O): strong H-bonding network (up to 4 H-bonds per molecule) → lower VP</li>"
         + "<li><b>Glucose</b> (C₆H₁₂O₆): large molecule, many –OH groups, extensive H-bonding → essentially non-volatile</li>"
         + "<li><b>NaCl</b> (ionic solid): ionic lattice, essentially zero VP at room temperature</li>"
         + "</ul>"
         + "<b>Clausius-Clapeyron equation:</b> ln(P₂/P₁) = −ΔH_vap/R · (1/T₂ − 1/T₁). "
         + "Relates VP at two temperatures to the enthalpy of vaporisation."
         + "<br><br><b>Colligative properties</b> depend on the <i>amount</i> of solute, not its identity: "
         + "ΔTb = Kb·m (boiling point elevation), ΔTf = Kf·m (freezing point depression), osmotic pressure π = iMRT."},

        {"lec7.catalyst",
         "Lecture 7 — Chemical Kinetics & Equilibria (Ch. 12+13)",
         "Catalysis & Activation Energy",
         "<b>Activation energy (Eₐ):</b> the energy barrier molecules must overcome to react. "
         + "Only the fraction of molecules with energy > Eₐ can react: f = exp(−Eₐ/RT)."
         + "<br><br><b>Arrhenius equation:</b> k = A·exp(−Eₐ/RT). Lower Eₐ → larger k → faster reaction."
         + "<br><br><b>A catalyst:</b>"
         + "<ul>"
         + "<li>Provides an alternative pathway with <b>lower Eₐ</b></li>"
         + "<li>Increases the rate of both forward AND reverse reactions equally</li>"
         + "<li>Is <b>NOT consumed</b> in the reaction (regenerated in the catalytic cycle)</li>"
         + "<li>Does <b>NOT change ΔG, ΔH, ΔS, or K</b> — only how fast equilibrium is reached</li>"
         + "<li>Does <b>NOT shift the equilibrium position</b></li>"
         + "</ul>"
         + "Types: homogeneous (same phase as reactants), heterogeneous (different phase, e.g. Pt in catalytic converters), biological (enzymes)."},

        {"lec7.le_chatelier",
         "Lecture 7 — Chemical Kinetics & Equilibria (Ch. 12+13)",
         "Le Chatelier's Principle",
         "<b>Le Chatelier's principle:</b> when a system at equilibrium is disturbed, it shifts in the direction that <i>partially counteracts</i> the change."
         + "<br><br><b>Pressure effect (gas-phase equilibria):</b>"
         + "<ul>"
         + "<li>Increasing pressure → shifts toward the side with <b>fewer moles of gas</b></li>"
         + "<li>Decreasing pressure → shifts toward more moles of gas</li>"
         + "<li>If Δn_gas = 0, pressure has no effect on the equilibrium position</li>"
         + "</ul>"
         + "<b>Example:</b> 2SO₂(g) + O₂(g) ⇌ 2SO₃(g): 3 mol gas on left, 2 on right. "
         + "Higher pressure → shifts right → more SO₃."
         + "<br><br><b>Temperature effect:</b>"
         + "<ul>"
         + "<li>Treat heat as a reactant (endothermic) or product (exothermic).</li>"
         + "<li>Increasing T → shifts away from heat → for exothermic: shifts left, K decreases.</li>"
         + "<li>Increasing T → for endothermic: shifts right, K increases.</li>"
         + "</ul>"
         + "<b>Concentration:</b> adding a reactant or removing a product shifts equilibrium forward; vice versa for reverse."},

        {"lec9.organic",
         "Lecture 9 — Organic Chemistry (Ch. 20)",
         "Functional Groups",
         "<b>Functional groups</b> determine chemical reactivity. Key groups and how to identify them:"
         + "<ul>"
         + "<li><b>Alcohol (–OH):</b> hydroxyl attached to C. Can H-bond. e.g. ethanol.</li>"
         + "<li><b>Carboxylic acid (–COOH):</b> C=O with OH on same C. Acidic. e.g. acetic acid.</li>"
         + "<li><b>Ester (–C(=O)–O–C):</b> C=O with O–C (not OH). Formed from acid + alcohol. e.g. ethyl acetate.</li>"
         + "<li><b>Ether (–C–O–C–):</b> O bridging two C atoms. e.g. diethyl ether, THF.</li>"
         + "<li><b>Aldehyde (–CHO):</b> C=O at chain end (one H on carbonyl C). e.g. formaldehyde, acetaldehyde.</li>"
         + "<li><b>Ketone (C=O):</b> C=O in the chain interior (two C neighbours). e.g. acetone.</li>"
         + "<li><b>Amine (–NH₂, –NHR, –NR₂):</b> N with H or alkyl groups. Basic. e.g. propylamine.</li>"
         + "<li><b>Amide (–C(=O)–NH–):</b> C=O adjacent to N. The peptide bond is an amide.</li>"
         + "</ul>"
         + "<b>Key distinction:</b> look at what is bonded to the C=O carbon. "
         + "NH next to C=O → amide. O–C next to C=O → ester. OH next to C=O → carboxylic acid."},

        {"lec9.isomers",
         "Lecture 9 — Organic Chemistry (Ch. 20)",
         "Structural Isomers & Alcohol Counting",
         "<b>Structural (constitutional) isomers:</b> same molecular formula, different connectivity → different molecules with different properties."
         + "<br><br><b>Counting alcohols with C₄H₁₀O</b> (–OH required):"
         + "<br>Systematically vary the C skeleton and OH position:"
         + "<ol>"
         + "<li><b>1-butanol</b>: CH₃CH₂CH₂CH₂OH (n-butyl, OH at C1)</li>"
         + "<li><b>2-butanol</b>: CH₃CH₂CH(OH)CH₃ (OH at C2)</li>"
         + "<li><b>2-methyl-1-propanol</b> (isobutanol): (CH₃)₂CHCH₂OH (branched, OH at C1)</li>"
         + "<li><b>2-methyl-2-propanol</b> (tert-butanol): (CH₃)₃COH (OH on branching C)</li>"
         + "</ol>"
         + "Total: <b>4 isomers</b>."
         + "<br><br><b>General approach:</b> first list all C skeletons (n-butyl, isobutyl), then place –OH at each non-equivalent carbon on each skeleton."},

        {"lec10.weak_acid",
         "Lecture 10 — Acids, Bases & Solubility (Ch. 14+15)",
         "Weak Acids & Ka",
         "<b>Weak acid HA</b> partially dissociates: HA ⇌ H⁺ + A⁻"
         + "<br>Equilibrium constant: Ka = [H⁺][A⁻]/[HA]. For weak acids Ka ≪ 1."
         + "<br><br><b>Consequences:</b>"
         + "<ul>"
         + "<li>[HA]_eq ≈ C (most acid remains undissociated) → <b>[HA] ≫ [H⁺]</b></li>"
         + "<li>pH &gt; −log(C) (higher than same-concentration strong acid)</li>"
         + "<li>[A⁻] = [H⁺] (from stoichiometry of 1:1 dissociation)</li>"
         + "</ul>"
         + "<b>Approximation (5% rule):</b> if degree of ionisation α &lt; 5%, then [H⁺] ≈ √(Ka·C)."
         + "<br><br><b>Example — 0.10 M acetic acid (pKa = 4.7):</b>"
         + "<br>Ka = 10⁻⁴·⁷ = 2.0×10⁻⁵"
         + "<br>[H⁺] = √(Ka·C) = √(2.0×10⁻⁵ × 0.10) = 1.41×10⁻³ M → pH = 2.85"
         + "<br><br>For the ICE table: if x/C &lt; 5%, the approximation is valid; otherwise solve the quadratic x² + Ka·x − Ka·C = 0."},

        {"lec10.buffer",
         "Lecture 10 — Acids, Bases & Solubility (Ch. 14+15)",
         "Buffers & Henderson-Hasselbalch",
         "<b>Buffer:</b> solution containing a weak acid (HA) and its conjugate base (A⁻) in significant amounts. Resists pH change when small amounts of acid or base are added."
         + "<br><br><b>Henderson-Hasselbalch equation:</b>"
         + "<br><br>&nbsp;&nbsp;&nbsp;&nbsp;pH = pKa + log([A⁻]/[HA])"
         + "<br><br><b>Key facts:</b>"
         + "<ul>"
         + "<li>When [A⁻] = [HA]: log(1) = 0 → pH = pKa</li>"
         + "<li>Effective buffer range: pH = pKa ± 1 (ratio 0.1 to 10)</li>"
         + "<li>Adding H⁺: A⁻ + H⁺ → HA (H⁺ consumed; pH drops only slightly)</li>"
         + "<li>Adding OH⁻: HA + OH⁻ → A⁻ + H₂O (OH⁻ consumed; pH rises only slightly)</li>"
         + "</ul>"
         + "<b>Example (Spring 2025 Q17):</b> NH₃/NH₄Cl buffer. Kb(NH₃) = 1.80×10⁻⁵; "
         + "pKa(NH₄⁺) = 14 − pKb = 14 − 4.74 = 9.26. After adding NaOH: NaOH reacts with NH₄⁺ → update moles of NH₃ and NH₄⁺, then apply Henderson-Hasselbalch."},
    };

    // ── Exam questions ────────────────────────────────────────────────────────
    // {noteKey, examLabel, question, answer}
    public static final String[][] EXAM_QUESTIONS = {

        {"lec1.isotopes",
         "Spring 2025",
         "Consider ⁷²Zn, ⁷⁵As, ⁷⁴Ge. These atoms have:",
         "The same number of neutrons (N=42 for all three). They are isotones."},

        {"lec2.dissolution",
         "Spring 2025",
         "How many moles of ions are formed by complete dissolution of 1 mol of aluminum sulfate (Al₂(SO₄)₃) in water?",
         "5 mol ions: Al₂(SO₄)₃ → 2 Al³⁺ + 3 SO₄²⁻ = 2 + 3 = 5."},

        {"lec2.grahams",
         "Spring 2025",
         "A mixture of 0.50 mol H₂(g) and 0.50 mol N₂(g) is placed in a 25 L container. The container is punctured. After some time, which statement is correct?",
         "The partial pressure of N₂ is larger than that of H₂. H₂ (M=2) effuses ~3.7× faster than N₂ (M=28), so more H₂ escapes."},

        {"lec4.IE",
         "August 2025 Reexam",
         "Which of the following atoms has the highest energy requirement for removal of an electron in the gas phase? (Se, O, K, Fe, Li)",
         "Oxygen (O) — highest first ionisation energy in this set due to small size and high Z_eff (period 2, group 16)."},

        {"lec4.radius",
         "Spring 2025",
         "Which of the following ions has the smallest ionic radius? Rb⁺, Sr²⁺, Br⁻, Se²⁻",
         "Sr²⁺ — all four ions are isoelectronic (36 electrons). Sr²⁺ has the most protons (38) → strongest pull → smallest radius."},

        {"lec4.config",
         "Spring 2025",
         "The atom with electron configuration 4s²4p⁵ in the outermost shell will be in:",
         "7th main group (halogens), 4th period — this is bromine (Br, Z=35)."},

        {"lec4.isoelectronic",
         "August 2025 Reexam",
         "Identify the ion that is NOT isoelectronic with neon.",
         "S²⁻ — it has 16+2=18 electrons (isoelectronic with Ar, not Ne)."},

        {"lec5.VSEPR",
         "Spring 2025",
         "What is the approximate expected H-N-H bond angle in N₂H₄ (hydrazine)?",
         "107° — each N has SN=4 (2 bonds to H, 1 to N, 1 lone pair) → trigonal pyramidal → angle compressed below 109.5°."},

        {"lec5.VSEPR",
         "Spring 2025",
         "Which of the following molecules does NOT possess a planar geometry? (SO₃, SiH₄, BH₃, SO₂, C₂H₄)",
         "SiH₄ — silicon has 4 bonds and 0 lone pairs → tetrahedral (3D), not planar."},

        {"lec5.formal_charge",
         "Spring 2025",
         "What is the formal charge on the nitrogen atom in nitrate (NO₃⁻) as determined from its Lewis structure?",
         "+1 — FC(N) = 5 − 0 − 8/2 = +1 (N has 0 lone pairs, 8 bonding electrons in 1 double + 2 single bonds)."},

        {"lec5.polarity",
         "Spring 2025",
         "Which statement is correct for CS₂ (carbon as central atom)?",
         "The molecule is linear and nonpolar — C has 2 C=S double bonds and no lone pairs (SN=2). Linear geometry with identical bonds → dipoles cancel."},

        {"lec5.polarity",
         "August 2025 Reexam",
         "Arrange in order of increasing covalent character: HI, NaBr, CsBr, Cl₂, LiF",
         "LiF < CsBr < NaBr < HI < Cl₂ (larger ΔEN = more ionic = less covalent; Cl₂ has ΔEN=0)."},

        {"lec5.lewis",
         "August 2025 Reexam",
         "Which illustration represents a valid Lewis structure for the oxalate anion (C₂O₄²⁻)?",
         "The correct structure has a C–C single bond, and each C forms one C=O double bond and one C–O⁻ single bond (formal charges minimised)."},

        {"lec5_6.hbond",
         "Spring 2025",
         "Which molecular property explains the relatively high boiling point for HF in comparison to HCl, HBr, and HI?",
         "Hydrogen bonding — F is the most electronegative element, giving very strong F–H···F hydrogen bonds despite HF having the lowest molar mass."},

        {"lec6.vapor_pressure",
         "Spring 2025",
         "Which of the following possesses the largest vapor pressure at room temperature? (Water, Glucose, NaCl, Ethanol)",
         "Ethanol — smallest molecule with only moderate hydrogen bonding → most volatile."},

        {"lec7.catalyst",
         "Spring 2025",
         "Which statement is correct for a catalyst's influence on a chemical reaction?",
         "It lowers the activation energy. A catalyst is NOT consumed and does NOT change the equilibrium constant K."},

        {"lec7.le_chatelier",
         "Spring 2025",
         "For which of the following reactions would an increase in pressure favor the formation of the products? "
         + "(a) H₂+I₂⇌2HI  (b) C(s)+O₂⇌CO₂  (c) 2SO₂+O₂⇌2SO₃  (d) CO+H₂O⇌CO₂+H₂  (e) NH₄Cl(s)⇌NH₃+HCl",
         "Reaction (c) — 3 mol gas on left → 2 mol gas on right. Higher pressure favours the side with fewer gas moles."},

        {"lec9.isomers",
         "Spring 2025",
         "How many alcohols have the molecular formula C₄H₁₀O?",
         "4: 1-butanol, 2-butanol, 2-methyl-1-propanol, 2-methyl-2-propanol."},

        {"lec9.organic",
         "August 2025 Reexam",
         "A molecule consists of an aromatic ring with (1) a branched alkyl chain and (2) an alkyl chain containing an amide bond attached. Which structure fits?",
         "The correct structure has a benzene ring, an isopropyl (branched) group, and a –CH₂–C(=O)–NH– chain (amide bond). Look for C=O adjacent to N–H."},

        {"lec10.weak_acid",
         "Spring 2025",
         "What is the pH in a 0.10 M aqueous solution of acetic acid (CH₃COOH, pKa = 4.7)?",
         "pH ≈ 2.9. Ka = 10⁻⁴·⁷ = 2.0×10⁻⁵; [H⁺] ≈ √(Ka·C) = √(2.0×10⁻³) = 1.41×10⁻³ M → pH = 2.85."},

        {"lec10.weak_acid",
         "Spring 2025",
         "Which of the following statements is correct for a 0.10 M solution of a weak acid HX?",
         "[HX] > [H⁺] — weak acid only partially ionises, so most HX remains undissociated."},

        {"lec10.buffer",
         "Spring 2025",
         "A buffer was prepared with 1.00 mol NH₃ (Kb=1.80×10⁻⁵) and 1.00 mol NH₄Cl in 1.00 L. 30.0 mL of 1.00 M NaOH was added to 500 mL of this buffer. What is the resulting pH?",
         "pH ≈ 9.31. pKa(NH₄⁺) = 9.26. NaOH converts 0.030 mol NH₄⁺ → NH₃. Apply Henderson-Hasselbalch with updated concentrations."},
    };

    // ── Calculator links ──────────────────────────────────────────────────────
    // noteKey → { {panelKey, tabIdx, buttonLabel}, ... }
    private static final Map<String, String[][]> CALC_LINKS = new HashMap<>();
    static {
        CALC_LINKS.put("lec1.isotopes",      new String[][]{{"stoich","8","Isotope / % Abundance"}});
        CALC_LINKS.put("lec2.dissolution",   new String[][]{{"stoich","12","Dissolution / Ion Count"}});
        CALC_LINKS.put("lec2.grahams",       new String[][]{{"kinetics","5","Graham's Law"}});
        CALC_LINKS.put("lec4.IE",            new String[][]{{"stoich","6","Element Lookup"}});
        CALC_LINKS.put("lec4.radius",        new String[][]{{"stoich","6","Element Lookup"}});
        CALC_LINKS.put("lec4.EN",            new String[][]{{"stoich","6","Element Lookup"},{"vsepr","0","VSEPR from Formula"}});
        CALC_LINKS.put("lec4.config",        new String[][]{{"stoich","9","Electron Configuration"}});
        CALC_LINKS.put("lec4.isoelectronic", new String[][]{{"stoich","6","Element Lookup"}});
        CALC_LINKS.put("lec5.VSEPR",         new String[][]{{"vsepr","0","VSEPR from Formula"},{"vsepr","2","VSEPR Reference Table"}});
        CALC_LINKS.put("lec5.lewis",         new String[][]{{"redox","6","Formal Charge"},{"vsepr","0","VSEPR from Formula"}});
        CALC_LINKS.put("lec5.formal_charge", new String[][]{{"redox","6","Formal Charge"}});
        CALC_LINKS.put("lec5.polarity",      new String[][]{{"vsepr","0","VSEPR from Formula"}});
        CALC_LINKS.put("lec5_6.hbond",       new String[][]{{"ref","-1","Reference Tables"}});
        CALC_LINKS.put("lec6.vapor_pressure",new String[][]{{"thermo","5","Clausius-Clapeyron"},{"stoich","7","Colligative Properties"}});
        CALC_LINKS.put("lec7.catalyst",      new String[][]{{"kinetics","0","Arrhenius Equation"},{"kinetics","1","Find Activation Energy"}});
        CALC_LINKS.put("lec7.le_chatelier",  new String[][]{{"equil","5","Q vs K Direction"}});
        CALC_LINKS.put("lec9.organic",       new String[][]{{"ref","-1","Reference Tables"}});
        CALC_LINKS.put("lec9.isomers",       new String[][]{{"ref","-1","Reference Tables"}});
        CALC_LINKS.put("lec10.weak_acid",    new String[][]{{"ph","2","pH — Weak Acid"},{"equil","0","ICE — Weak Acid"}});
        CALC_LINKS.put("lec10.buffer",       new String[][]{{"ph","4","Buffer pH"},{"ph","6","Buffer + Titrant"}});
    }

    // ── Lookup helpers ────────────────────────────────────────────────────────

    public static List<String[]> findNotes(String keyStr) {
        List<String[]> result = new ArrayList<>();
        if (keyStr == null || keyStr.isEmpty()) return result;
        for (String key : keyStr.split(",")) {
            key = key.trim();
            for (String[] note : NOTES) {
                if (note[0].equals(key)) { result.add(note); break; }
            }
        }
        return result;
    }

    public static List<String[]> findExamQuestions(String keyStr) {
        List<String[]> result = new ArrayList<>();
        if (keyStr == null || keyStr.isEmpty()) return result;
        Set<String> keys = new HashSet<>(Arrays.asList(keyStr.split(",")));
        for (String[] q : EXAM_QUESTIONS) {
            for (String k : keys) {
                if (q[0].equals(k.trim())) { result.add(q); break; }
            }
        }
        return result;
    }

    public static List<String[]> findCalcLinks(String keyStr) {
        List<String[]> seen = new ArrayList<>();
        Set<String> labels = new HashSet<>();
        if (keyStr == null || keyStr.isEmpty()) return seen;
        for (String key : keyStr.split(",")) {
            String[][] links = CALC_LINKS.get(key.trim());
            if (links == null) continue;
            for (String[] link : links) {
                if (labels.add(link[2])) seen.add(link);
            }
        }
        return seen;
    }
}
