package epidemic.model;

import epidemic.strategies.movement.MovementStrategy;

public class Animal extends Agent {

    private double speciesVirulence;

    public Animal(Point2D position, int age, SpeciesType speciesType, double baseSpeed,
                  double speciesVirulence, MovementStrategy movementStrategy) {

        super(position, age, speciesType, baseSpeed, movementStrategy);
        this.speciesVirulence = speciesVirulence;
    }

    public double getSpeciesVirulence() { return speciesVirulence; }
    public void setSpeciesVirulence(double speciesVirulence) { this.speciesVirulence = speciesVirulence; }
}