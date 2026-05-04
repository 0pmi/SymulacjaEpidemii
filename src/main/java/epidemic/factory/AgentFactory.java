package epidemic.factory;

import epidemic.model.*;
import epidemic.service.Config;
import epidemic.strategies.movement.MovementStrategy;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Fabryka odpowiedzialna za spójne tworzenie instancji agentów (wzorzec Abstract Factory).
 * Zapewnia scentralizowane miejsce inicjalizacji parametrów początkowych, takich jak wiek,
 * prędkość bazowa, przypisanie strategii ruchu czy losowanie unikalnej osobowości.
 */
public class AgentFactory {

    private final PersonalityFactory personalityFactory;

    /**
     * Inicjalizuje fabrykę agentów.
     *
     * @param personalityFactory Fabryka dostarczająca profile psychologiczne dla nowo tworzonych ludzi.
     */
    public AgentFactory(PersonalityFactory personalityFactory) {
        this.personalityFactory = personalityFactory;
    }

    /**
     * Tworzy nowego ludzkiego agenta z pełnym profilem psychologicznym.
     *
     * @param pos Pozycja startowa na mapie.
     * @param age Początkowy wiek agenta.
     * @param baseSpeed Bazowa prędkość poruszania się.
     * @param personality Profil osobowości decydujący o reakcjach agenta na zagrożenie.
     * @param strategy Domyślna strategia poruszania się w stanie spokoju.
     * @return Gotowa do dodania na mapę instancja Human.
     */
    public Human createHuman(Point2D pos, int age, double baseSpeed, Personality personality, MovementStrategy strategy) {
        return new Human(pos, age, baseSpeed, Config.getDouble("human.initialResistance", 0.1), personality, strategy);
    }

    /**
     * Tworzy nowego agenta zwierzęcego określonego gatunku.
     *
     * @param pos Pozycja startowa na mapie.
     * @param age Początkowy wiek zwierzęcia.
     * @param baseSpeed Bazowa prędkość poruszania się.
     * @param type Konkretny gatunek zwierzęcia (np. BAT, RAT, DOG).
     * @param strategy Domyślna strategia poruszania się w stanie spokoju.
     * @return Gotowa do dodania na mapę instancja Animal.
     */
    public Animal createAnimal(Point2D pos, int age, double baseSpeed, SpeciesType type, MovementStrategy strategy) {
        return new Animal(pos, age, type, baseSpeed, strategy);
    }

    /**
     * Rozwiązuje logikę dziedziczenia i tworzy potomka dla podanej pary agentów.
     * Metoda polimorficzna – deleguje tworzenie do odpowiednich podmetod w zależności
     * od typu rodziców (Human/Animal).
     *
     * @param parentA Pierwszy rodzic biorący udział w rozrodzie.
     * @param parentB Drugi rodzic biorący udział w rozrodzie.
     * @return Nowy agent z parametrami odziedziczonymi po rodzicach.
     * @throws IllegalArgumentException Jeśli gatunki rodziców są niezgodne.
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
        Personality childPersonality = personalityFactory.generateRandomPersonality();

        return new Human(pos, 0, a.getBaseSpeed(), Config.getDouble("human.offspringResistance", 1.0), childPersonality, strategy);
    }

    private Animal createAnimalOffspring(Point2D pos, SpeciesType type, Animal a, MovementStrategy strategy) {
        return new Animal(pos, 0, type, a.getBaseSpeed(), strategy);
    }
}