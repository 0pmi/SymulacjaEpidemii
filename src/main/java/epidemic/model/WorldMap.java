package epidemic.model;

import epidemic.service.SpatialManager;
import java.util.ArrayList;
import java.util.List;

public class WorldMap {
    private List<Agent> agents;
    private List<Hospital> hospitals;
    private int width;
    private int height;

    private SpatialManager spatialManager;

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

    public void addAgent(Agent agent) {
        agentsToAdd.add(agent);
    }

    public void removeAgent(Agent agent) {
        agentsToRemove.add(agent);
    }

    public void applyChanges() {
        agents.addAll(agentsToAdd);
        agentsToAdd.clear();
        agents.removeAll(agentsToRemove);
        agentsToRemove.clear();
    }

    public List<Agent> getNeighbors(Point2D pos, double radius) {
        return new ArrayList<>();
    }

    public List<Agent> getNeighborsForAgent(Agent agent, double radius) {
        return spatialManager.getNearbyAgents(agent, radius);
    }

    public void rebuildSpatialIndex() {
        spatialManager.rebuild(this);
    }

    // Gettery
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public List<Agent> getAgents() { return agents; }
    public List<Hospital> getHospitals() { return hospitals; }

    public void addHospital(Hospital hospital) {
        this.hospitals.add(hospital);
    }

    public Hospital getHospitalAt(Point2D pos) {
        return hospitals.stream()
                .filter(h -> h.getPosition().equals(pos))
                .findFirst()
                .orElse(null);
    }
    public boolean isWithinBounds(Point2D pos) {
        return pos.x() >= 0 && pos.x() < width &&
                pos.y() >= 0 && pos.y() < height;
    }
}