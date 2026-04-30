package epidemic.model;

import epidemic.service.SpatialManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Reprezentuje środowisko przestrzenne symulacji.
 * Zarządza kolekcjami agentów i szpitali oraz deleguje złożone zapytania
 * przestrzenne do dedykowanego SpatialManagera.
 */
public class WorldMap {
    private List<Agent> agents;
    private List<Hospital> hospitals;
    private int width;
    private int height;

    private SpatialManager spatialManager;

    // Bufory do bezpiecznego zarządzania cyklem życia agentów podczas iteracji
    private List<Agent> agentsToAdd;
    private List<Agent> agentsToRemove;

    public WorldMap(int width, int height, double cellSize) {
        this.width = width;
        this.height = height;
        this.agents = new ArrayList<>();
        this.hospitals = new ArrayList<>();
        this.agentsToAdd = new ArrayList<>();
        this.agentsToRemove = new ArrayList<>();
        this.spatialManager = new SpatialManager(width, height, cellSize);
    }

    /**
     * Zleca dodanie agenta do mapy. Agent zostanie faktycznie dodany
     * dopiero po wywołaniu metody {@link #applyChanges()}.
     * @param agent Agent do dodania.
     */
    public void addAgent(Agent agent) {
        agentsToAdd.add(agent);
    }

    /**
     * Zleca usunięcie agenta z mapy. Agent zostanie faktycznie usunięty
     * dopiero po wywołaniu metody {@link #applyChanges()}.
     * @param agent Agent do usunięcia.
     */
    public void removeAgent(Agent agent) {
        agentsToRemove.add(agent);
    }

    /**
     * Aplikuje zakolejkowane zmiany z buforów do głównej listy agentów.
     * Używane do zapobiegania ConcurrentModificationException w głównej pętli.
     */
    public void applyChanges() {
        agents.addAll(agentsToAdd);
        agentsToAdd.clear();
        agents.removeAll(agentsToRemove);
        agentsToRemove.clear();
    }

    public List<Agent> getNeighbors(Point2D pos, double radius) {
        return spatialManager.getNearbyAgentsAtPos(pos, radius);
    }

    public List<Agent> getNeighborsForAgent(Agent agent, double radius) {
        return spatialManager.getNearbyAgents(agent, radius);
    }

    public void rebuildSpatialIndex() {
        spatialManager.rebuild(this);
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public List<Agent> getAgents() { return agents; }
    public List<Hospital> getHospitals() { return hospitals; }

    public void addHospital(Hospital hospital) {
        this.hospitals.add(hospital);
    }

    public SpatialManager getSpatialManager() { return spatialManager; }

    /**
     * Wyszukuje szpital znajdujący się dokładnie we wskazanych współrzędnych.
     * @param pos Pozycja do sprawdzenia.
     * @return Obiekt Hospital lub null, jeśli na danym polu nie ma szpitala.
     */
    public Hospital getHospitalAt(Point2D pos) {
        return hospitals.stream()
                .filter(h -> h.getPosition().equals(pos))
                .findFirst()
                .orElse(null);
    }

    /**
     * Sprawdza, czy podana pozycja znajduje się wewnątrz granic mapy.
     * @param pos Pozycja do weryfikacji.
     * @return true, jeśli punkt leży w granicach, w przeciwnym razie false.
     */
    public boolean isWithinBounds(Point2D pos) {
        return pos.x() >= 0 && pos.x() < width &&
                pos.y() >= 0 && pos.y() < height;
    }
}