package epidemic.statistics;

/**
 * Interfejs dla obiektów nasłuchujących zmian w symulacji.
 */
public interface Observer {
    void update(EpochData data);
}