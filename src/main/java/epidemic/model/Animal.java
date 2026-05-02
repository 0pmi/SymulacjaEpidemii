package epidemic.model;

import epidemic.strategies.movement.MovementStrategy;

import java.util.List;

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
    public List<InspectionProperty> getInspectionProperties() {
        // Generujemy listę dziedziczoną z klasy Agent
        List<InspectionProperty> props = super.getInspectionProperties();

        // Uzupełniamy tylko, jeśli zwierzę żyje
        if (!isDead()) {
            props.add(InspectionProperty.text("Prędkość bazowa", String.valueOf(getBaseSpeed())));
        }

        return props;
    }
}