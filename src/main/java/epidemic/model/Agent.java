package epidemic.model;

import epidemic.service.Config;
import epidemic.strategies.movement.MovementStrategy;
/**
 * Abstrakcyjna klasa bazowa reprezentująca każdą encję zdolną do poruszania się
 * i uczestniczenia w procesie epidemicznym na mapie symulacji.
 * Centralizuje zarządzanie pozycją, stanem zdrowia oraz podstawowym cyklem życia.
 */
public abstract class Agent implements Inspectable {

    private Point2D position;
    private Point2D currentTarget;
    private double baseSpeed;
    private double currentSpeed;
    private MovementStrategy movementStrategy;

    private final SpeciesType speciesType;
    private int age;
    private boolean isDead;
    private double naturalMortalityRate;
    private int lastReproductionEpoch;
    private int remainingInfectionEpochs;
    private boolean diedFromVirus = false;
    private HealthStatus healthStatus;

    public Agent(Point2D position, int age, SpeciesType speciesType, double baseSpeed, MovementStrategy movementStrategy) {
        this.position = position;
        this.currentTarget = position;
        this.age = age;
        this.speciesType = speciesType;
        this.baseSpeed = baseSpeed;
        this.currentSpeed = baseSpeed;
        this.healthStatus = HealthStatus.HEALTHY;
        this.naturalMortalityRate = Config.getDouble("agent.defaultNaturalMortality", 0.01);
        this.isDead = false;
        this.lastReproductionEpoch = 0;
        this.remainingInfectionEpochs = 0;
        this.movementStrategy = movementStrategy;
    }
    @Override
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Inspektor Agenta ===\n");
        sb.append("ID: ").append(Integer.toHexString(this.hashCode())).append("\n");
        sb.append("Typ: ").append(getSpeciesType()).append("\n");
        sb.append("Wiek: ").append(getAge()).append("\n");
        sb.append("Stan zdrowia: ").append(getHealthStatus()).append("\n");
        return sb.toString();
    }
    /**
     * Zwiększa wiek agenta o jedną jednostkę.
     * Powinno być wywoływane co epokę symulacyjną.
     */
    public void incrementAge() {
        this.age++;
    }

    /**
     * Określa, czy agent jest podatny na nową infekcję.
     * Martwi agenci oraz ci, którzy już wykazują objawy (SICK), nie mogą zostać ponownie zainfekowani w tym samym czasie.
     *
     * @return true, jeśli agent może złapać wirusa, false w przeciwnym wypadku.
     */
    public boolean canBeInfected() {
        return !isDead && healthStatus != HealthStatus.SICK;
    }

    /**
     * Zmniejsza licznik pozostałych epok infekcji.
     * Metoda uodporniona na zejście poniżej zera. Stanowi podstawę mechanizmu wyzdrowienia (RECOVERED).
     */
    public void decrementInfectionTimer() {
        if (this.remainingInfectionEpochs > 0) {
            this.remainingInfectionEpochs--;
        }
    }
    /**
     * Metoda wywoływana w każdej epoce, pozwalająca agentowi na analizę środowiska i podjęcie decyzji.
     *
     * @param context Aktualny stan środowiska symulacyjnego.
     */
    public void think(WorldContext context) {}


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
    public boolean isDiedFromVirus() { return diedFromVirus; }
    public void setDiedFromVirus(boolean diedFromVirus) { this.diedFromVirus = diedFromVirus; }
    public int getRemainingInfectionEpochs() {return remainingInfectionEpochs; }

    public void setRemainingInfectionEpochs(int remainingInfectionEpochs) {this.remainingInfectionEpochs = remainingInfectionEpochs; }

    /**
     * Zwraca mnożnik podatności zależny od cech gatunku lub środowiska.
     * Pobiera wartość bazową z globalnej konfiguracji, chyba że zostanie to nadpisane.
     *
     * @return Mnożnik podatności (wartość domyślna to 1.0).
     */
    public double getVulnerabilityMultiplier() {
        return Config.getDouble("agent.defaultVulnerability", 1.0);
    }
    public MovementStrategy getMovementStrategy() {return movementStrategy; }
    public void setMovementStrategy(MovementStrategy strategy) {this.movementStrategy = strategy; }
    public double getVirulence() {
        return getSpeciesType().getBaseVirulence();
    }
}