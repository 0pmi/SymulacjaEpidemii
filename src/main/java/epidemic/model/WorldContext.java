package epidemic.model;

/**
 * Kapsułkuje globalny stan symulacji w danej epoce.
 * Obiekt ten jest przekazywany do agentów w fazie podejmowania decyzji (metoda think),
 * zapobiegając ich bezpośredniemu sprzężeniu z silnikiem symulacji.
 */
public class WorldContext {
    private final double infectionPercentage;
    private final boolean vaccineAvailable;
    private final int currentEpoch;
    private final double mortalityRatio;

    public WorldContext(double infectionPercentage, boolean vaccineAvailable, int currentEpoch, double mortalityRatio) {
        this.infectionPercentage = infectionPercentage;
        this.vaccineAvailable = vaccineAvailable;
        this.currentEpoch = currentEpoch;
        this.mortalityRatio = mortalityRatio;
    }

    public double getInfectionPercentage() { return infectionPercentage; }
    public boolean isVaccineAvailable() { return vaccineAvailable; }
    public int getCurrentEpoch() { return currentEpoch; }
    public double getMortalityRatio() { return mortalityRatio; }
}