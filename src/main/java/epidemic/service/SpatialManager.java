package epidemic.service;

import epidemic.model.Agent;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Optymalizuje zapytania przestrzenne w symulacji poprzez implementację struktury
 * podziału przestrzennego (Grid Spatial Partitioning / Bin-Lattice).
 * Dzieli mapę na logiczną siatkę komórek, co pozwala na błyskawiczne znajdowanie agentów
 * w zadanym promieniu, drastycznie redukując złożoność obliczeniową z O(N^2) do wartości
 * bliskich O(N) poprzez eliminację konieczności iterowania po całej populacji świata.
 */
public class SpatialManager {

    private final double cellSize;
    private final int cols;
    private final int rows;
    private final List<Agent>[][] grid;

    /**
     * Inicjalizuje nową siatkę wyszukiwań przestrzennych.
     *
     * @param worldWidth Całkowita szerokość mapy świata.
     * @param worldHeight Całkowita wysokość mapy świata.
     * @param cellSize Rozmiar pojedynczej komórki siatki. Powinien być skorelowany
     *                 z maksymalnym promieniem wyszukiwania (np. promieniem zakażenia),
     *                 aby zoptymalizować liczbę odpytywanych komórek sąsiadujących.
     */
    @SuppressWarnings("unchecked")
    public SpatialManager(double worldWidth, double worldHeight, double cellSize) {
        this.cellSize = cellSize;
        this.cols = (int) Math.ceil(worldWidth / cellSize);
        this.rows = (int) Math.ceil(worldHeight / cellSize);

        this.grid = new ArrayList[cols][rows];
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                grid[i][j] = new ArrayList<>();
            }
        }
    }

    /**
     * Czyści siatkę i od nowa przypisuje żywych agentów do odpowiednich komórek
     * na podstawie ich zaktualizowanych koordynatów.
     * Wywoływane obowiązkowo raz na epokę po zakończeniu fazy przemieszczania się wszystkich jednostek.
     *
     * @param worldMap Mapa świata zawierająca aktualną listę agentów.
     */
    public void rebuild(WorldMap worldMap) {
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                grid[i][j].clear();
            }
        }

        for (Agent agent : worldMap.getAgents()) {
            if (agent.isDead()) continue;

            Point2D pos = agent.getPosition();
            int col = (int) (pos.x() / cellSize);
            int row = (int) (pos.y() / cellSize);

            col = Math.max(0, Math.min(col, cols - 1));
            row = Math.max(0, Math.min(row, rows - 1));

            grid[col][row].add(agent);
        }
    }

    /**
     * Wyszukuje wszystkich żywych agentów przebywających w określonym promieniu
     * od wskazanego agenta docelowego. Optymalizuje proces badając tylko komórki
     * siatki pokrywające się z obszarem wyznaczonego promienia (Bounding Box).
     *
     * @param centerAgent Agent stanowiący środek okręgu wyszukiwania. Wynik naturalnie wyklucza tego agenta.
     * @param radius Promień wyszukiwania w jednostkach miary przestrzeni mapy.
     * @return Lista znalezionych sąsiadów w zasięgu.
     */
    public List<Agent> getNearbyAgents(Agent centerAgent, double radius) {
        List<Agent> nearby = new ArrayList<>();
        Point2D centerPos = centerAgent.getPosition();

        int startCol = (int) ((centerPos.x() - radius) / cellSize);
        int endCol   = (int) ((centerPos.x() + radius) / cellSize);
        int startRow = (int) ((centerPos.y() - radius) / cellSize);
        int endRow   = (int) ((centerPos.y() + radius) / cellSize);

        startCol = Math.max(0, startCol);
        endCol   = Math.min(cols - 1, endCol);
        startRow = Math.max(0, startRow);
        endRow   = Math.min(rows - 1, endRow);

        for (int i = startCol; i <= endCol; i++) {
            for (int j = startRow; j <= endRow; j++) {
                for (Agent potentialNeighbor : grid[i][j]) {
                    if (potentialNeighbor == centerAgent) continue;

                    double distance = calculateDistance(centerPos, potentialNeighbor.getPosition());
                    if (distance <= radius) {
                        nearby.add(potentialNeighbor);
                    }
                }
            }
        }
        return nearby;
    }

    /**
     * Wyszukuje agentów w określonym promieniu bazując na abstrakcyjnym punkcie na mapie,
     * a nie na konkretnym agencie. Przydatne np. do weryfikacji zagęszczenia tłumu w danym sektorze.
     *
     * @param centerPos Dokładny punkt centralny obszaru wyszukiwania.
     * @param radius Promień wyszukiwania.
     * @return Lista jednostek w zasięgu zadanego punktu.
     */
    public List<Agent> getNearbyAgentsAtPos(Point2D centerPos, double radius) {
        List<Agent> nearby = new ArrayList<>();

        int startCol = (int) ((centerPos.x() - radius) / cellSize);
        int endCol   = (int) ((centerPos.x() + radius) / cellSize);
        int startRow = (int) ((centerPos.y() - radius) / cellSize);
        int endRow   = (int) ((centerPos.y() + radius) / cellSize);

        startCol = Math.max(0, startCol);
        endCol   = Math.min(cols - 1, endCol);
        startRow = Math.max(0, startRow);
        endRow   = Math.min(rows - 1, endRow);

        for (int i = startCol; i <= endCol; i++) {
            for (int j = startRow; j <= endRow; j++) {
                for (Agent potentialNeighbor : grid[i][j]) {
                    double distance = calculateDistance(centerPos, potentialNeighbor.getPosition());
                    if (distance <= radius) {
                        nearby.add(potentialNeighbor);
                    }
                }
            }
        }
        return nearby;
    }

    /*
     * Oblicza odległość euklidesową (w linii prostej) między dwoma punktami.
     */
    private double calculateDistance(Point2D p1, Point2D p2) {
        double dx = p1.x() - p2.x();
        double dy = p1.y() - p2.y();
        return Math.sqrt(dx * dx + dy * dy);
    }
}