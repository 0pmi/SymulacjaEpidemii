package epidemic.model;

/**
 * Niemutowalna struktura danych reprezentująca główny patogen w symulacji.
 * Kapsułkuje podstawowe, niezmienne parametry określające mechanikę zakaźności
 * oraz domyślny czas trwania procesu chorobowego w organizmie.
 */
public class Virus {
    private final double baseInfectionProbability;
    private final double infectionRadius;
    private final int defaultInfectionDuration;

    /**
     * Inicjalizuje nowy szczep patogenu.
     *
     * @param baseInfectionProbability Bazowa szansa (od 0.0 do 1.0) na pomyślną transmisję wirusa przy bliskim kontakcie.
     * @param infectionRadius Maksymalny dystans w jednostkach przestrzennych, na którym wirus może przenieść się bezpośrednio na nową ofiarę.
     * @param defaultInfectionDuration Standardowy czas trwania choroby (w epokach) u zakażonego agenta przed ewentualnym wyzdrowieniem.
     */
    public Virus(double baseInfectionProbability, double infectionRadius, int defaultInfectionDuration) {
        this.baseInfectionProbability = baseInfectionProbability;
        this.infectionRadius = infectionRadius;
        this.defaultInfectionDuration = defaultInfectionDuration;
    }
    /**
     * Pobiera bazowe prawdopodobieństwo udanej infekcji.
     *
     * @return Wartość ułamkowa reprezentująca zjadliwość wirusa w idealnych warunkach.
     */
    public double getBaseInfectionProbability() { return baseInfectionProbability; }

    /**
     * Pobiera promień rażenia patogenu.
     *
     * @return Dystans maksymalnego zasięgu infekcji kropelkowej.
     */
    public double getInfectionRadius() { return infectionRadius; }

    /**
     * Pobiera standardowy czas trwania infekcji.
     *
     * @return Liczba epok potrzebna do naturalnego zwalczenia wirusa przez organizm.
     */
    public int getDefaultInfectionDuration() { return defaultInfectionDuration; }
}