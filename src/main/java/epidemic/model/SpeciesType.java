package epidemic.model;

public enum SpeciesType {
    HUMAN(1.0, 18), // (virulence, maturityAge)
    BAT(2.5, 2),
    DOG(1.3, 7),
    RAT(1.8, 1);

    private final double baseVirulence;
    private final int maturityAge;

    SpeciesType(double baseVirulence, int maturityAge) {
        this.baseVirulence = baseVirulence;
        this.maturityAge = maturityAge;
    }

    public double getBaseVirulence() { return baseVirulence; }
    public int getMaturityAge() { return maturityAge; }
}
