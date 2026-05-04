package epidemic.statistics;

/**
 * Definiuje kontrakt dla subskrybentów (Obserwatorów) w ramach wzorca projektowego Obserwator.
 * Obiekty implementujące ten interfejs nasłuchują powiadomień emitowanych
 * przez obiekt obserwowany (np. główny silnik symulacji).
 */
public interface Observer {

    /**
     * Metoda wywoływana przez podmiot (Subject) w momencie wystąpienia zmiany stanu lub cyklu.
     *
     * @param data Zamrożona paczka danych (DTO) zawierająca podsumowanie właśnie zakończonej epoki.
     */
    void update(EpochData data);
}