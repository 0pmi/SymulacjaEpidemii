package epidemic.model;

/**
 * Reprezentuje niemutowalny punkt w dwuwymiarowej dyskretnej przestrzeni symulacji.
 * Wykorzystywany do określania pozycji agentów i celów ich podróży.
 */
public record Point2D(int x, int y) {
    /**
     * Oblicza odległość euklidesową między bieżącym punktem a punktem docelowym.
     *
     * @param other Punkt, do którego obliczana jest odległość.
     * @return Rzeczywista odległość między punktami.
     */
    public double distanceTo(Point2D other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }
}