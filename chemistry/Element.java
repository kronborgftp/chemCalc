package chemistry;

public class Element {
    public final String symbol;
    public final String name;
    public final int atomicNumber;
    public final double atomicMass;
    public final double electronegativity;
    public final int period;
    public final int group;

    public Element(String symbol, String name, int atomicNumber, double atomicMass,
                   double electronegativity, int period, int group) {
        this.symbol = symbol;
        this.name = name;
        this.atomicNumber = atomicNumber;
        this.atomicMass = atomicMass;
        this.electronegativity = electronegativity;
        this.period = period;
        this.group = group;
    }

    @Override
    public String toString() {
        return String.format("%-3s %-18s Z=%-3d M=%-10.4f EN=%.2f  Period=%-2d Group=%d",
                symbol, name, atomicNumber, atomicMass, electronegativity, period, group);
    }
}
