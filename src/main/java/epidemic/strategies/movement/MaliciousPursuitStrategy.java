package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.HealthStatus;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;
import epidemic.service.Config;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Strategia złośliwa. Agent aktywnie poszukuje zdrowych jednostek, aby się do nich zbliżyć i je zarazić.
 */
public class MaliciousPursuitStrategy implements MovementStrategy {

    @Override
    public Point2D calculateNextPosition(Agent agent, WorldMap world) {
        double searchRadius = Config.getDouble("movement.malicious.radius", 10.0);
        List<Agent> neighbors = world.getNeighbors(agent.getPosition(), searchRadius);

        Agent closestVictim = null;
        double minDistance = Double.MAX_VALUE;

        for (Agent neighbor : neighbors) {
            // Mściciel szuka na celownik wyłącznie zdrowych ofiar
            if (neighbor != agent && neighbor.getHealthStatus() == HealthStatus.HEALTHY) {
                double dist = agent.getPosition().distanceTo(neighbor.getPosition());
                if (dist < minDistance) {
                    minDistance = dist;
                    closestVictim = neighbor;
                }
            }
        }

        // Brak ofiar - błąka się i czeka
        if (closestVictim == null) {
            int range = Config.getInt("movement.random.stepRange", 3);
            int dx = ThreadLocalRandom.current().nextInt(range) - (range / 2);
            int dy = ThreadLocalRandom.current().nextInt(range) - (range / 2);
            return new Point2D(agent.getPosition().x() + dx, agent.getPosition().y() + dy);
        }

        int dx = Integer.compare(closestVictim.getPosition().x(), agent.getPosition().x());
        int dy = Integer.compare(closestVictim.getPosition().y(), agent.getPosition().y());

        return new Point2D(agent.getPosition().x() + dx, agent.getPosition().y() + dy);
    }
}