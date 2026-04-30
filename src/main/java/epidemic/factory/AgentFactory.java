package epidemic.factory;

import epidemic.model.*;
import epidemic.service.Config;
import epidemic.strategies.movement.MovementStrategy;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Fabryka odpowiedzialna za centralizację logiki tworzenia nowych instancji agentów.
 * Upraszcza inicjalizację obiektów oraz logikę dziedziczenia cech podczas rozmnażania.
 */
public class AgentFactory {

    public Human createHuman(Point2D pos, int age, double baseSpeed, Personality personality, MovementStrategy strategy) {
        return new Human(pos, age, baseSpeed, Config.getDouble("human.initialResistance", 0.1), personality, strategy);
    }

    public Animal createAnimal(Point2D pos, int age, double baseSpeed, SpeciesType type, MovementStrategy strategy) {
        return new Animal(pos, age, type, baseSpeed, strategy);
    }

    /**
     * Generuje nowego agenta na podstawie parametrów pary rodziców.
     * Potomstwo dziedziczy pozycję startową, prędkość bazową, rodzaj strategii poruszania się
     * oraz (w przypadku ludzi) osobowość jednego z rodziców.
     *
     * @param parentA Pierwszy rodzic (inicjator).
     * @param parentB Drugi rodzic.
     * @return Nowa instancja agenta lub null, jeśli krzyżówka jest niedozwolona.
     */
    public Agent createOffspring(Agent parentA, Agent parentB) {
        Point2D birthPos = parentA.getPosition();
        SpeciesType type = parentA.getSpeciesType();
        MovementStrategy childStrategy = parentA.getMovementStrategy();

        if (parentA instanceof Human humanA && parentB instanceof Human humanB) {
            return createHumanOffspring(birthPos, humanA, humanB, childStrategy);
        } else if (parentA instanceof Animal animalA) {
            return createAnimalOffspring(birthPos, type, animalA, childStrategy);
        }

        return null;
    }

    private Human createHumanOffspring(Point2D pos, Human a, Human b, MovementStrategy strategy) {
        Personality childPersonality = ThreadLocalRandom.current().nextBoolean() ?
                a.getPersonality() : b.getPersonality();
        return new Human(pos, 0, a.getBaseSpeed(), Config.getDouble("human.offspringResistance", 1.0), childPersonality, strategy);
    }

    private Animal createAnimalOffspring(Point2D pos, SpeciesType type, Animal a, MovementStrategy strategy) {
        return new Animal(pos, 0, type, a.getBaseSpeed(), strategy);
    }
}