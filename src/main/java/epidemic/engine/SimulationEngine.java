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
 * Główny silnik sterujący upływem czasu w symulacji.
 * Orkiestruje pracę poszczególnych Menedżerów w ścisłej kolejności (Zachowanie -> Ruch -> Infekcje -> Medycyna -> Rozród -> Zgony).
 * Powiadamia podpiętych obserwatorów o zmianach w ekosystemie na koniec każdej epoki.
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
     * Uruchamia pojedynczy cykl (epokę) symulacji.
     * Wykonuje pełny przebieg logiki biznesowej dla wszystkich agentów.
     */
    public void runNextEpoch() {
        WorldContext context = calculateContext();

        // 1. Zbieranie informacji i decyzje
        behaviourManager.updateBehaviours(world, context);
        // 2. Przemieszczanie po planszy
        movementManager.moveAgents(world);
        // 3. Rozprzestrzenianie patogenu
        infectionManager.processInfections(world);
        // 4. Interwencje medyczne w szpitalach
        medicalManager.processMedicalCare(world, context);
        // 5. Powoływanie do życia nowego pokolenia
        reproductionManager.handleReproduction(world, world.getSpatialManager(), currentEpoch);
        // 6. Przetwarzanie postępów choroby i ewentualnych zgonów
        int newVirusDeaths = mortalityManager.processLifeCycles(world, world.getAgents());
        int totalDeathsThisStep = (int) world.getAgents().stream().filter(Agent::isDead).count();
        this.totalVirusDeaths += newVirusDeaths;
        this.totalNaturalDeaths += (totalDeathsThisStep - newVirusDeaths);

        // 7. Aplikowanie zmian na mapie oraz notyfikowanie observerów
        notifyObservers();
        world.applyChanges();
        world.decayInfectionFields();
        currentEpoch++;

        // Okresowe starzenie się populacji (zależne od konfiguracji)
        if (currentEpoch % Config.getInt("simulation.ageRate", 12) == 0){
            for (Agent a : world.getAgents()) {
                a.incrementAge();
            }
        }
    }

    /**
     * Generuje obiekt kontekstu dla bieżącej epoki. Kontekst ten służy agentom
     * jako wiedza o świecie zewnętrznym (np. do podejmowania decyzji o kwarantannie).
     */
    private WorldContext calculateContext() {
        List<Agent> agents = world.getAgents();
        if (agents.isEmpty()) return new WorldContext(0, vaccineAvailable, currentEpoch, 0);

        long sick = agents.stream().filter(a -> a.getHealthStatus() == HealthStatus.SICK).count();
        double infectionRate = (double) sick / agents.size();

        return new WorldContext(infectionRate, this.vaccineAvailable, currentEpoch,
                Config.getDouble("simulation.baseContextValue", 0.01));
    }

    @Override
    public void addObserver(Observer observer) { observers.add(observer); }

    @Override
    public void removeObserver(Observer observer) { observers.remove(observer); }

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