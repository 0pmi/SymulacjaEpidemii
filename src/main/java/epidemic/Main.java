package epidemic;

import epidemic.engine.SimulationEngine;
import epidemic.factory.AgentFactory;
import epidemic.gui.SimulationFrame;
import epidemic.model.*;
import epidemic.service.Config;
import epidemic.strategies.decision.PanickedDecisionStrategy;
import epidemic.strategies.decision.RationalDecisionStrategy;
import epidemic.strategies.decision.VindictiveDecisionStrategy;
import epidemic.strategies.mortality.SigmoidMortalityStrategy;
import epidemic.strategies.movement.*;

import java.util.Random;

/**
 * Główny punkt wejścia do aplikacji.
 * Pełni rolę skryptu bootstrapującego (setup/init), konfigurując mapę,
 * agentów, szpitale oraz silnik przed oddaniem kontroli do interfejsu graficznego.
 */
public class Main {
    public static void main(String[] args) {
        // 1. Inicjalizacja konfiguracji
        Config.load();
        SpeciesType.initAllFromConfig();

        // 2. Konfiguracja Świata i Wirusa
        int width = Config.getInt("world.width", 100);
        int height = Config.getInt("world.height", 100);
        double cellSize = Config.getDouble("world.cellSize", 5.0);

        WorldMap world = new WorldMap(width, height, cellSize);
        Virus virus = new Virus(
                Config.getDouble("virus.defaultProb", 0.25),
                Config.getDouble("virus.defaultRadius", 3.5),
                Config.getInt("virus.defaultDuration", 15)
        );

        AgentFactory factory = new AgentFactory();
        Random random = new Random();

        // 3. Przygotowanie Strategii
        MovementStrategy seekHospital = new SeekHospitalStrategy();
        MovementStrategy distancing = new SocialDistancingStrategy();
        MovementStrategy normalMove = new RandomWalkStrategy();
        MovementStrategy seekMate = new SeekMateStrategy();
        MovementStrategy maliciousPursuit = new MaliciousPursuitStrategy();

        // 4. Dynamiczne dodawanie Szpitali
        int hospitalCount = Config.getInt("hospital.count", 0);
        int hospitalCap = Config.getInt("hospital.capacity", 50);
        for (int i = 0; i < hospitalCount; i++) {
            int hX = Config.getInt("hospital." + i + ".x", random.nextInt(width));
            int hY = Config.getInt("hospital." + i + ".y", random.nextInt(height));
            world.addHospital(new Hospital(hospitalCap, new Point2D(hX, hY)));
        }

        // 5. Zaludnianie: Ludzie
        int humanCount = Config.getInt("pop.humans", 450);
        double rationalRatio = Config.getDouble("human.rationalRatio", 0.4);
        double panickedRatio = Config.getDouble("human.panickedRatio", 0.4);

        for (int i = 0; i < humanCount; i++) {
            Point2D pos = new Point2D(random.nextInt(width), random.nextInt(height));
            Personality personality;

            double rand = random.nextDouble();

            // Losowanie osobowości na podstawie proporcji
            if (rand < rationalRatio) {
                personality = new Personality(new RationalDecisionStrategy(
                        seekHospital, distancing, normalMove, seekMate));
            } else if (rand < rationalRatio + panickedRatio) {
                personality = new Personality(new PanickedDecisionStrategy(
                        distancing, normalMove, seekHospital, seekMate)); // DODANY CZWARTY ARGUMENT
            } else {
                // NOWE: W przeciwnym razie agent rodzi się z potencjałem bycia Mściwym!
                personality = new Personality(new VindictiveDecisionStrategy(
                        maliciousPursuit, seekHospital, normalMove));
            }

            int age = Config.getInt("human.minAge", 20) + random.nextInt(Config.getInt("human.maxAgeRange", 40));
            double speed = Config.getDouble("human.speed", 1.0);

            Human human = factory.createHuman(pos, age, speed, personality, normalMove);
            world.addAgent(human);
        }

        // 6. Zaludnianie: Zwierzęta
        spawnAnimals(world, factory, SpeciesType.BAT, Config.getInt("pop.bats", 10), true, virus, random);
        spawnAnimals(world, factory, SpeciesType.RAT, Config.getInt("pop.rats", 10), true, virus, random);
        spawnAnimals(world, factory, SpeciesType.DOG, Config.getInt("pop.dogs", 10), false, virus, random);

        // Aplikacja początkowego stanu świata
        world.applyChanges();

        // 7. Inicjalizacja Silnika i GUI
        SimulationEngine engine = new SimulationEngine(
                world, virus, new SigmoidMortalityStrategy(), factory
        );

        SimulationFrame frame = new SimulationFrame(engine, world);
        frame.start();

    }

    private static void spawnAnimals(WorldMap world, AgentFactory factory, SpeciesType type,
                                     int count, boolean startSick, Virus virus, Random random) {
        double speed = Config.getDouble("animal.speed", 1.5);
        int age = Config.getInt("animal.defaultAge", 2);

        for (int i = 0; i < count; i++) {
            Point2D pos = new Point2D(random.nextInt(world.getWidth()), random.nextInt(world.getHeight()));
            Animal animal = factory.createAnimal(pos, age, speed, type, new RandomWalkStrategy());

            if (startSick) {
                animal.setHealthStatus(HealthStatus.SICK);
                animal.setRemainingInfectionEpochs(virus.getDefaultInfectionDuration());
            }
            world.addAgent(animal);
        }
    }
}