package epidemic.model;

import epidemic.strategies.movement.MovementStrategy;

public class Human extends Agent implements HospitalUser {

    // --- Unikalne cechy biologiczne człowieka ---
    private double resistance;

    // --- Cechy behawioralne i medyczne ---
    private final Personality personality;
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
    
    @Override
    public void think(WorldContext context) {
        personality.updateMentalState(this, context);
    }

    // --- Gettery i Settery ---

    public double getResistance() { return resistance; }
    public void setResistance(double resistance) { this.resistance = resistance; }

    public Personality getPersonality() { return personality; }

    public boolean isVaccinated() { return isVaccinated; }
    public void setVaccinated(boolean vaccinated) { this.isVaccinated = vaccinated; }

    public boolean isWearingMask() { return isWearingMask; }
    public void setWearingMask(boolean wearingMask) { this.isWearingMask = wearingMask; }

    public void setWantsHospital(boolean wantsHospital) { this.wantsHospital = wantsHospital; }

    @Override
    public boolean isWantsHospital() {
        return this.wantsHospital;
    }

    @Override
    public boolean isInHospital() {
        return this.isInHospital;
    }

    @Override
    public void setIsInHospital(boolean status) {
        this.isInHospital = status;
    }

    @Override
    public double getVulnerabilityMultiplier() {
        double multiplier = 1.0;

        if (isWearingMask()) multiplier *= 0.3;
        if (isVaccinated()) multiplier *= 0.1;
        multiplier *= (1.0 - getResistance());

        return multiplier;
    }
}