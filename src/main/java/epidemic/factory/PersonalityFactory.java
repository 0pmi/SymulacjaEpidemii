package epidemic.factory;

import epidemic.model.Personality;
import epidemic.service.Config;
import epidemic.strategies.decision.*;
import epidemic.strategies.movement.MovementStrategy;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Fabryka odpowiedzialna za kreowanie osobowości (strategii decyzyjnych) agentów.
 * Centralizuje logikę losowania cech na podstawie proporcji zdefiniowanych w konfiguracji.
 */
public class PersonalityFactory {

    private final MovementStrategy seekHospital;
    private final MovementStrategy distancing;
    private final MovementStrategy normalMove;
    private final MovementStrategy seekMate;
    private final MovementStrategy maliciousPursuit;

    public PersonalityFactory(MovementStrategy seekHospital, MovementStrategy distancing,
                              MovementStrategy normalMove, MovementStrategy seekMate,
                              MovementStrategy maliciousPursuit) {
        this.seekHospital = seekHospital;
        this.distancing = distancing;
        this.normalMove = normalMove;
        this.seekMate = seekMate;
        this.maliciousPursuit = maliciousPursuit;
    }

    /**
     * Generuje nową osobowość bazując na rozkładzie prawdopodobieństwa z pliku Config.
     */
    public Personality generateRandomPersonality() {
        double rationalRatio = Config.getDouble("human.rationalRatio", 0.4);
        double panickedRatio = Config.getDouble("human.panickedRatio", 0.4);
        double rand = ThreadLocalRandom.current().nextDouble();

        if (rand < rationalRatio) {
            return new Personality(new RationalDecisionStrategy(
                    seekHospital, distancing, normalMove, seekMate));
        } else if (rand < rationalRatio + panickedRatio) {
            return new Personality(new PanickedDecisionStrategy(
                    distancing, normalMove, seekHospital, seekMate));
        } else {
            return new Personality(new VindictiveDecisionStrategy(
                    maliciousPursuit, seekHospital, normalMove));
        }
    }
}