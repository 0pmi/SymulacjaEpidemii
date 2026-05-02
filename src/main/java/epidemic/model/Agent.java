package epidemic.model;

import epidemic.service.Config;
import epidemic.strategies.movement.MovementStrategy;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
    public String getObjectName() {
        return "Typ: " + getSpeciesType().name();
    }

    @Override
    public List<InspectionProperty> getInspectionProperties() {
        List<InspectionProperty> props = new ArrayList<>();

        // 1. Pozycja
        props.add(InspectionProperty.text("Pozycja", "[" + getPosition().x() + ", " + getPosition().y() + "]"));

        // 2. Wiek - konfiguracja maksymalnego wieku z systemu
        int maxAge = Config.getInt("mortality.maxAge", 100);
        // Przekazujemy aktualny wiek do etykiety, aby tekst nad paskiem był precyzyjny
        props.add(InspectionProperty.progressBar("Wiek (" + getAge() + ")", getAge(), maxAge, new Color(46, 139, 87)));

        // 3. Jeśli agent nie żyje, natychmiast przerywamy budowanie dalszych statystyk
        if (isDead()) {
            props.add(InspectionProperty.textColored("STAN", "MARTWY", Color.BLACK));
            return props;
        }

        // 4. Stan zdrowia (z odpowiednim formatowaniem kolorystycznym)
        Color healthColor = getColorForStatus(getHealthStatus());
        props.add(InspectionProperty.textColored("Stan Zdrowia", getHealthStatus().toString(), healthColor));

        // 5. Przebieg infekcji (Pasek pokazujący się tylko dla chorych i nosicieli)
        if (getHealthStatus() == HealthStatus.SICK || getHealthStatus() == HealthStatus.CARRIER) {
            int defaultDuration = Config.getInt("virus.defaultDuration", 30);
            props.add(InspectionProperty.progressBar(
                    "Do końca infekcji (" + getRemainingInfectionEpochs() + " / " + defaultDuration + ")",
                    getRemainingInfectionEpochs(),
                    defaultDuration,
                    Color.RED
            ));
        }

        // 6. Wskaźniki biomechaniczne
        props.add(InspectionProperty.text("Podatność", String.format("%.2f", getVulnerabilityMultiplier())));
        props.add(InspectionProperty.text("Strategia Ruchu", getMovementStrategy().getClass().getSimpleName()));

        return props;
    }

    /**
     * Tłumaczy status medyczny na kolor.
     */
    protected Color getColorForStatus(HealthStatus status) {
        return switch (status) {
            case HEALTHY -> new Color(34, 139, 34);
            case SICK -> Color.RED;
            case CARRIER -> Color.ORANGE;
            case RECOVERED -> new Color(0, 191, 255);
        };
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