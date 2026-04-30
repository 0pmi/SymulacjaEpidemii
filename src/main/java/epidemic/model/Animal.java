package epidemic.model;

import epidemic.strategies.movement.MovementStrategy;
/**
 * Reprezentuje zwierzęce wektory zakażeń w symulacji (np. szczury, nietoperze, psy).
 * Klasa opiera się w głównej mierze na bazowej mechanice klasy Agent,
 * ograniczając złożone zachowania społeczne i medyczne obecne u ludzi.
 */
public class Animal extends Agent {

    public Animal(Point2D position, int age, SpeciesType speciesType,
                  double baseSpeed, MovementStrategy movementStrategy) {

        super(position, age, speciesType, baseSpeed, movementStrategy);
    }
    @Override
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder(super.getDetailedInfo());
        sb.append("\n--- Instynkt Zwierzęcy ---\n");
        sb.append("Prędkość bazowa: ").append(getBaseSpeed()).append("\n");
        return sb.toString();
    }
}