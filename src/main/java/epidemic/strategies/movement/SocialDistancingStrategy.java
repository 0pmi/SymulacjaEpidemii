package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;
import epidemic.service.Config;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Strategia ruchu realizująca nakaz dystansowania społecznego.
 *
 * Wykorzystuje algorytm pól potencjałów (Potential Fields) do generowania sił
 * odpychających od innych agentów oraz od granic mapy, co minimalizuje zjawisko
 * agregacji agentów w narożnikach i na obrzeżach symulacji.
 */
public class SocialDistancingStrategy implements MovementStrategy {

    private final double perceptionRadius = Config.getDouble("movement.distancing.radius", 5.0);
    private final double edgeMargin = Config.getDouble("movement.boundary.margin", 3.0);

    /** Wagi sił wpływające na priorytetyzację kierunku ruchu. */
    private final double socialForceWeight = Config.getDouble("movement.social.weight", 1.0);
    private final double boundaryForceWeight = Config.getDouble("movement.boundary.weight", 2.5);

    /** Rekord pomocniczy reprezentujący wektor siły o wysokiej precyzji. */
    private record ForceVector(double x, double y) {}

    @Override
    public Point2D calculateNextPosition(Agent agent, WorldMap world) {
        Point2D currentPos = agent.getPosition();
        List<Agent> neighbors = world.getNeighbors(currentPos, perceptionRadius);

        double totalX = 0;
        double totalY = 0;

        // 1. Obliczanie sił społecznych (repulsja od sąsiadów)
        if (neighbors != null && !neighbors.isEmpty()) {
            for (Agent neighbor : neighbors) {
                // Wektor ucieczki: (Pozycja_Bieżąca - Pozycja_Sąsiada)
                totalX += (currentPos.x() - neighbor.getPosition().x()) * socialForceWeight;
                totalY += (currentPos.y() - neighbor.getPosition().y()) * socialForceWeight;
            }
        }

        // 2. Obliczanie sił granicznych (repulsja od krawędzi mapy)
        ForceVector boundaryForce = calculateBoundaryRepulsion(currentPos, world);
        totalX += boundaryForce.x() * boundaryForceWeight;
        totalY += boundaryForce.y() * boundaryForceWeight;

        // 3. Normalizacja i wyznaczenie kierunku ruchu
        int dx = 0;
        int dy = 0;

        // Jeśli siły wypadkowe są zauważalne, wyznaczamy kierunek
        if (Math.abs(totalX) > 0.001 || Math.abs(totalY) > 0.001) {
            dx = (int) Math.signum(totalX);
            dy = (int) Math.signum(totalY);
        } else {
            // W przypadku braku bodźców (równowaga sił), wykonujemy błądzenie losowe
            dx = ThreadLocalRandom.current().nextInt(3) - 1;
            dy = ThreadLocalRandom.current().nextInt(3) - 1;
        }

        Point2D targetPos = new Point2D(currentPos.x() + dx, currentPos.y() + dy);
        return clampToWorldBounds(targetPos, world);
    }

    private ForceVector calculateBoundaryRepulsion(Point2D pos, WorldMap world) {
        double fx = 0;
        double fy = 0;

        if (pos.x() < edgeMargin) {
            fx = (edgeMargin - pos.x()) / edgeMargin;
        } else if (pos.x() > world.getWidth() - edgeMargin) {
            fx = -(pos.x() - (world.getWidth() - edgeMargin)) / edgeMargin;
        }

        if (pos.y() < edgeMargin) {
            fy = (edgeMargin - pos.y()) / edgeMargin;
        } else if (pos.y() > world.getHeight() - edgeMargin) {
            fy = -(pos.y() - (world.getHeight() - edgeMargin)) / edgeMargin;
        }

        return new ForceVector(fx, fy);
    }

    private Point2D clampToWorldBounds(Point2D pos, WorldMap world) {
        int x = Math.max(0, Math.min(world.getWidth() - 1, pos.x()));
        int y = Math.max(0, Math.min(world.getHeight() - 1, pos.y()));
        return new Point2D(x, y);
    }
}