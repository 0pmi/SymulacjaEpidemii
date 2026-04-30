package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;

/**
 * Pasywna strategia ruchu.
 * Agent z przypisaną tą strategią pozostaje w bezruchu (np. martwy, poddany kwarantannie,
 * lub w zaawansowanym stadium choroby).
 */
public class StaticStrategy implements MovementStrategy {

    /**
     * Zwraca aktualną pozycję agenta bez żadnych modyfikacji.
     */
    @Override
    public Point2D calculateNextPosition(Agent agent, WorldMap world) {
        return agent.getPosition();
    }
}