package epidemic.strategies.movement;

import epidemic.model.*;
import java.util.concurrent.ThreadLocalRandom;

public class RandomWalkStrategy implements MovementStrategy {
    @Override
    public Point2D calculateNextPosition(Agent agent, WorldMap world) {
        Point2D current = agent.getPosition();
        int dx = ThreadLocalRandom.current().nextInt(3) - 1;
        int dy = ThreadLocalRandom.current().nextInt(3) - 1;

        return new Point2D(current.x() + dx, current.y() + dy);
    }
}