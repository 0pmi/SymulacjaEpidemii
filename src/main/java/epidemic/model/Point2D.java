package epidemic.model;

/**
 * Niemutowalna struktura danych reprezentująca dyskretny punkt
 * w dwuwymiarowej przestrzeni świata symulacji.
 * Wykorzystywana do mapowania koordynatów agentów, infrastruktury oraz wektorów ruchu.
 *
 * @param x Współrzędna pozioma.
 * @param y Współrzędna pionowa.
 */
public record Point2D(int x, int y) {
    /**
     * Oblicza standardową odległość euklidesową (w linii prostej)
     * między bieżącym punktem a podanym punktem docelowym.
     *
     * @param other Docelowy punkt odniesienia.
     * @return Rzeczywista wartość odległości w jednostkach miary przestrzeni.
     */
    public double distanceTo(Point2D other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }
}