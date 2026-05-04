package epidemic.model;

import epidemic.service.Config;
import epidemic.strategies.movement.MovementStrategy;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstrakcyjna klasa bazowa reprezentująca każdą autonomiczną encję zdolną do poruszania się
 * i uczestniczenia w procesie epidemicznym na mapie symulacji.
 * Centralizuje zarządzanie pozycją przestrzenną, statusem medycznym (HealthStatus)
 * oraz podstawowym cyklem życia biologicznym (wiek, reprodukcja, zgon).
 * Implementuje interfejs Inspectable na potrzeby dynamicznego GUI.
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

    /**
     * Inicjalizuje nowego agenta z podstawowymi parametrami biologicznymi.
     * Wartości takie jak status zdrowotny (HEALTHY) i wskaźniki umieralności
     * są pobierane i ustawiane zgodnie z globalną konfiguracją (Config).
     *
     * @param position Początkowa koordynata przestrzenna na siatce mapy.
     * @param age Początkowy wiek jednostki.
     * @param speciesType Gatunek przypisany do jednostki, determinujący np. zjadliwość.
     * @param baseSpeed Prędkość przemieszczania się w warunkach normalnych.
     * @param movementStrategy Przypisana strategia poruszania się (wzorzec Strategy).
     */
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

    /**
     * Zwraca nagłówek dla inspektora GUI w oparciu o przypisany gatunek.
     */
    @Override
    public String getObjectName() {
        return "Typ: " + getSpeciesType().name();
    }

    /**
     * Konstruuje hierarchiczną listę właściwości telemetrycznych agenta, używanych
     * przez warstwę widoku do renderowania paska bocznego. Metoda polimorficzna
     * – może być nadpisywana przez klasy potomne. Dla agentów chorych (SICK/CARRIER)
     * generowany jest dodatkowy pasek postępu infekcji.
     *
     * @return Uporządkowana lista obiektów InspectionProperty.
     */
    @Override
    public List<InspectionProperty> getInspectionProperties() {
        List<InspectionProperty> props = new ArrayList<>();

        props.add(InspectionProperty.text("Pozycja", "[" + getPosition().x() + ", " + getPosition().y() + "]"));
        int maxAge = Config.getInt("mortality.maxAge", 100);
        props.add(InspectionProperty.progressBar("Wiek (" + getAge() + ")", getAge(), maxAge, new Color(46, 139, 87)));

        if (isDead()) {
            props.add(InspectionProperty.textColored("STAN", "MARTWY", Color.BLACK));
            return props;
        }

        Color healthColor = getColorForStatus(getHealthStatus());
        props.add(InspectionProperty.textColored("Stan Zdrowia", getHealthStatus().toString(), healthColor));

        if (getHealthStatus() == HealthStatus.SICK || getHealthStatus() == HealthStatus.CARRIER) {
            int defaultDuration = Config.getInt("virus.defaultDuration", 30);
            props.add(InspectionProperty.progressBar(
                    "Do końca infekcji (" + getRemainingInfectionEpochs() + " / " + defaultDuration + ")",
                    getRemainingInfectionEpochs(),
                    defaultDuration,
                    Color.RED
            ));
        }

        props.add(InspectionProperty.text("Podatność", String.format("%.2f", getVulnerabilityMultiplier())));
        props.add(InspectionProperty.text("Strategia Ruchu", getMovementStrategy().getClass().getSimpleName()));

        return props;
    }

    /**
     * Mapuje enumerację HealthStatus na dedykowany obiekt koloru z pakietu AWT.
     * Używane do ujednoliconych wizualizacji na płótnie mapy i w oknie inspekcji.
     *
     * @param status Bieżący stan chorobowy agenta.
     * @return Skorelowany obiekt typu Color.
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
     * Inkrementuje licznik wieku agenta o jedną jednostkę.
     * Częstotliwość wywoływania tej metody (np. co X epok) jest regulowana przez menedżerów.
     */
    public void incrementAge() {
        this.age++;
    }

    /**
     * Weryfikuje podatność agenta na nową infekcję w obecnym kroku symulacji.
     * Martwi agenci oraz nosiciele z aktywną fazą pełnoobjawową (SICK) są odrzucani.
     *
     * @return Wartość true, jeśli agent spełnia fizjologiczne kryteria do zakażenia wirusem.
     */
    public boolean canBeInfected() {
        return !isDead && healthStatus != HealthStatus.SICK;
    }

    /**
     * Zmniejsza wskaźnik żywotności wirusa w organizmie agenta.
     * Chroni przed błędnym wyznaczeniem wartości poniżej zera. Osiągnięcie wartości
     * minimalnej stanowi warunek przejścia do stanu ozdrowieńca (RECOVERED).
     */
    public void decrementInfectionTimer() {
        if (this.remainingInfectionEpochs > 0) {
            this.remainingInfectionEpochs--;
        }
    }

    /**
     * Polimorficzny punkt wywołania mechanizmów decyzyjnych (Kognitywistyka).
     * Domyślna implementacja w klasie bazowej nie wykonuje żadnych akcji (no-op).
     *
     * @param context Aktualny zestaw danych telemetrycznych środowiska.
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
     * Kalkuluje finalny współczynnik podatności jednostki na infekcję,
     * na bazie domyślnych wartości z konfiguracji. Metoda jest zazwyczaj
     * nadpisywana przez klasy potomne dla uwzględnienia odporności gatunkowej i odzieży.
     *
     * @return Mnożnik szansy na infekcję (standardowo 1.0 dla zerowych bonusów ochronnych).
     */
    public double getVulnerabilityMultiplier() {
        return Config.getDouble("agent.defaultVulnerability", 1.0);
    }
    public MovementStrategy getMovementStrategy() {return movementStrategy; }
    public void setMovementStrategy(MovementStrategy strategy) {this.movementStrategy = strategy; }

    /**
     * Zwraca zjadliwość agenta dla wariantu środowiskowego (bazując na rodzaju gatunku).
     */
    public double getVirulence() {
        return getSpeciesType().getBaseVirulence();
    }
}