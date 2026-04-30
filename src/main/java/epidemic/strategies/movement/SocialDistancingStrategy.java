package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class SocialDistancingStrategy implements MovementStrategy {

    private final double perceptionRadius = 5.0;

    @Override
    public Point2D calculateNextPosition(Agent agent, WorldMap world) {
        Point2D currentPos = agent.getPosition();
        List<Agent> neighbors = world.getNeighbors(currentPos, perceptionRadius);

        if (neighbors.isEmpty()) {
            int dx = ThreadLocalRandom.current().nextInt(3) - 1;
            int dy = ThreadLocalRandom.current().nextInt(3) - 1;
            return new Point2D(currentPos.x() + dx, currentPos.y() + dy);
        }

        double fleeVectorX = 0;
        double fleeVectorY = 0;

        for (Agent neighbor : neighbors) {
            fleeVectorX += (currentPos.x() - neighbor.getPosition().x());
            fleeVectorY += (currentPos.y() - neighbor.getPosition().y());
        }

        int dx = (int) Math.signum(fleeVectorX);
        int dy = (int) Math.signum(fleeVectorY);

        if (dx == 0 && dy == 0) {
            dx = ThreadLocalRandom.current().nextInt(3) - 1;
        }

        return new Point2D(currentPos.x() + dx, currentPos.y() + dy);
    }
}