package epidemic.model;

import epidemic.strategies.movement.MovementStrategy;

import java.util.List;

/**
 * Reprezentuje zwierzęce wektory zakażeń w symulacji (np. szczury, nietoperze, psy).
 * Klasa korzysta z mechanizmów podstawowego silnika symulacyjnego, oferując
 * uproszczony schemat kognitywny – zwierzęta nie posiadają profili psychologicznych
 * i polegają bezpośrednio na standardowej lokomocji bez analizy parametrów zewnętrznych.
 */
public class Animal extends Agent {

    /**
     * Inicjalizuje nową faunę w ekosystemie na wyznaczonych parametrach fizykalnych.
     *
     * @param position Startowa pozycja zwierzęcia w dwuwymiarowym gridzie.
     * @param age Wiek organizmu.
     * @param speciesType Determinuje typ gatunku od którego zależna jest zjadliwość transmisji wirusa.
     * @param baseSpeed Indywidualna prędkość relokacji w trakcie przemieszczania.
     * @param movementStrategy Pasywna strategia instynktowna ruchu (np. zwykłe błądzenie i unikanie marginesów).
     */
    public Animal(Point2D position, int age, SpeciesType speciesType,
                  double baseSpeed, MovementStrategy movementStrategy) {

        super(position, age, speciesType, baseSpeed, movementStrategy);
    }

    /**
     * Rozszerza podstawowe inspekcje GUI agenta dla zdefiniowanej instancji fauny,
     * dodając odczyty związane z unikalnymi wartościami takimi jak prędkość bazowa.
     *
     * @return Uporządkowana lista obiektów InspectionProperty.
     */
    @Override
    public List<InspectionProperty> getInspectionProperties() {
        List<InspectionProperty> props = super.getInspectionProperties();

        if (!isDead()) {
            props.add(InspectionProperty.text("Prędkość bazowa", String.valueOf(getBaseSpeed())));
        }

        return props;
    }
}