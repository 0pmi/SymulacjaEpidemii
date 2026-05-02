package epidemic.model;

import epidemic.service.Config;
import epidemic.strategies.movement.MovementStrategy;

import java.awt.*;
import java.util.List;

/**
 * Reprezentuje ludzkiego agenta z zaawansowaną logiką behawioralną i statusem medycznym.
 * Ludzie mogą korzystać ze środków ochrony osobistej, podlegać profilaktyce medycznej
 * oraz podejmować decyzje na podstawie przypisanej osobowości (Personality).
 */
public class Human extends Agent implements HospitalUser {

    private double resistance;
    private final Personality personality;
    private boolean isVaccinated;
    private boolean isWearingMask;
    private boolean wantsHospital;
    private boolean isInHospital;
    private boolean isHostile;

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
        this.isHostile = false;
    }

    @Override
    public List<InspectionProperty> getInspectionProperties() {
        // Pobieramy podstawowe informacje wygenerowane przez klasę Agent
        List<InspectionProperty> props = super.getInspectionProperties();

        // Jeśli metoda bazowa uznała, że agent nie żyje, nie ma sensu wyświetlać szczepień
        if (isDead()) {
            return props;
        }

        // Dodajemy specyficzne dla człowieka dane
        if (isHostile()) {
            props.add(InspectionProperty.textColored("Status Agresji", "WŚCIEKŁY!", Color.BLACK));
        }

        // Atrybuty medyczne z interfejsu HospitalUser
        props.add(InspectionProperty.text("Szczepienie", isVaccinated() ? "Tak" : "Nie"));
        props.add(InspectionProperty.text("W szpitalu", isInHospital() ? "Tak" : "Nie"));
        props.add(InspectionProperty.text("Chce do szpitala", isWantsHospital() ? "Tak" : "Nie"));

        // Strategia behawioralna podświetlona kolorem SteelBlue
        String decisionName = getPersonality().getDecisionStrategy().getClass().getSimpleName();
        props.add(InspectionProperty.textColored("Strategia Decyzyjna", decisionName, new Color(70, 130, 180)));

        return props;
    }

    /**
     * Deleguje proces podejmowania decyzji w epoce do przypisanego obiektu Osobowości.
     *
     * @param context Kontekst świata symulacji, z którego agent czerpie informacje.
     */
    @Override
    public void think(WorldContext context) {
        personality.updateMentalState(this, context);
    }

    public double getResistance() { return resistance; }
    public void setResistance(double resistance) { this.resistance = resistance; }

    public Personality getPersonality() { return personality; }

    public boolean isVaccinated() { return isVaccinated; }
    public void setVaccinated(boolean vaccinated) { this.isVaccinated = vaccinated; }

    public boolean isWearingMask() { return isWearingMask; }
    public void setWearingMask(boolean wearingMask) { this.isWearingMask = wearingMask; }

    public void setWantsHospital(boolean wantsHospital) { this.wantsHospital = wantsHospital; }
    public boolean isHostile() { return isHostile; }
    public void setHostile(boolean hostile) { this.isHostile = hostile; }

    @Override
    public boolean isWantsHospital() { return this.wantsHospital; }

    @Override
    public boolean isInHospital() { return this.isInHospital; }

    @Override
    public void setIsInHospital(boolean status) { this.isInHospital = status; }

    /**
     * Oblicza skumulowaną podatność na zakażenie (wartość mniejsza = trudniej zarazić).
     * Uwzględnia wrodzoną odporność (resistance) oraz stosowane środki zapobiegawcze,
     * pobierając ich wagi z globalnej konfiguracji systemu.
     *
     * @return Ułamek określający szansę na zarażenie w kontakcie z wirusem.
     */
    @Override
    public double getVulnerabilityMultiplier() {
        double multiplier = 1.0;

        if (isWearingMask()) {
            multiplier *= Config.getDouble("vulnerability.maskMultiplier", 0.3);
        }
        if (isVaccinated()) {
            multiplier *= Config.getDouble("vulnerability.vaccineMultiplier", 0.1);
        }

        multiplier *= (1.0 - getResistance());

        return multiplier;
    }
}