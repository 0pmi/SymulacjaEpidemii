package epidemic.engine;

import epidemic.managers.*;
import epidemic.model.Virus;
import epidemic.statistics.Statistics;

public class SimulationEngine {
    private WorldMap world;
    private Statistics stats;
    private Virus virus;

    private InfectionManager infectionManager;
    private MortalityManager mortalityManager;
    private MovementManager movementManager;

    private int currentEpoch;

    public SimulationEngine(WorldMap world, Virus virus) {
        this.world = world;
        this.virus = virus;
        this.currentEpoch = 0;
        // TODO
    }

    public void runNextEpoch() {
        // TODO
        currentEpoch++;
    }

    private void handleMovement() {}
    private void handleLifeCycle() {}
    private void handleInfections() {}
    private void handleReproduction() {}
    private void handleMedical() {}
}