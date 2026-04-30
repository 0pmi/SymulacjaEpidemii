package epidemic.service;

import epidemic.model.Agent;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Optymalizuje zapytania przestrzenne w symulacji poprzez podział mapy na logiczną siatkę (grid).
 * Pozwala na błyskawiczne znajdowanie agentów w zadanym promieniu bez konieczności
 * iterowania po całej populacji świata.
 */
public class SpatialManager {

    private final double cellSize;
    private final int cols;
    private final int rows;
    private final List<Agent>[][] grid;

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
     * Czyści siatkę i przypisuje agentów do odpowiednich komórek na podstawie ich bieżących koordynatów.
     * Wywoływane obowiązkowo raz na epokę po zakończeniu fazy ruchu wszystkich agentów.
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
     * Wyszukuje wszystkich żywych agentów w promieniu od danego agenta docelowego.
     *
     * @param centerAgent Agent stanowiący środek okręgu wyszukiwania. Wynik nie zawiera tego agenta.
     * @param radius Promień wyszukiwania w jednostkach mapy.
     * @return Lista znalezionych sąsiadów.
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

    private double calculateDistance(Point2D p1, Point2D p2) {
        double dx = p1.x() - p2.x();
        double dy = p1.y() - p2.y();
        return Math.sqrt(dx * dx + dy * dy);
    }
}