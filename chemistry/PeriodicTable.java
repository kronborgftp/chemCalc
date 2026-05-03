package chemistry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PeriodicTable {
    private static PeriodicTable instance;
    private final Map<String, Element> elements = new HashMap<>();

    private PeriodicTable() {
        // symbol, name, Z, atomicMass, electronegativity, period, group
        add("H",  "Hydrogen",      1,   1.008,  2.20, 1,  1);
        add("He", "Helium",        2,   4.003,  0.00, 1, 18);
        add("Li", "Lithium",       3,   6.941,  0.98, 2,  1);
        add("Be", "Beryllium",     4,   9.012,  1.57, 2,  2);
        add("B",  "Boron",         5,  10.811,  2.04, 2, 13);
        add("C",  "Carbon",        6,  12.011,  2.55, 2, 14);
        add("N",  "Nitrogen",      7,  14.007,  3.04, 2, 15);
        add("O",  "Oxygen",        8,  15.999,  3.44, 2, 16);
        add("F",  "Fluorine",      9,  18.998,  3.98, 2, 17);
        add("Ne", "Neon",         10,  20.180,  0.00, 2, 18);
        add("Na", "Sodium",       11,  22.990,  0.93, 3,  1);
        add("Mg", "Magnesium",    12,  24.305,  1.31, 3,  2);
        add("Al", "Aluminum",     13,  26.982,  1.61, 3, 13);
        add("Si", "Silicon",      14,  28.086,  1.90, 3, 14);
        add("P",  "Phosphorus",   15,  30.974,  2.19, 3, 15);
        add("S",  "Sulfur",       16,  32.065,  2.58, 3, 16);
        add("Cl", "Chlorine",     17,  35.453,  3.16, 3, 17);
        add("Ar", "Argon",        18,  39.948,  0.00, 3, 18);
        add("K",  "Potassium",    19,  39.098,  0.82, 4,  1);
        add("Ca", "Calcium",      20,  40.078,  1.00, 4,  2);
        add("Sc", "Scandium",     21,  44.956,  1.36, 4,  3);
        add("Ti", "Titanium",     22,  47.867,  1.54, 4,  4);
        add("V",  "Vanadium",     23,  50.942,  1.63, 4,  5);
        add("Cr", "Chromium",     24,  51.996,  1.66, 4,  6);
        add("Mn", "Manganese",    25,  54.938,  1.55, 4,  7);
        add("Fe", "Iron",         26,  55.845,  1.83, 4,  8);
        add("Co", "Cobalt",       27,  58.933,  1.88, 4,  9);
        add("Ni", "Nickel",       28,  58.693,  1.91, 4, 10);
        add("Cu", "Copper",       29,  63.546,  1.90, 4, 11);
        add("Zn", "Zinc",         30,  65.380,  1.65, 4, 12);
        add("Ga", "Gallium",      31,  69.723,  1.81, 4, 13);
        add("Ge", "Germanium",    32,  72.630,  2.01, 4, 14);
        add("As", "Arsenic",      33,  74.922,  2.18, 4, 15);
        add("Se", "Selenium",     34,  78.971,  2.55, 4, 16);
        add("Br", "Bromine",      35,  79.904,  2.96, 4, 17);
        add("Kr", "Krypton",      36,  83.798,  3.00, 4, 18);
        add("Rb", "Rubidium",     37,  85.468,  0.82, 5,  1);
        add("Sr", "Strontium",    38,  87.620,  0.95, 5,  2);
        add("Y",  "Yttrium",      39,  88.906,  1.22, 5,  3);
        add("Zr", "Zirconium",    40,  91.224,  1.33, 5,  4);
        add("Nb", "Niobium",      41,  92.906,  1.60, 5,  5);
        add("Mo", "Molybdenum",   42,  95.960,  2.16, 5,  6);
        add("Tc", "Technetium",   43,  98.000,  1.90, 5,  7);
        add("Ru", "Ruthenium",    44, 101.070,  2.20, 5,  8);
        add("Rh", "Rhodium",      45, 102.906,  2.28, 5,  9);
        add("Pd", "Palladium",    46, 106.420,  2.20, 5, 10);
        add("Ag", "Silver",       47, 107.868,  1.93, 5, 11);
        add("Cd", "Cadmium",      48, 112.411,  1.69, 5, 12);
        add("In", "Indium",       49, 114.818,  1.78, 5, 13);
        add("Sn", "Tin",          50, 118.710,  1.96, 5, 14);
        add("Sb", "Antimony",     51, 121.760,  2.05, 5, 15);
        add("Te", "Tellurium",    52, 127.600,  2.10, 5, 16);
        add("I",  "Iodine",       53, 126.904,  2.66, 5, 17);
        add("Xe", "Xenon",        54, 131.293,  2.60, 5, 18);
        add("Cs", "Cesium",       55, 132.905,  0.79, 6,  1);
        add("Ba", "Barium",       56, 137.327,  0.89, 6,  2);
        add("La", "Lanthanum",    57, 138.905,  1.10, 6,  0);
        add("Ce", "Cerium",       58, 140.116,  1.12, 6,  0);
        add("Pr", "Praseodymium", 59, 140.908,  1.13, 6,  0);
        add("Nd", "Neodymium",    60, 144.242,  1.14, 6,  0);
        add("Hf", "Hafnium",      72, 178.490,  1.30, 6,  4);
        add("Ta", "Tantalum",     73, 180.948,  1.50, 6,  5);
        add("W",  "Tungsten",     74, 183.840,  2.36, 6,  6);
        add("Re", "Rhenium",      75, 186.207,  1.90, 6,  7);
        add("Os", "Osmium",       76, 190.230,  2.20, 6,  8);
        add("Ir", "Iridium",      77, 192.217,  2.20, 6,  9);
        add("Pt", "Platinum",     78, 195.084,  2.28, 6, 10);
        add("Au", "Gold",         79, 196.967,  2.54, 6, 11);
        add("Hg", "Mercury",      80, 200.590,  2.00, 6, 12);
        add("Tl", "Thallium",     81, 204.383,  1.62, 6, 13);
        add("Pb", "Lead",         82, 207.200,  2.33, 6, 14);
        add("Bi", "Bismuth",      83, 208.980,  2.02, 6, 15);
        add("Po", "Polonium",     84, 209.000,  2.00, 6, 16);
        add("At", "Astatine",     85, 210.000,  2.20, 6, 17);
        add("Rn", "Radon",        86, 222.000,  0.00, 6, 18);
        // Special: electron (used in redox half-reactions)
        add("e",  "Electron",     -1,   0.000,  0.00, 0,  0);
    }

    private void add(String sym, String name, int z, double m, double en, int period, int group) {
        elements.put(sym, new Element(sym, name, z, m, en, period, group));
    }

    public static PeriodicTable getInstance() {
        if (instance == null) instance = new PeriodicTable();
        return instance;
    }

    public Element get(String symbol) { return elements.get(symbol); }
    public Collection<Element> getAll() { return elements.values(); }

    public double getMolarMass(String symbol) {
        Element e = elements.get(symbol);
        return e != null ? e.atomicMass : 0.0;
    }
}
