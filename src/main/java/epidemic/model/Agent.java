package epidemic.model;

public abstract class Agent {

    // --- Dane przestrzenne i fizyka ruchu ---
    private Point2D position;
    private Point2D currentTarget;
    private double baseSpeed;
    private double currentSpeed;

    // --- Biologia i cykl życia ---
    private final SpeciesType speciesType;
    private int age;
    private boolean isDead;
    private double naturalMortalityRate;
    private int lastReproductionEpoch;

    // --- Stan epidemiczny ---
    private HealthStatus healthStatus;

    public Agent(Point2D position, int age, SpeciesType speciesType, double baseSpeed, double naturalMortalityRate) {
        this.position = position;
        this.currentTarget = position;
        this.age = age;
        this.speciesType = speciesType;
        this.baseSpeed = baseSpeed;
        this.currentSpeed = baseSpeed;
        this.naturalMortalityRate = naturalMortalityRate;
        this.healthStatus = HealthStatus.HEALTHY;
        this.isDead = false;
        this.lastReproductionEpoch = 0;
    }

    public void incrementAge() {
        this.age++;
    }

    public boolean canBeInfected() {
        return !isDead && healthStatus != HealthStatus.SICK;
    }

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

    public double getNaturalMortalityRate() { return naturalMortalityRate; }
    public void setNaturalMortalityRate(double naturalMortalityRate) { this.naturalMortalityRate = naturalMortalityRate; }

    public double getBaseSpeed() { return baseSpeed; }

    public double getCurrentSpeed() { return currentSpeed; }
    public void setCurrentSpeed(double currentSpeed) { this.currentSpeed = currentSpeed; }

    public int getLastReproductionEpoch() { return lastReproductionEpoch; }
    public void setLastReproductionEpoch(int lastReproductionEpoch) { this.lastReproductionEpoch = lastReproductionEpoch; }
}