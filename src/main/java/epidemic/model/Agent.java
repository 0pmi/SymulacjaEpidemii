package epidemic.model;

public abstract class Agent {

    protected Point2D position;
    protected HealthStatus healthStatus;
    protected int age;
    protected double naturalMortalityRate;

    private boolean isDead;
    private boolean isInHospital;

    public Agent(Point2D position, int age, double naturalMortalityRatem) {
        this.position = position;
        this.age = age;
        this.naturalMortalityRate = naturalMortalityRatem;

        this.healthStatus = HealthStatus.HEALTHY;
        this.isDead = false;
        this.isInHospital = false;
    }

    public void move(Point2D newPos) {
        // TODO:
    }

    public void incrementAge() {
        // TODO:
    }

    public boolean canBeInfected() {
        // TODO:
        return false;
    }

    public boolean isDead() {
        return this.isDead;
    }

    public boolean wantsHospital() {
        return false;
    }

    public boolean isInHospital() {
        return this.isInHospital;
    }

    public void setIsInHospital(boolean val) {
        this.isInHospital = val;
    }

    public Point2D getPosition() {
        return this.position;
    }

    public HealthStatus getHealthStatus() {
        return this.healthStatus;
    }

    public void setHealthStatus(HealthStatus healthStatus) {
        this.healthStatus = healthStatus;
    }
}
