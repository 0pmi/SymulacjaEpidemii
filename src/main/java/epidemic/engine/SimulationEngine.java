package epidemic.engine;

import epidemic.factory.AgentFactory;
import epidemic.managers.*;
import epidemic.model.*;
import epidemic.statistics.*;
import epidemic.strategies.mortality.MortalityStrategy;
import java.util.ArrayList;
import java.util.List;

public class SimulationEngine implements Subject {
    private final WorldMap world;
    private final Virus virus;
    private final Statistics stats;
    private final List<Observer> observers = new ArrayList<>();
    private int currentEpoch = 0;
    private boolean vaccineAvailable = false;

    private final BehaviourManager behaviourManager;
    private final MovementManager movementManager;
    private final InfectionManager infectionManager;
    private final MedicalManager medicalManager;
    private final ReproductionManager reproductionManager;
    private final MortalityManager mortalityManager;

    public SimulationEngine(WorldMap world, Virus virus, MortalityStrategy mortalityStrategy, AgentFactory factory) {
        this.world = world;
        this.virus = virus;
        this.stats = new Statistics();
        this.addObserver(this.stats);

        this.behaviourManager = new BehaviourManager();
        this.movementManager = new MovementManager();
        this.infectionManager = new InfectionManager(virus);
        this.medicalManager = new MedicalManager();
        this.reproductionManager = new ReproductionManager(factory);
        this.mortalityManager = new MortalityManager(mortalityStrategy);
    }

    public void runNextEpoch() {
        WorldContext context = calculateContext();

        behaviourManager.updateBehaviours(world, context);
        movementManager.moveAgents(world);
        infectionManager.processInfections(world.getAgents(), world.getSpatialManager());
        medicalManager.processMedicalCare(world, context);
        reproductionManager.handleReproduction(world, world.getSpatialManager(), currentEpoch);
        mortalityManager.processLifeCycles(world.getAgents());

        world.applyChanges();
        notifyObservers();
        currentEpoch++;
        if (currentEpoch % 12 == 0) {
            for (Agent a : world.getAgents()) {
                a.incrementAge();
            }
        }
    }

    private WorldContext calculateContext() {
        List<Agent> agents = world.getAgents();
        if (agents.isEmpty()) return new WorldContext(0, vaccineAvailable, currentEpoch, 0);

        long sick = agents.stream().filter(a -> a.getHealthStatus() == HealthStatus.SICK).count();
        double infectionRate = (double) sick / agents.size();

        return new WorldContext(infectionRate, this.vaccineAvailable, currentEpoch, 0.01);
    }

    @Override
    public void addObserver(Observer observer) { observers.add(observer); }
    @Override
    public void removeObserver(Observer observer) { observers.remove(observer); }
    @Override
    public void notifyObservers() {
        List<Agent> agents = world.getAgents();
        int healthy = (int) agents.stream().filter(a -> a.getHealthStatus() == HealthStatus.HEALTHY).count();
        int sick = (int) agents.stream().filter(a -> a.getHealthStatus() == HealthStatus.SICK).count();
        int recovered = (int) agents.stream().filter(a -> a.getHealthStatus() == HealthStatus.RECOVERED).count();

        EpochData data = new EpochData(currentEpoch, healthy, sick, recovered, 0, 0, agents.size());
        observers.forEach(o -> o.update(data));
    }
    public void setVaccineAvailable(boolean vaccineAvailable) {
        this.vaccineAvailable = vaccineAvailable;
    }
    public Statistics getStats() { return stats; }
}