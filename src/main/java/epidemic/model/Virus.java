package epidemic.model;

public class Virus {
    private final double baseInfectionProbability;
    private final double infectionRadius;
    private final int defaultInfectionDuration;

    public Virus(double baseInfectionProbability, double infectionRadius, int defaultInfectionDuration) {
        this.baseInfectionProbability = baseInfectionProbability;
        this.infectionRadius = infectionRadius;
        this.defaultInfectionDuration = defaultInfectionDuration;
    }

    public double getBaseInfectionProbability() { return baseInfectionProbability; }
    public double getInfectionRadius() { return infectionRadius; }
    public int getDefaultInfectionDuration() { return defaultInfectionDuration; }
}