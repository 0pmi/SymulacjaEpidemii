package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;
import epidemic.service.Config;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Podstawowa strategia ruchu realizująca błądzenie losowe (Random Walk).
 * Agent wybiera kierunek całkowicie przypadkowo w ramach skonfigurowanego zakresu.
 */
public class RandomWalkStrategy implements MovementStrategy {

    /**
     * Wylicza nową pozycję na podstawie losowego przesunięcia.
     * Wykorzystuje zmienną "movement.random.stepRange" z konfiguracji globalnej
     * (domyślnie 3, co oznacza przesunięcie w osi o -1, 0 lub 1).
     */
    @Override
    public Point2D calculateNextPosition(Agent agent, WorldMap world) {
        Point2D current = agent.getPosition();
        int range = Config.getInt("movement.random.stepRange", 3);

        // Zapewnia przesunięcie względem środka zakresu, np. dla range=3 daje liczby z puli {-1, 0, 1}
        int dx = ThreadLocalRandom.current().nextInt(range) - (range / 2);
        int dy = ThreadLocalRandom.current().nextInt(range) - (range / 2);

        return new Point2D(current.x() + dx, current.y() + dy);
    }
}