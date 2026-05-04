package epidemic.model;

import epidemic.service.Config;
import epidemic.strategies.movement.MovementStrategy;

import java.awt.*;
import java.util.List;

/**
 * Rozszerzenie agenta symulującego zawiłości i cechy gatunku ludzkiego.
 * Ludzie posiadają możliwość stosowania zaawansowanej profilaktyki (szczepienia, maseczki)
 * oraz podejmują decyzje w locie w oparciu o przydzielony im profil psychologiczny (Personality).
 * Implementuje interfejs HospitalUser pozwalający na interakcję ze specjalistyczną infrastrukturą mapy.
 */
public class Human extends Agent implements HospitalUser {

    private double resistance;
    private final Personality personality;
    private boolean isVaccinated;
    private boolean isWearingMask;
    private boolean wantsHospital;
    private boolean isInHospital;
    private boolean isHostile;

    /**
     * Inicjalizuje nową jednostkę ludzką z rozbudowanym stanem socjologicznym.
     *
     * @param position Startowa pozycja agenta na mapie wektorowej.
     * @param age Wiek w jednostkach arbitralnych używanych przez silnik do kalkulacji zgonów.
     * @param baseSpeed Indywidualna prędkość przemieszczania na jeden cykl zegarowy.
     * @param resistance Wrodzona odporność biologiczna (wartość redukująca szansę na zakażenie).
     * @param personality Profil psychologiczny zarządzający cyklem decyzyjnym (think).
     * @param movementStrategy Domyślny wzorzec lokomocji nadany agentowi.
     */
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

    /**
     * Zwiększa standardową listę metadanych agenta o parametry ściśle związane z gatunkiem
     * ludzkim i infrastrukturą socjologiczną (status szczepień, szpitali oraz agresji).
     *
     * @return Uporządkowana i pokolorowana lista obiektów InspectionProperty.
     */
    @Override
    public List<InspectionProperty> getInspectionProperties() {
        List<InspectionProperty> props = super.getInspectionProperties();

        if (isDead()) {
            return props;
        }
        if (isHostile()) {
            props.add(InspectionProperty.textColored("Status Agresji", "WŚCIEKŁY!", Color.BLACK));
        }
        props.add(InspectionProperty.text("Szczepienie", isVaccinated() ? "Tak" : "Nie"));
        props.add(InspectionProperty.text("W szpitalu", isInHospital() ? "Tak" : "Nie"));
        props.add(InspectionProperty.text("Chce do szpitala", isWantsHospital() ? "Tak" : "Nie"));

        String decisionName = getPersonality().getDecisionStrategy().getClass().getSimpleName();
        props.add(InspectionProperty.textColored("Strategia Decyzyjna", decisionName, new Color(70, 130, 180)));

        return props;
    }

    /**
     * Wyzwala ocenę poznawczą agenta, przekazując sterowanie do obiektu typu Personality.
     * Na podstawie aktualnych danych (context), agent może zmienić stan swoich maseczek,
     * chęć hospitalizacji, a w skrajnych przypadkach zaktualizować swój obiekt strategii MovementStrategy.
     *
     * @param context Aktualny obraz świata z perspektywy centralnej administracji (wskaźniki zakażeń).
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
     * Oblicza skumulowaną i zredukowaną podatność organizmu na infekcje.
     * Pobiera wagi wpływu z konfiguracji (Config) w celu zbilansowania wartości ochrony,
     * wliczając zastosowanie wrodzonej rezystancji biologicznej oraz wyposażenia osobistego.
     * Mniejszy zwrot funkcji odzwierciedla zwiększone szanse na pomyślne przetrwanie bliskiego kontaktu.
     *
     * @return Skalar określający ostateczny współczynnik podatności komórek w ciele agenta na patogen.
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