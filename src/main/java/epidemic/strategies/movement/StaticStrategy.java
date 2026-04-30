package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;

public class StaticStrategy implements MovementStrategy {

    @Override
    public Point2D calculateNextPosition(Agent agent, WorldMap world) {
        return agent.getPosition();
    }
}