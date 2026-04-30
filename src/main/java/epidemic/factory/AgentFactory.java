package epidemic.factory;

import epidemic.model.*;
import epidemic.strategies.movement.MovementStrategy;
import java.util.concurrent.ThreadLocalRandom;

public class AgentFactory {
    public Human createHuman(Point2D pos, int age, double baseSpeed, Personality personality, MovementStrategy strategy) {
        return new Human(pos, age, baseSpeed,0.1, personality, strategy);
    }

    public Animal createAnimal(Point2D pos, int age, double baseSpeed, SpeciesType type, MovementStrategy strategy) {
        return new Animal(pos, age, type, baseSpeed, strategy);
    }

    public Agent createOffspring(Agent parentA, Agent parentB) {
        Point2D birthPos = parentA.getPosition();
        SpeciesType type = parentA.getSpeciesType();
        MovementStrategy childStrategy = parentA.getMovementStrategy(); // Dziecko uczy się od rodzica A ;)

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
        return new Human(pos, 0, a.getBaseSpeed(), 1.0, childPersonality, strategy);
    }

    private Animal createAnimalOffspring(Point2D pos, SpeciesType type, Animal a, MovementStrategy strategy) {
        return new Animal(pos, 0, type, a.getBaseSpeed(), strategy);
    }
}