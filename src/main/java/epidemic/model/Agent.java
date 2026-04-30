package epidemic.model;

import epidemic.strategies.movement.MovementStrategy;

public abstract class Agent {

    // --- Dane przestrzenne i fizyka ruchu ---
    private Point2D position;
    private Point2D currentTarget;
    private double baseSpeed;
    private double currentSpeed;
    private MovementStrategy movementStrategy;

    // --- Biologia i cykl życia ---
    private final SpeciesType speciesType;
    private int age;
    private boolean isDead;
    private double naturalMortalityRate;
    private int lastReproductionEpoch;
    private int remainingInfectionEpochs;

    // --- Stan epidemiczny ---
    private HealthStatus healthStatus;

    public Agent(Point2D position, int age, SpeciesType speciesType, double baseSpeed, MovementStrategy movementStrategy) {
        this.position = position;
        this.currentTarget = position;
        this.age = age;
        this.speciesType = speciesType;
        this.baseSpeed = baseSpeed;
        this.currentSpeed = baseSpeed;
        this.healthStatus = HealthStatus.HEALTHY;
        this.isDead = false;
        this.lastReproductionEpoch = 0;
        this.remainingInfectionEpochs = 0;
        this.movementStrategy = movementStrategy;
    }

    public void incrementAge() {
        this.age++;
    }

    public boolean canBeInfected() {
        return !isDead && healthStatus != HealthStatus.SICK;
    }

    public void decrementInfectionTimer() {
        if (this.remainingInfectionEpochs > 0) {
            this.remainingInfectionEpochs--;
        }
    }
    public void think(WorldContext context) {}

    // --- Gettery i Settery ---

    public Point2D getPosition() { return position; }
    public void setPosition(Point2D position) { this.position = position; }

    public Point2D getCurrentTarget() { return currentTarget; }
    public void setCurrentTarget(Point2D currentTarget) { this.currentTarget = currentTarget; }

    public SpeciesType getSpeciesType() { return speciesType; }

    public int getAge() { return age; }

    public HealthStatus getHealthStatus() { return healthStatus; }
    public void setHealthStatus(HealthStatus healthStatus) { this.healthStatus = healthStatus; }

    public boolean isDead() { return isDead; }
    public void setDead(boolean dead) { this.isDead = dead; }

    public double getBaseSpeed() { return baseSpeed; }

    public double getCurrentSpeed() { return currentSpeed; }
    public void setCurrentSpeed(double currentSpeed) { this.currentSpeed = currentSpeed; }

    public int getLastReproductionEpoch() { return lastReproductionEpoch; }
    public void setLastReproductionEpoch(int lastReproductionEpoch) { this.lastReproductionEpoch = lastReproductionEpoch; }

    public int getRemainingInfectionEpochs() {return remainingInfectionEpochs; }

    public void setRemainingInfectionEpochs(int remainingInfectionEpochs) {this.remainingInfectionEpochs = remainingInfectionEpochs; }
    public double getVulnerabilityMultiplier() {
        return 1.0;
    }

    public MovementStrategy getMovementStrategy() {return movementStrategy; }
    public void setMovementStrategy(MovementStrategy strategy) {this.movementStrategy = strategy; }
    public double getVirulence() {
        return getSpeciesType().getBaseVirulence();
    }
}