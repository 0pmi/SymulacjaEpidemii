package epidemic.model;

import epidemic.service.Config;

/**
 * Reprezentuje stacjonarną chmurę zakaźnego aerozolu (miasmę) pozostawioną przez zakażonego agenta.
 * Posiada własny cykl życia – z każdą epoką traci na sile (rozprasza się) aż do całkowitego zaniku.
 */
public class InfectionField {
    private final Point2D position;
    private int remainingEpochs;
    private double infectivity;

    /**
     * Tworzy nową strefę skażenia.
     *
     * @param position Współrzędne strefy (zaokrąglone do siatki).
     * @param initialInfectivity Początkowa siła zakaźna chmury.
     */
    public InfectionField(Point2D position, double initialInfectivity) {
        this.position = position;
        this.remainingEpochs = Config.getInt("infectionField.defaultExpiration", 10);
        this.infectivity = initialInfectivity;
    }

    /**
     * Odświeża strefę skażenia (np. gdy do pomieszczenia wejdzie kolejny zarażony agent).
     * Resetuje licznik czasu życia i ustawia siłę infekcji na wyższą z dwóch wartości.
     *
     * @param newInfectivity Siła zakaźna nowego źródła.
     */
    public void refresh(double newInfectivity) {
        this.remainingEpochs = Config.getInt("infectionField.defaultExpiration", 10);
        this.infectivity = Math.max(this.infectivity, newInfectivity);
    }

    /**
     * Symuluje rozpraszanie się wirusa w powietrzu.
     * Zmniejsza pozostały czas życia oraz redukuje siłę zakaźną o ustalony współczynnik.
     */
    public void decay() {
        this.remainingEpochs--;
        double dissipation = Config.getDouble("infectionField.dissipationRate", 0.05);
        this.infectivity *= (1.0 - dissipation);
    }

    /**
     * Weryfikuje, czy chmura powinna zostać usunięta z mapy.
     *
     * @return true, jeśli czas życia dobiegł końca lub siła zakaźna spadła poniżej minimalnego progu.
     */
    public boolean isExpired() {
        double minInfectivity = Config.getDouble("infectionField.minInfectivity", 0.01);
        return remainingEpochs <= 0 || infectivity < minInfectivity;
    }

    public Point2D getPosition() {
        return position;
    }

    public double getInfectivity() {
        return infectivity;
    }
}