package epidemic;

import epidemic.engine.SimulationEngine;
import epidemic.factory.AgentFactory;
import epidemic.gui.SimulationFrame;
import epidemic.model.*;
import epidemic.strategies.decision.PanickedDecisionStrategy;
import epidemic.strategies.decision.RationalDecisionStrategy;
import epidemic.strategies.mortality.SigmoidMortalityStrategy;
import epidemic.strategies.movement.*;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // 1. Konfiguracja Świata i Wirusa
        WorldMap world = new WorldMap(100, 100, 5.0);
        Virus virus = new Virus(0.25, 8, 15);
        AgentFactory factory = new AgentFactory();
        Random random = new Random();

        // 2. Przygotowanie Strategii Ruchu
        MovementStrategy seekHospital = new SeekHospitalStrategy();
        MovementStrategy distancing = new SocialDistancingStrategy();
        MovementStrategy normalMove = new RandomWalkStrategy();

        // 3. Dodanie Szpitali
        world.addHospital(new Hospital(50, new Point2D(20, 20)));
        world.addHospital(new Hospital(50, new Point2D(80, 80)));

        // 4. Zaludnianie Świata
        for (int i = 0; i < 200; i++) {
            Point2D pos = new Point2D(random.nextInt(100), random.nextInt(100));
            Personality personality;
            if (i % 2 == 0) {
                personality = new Personality(new RationalDecisionStrategy(seekHospital, distancing, normalMove));
            } else {
                personality = new Personality(new PanickedDecisionStrategy(distancing, normalMove, seekHospital));
            }

            Human human = factory.createHuman(pos, 20 + random.nextInt(40), 1.0, personality, normalMove);
            world.addAgent(human);
        }

        // 5. Dodanie Zwierząt
        for (int i = 0; i < 10; i++) {
            Point2D pos = new Point2D(random.nextInt(100), random.nextInt(100));
            Animal bat = factory.createAnimal(pos, 2, 1.5, SpeciesType.BAT, normalMove);

            // Pacjent Zero - jeden z nietoperzy jest zarażony
            if (i < 5) {
                bat.setHealthStatus(HealthStatus.SICK);
                bat.setRemainingInfectionEpochs(virus.getDefaultInfectionDuration());
            }
            world.addAgent(bat);
        }

        world.applyChanges();

        // 6. Inicjalizacja Silnika
        SimulationEngine engine = new SimulationEngine(
                world,
                virus,
                new SigmoidMortalityStrategy(),
                factory
        );

        // 7. Pętla Symulacji
        SimulationFrame frame = new SimulationFrame(engine, world);
        frame.start();

    }
}