package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.HealthStatus;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;
import epidemic.service.Config;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Strategia kierująca agenta w stronę najbliższego potencjalnego partnera do rozrodu.
 */
public class SeekMateStrategy implements MovementStrategy {

    @Override
    public Point2D calculateNextPosition(Agent agent, WorldMap world) {
        double searchRadius = Config.getDouble("movement.seekMate.radius", 15.0);
        List<Agent> neighbors = world.getNeighbors(agent.getPosition(), searchRadius);

        Agent closestMate = null;
        double minDistance = Double.MAX_VALUE;
        int requiredAge = agent.getSpeciesType().getMaturityAge();

        for (Agent neighbor : neighbors) {
            // Szuka dorosłego, zdrowego partnera tego samego gatunku
            if (neighbor != agent &&
                    neighbor.getSpeciesType() == agent.getSpeciesType() &&
                    neighbor.getAge() >= requiredAge &&
                    neighbor.getHealthStatus() == HealthStatus.HEALTHY) {

                double dist = agent.getPosition().distanceTo(neighbor.getPosition());
                if (dist < minDistance) {
                    minDistance = dist;
                    closestMate = neighbor;
                }
            }
        }

        // Jeśli w pobliżu nie ma partnerów, powrót do błądzenia losowego
        if (closestMate == null) {
            int range = Config.getInt("movement.random.stepRange", 3);
            int dx = ThreadLocalRandom.current().nextInt(range) - (range / 2);
            int dy = ThreadLocalRandom.current().nextInt(range) - (range / 2);
            return new Point2D(agent.getPosition().x() + dx, agent.getPosition().y() + dy);
        }

        // Kieruje się prosto na partnera
        int dx = Integer.compare(closestMate.getPosition().x(), agent.getPosition().x());
        int dy = Integer.compare(closestMate.getPosition().y(), agent.getPosition().y());

        return new Point2D(agent.getPosition().x() + dx, agent.getPosition().y() + dy);
    }
}