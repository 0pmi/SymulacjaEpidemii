package epidemic.model;

import epidemic.service.Config;

/**
 * Reprezentuje stacjonarną chmurę zakaźnego aerozolu (miasmę) pozostawioną w środowisku
 * przez przechodzącego nosiciela patogenu.
 * Posiada niezależny cykl życia – z każdą epoką traci na sile (rozprasza się)
 * aż do całkowitego zaniku i usunięcia z mapy.
 */
public class InfectionField {
    private final Point2D position;
    private int remainingEpochs;
    private double infectivity;

    /**
     * Tworzy nową, aktywną strefę skażenia.
     * Czas jej wygaśnięcia ustalany jest na podstawie bazowej konfiguracji środowiska.
     *
     * @param position Punkt centralny chmury (zazwyczaj zgodny z węzłem siatki przestrzennej).
     * @param initialInfectivity Początkowa, maksymalna siła zakaźna aerozolu w momencie powstania.
     */
    public InfectionField(Point2D position, double initialInfectivity) {
        this.position = position;
        this.remainingEpochs = Config.getInt("infectionField.defaultExpiration", 10);
        this.infectivity = initialInfectivity;
    }

    /**
     * Ponownie aktywuje strefę skażenia, wydłużając jej żywotność i potencjalnie
     * zwiększając jej stężenie (np. na skutek ponownego wejścia zarażonego agenta na dany obszar).
     * Resetuje licznik czasu życia i zachowuje wyższą siłę infekcji z dostępnych.
     *
     * @param newInfectivity Siła zakaźna wniesiona przez nowe źródło.
     */
    public void refresh(double newInfectivity) {
        this.remainingEpochs = Config.getInt("infectionField.defaultExpiration", 10);
        this.infectivity = Math.max(this.infectivity, newInfectivity);
    }

    /**
     * Symuluje fizyczne rozpraszanie się wirusa w środowisku.
     * Wywoływana cyklicznie co epokę, skraca licznik życia chmury i geometrycznie
     * redukuje jej siłę zakaźną o współczynnik utraty zdefiniowany w konfiguracji.
     */
    public void decay() {
        this.remainingEpochs--;
        double dissipation = Config.getDouble("infectionField.dissipationRate", 0.05);
        this.infectivity *= (1.0 - dissipation);
    }

    /**
     * Weryfikuje kryteria dopuszczające usunięcie chmury z przestrzeni mapy.
     *
     * @return {@code true}, jeśli czas życia dobiegł końca lub siła zakaźna spadła poniżej
     *         minimalnego, bezpiecznego progu zdefiniowanego w konfiguracji.
     */
    public boolean isExpired() {
        double minInfectivity = Config.getDouble("infectionField.minInfectivity", 0.01);
        return remainingEpochs <= 0 || infectivity < minInfectivity;
    }
    /**
     * Pobiera aktualne koordynaty strefy zakaźnej.
     *
     * @return Wektor położenia chmury.
     */
    public Point2D getPosition() {
        return position;
    }

    /**
     * Pobiera obecne stężenie patogenu w strefie.
     *
     * @return Ułamek określający siłę zakaźną w danym kroku symulacji.
     */
    public double getInfectivity() {
        return infectivity;
    }
}