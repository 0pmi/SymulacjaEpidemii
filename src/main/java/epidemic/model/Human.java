package epidemic.model;

import epidemic.strategies.movement.MovementStrategy;

public class Human extends Agent {

    // --- Unikalne cechy biologiczne człowieka ---
    private double resistance;

    // --- Cechy behawioralne i medyczne ---
    private Personality personality;
    private boolean isVaccinated;
    private boolean isWearingMask;

    private boolean wantsHospital;

    private boolean isInHospital;

    public Human(Point2D position, int age, double baseSpeed,
                 double resistance, Personality personality,
                 MovementStrategy movementStrategy) {

        super(position, age, SpeciesType.HUMAN, baseSpeed, movementStrategy);

        this.resistance = resistance;
        this.personality = personality;

        this.isVaccinated = false;
        this.isWearingMask = false;
        this.wantsHospital = false;
        this.isInHospital = false;
    }

    // --- Gettery i Settery ---

    public double getResistance() { return resistance; }
    public void setResistance(double resistance) { this.resistance = resistance; }

    public Personality getPersonality() { return personality; }

    public boolean isVaccinated() { return isVaccinated; }
    public void setVaccinated(boolean vaccinated) { this.isVaccinated = vaccinated; }

    public boolean isWearingMask() { return isWearingMask; }
    public void setWearingMask(boolean wearingMask) { this.isWearingMask = wearingMask; }

    public boolean wantsHospital() { return wantsHospital; }
    public void setWantsHospital(boolean wantsHospital) { this.wantsHospital = wantsHospital; }

    public boolean isInHospital() { return isInHospital; }
    public void setIsInHospital(boolean isInHospital) { this.isInHospital = isInHospital; }

    @Override
    public double getVulnerabilityMultiplier() {
        double multiplier = 1.0;

        if (isWearingMask()) multiplier *= 0.3;
        if (isVaccinated()) multiplier *= 0.1;
        multiplier *= (1.0 - getResistance());

        return multiplier;
    }
}