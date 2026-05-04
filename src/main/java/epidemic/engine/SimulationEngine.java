package epidemic.engine;

import epidemic.factory.AgentFactory;
import epidemic.managers.*;
import epidemic.model.*;
import epidemic.service.Config;
import epidemic.statistics.*;
import epidemic.strategies.mortality.MortalityStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * Główny koordynator cyklu życia symulacji (wzorzec Fasada / Orchestrator).
 * Zarządza sekwencyjnym wywoływaniem poszczególnych menedżerów w obrębie pojedynczej
 * epoki (kroku czasowego). Gwarantuje deterministyczną kolejność faz:
 * zachowanie -> ruch -> infekcje -> medycyna -> rozród -> zgony.
 *
 * <p>Implementuje wzorzec Obserwatora (Subject), rozgłaszając zagregowane
 * statystyki populacji po pomyślnym zakończeniu każdej epoki.</p>
 */
public class SimulationEngine implements Subject {
    private final WorldMap world;
    private final Virus virus;
    private final Statistics stats;
    private final List<Observer> observers = new ArrayList<>();
    private int totalVirusDeaths = 0;
    private int totalNaturalDeaths = 0;
    private int currentEpoch = 0;
    private boolean vaccineAvailable = false;
    private boolean paused = true;

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
        this.addObserver(this.stats); // Statystyki domyślnie nasłuchują silnika

        this.behaviourManager = new BehaviourManager();
        this.movementManager = new MovementManager();
        this.infectionManager = new InfectionManager(virus);
        this.medicalManager = new MedicalManager();
        this.reproductionManager = new ReproductionManager(factory);
        this.mortalityManager = new MortalityManager(mortalityStrategy);
    }

    /**
     * Uruchamia pojedynczy cykl symulacji.
     * Przetwarza logikę biznesową dla wszystkich agentów, aplikuje zmiany na mapie
     * i powiadamia podpiętych obserwatorów o nowym stanie środowiska.
     */
    public void runNextEpoch() {
        WorldContext context = calculateContext();

        behaviourManager.updateBehaviours(world, context);
        movementManager.moveAgents(world);
        infectionManager.processInfections(world);
        medicalManager.processMedicalCare(world, context);
        reproductionManager.handleReproduction(world, world.getSpatialManager(), currentEpoch);
        int newVirusDeaths = mortalityManager.processLifeCycles(world, world.getAgents());
        int totalDeathsThisStep = (int) world.getAgents().stream().filter(Agent::isDead).count();
        this.totalVirusDeaths += newVirusDeaths;
        this.totalNaturalDeaths += (totalDeathsThisStep - newVirusDeaths);

        notifyObservers();
        world.applyChanges();
        world.decayInfectionFields();
        currentEpoch++;

        if (currentEpoch % Config.getInt("simulation.ageRate", 12) == 0){
            for (Agent a : world.getAgents()) {
                a.incrementAge();
            }
        }
    }

    /**
     * Generuje globalny kontekst informacyjny dla bieżącej epoki.
     * Kontekst ten służy agentom jako "wiedza o świecie" (np. stopień rozprzestrzenienia
     * wirusa), na podstawie której podejmują decyzje behawioralne.
     *
     * @return Obiekt agregujący globalne parametry w danej epoce.
     */
    private WorldContext calculateContext() {
        List<Agent> agents = world.getAgents();
        if (agents.isEmpty()) return new WorldContext(0, vaccineAvailable, currentEpoch, 0);

        long sick = agents.stream().filter(a -> a.getHealthStatus() == HealthStatus.SICK).count();
        double infectionRate = (double) sick / agents.size();

        return new WorldContext(infectionRate, this.vaccineAvailable, currentEpoch,
                Config.getDouble("simulation.baseContextValue", 0.01));
    }

    /**
     * Rejestruje nowego obserwatora nasłuchującego zmian statystycznych.
     * @param observer Obiekt implementujący interfejs Observer (np. moduł statystyk).
     */
    @Override
    public void addObserver(Observer observer) { observers.add(observer); }

    /**
     * Wyrejestrowuje istniejącego obserwatora.
     * @param observer Obiekt do usunięcia z listy subskrybentów.
     */
    @Override
    public void removeObserver(Observer observer) { observers.remove(observer); }

    /**
     * Oblicza bieżące podsumowanie demograficzne (zdrowi, chorzy, zmarli)
     * i wysyła je do wszystkich zarejestrowanych obserwatorów.
     */
    @Override
    public void notifyObservers() {
        List<Agent> agents = world.getAgents();

        int healthy = (int) agents.stream().filter(a -> !a.isDead() && a.getHealthStatus() == HealthStatus.HEALTHY).count();
        int sick = (int) agents.stream().filter(a -> !a.isDead() && a.getHealthStatus() == HealthStatus.SICK).count();
        int recovered = (int) agents.stream().filter(a -> !a.isDead() && a.getHealthStatus() == HealthStatus.RECOVERED).count();
        int aliveTotal = (int) agents.stream().filter(a -> !a.isDead()).count();

        int cumulativeDead = totalNaturalDeaths + totalVirusDeaths;

        EpochData data = new EpochData(
                currentEpoch,
                healthy,
                sick,
                recovered,
                cumulativeDead,
                totalVirusDeaths,
                aliveTotal
        );

        observers.forEach(o -> o.update(data));
    }
    public void setVaccineAvailable(boolean vaccineAvailable) { this.vaccineAvailable = vaccineAvailable; }
    public Statistics getStats() { return stats; }
    public boolean isPaused() { return paused; }
    public void setPaused(boolean paused) { this.paused = paused; }
}