package epidemic.statistics;

/**
 * Interfejs implementowany przez obiekty (Silnik Symulacji),
 * które są źródłem zdarzeń wewnątrz wzorca projektowego Obserwator.
 */
public interface Subject {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
}