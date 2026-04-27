package epidemic.model;

public class Virus {
    private double virulence;
    private int avgRecoveryTime;
    private double baseMortalityRate;

    public Virus(double virulence, int avgRecoveryTime, double baseMortalityRate) {
        this.virulence = virulence;
        this.avgRecoveryTime = avgRecoveryTime;
        this.baseMortalityRate = baseMortalityRate;
    }

    public double calculateInfectionProb(double resistance) {
        return 0.0; // TODO
    }
}