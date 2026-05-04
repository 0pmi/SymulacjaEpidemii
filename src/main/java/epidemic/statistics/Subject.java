package epidemic.statistics;

/**
 * Definiuje kontrakt dla obiektu obserwowanego (Podmiotu) w ramach wzorca projektowego Obserwator (Observer).
 * Implementowany zazwyczaj przez główne komponenty sterujące (np. Silnik Symulacji),
 * które emitują zdarzenia i rozgłaszają zmiany swojego stanu wewnętrznego do podpiętych subskrybentów.
 */
public interface Subject {

    /**
     * Rejestruje nowego obserwatora w liście subskrybentów.
     *
     * @param observer Obiekt nasłuchujący, implementujący interfejs {@link Observer}.
     */
    void addObserver(Observer observer);

    /**
     * Wyrejestrowuje obserwatora z listy subskrybentów.
     *
     * @param observer Obiekt do usunięcia z listy powiadomień.
     */
    void removeObserver(Observer observer);

    /**
     * Synchronicznie powiadamia wszystkich zarejestrowanych obserwatorów o wystąpieniu zdarzenia
     * (np. o zakończeniu przetwarzania epoki), zazwyczaj przekazując im nową paczkę danych.
     */
    void notifyObservers();
}