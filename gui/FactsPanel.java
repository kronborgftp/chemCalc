package gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class FactsPanel extends JPanel {

    private static final Color BG      = new Color(245, 246, 250);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color HEADING = new Color(18, 32, 68);
    private static final Color ACCENT  = new Color(37, 99, 235);
    private static final Color DIVIDER = new Color(220, 222, 232);
    private static final Color ANS_BG  = new Color(236, 253, 245);
    private static final Color ANS_FG  = new Color(6, 95, 70);
    private static final Color NOTE_BG = new Color(248, 249, 255);
    private static final Color EXAM_BG = new Color(255, 252, 240);

    // {topic, title, answer, explanation, keywords, lectureKeys}
    public static final String[][] FACTS = {
        {"Periodic Trends","Ionization energy: general trend",
         "Increases left→right across a period; decreases top→bottom down a group.",
         "Across a period, Z_eff (effective nuclear charge) increases while shielding stays similar — electrons are held more tightly. Down a group, electrons are in higher shells, farther from the nucleus and more shielded.",
         "ionization energy|IE|first ionization|remove electron|ioniseringsenergi",
         "lec4.IE"},

        {"Periodic Trends","Ionization energy: which atom has the highest IE among O, Se, K, Fe, Li?",
         "Oxygen (O) — period 2, group 16.",
         "O is small and has high Z_eff. K and Li (group 1) have very low IE. Fe is a transition metal with moderate IE. Se is below O in the same group, so O wins.",
         "oxygen|highest ionization|Se O K Fe Li|remove electron|ionization energy|ioniseringsenergi",
         "lec4.IE"},

        {"Periodic Trends","Ionic radius: isoelectronic series (same # electrons, different nuclear charge)",
         "More protons → smaller ion. Among isoelectronic ions, the one with the most protons is smallest.",
         "Rb+(37p), Sr2+(38p), Br-(35p), Se2-(34p) all have 36 electrons (Kr config). Sr2+ has the most protons → smallest radius.",
         "ionic radius|isoelectronic|smallest|largest|rb sr br se|ionradius|mindste",
         "lec4.radius"},

        {"Periodic Trends","Atomic radius: general trend",
         "Decreases left→right across a period; increases top→bottom down a group.",
         "Increasing nuclear charge pulls electrons inward across a period. Adding new electron shells increases size down a group.",
         "atomic radius|size|atomradius|størrelsesorden",
         "lec4.radius"},

        {"Periodic Trends","Electronegativity: general trend",
         "Increases left→right; increases bottom→top. Fluorine is the most electronegative element.",
         "Electronegative atoms attract bonding electrons more strongly. F > O > N > Cl > Br > C > S > I > H.",
         "electronegativity|electronegative|pauling|EN|elektronegativitet",
         "lec4.EN"},

        {"Periodic Trends","Electron affinity: general trend",
         "Generally increases across a period (more negative). Halogens have the most negative EA.",
         "Adding an electron to a small, high-nuclear-charge atom releases a lot of energy. Noble gases have positive (unfavourable) EA.",
         "electron affinity|EA|elektronaffinitet",
         "lec4.IE"},

        {"Periodic Trends","Outermost shell electrons for phosphorus (P)",
         "5 valence electrons — P is in group 15.",
         "Phosphorus: [Ne] 3s2 3p3. The outermost (n=3) shell has 2+3 = 5 electrons.",
         "phosphorus|valence electrons|outermost shell|phosphor|yderste skal|group 15",
         "lec4.config"},

        {"Intermolecular Forces","What explains HF's unusually high boiling point vs HCl, HBr, HI?",
         "Hydrogen bonding — HF forms strong H-bonds due to F being very electronegative and small.",
         "Despite lower molar mass than HCl, HBr, HI, HF has the highest boiling point because F-H...F hydrogen bonds are exceptionally strong.",
         "HF|boiling point|hydrogen bonding|hydrogen bond|HCl HBr HI|kogepunkt|hydrogenbinding",
         "lec5_6.hbond"},

        {"Intermolecular Forces","Vapor pressure at room temperature: water, ethanol, glucose, sodium chloride",
         "Ethanol has the highest vapor pressure at room temperature.",
         "Vapor pressure is proportional to volatility. Ethanol (MW=46) has only moderate H-bonding and is small → most volatile. Water has stronger H-bonds → lower VP. Glucose and NaCl are essentially non-volatile.",
         "vapor pressure|dampopyk|damptryk|highest|ethanol|water|glucose|sodium chloride|volatility|stuetemperatur",
         "lec6.vapor_pressure"},

        {"Intermolecular Forces","Types of intermolecular forces (strongest to weakest)",
         "Ion-dipole > H-bonding > Dipole-dipole > London (van der Waals) dispersion forces.",
         "Ion-dipole involves full ionic charges. H-bonding is a special strong dipole-dipole (N, O, F bonded to H). London forces exist in all molecules; stronger in larger/heavier molecules.",
         "intermolecular forces|vdw|van der waals|london dispersion|hydrogen bonding|ion-dipole|vekselvirkninger",
         "lec5_6.hbond"},

        {"Kinetics & Catalysis","How does a catalyst affect a chemical reaction?",
         "It lowers the activation energy (Ea). It is NOT consumed and does NOT change the equilibrium constant K.",
         "A catalyst provides an alternative pathway with lower Ea. K, DeltaG, DeltaH, and DeltaS are all unchanged. Only the speed at which equilibrium is reached changes.",
         "catalyst|katalysator|activation energy|aktiveringsenergi|lowers|consumption|equilibrium|not consumed|sænker|forbrugt",
         "lec7.catalyst"},

        {"Equilibrium","Le Chatelier: increasing pressure favors which side?",
         "The side with fewer moles of gas.",
         "Increasing pressure compresses the system. Equilibrium shifts to reduce the number of gas moles. E.g. 2SO2(g)+O2(g) = 2SO3(g): 3 mol gas → 2 mol gas, so higher pressure favors SO3.",
         "le chatelier|pressure|tryk|stigning|fewer moles|gas|favor products|trykket|increase pressure|stigning i trykket",
         "lec7.le_chatelier"},

        {"Equilibrium","Le Chatelier: increasing temperature — which direction?",
         "The endothermic direction. For exothermic reactions, higher T shifts equilibrium toward reactants.",
         "Temperature adds energy. For exothermic (delta H < 0), adding heat pushes equilibrium left, K decreases. For endothermic (delta H > 0), pushes right, K increases.",
         "le chatelier|temperature|temperatur|heat|exothermic|endothermic|shift|varme",
         "lec7.le_chatelier"},

        {"Equilibrium","Which reaction: increased pressure favors the products?",
         "(c) 2SO2(g) + O2(g) = 2SO3(g) — 3 mol gas on left, 2 mol on right.",
         "Only reactions with fewer gas moles on the product side are favoured. (a) 2→2 mol, (b) 1→1 mol, (d) 2→2 mol, (e) 0→2 mol (more gas on right — disfavoured). Only (c) reduces from 3 to 2 mol.",
         "pressure|increase pressure|2SO2|O2|SO3|tryk|trykstigning|Le Chatelier|moles of gas|favorisere dannelsen",
         "lec7.le_chatelier"},

        {"Acids & Bases","Weak acid HX 0.10 M: which statement is correct?",
         "[HX] > [H+] — the acid only partially ionises, so most remains as HX.",
         "A weak acid only partially dissociates. If Ka << C, most HX remains undissociated, so [HX] at equilibrium >> [H+]. pH is NOT 1.",
         "weak acid|svag syre|partial dissociation|[HX]|[H+]|0.10 M|partial ionisation|HX",
         "lec10.weak_acid"},

        {"Acids & Bases","Henderson-Hasselbalch equation",
         "pH = pKa + log([A-]/[HA]) — for a weak acid/conjugate base buffer.",
         "When [A-]=[HA], log(1)=0 so pH = pKa. Buffer is most effective in the range pKa ± 1. Apply when both the weak acid and its conjugate base are present.",
         "henderson hasselbalch|buffer|pKa|pH|[A-]/[HA]|buffer equation|pufferopløsning",
         "lec10.buffer"},

        {"Bonding","Formal charge formula",
         "FC = V - L - B/2  (V = valence electrons, L = lone-pair e- on atom, B = bonding e- on atom)",
         "V is the group number for main-group elements. L is non-bonding electrons on that atom. B counts all electrons in bonds to that atom (single bond = 2, double = 4). Sum of all FC = overall charge.",
         "formal charge|FC|Lewis structure|V-L-B|formelle ladning|lewis-struktur",
         "lec5.formal_charge"},

        {"Bonding","Formal charge on N in nitrate NO3-",
         "+1 (nitrogen has formal charge +1 in the nitrate ion)",
         "N in NO3-: V=5, L=0, B=8 (1 double + 2 single bonds). FC = 5 - 0 - 8/2 = +1. Each singly-bonded O has FC=-1, doubly-bonded O has FC=0. Sum = +1-1-1+0 = -1 (correct).",
         "formal charge|NO3-|nitrate|nitrogen|+1|FC|N formal charge|nitrogenatom|nitrat",
         "lec5.formal_charge"},

        {"Bonding","Which molecules have planar geometry?",
         "SO3 (trigonal planar), BH3 (trigonal planar), SO2 (bent — planar), C2H4 (planar). SiH4 is tetrahedral — NOT planar.",
         "Planar = all atoms in one plane. Trigonal planar, linear, and bent are all planar. Tetrahedral (SN=4, LP=0) is 3D — not planar.",
         "planar geometry|plan geometri|SiH4|SO3|BH3|SO2|C2H4|tetrahedral|not planar|trigonal planar|ikke en plan",
         "lec5.VSEPR"},

        {"Bonding","CS2 molecular geometry and polarity",
         "Linear and nonpolar — C is the central atom with 2 double bonds to S, no lone pairs.",
         "Carbon has 4 VE, each S has 6. Total VE=16. Two C=S double bonds. Central C has 0 lone pairs → linear (SN=2). Symmetric linear → dipoles cancel → nonpolar.",
         "CS2|carbon disulfide|linear|nonpolar|upolært|polarity|central atom|kulstof|vinklet|lineært",
         "lec5.VSEPR,lec5.polarity"},

        {"Bonding","Bond character: covalent vs ionic (electronegativity difference)",
         "delta EN > 1.7 → mostly ionic; delta EN < 0.4 → mostly covalent; in between → polar covalent.",
         "Larger EN difference = more ionic = less covalent. LiF: largest delta EN → most ionic. Cl2: delta EN=0 → purely covalent. Order: LiF < CsBr < NaBr < HI < Cl2.",
         "covalent character|ionic character|electronegativity difference|LiF|CsBr|NaBr|HI|Cl2|bond type|kovalent|bindingskarakter",
         "lec5.polarity,lec4.EN"},

        {"Bonding","Lewis structure for oxalate C2O4 2-",
         "C-C single bond; each C has one C=O (double) and one C-O- (single). Formal charges minimised.",
         "Total VE = 2x4 + 4x6 + 2 = 34. Each C bonds to 2 O + 1 C. Best structure: C-C single bond, each C has one C=O and one C-O-. This gives FC=0 on each C.",
         "oxalate|C2O4|Lewis structure|oxalatanionen|lewis-struktur",
         "lec5.lewis"},

        {"Organic Chemistry","How many alcohols have molecular formula C4H10O?",
         "4 alcohols: 1-butanol, 2-butanol, 2-methyl-1-propanol, 2-methyl-2-propanol.",
         "Vary the C skeleton and OH position. n-butyl: OH at C1 or C2. Isobutyl: OH at C1 or C2 (branching C). Total = 4.",
         "alcohols|C4H10O|isomers|how many|butanol|1-butanol|2-butanol|methyl|isobutyl|alkoholer|molekylformlen",
         "lec9.isomers"},

        {"Organic Chemistry","Common functional groups: amide, ester, ether, amine, carboxylic acid",
         "Amide: -C(=O)-NH-. Ester: -C(=O)-O-. Ether: -O-. Amine: -NH2. Carboxylic acid: -COOH.",
         "Look at what is bonded to C=O: NH next to C=O → amide. O-C next to C=O → ester. OH next to C=O → carboxylic acid. Just C=O in chain interior → ketone. At chain end → aldehyde.",
         "functional group|amide|ester|ether|amine|aldehyde|ketone|carboxylic acid|organic|amidbinding|funktionelle grupper",
         "lec9.organic"},

        {"Organic Chemistry","How to identify a branched alkyl chain",
         "A branched chain has a carbon bonded to 3 or more other carbons (not in a straight line).",
         "In a skeletal structure, a branch point (vertex) has more than 2 lines meeting it. Isobutyl, sec-butyl, tert-butyl, and isopropyl are all branched.",
         "branched|forgrenet|alkyl chain|alkylkæde|branch|isopropyl|isobutyl",
         "lec9.isomers"},

        {"Atomic Structure","Isotopes vs isobars vs isotones",
         "Isotopes: same Z. Isobars: same A (mass number). Isotones: same N (neutrons).",
         "72Zn (Z=30, N=42), 75As (Z=33, N=42), 74Ge (Z=32, N=42) all share N=42 → they are isotones.",
         "isotope|isobar|isotone|protons|neutrons|mass number|same neutrons|Zn As Ge|nukleontal|72Zn|75As|74Ge",
         "lec1.isotopes"},

        {"Atomic Structure","Isoelectronic species — which is NOT isoelectronic with neon (10 e-)?",
         "S2- is NOT isoelectronic with Ne — it has 18 electrons (like Ar).",
         "Neon: 10 electrons. Na+(11-1=10), F-(9+1=10), O2-(8+2=10), Al3+(13-3=10) all have 10 e-. S(16)+2=18 — same as Ar, NOT Ne.",
         "isoelectronic|isselektronisk|same electrons|electron count|Na+|F-|O2-|Al3+|S2-|neon|ikke isoelektronisk",
         "lec4.isoelectronic"},

        {"Atomic Structure","Electron configuration 4s2 4p5 — which group and period?",
         "7th main group (halogens), 4th period — this is bromine (Br).",
         "Highest n=4 → Period 4. 2 s-electrons + 5 p-electrons = 7 valence electrons → Group 17. Element = Br (Z=35).",
         "electron configuration|4s2 4p5|group|period|7th main group|halogen|bromine|Br|elektronkonfiguration|outermost shell",
         "lec4.config"},

        {"Stoichiometry","How many moles of ions from 1 mol Al2(SO4)3?",
         "5 mol ions: 2 Al3+ + 3 SO42-.",
         "Al2(SO4)3 → 2 Al3+ + 3 SO42-. Subscripts give the ion counts: 2+3=5 ions per formula unit.",
         "ions|dissolution|Al2(SO4)3|aluminum sulfate|aluminiumsulfat|moles of ions|5 mol|how many moles|complete dissolution|opløsning|dannes|komplet",
         "lec2.dissolution"},

        {"Stoichiometry","Ion counts from common ionic salts (complete dissolution)",
         "NaCl → 2. MgCl2 → 3. CaCl2 → 3. AlCl3 → 4. Al2(SO4)3 → 5. Na2SO4 → 3.",
         "Total ions = sum of the subscripts in the ionic formula. Each subscript tells how many of that ion are produced per formula unit.",
         "ions|dissolution|NaCl|MgCl2|AlCl3|CaCl2|how many ions|complete dissolution|ionic compound|ioner",
         "lec2.dissolution"},

        {"Gases","Punctured container with H2 and N2: what happens to partial pressures?",
         "H2 effuses faster (lighter), so after time the partial pressure of N2 is larger than that of H2.",
         "Graham's law: rate proportional to 1/sqrt(M). H2 (M=2) effuses sqrt(28/2) ≈ 3.7x faster than N2 (M=28). More H2 escapes → remaining gas richer in N2.",
         "effusion|diffusion|graham|molar mass|lighter gas|H2 N2|partial pressure|container punctured|nål|needle|punkteres|partialtryk",
         "lec2.grahams"},

        {"Stoichiometry","Mass percentage of an element in a compound",
         "mass% = (n x M_element / M_compound) x 100",
         "Example: mass% of Li in LiFePO4 (M ≈ 157.76 g/mol): (1 x 6.941 / 157.76) x 100 = 4.4%.",
         "mass percentage|mass percent|masseprocent|lithium|LiFePO4|percent composition|masseprocenten",
         ""},
    };

    // ── Instance state ────────────────────────────────────────────────────────
    private final BiConsumer<String, Integer> nav;
    private JPanel     resultsPanel;
    private JTextField searchField;
    private JPanel     filterPanel;
    private String     activeFilter = "All";

    // CardLayout switching between list and detail views
    private final CardLayout centerCards = new CardLayout();
    private final JPanel     centerPanel = new JPanel(centerCards);

    private final CardLayout controlCards = new CardLayout();
    private final JPanel     controlPanel = new JPanel(controlCards);

    public FactsPanel(BiConsumer<String, Integer> nav) {
        this.nav = nav;
        setLayout(new BorderLayout());
        setBackground(BG);

        // ── Hero ──────────────────────────────────────────────────────────────
        JPanel hero = new JPanel();
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));
        hero.setBackground(HEADING);
        hero.setBorder(BorderFactory.createEmptyBorder(18, 32, 16, 32));

        JLabel heroTitle = new JLabel("Chemistry Facts");
        heroTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        heroTitle.setForeground(Color.WHITE);
        heroTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heroSub = new JLabel("Searchable answers for conceptual exam questions. Click any card to read the full lecture notes.");
        heroSub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        heroSub.setForeground(new Color(160, 180, 230));
        heroSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        hero.add(heroTitle);
        hero.add(Box.createVerticalStrut(5));
        hero.add(heroSub);

        // ── Control bar (switches between search+filter and back button) ───────
        controlPanel.setBackground(BG);

        // "list" controls
        JPanel listControls = new JPanel();
        listControls.setLayout(new BoxLayout(listControls, BoxLayout.Y_AXIS));
        listControls.setBackground(BG);

        JPanel searchRow = new JPanel(new BorderLayout(8, 0));
        searchRow.setBackground(BG);
        searchRow.setBorder(BorderFactory.createEmptyBorder(12, 24, 6, 24));
        searchRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        searchField = new JTextField();
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 205, 220), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        clearBtn.setBackground(new Color(240, 241, 245));
        clearBtn.setForeground(new Color(80, 85, 100));
        clearBtn.setBorderPainted(false);
        clearBtn.setFocusPainted(false);
        clearBtn.addActionListener(e -> { searchField.setText(""); activeFilter = "All"; recolorFilters("All"); refresh(""); });

        searchRow.add(new JLabel("Search: "), BorderLayout.WEST);
        searchRow.add(searchField, BorderLayout.CENTER);
        searchRow.add(clearBtn, BorderLayout.EAST);
        listControls.add(searchRow);

        filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterPanel.setBackground(BG);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 8, 16));

        List<String> topics = new ArrayList<>();
        topics.add("All");
        for (String[] f : FACTS) if (!topics.contains(f[0])) topics.add(f[0]);
        for (String topic : topics) {
            JButton btn = filterBtn(topic, topic.equals("All"));
            btn.addActionListener(e -> { activeFilter = topic; recolorFilters(topic); refresh(searchField.getText()); });
            filterPanel.add(btn);
        }
        listControls.add(filterPanel);

        // "detail" controls — just a back button
        JPanel detailNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        detailNav.setBackground(BG);
        detailNav.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));

        JButton backBtn = new JButton("← Back to Facts");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        backBtn.setBackground(new Color(235, 237, 245));
        backBtn.setForeground(HEADING);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(e -> showList());
        detailNav.add(backBtn);

        controlPanel.add(listControls, "list");
        controlPanel.add(detailNav,    "detail");
        controlCards.show(controlPanel, "list");

        // ── Top bar = hero + controls ─────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.add(hero,         BorderLayout.NORTH);
        topBar.add(controlPanel, BorderLayout.CENTER);

        add(topBar, BorderLayout.NORTH);

        // ── Center: CardLayout between list and detail ─────────────────────────
        centerPanel.setBackground(BG);

        resultsPanel = new JPanel(new GridBagLayout());
        resultsPanel.setBackground(BG);

        JScrollPane listScroll = new JScrollPane(resultsPanel,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        listScroll.setBorder(null);
        listScroll.getViewport().setBackground(BG);
        listScroll.getVerticalScrollBar().setUnitIncrement(20);

        centerPanel.add(listScroll,           "list");
        centerPanel.add(new JPanel(),         "detail"); // placeholder, rebuilt on each click
        centerCards.show(centerPanel, "list");

        add(centerPanel, BorderLayout.CENTER);

        // Wire search
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { refresh(searchField.getText()); }
            public void removeUpdate(DocumentEvent e)  { refresh(searchField.getText()); }
            public void changedUpdate(DocumentEvent e) {}
        });

        refresh("");
    }

    // ── List view ─────────────────────────────────────────────────────────────

    private void showList() {
        controlCards.show(controlPanel, "list");
        centerCards.show(centerPanel,  "list");
    }

    private void showDetail(String[] fact) {
        JPanel detail = buildDetailPanel(fact);
        centerPanel.remove(centerPanel.getComponent(1));
        centerPanel.add(detail, "detail", 1);
        controlCards.show(controlPanel, "detail");
        centerCards.show(centerPanel,   "detail");
    }

    private void recolorFilters(String active) {
        for (Component c : filterPanel.getComponents()) {
            if (c instanceof JButton btn) {
                boolean sel = btn.getText().equals(active);
                btn.setBackground(sel ? ACCENT : new Color(235, 237, 245));
                btn.setForeground(sel ? Color.WHITE : HEADING);
            }
        }
    }

    private JButton filterBtn(String label, boolean active) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setBackground(active ? ACCENT : new Color(235, 237, 245));
        btn.setForeground(active ? Color.WHITE : HEADING);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(4, 12, 4, 12));
        return btn;
    }

    private void refresh(String query) {
        resultsPanel.removeAll();
        String q = query.toLowerCase().trim();

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1.0; g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST;
        g.insets = new Insets(0, 20, 12, 20);

        int row = 0, shown = 0;
        for (String[] fact : FACTS) {
            if (!activeFilter.equals("All") && !fact[0].equals(activeFilter)) continue;
            if (!q.isEmpty()) {
                String hay = (fact[0]+" "+fact[1]+" "+fact[2]+" "+fact[3]+" "+fact[4]).toLowerCase();
                boolean match = false;
                for (String w : q.split("\\s+")) if (hay.contains(w)) { match = true; break; }
                if (!match) continue;
            }
            g.gridy = row++;
            resultsPanel.add(factCard(fact), g);
            shown++;
        }

        if (shown == 0) {
            g.gridy = 0;
            JLabel none = new JLabel("No facts match. Try different keywords.");
            none.setFont(new Font("SansSerif", Font.ITALIC, 13));
            none.setForeground(new Color(130, 135, 150));
            none.setBorder(BorderFactory.createEmptyBorder(20, 4, 10, 4));
            resultsPanel.add(none, g);
            row = 1;
        }

        // Vertical filler
        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0; filler.gridy = row; filler.weightx = 1; filler.weighty = 1;
        filler.fill = GridBagConstraints.BOTH;
        JPanel pad = new JPanel(); pad.setOpaque(false);
        resultsPanel.add(pad, filler);

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    private JPanel factCard(String[] fact) {
        boolean hasDetail = fact.length > 5 && !fact[5].isEmpty();

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(hasDetail ? new Color(196, 210, 250) : DIVIDER, 1, true),
            BorderFactory.createEmptyBorder(14, 18, 10, 18)));
        card.setCursor(hasDetail ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST;

        // Topic chip
        g.gridy = 0; g.insets = new Insets(0, 0, 8, 0);
        JPanel chipRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        chipRow.setBackground(CARD_BG);
        JLabel chip = new JLabel("  " + fact[0] + "  ");
        chip.setFont(new Font("SansSerif", Font.BOLD, 10));
        chip.setForeground(ACCENT);
        chip.setBackground(new Color(219, 234, 254));
        chip.setOpaque(true);
        chip.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        chipRow.add(chip);
        card.add(chipRow, g);

        // Title
        g.gridy = 1; g.insets = new Insets(0, 0, 8, 0);
        JLabel titleLbl = new JLabel("<html>" + esc(fact[1]) + "</html>");
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLbl.setForeground(HEADING);
        card.add(titleLbl, g);

        // Answer box
        g.gridy = 2; g.insets = new Insets(0, 0, 8, 0);
        JPanel ansBox = new JPanel(new GridBagLayout());
        ansBox.setBackground(ANS_BG);
        ansBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(167, 243, 208), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        GridBagConstraints ag = new GridBagConstraints();
        ag.gridx = 0; ag.weightx = 1; ag.fill = GridBagConstraints.HORIZONTAL;
        JLabel ansLbl = new JLabel("<html><b>Answer:</b> " + esc(fact[2]) + "</html>");
        ansLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        ansLbl.setForeground(ANS_FG);
        ansBox.add(ansLbl, ag);
        card.add(ansBox, g);

        // Explanation
        g.gridy = 3; g.insets = new Insets(0, 0, hasDetail ? 8 : 0, 0);
        JLabel explLbl = new JLabel("<html><font color='#4b5563'>" + esc(fact[3]) + "</font></html>");
        explLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        card.add(explLbl, g);

        // "Read more" hint if lecture notes exist
        if (hasDetail) {
            g.gridy = 4; g.insets = new Insets(0, 0, 0, 0);
            JLabel hint = new JLabel("<html><font color='#2563eb'>📖 Click to read lecture notes &amp; past exam questions →</font></html>");
            hint.setFont(new Font("SansSerif", Font.PLAIN, 11));
            card.add(hint, g);
        }

        if (hasDetail) {
            final String[] f = fact;
            card.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) { showDetail(f); }
                public void mouseEntered(java.awt.event.MouseEvent e) { card.setBackground(new Color(240, 245, 255)); ansBox.setBackground(ANS_BG); chipRow.setBackground(new Color(240,245,255)); }
                public void mouseExited(java.awt.event.MouseEvent e)  { card.setBackground(CARD_BG); chipRow.setBackground(CARD_BG); }
            });
        }

        return card;
    }

    // ── Detail view ───────────────────────────────────────────────────────────

    private JPanel buildDetailPanel(String[] fact) {
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(BG);
        content.setBorder(BorderFactory.createEmptyBorder(20, 28, 28, 28));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST;

        int row = 0;

        // Full fact card (no click)
        g.gridy = row++; g.insets = new Insets(0, 0, 20, 0);
        content.add(staticFactCard(fact), g);

        String keys = fact.length > 5 ? fact[5] : "";
        List<String[]> notes    = LectureData.findNotes(keys);
        List<String[]> examQs   = LectureData.findExamQuestions(keys);
        List<String[]> calcLinks= LectureData.findCalcLinks(keys);

        // Lecture notes section
        if (!notes.isEmpty()) {
            g.gridy = row++; g.insets = new Insets(0, 0, 10, 0);
            content.add(sectionHeader("Lecture Notes", new Color(239, 246, 255), ACCENT), g);

            for (String[] note : notes) {
                g.gridy = row++; g.insets = new Insets(0, 0, 12, 0);
                content.add(noteCard(note), g);
            }
        }

        // Past exam questions section
        if (!examQs.isEmpty()) {
            g.gridy = row++; g.insets = new Insets(8, 0, 10, 0);
            content.add(sectionHeader("Past Exam Questions", new Color(255, 252, 235), new Color(146, 64, 14)), g);

            for (String[] q : examQs) {
                g.gridy = row++; g.insets = new Insets(0, 0, 10, 0);
                content.add(examCard(q), g);
            }
        }

        // Related calculators section
        if (!calcLinks.isEmpty()) {
            g.gridy = row++; g.insets = new Insets(8, 0, 10, 0);
            content.add(sectionHeader("Related Calculators", new Color(240, 253, 244), ANS_FG), g);

            JPanel calcRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
            calcRow.setBackground(BG);
            for (String[] link : calcLinks) {
                JButton btn = new JButton(link[2] + " →");
                btn.setFont(new Font("SansSerif", Font.BOLD, 12));
                btn.setBackground(new Color(219, 234, 254));
                btn.setForeground(ACCENT);
                btn.setBorderPainted(false);
                btn.setFocusPainted(false);
                btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btn.addActionListener(e -> nav.accept(link[0], Integer.parseInt(link[1])));
                calcRow.add(btn);
            }
            g.gridy = row++; g.insets = new Insets(0, 0, 0, 0);
            content.add(calcRow, g);
        }

        // Bottom spacer
        GridBagConstraints filler = new GridBagConstraints();
        filler.gridx = 0; filler.gridy = row; filler.weightx = 1; filler.weighty = 1;
        filler.fill = GridBagConstraints.BOTH;
        JPanel pad = new JPanel(); pad.setOpaque(false);
        content.add(pad, filler);

        JScrollPane scroll = new JScrollPane(content,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.getVerticalScrollBar().setValue(0);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel staticFactCard(String[] fact) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(196, 210, 250), 2, true),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST;

        g.gridy = 0; g.insets = new Insets(0, 0, 8, 0);
        JLabel topicLbl = new JLabel("  " + fact[0] + "  ");
        topicLbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        topicLbl.setForeground(ACCENT);
        topicLbl.setBackground(new Color(219, 234, 254));
        topicLbl.setOpaque(true);
        topicLbl.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
        JPanel cr = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        cr.setBackground(CARD_BG); cr.add(topicLbl);
        card.add(cr, g);

        g.gridy = 1; g.insets = new Insets(0, 0, 10, 0);
        JLabel titleLbl = new JLabel("<html>" + esc(fact[1]) + "</html>");
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        titleLbl.setForeground(HEADING);
        card.add(titleLbl, g);

        g.gridy = 2; g.insets = new Insets(0, 0, 10, 0);
        JPanel ansBox = new JPanel(new GridBagLayout());
        ansBox.setBackground(ANS_BG);
        ansBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(167, 243, 208), 1, true),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        GridBagConstraints ag = new GridBagConstraints();
        ag.gridx = 0; ag.weightx = 1; ag.fill = GridBagConstraints.HORIZONTAL;
        JLabel ansLbl = new JLabel("<html><b>Answer:</b> " + esc(fact[2]) + "</html>");
        ansLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        ansLbl.setForeground(ANS_FG);
        ansBox.add(ansLbl, ag);
        card.add(ansBox, g);

        g.gridy = 3; g.insets = new Insets(0, 0, 0, 0);
        JLabel explLbl = new JLabel("<html><font color='#4b5563'>" + esc(fact[3]) + "</font></html>");
        explLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        card.add(explLbl, g);

        return card;
    }

    private JPanel noteCard(String[] note) {
        // note = {key, lectureRef, sectionTitle, htmlContent}
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(NOTE_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(196, 200, 240), 1, true),
            BorderFactory.createEmptyBorder(14, 18, 14, 18)));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST;

        // Lecture source label
        g.gridy = 0; g.insets = new Insets(0, 0, 4, 0);
        JLabel srcLbl = new JLabel(note[1]);
        srcLbl.setFont(new Font("SansSerif", Font.ITALIC, 11));
        srcLbl.setForeground(new Color(100, 110, 160));
        card.add(srcLbl, g);

        // Section title
        g.gridy = 1; g.insets = new Insets(0, 0, 10, 0);
        JLabel secLbl = new JLabel(note[2]);
        secLbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        secLbl.setForeground(HEADING);
        card.add(secLbl, g);

        // HTML content via JEditorPane (preserves bold, bullets, line breaks)
        g.gridy = 2; g.insets = new Insets(0, 0, 0, 0);
        JEditorPane editor = new JEditorPane("text/html",
            "<html><body style='font-family:SansSerif;font-size:12pt;color:#1e293b;'>"
            + note[3] + "</body></html>");
        editor.setEditable(false);
        editor.setBackground(NOTE_BG);
        editor.setOpaque(true);
        editor.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        editor.setFont(new Font("SansSerif", Font.PLAIN, 12));
        card.add(editor, g);

        return card;
    }

    private JPanel examCard(String[] q) {
        // q = {noteKey, examLabel, question, answer}
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(EXAM_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(253, 230, 138), 1, true),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST;

        g.gridy = 0; g.insets = new Insets(0, 0, 6, 0);
        JLabel examLbl = new JLabel(q[1]);
        examLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        examLbl.setForeground(new Color(146, 64, 14));
        card.add(examLbl, g);

        g.gridy = 1; g.insets = new Insets(0, 0, 8, 0);
        JLabel qLbl = new JLabel("<html><i>" + esc(q[2]) + "</i></html>");
        qLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        qLbl.setForeground(HEADING);
        card.add(qLbl, g);

        g.gridy = 2; g.insets = new Insets(0, 0, 0, 0);
        JLabel aLbl = new JLabel("<html><b>Answer:</b> " + esc(q[3]) + "</html>");
        aLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        aLbl.setForeground(new Color(6, 95, 70));
        card.add(aLbl, g);

        return card;
    }

    private JPanel sectionHeader(String title, Color bg, Color fg) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(bg);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 210, 230), 1, true),
            BorderFactory.createEmptyBorder(8, 14, 8, 14)));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(fg);
        p.add(lbl);
        return p;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static String esc(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public static List<int[]> scoreFacts(String questionText) {
        String q = questionText.toLowerCase();
        List<int[]> scores = new ArrayList<>();
        for (int i = 0; i < FACTS.length; i++) {
            String[] f = FACTS[i];
            int score = 0;
            for (String kw : f[4].split("\\|")) {
                kw = kw.toLowerCase().trim();
                if (!kw.isEmpty() && q.contains(kw)) score += kw.contains(" ") ? 3 : 1;
            }
            String hay = (f[0]+" "+f[1]+" "+f[2]).toLowerCase();
            for (String word : hay.split("[^a-zA-Z0-9åæøÅÆØ]+")) {
                if (word.length() > 4 && q.contains(word)) score += 1;
            }
            if (score > 0) scores.add(new int[]{i, score});
        }
        scores.sort((a, b) -> b[1] - a[1]);
        return scores;
    }
}
