package epidemic.model;

/**
 * Interfejs definiujący kontrakt dla encji mogących korzystać z infrastruktury medycznej (szpitali).
 * Pozwala na odseparowanie złożonej logiki Agenta od wymagań Menedżera Medycznego,
 * wprowadzając zasadę odwrócenia zależności (Dependency Inversion).
 */
public interface HospitalUser {

    /**
     * Weryfikuje, czy pacjent wyraża chęć udania się do szpitala.
     *
     * @return {@code true}, jeśli pacjent aktywnie dąży do hospitalizacji.
     */
    boolean isWantsHospital();

    /**
     * Modyfikuje decyzję pacjenta dotyczącą chęci udania się do szpitala.
     *
     * @param status Zaktualizowana wartość chęci na hospitalizację.
     */
    void setWantsHospital(boolean status);

    /**
     * Sprawdza, czy pacjent został przyjęty i aktualnie znajduje się na oddziale szpitalnym.
     *
     * @return {@code true}, jeśli pacjent fizycznie korzysta z placówki.
     */
    boolean isInHospital();

    /**
     * Zmienia status obecności pacjenta w placówce.
     *
     * @param status {@code true} przy przyjęciu, {@code false} po zwolnieniu pacjenta.
     */
    void setIsInHospital(boolean status);

    /**
     * Udostępnia aktualne współrzędne pacjenta na mapie.
     * Koordynaty te są niezbędne dla menedżerów logiki do weryfikacji wejścia do budynku placówki.
     *
     * @return Punkt przestrzenny zajmowany aktualnie przez pacjenta.
     */
    Point2D getPosition();

    /**
     * Zwraca aktualną kondycję biologiczną pacjenta.
     *
     * @return Wartość ze słownika stanów medycznych (HealthStatus).
     */
    HealthStatus getHealthStatus();

    /**
     * Ustawia nowy stan zdrowotny pacjenta na skutek diagnozy, leczenia lub rozwoju choroby.
     *
     * @param status Nowo przypisany status medyczny.
     */
    void setHealthStatus(HealthStatus status);

    /**
     * Sprawdza, czy pacjent odbył szczepienie prewencyjne.
     *
     * @return {@code true}, jeśli organizm uzyskał odporność poszczepienną.
     */
    boolean isVaccinated();

    /**
     * Rejestruje pacjenta w systemie jako zaszczepionego.
     *
     * @param vaccinated Docelowy stan szczepienia.
     */
    void setVaccinated(boolean vaccinated);

    /**
     * Zwraca czas trwania objawów w krokach symulacji.
     *
     * @return Czas w epokach do całkowitego wyzdrowienia.
     */
    int getRemainingInfectionEpochs();

    /**
     * Nadpisuje czas, po upłynięciu którego infekcja ustąpi w organizmie.
     *
     * @param epochs Nowa liczba epok potrzebna do zakończenia procesu chorobowego.
     */
    void setRemainingInfectionEpochs(int epochs);
}