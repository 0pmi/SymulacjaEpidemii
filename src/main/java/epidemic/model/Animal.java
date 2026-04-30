package epidemic.model;

import epidemic.strategies.movement.MovementStrategy;

public class Animal extends Agent {


    public Animal(Point2D position, int age, SpeciesType speciesType,
                  double baseSpeed, MovementStrategy movementStrategy) {

        super(position, age, speciesType, baseSpeed, movementStrategy);
    }
}