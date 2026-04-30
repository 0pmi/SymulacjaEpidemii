package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;
import epidemic.service.Config;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Strategia realizująca nakaz dystansowania społecznego.
 * Agent analizuje swoje otoczenie i próbuje poruszać się w kierunku przeciwnym
 * do zaobserwowanych w pobliżu innych agentów (algorytm wektora ucieczki).
 */
public class SocialDistancingStrategy implements MovementStrategy {

    private final double perceptionRadius = Config.getDouble("movement.distancing.radius", 5.0);

    @Override
    public Point2D calculateNextPosition(Agent agent, WorldMap world) {
        Point2D currentPos = agent.getPosition();
        List<Agent> neighbors = world.getNeighbors(currentPos, perceptionRadius);

        // Jeśli wokół nie ma nikogo, zachowuj się jak w trybie błądzenia losowego
        if (neighbors.isEmpty()) {
            int dx = ThreadLocalRandom.current().nextInt(3) - 1;
            int dy = ThreadLocalRandom.current().nextInt(3) - 1;
            return new Point2D(currentPos.x() + dx, currentPos.y() + dy);
        }

        double fleeVectorX = 0;
        double fleeVectorY = 0;

        // Sumowanie wektorów skierowanych OD sąsiadów DO bieżącego agenta
        for (Agent neighbor : neighbors) {
            fleeVectorX += (currentPos.x() - neighbor.getPosition().x());
            fleeVectorY += (currentPos.y() - neighbor.getPosition().y());
        }

        // Normalizacja do pojedynczych kroków dyskretnych na siatce (wartości -1, 0 lub 1)
        int dx = (int) Math.signum(fleeVectorX);
        int dy = (int) Math.signum(fleeVectorY);

        // Zapobieganie utknięciu w martwym punkcie, gdy wektory ucieczki idealnie się znoszą
        if (dx == 0 && dy == 0) {
            dx = ThreadLocalRandom.current().nextInt(3) - 1;
        }

        return new Point2D(currentPos.x() + dx, currentPos.y() + dy);
    }
}