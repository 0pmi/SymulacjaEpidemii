package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;
import epidemic.service.Config;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Podstawowa strategia ruchu (wzorzec Strategy) realizująca algorytm błądzenia losowego (Random Walk).
 * Agent z przypisaną tą strategią wybiera kierunek wektora przesunięcia całkowicie
 * stochastycznie, w ramach zakresu zdefiniowanego w globalnej konfiguracji systemu.
 */
public class RandomWalkStrategy implements MovementStrategy {

    /**
     * Wylicza nową, stochastyczną pozycję agenta.
     * Algorytm korzysta ze zmiennej konfiguracyjnej {@code movement.random.stepRange},
     * centrując losowane wartości wokół zera, co gwarantuje możliwość ruchu
     * w każdym z ośmiu kierunków lub pozostania w miejscu.
     *
     * @param agent Agent poddawany losowemu przemieszczeniu.
     * @param world Stan mapy symulacyjnej (nieużywany bezpośrednio w tej strategii,
     *              lecz wymagany przez uniwersalny kontrakt interfejsu).
     * @return Skalkulowana nowa pozycja w dwuwymiarowej przestrzeni mapy.
     */
    @Override
    public Point2D calculateNextPosition(Agent agent, WorldMap world) {
        Point2D current = agent.getPosition();
        int range = Config.getInt("movement.random.stepRange", 3);

        /*
         * Operacja matematyczna centrująca losowany przedział.
         * Dla domyślnego zakresu 3, operacja (0..2) - 1 generuje idealnie zbilansowaną pulę kroków {-1, 0, 1}.
         */
        int dx = ThreadLocalRandom.current().nextInt(range) - (range / 2);
        int dy = ThreadLocalRandom.current().nextInt(range) - (range / 2);

        return new Point2D(current.x() + dx, current.y() + dy);
    }
}