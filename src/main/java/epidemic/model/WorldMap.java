package epidemic.model;

import java.util.ArrayList;
import java.util.List;

public class WorldMap {
    private List<Agent> agents;
    private List<Hospital> hospitals;
    private int width;
    private int height;
    private List<InfectionField> infectionFields;
    private List<Agent> agentsToAdd;
    private List<Agent> agentsToRemove;

    public WorldMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.agents = new ArrayList<>();
        this.hospitals = new ArrayList<>();
        this.infectionFields = new ArrayList<>();
        this.agentsToAdd = new ArrayList<>();
        this.agentsToRemove = new ArrayList<>();
    }

    public List<Agent> getNeighbors(Point2D pos, double radius) {
        return new ArrayList<>(); // TODO:
    }

    public void addAgent(Agent agent) {
        // TODO:
    }

    public Hospital getHospitalAt(Point2D pos) {
        return null; // TODO
    }

    public void removeAgent(Agent agent) {
        // TODO
    }

    public void updateInfectionFields() {
        // TODO
    }

    public List<InfectionField> getInfectionFieldsAt(Point2D pos) {
        return new ArrayList<>(); // TODO
    }

    public void addInfectionField(InfectionField field) {
        // TODO
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public List<Hospital> getHospitals() {
        return hospitals;
    }
}