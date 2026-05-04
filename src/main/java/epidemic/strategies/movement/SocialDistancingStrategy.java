package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;
import epidemic.service.Config;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Zaawansowana strategia ruchu (wzorzec Strategy) realizująca protokoły dystansowania społecznego.
 * Wykorzystuje algorytm pól potencjałów (Potential Fields) do generowania
 * dynamicznych wektorów sił odpychających. Agent poddawany jest repulsji zarówno
 * ze strony innych jednostek, jak i samych granic mapy, co skutecznie zapobiega
 * sztucznemu zjawisku agregacji i blokowania się tłumu w narożnikach symulacji.
 */
public class SocialDistancingStrategy implements MovementStrategy {

    private final double perceptionRadius = Config.getDouble("movement.distancing.radius", 5.0);
    private final double edgeMargin = Config.getDouble("movement.boundary.margin", 3.0);

    /** Wagi sił wpływające na priorytetyzację kierunku ruchu. */
    private final double socialForceWeight = Config.getDouble("movement.social.weight", 1.0);
    private final double boundaryForceWeight = Config.getDouble("movement.boundary.weight", 2.5);

    /**
     * Wewnętrzna struktura danych (DTO) reprezentująca wektor siły o wysokiej precyzji zmiennoprzecinkowej,
     * używana podczas sumowania repulsywnych wpływów środowiskowych.
     *
     * @param x Składowa pozioma wektora siły.
     * @param y Składowa pionowa wektora siły.
     */
    private record ForceVector(double x, double y) {}

    /**
     * Oblicza nową pozycję agenta na podstawie bilansu sił w środowisku.
     * Algorytm sumuje wektory ucieczki od wszystkich sąsiadów w promieniu percepcji
     * oraz wektory odpychające od krawędzi mapy, uwzględniając konfigurację wag.
     * Wypadkowa siła jest następnie normalizowana do pojedynczego kroku. W przypadku
     * idealnej równowagi sił (brak dominujących bodźców), agent wykonuje losowy krok,
     * aby uniknąć uwięzienia w lokalnym minimum.
     *
     * @param agent Agent poddawany działaniu sił dystansowania społecznego.
     * @param world Stan mapy symulacyjnej pozwalający na odczyt pozycji sąsiadów i granic.
     * @return Znormalizowana pozycja docelowa, bezpiecznie ograniczona do obszaru mapy.
     */
    @Override
    public Point2D calculateNextPosition(Agent agent, WorldMap world) {
        Point2D currentPos = agent.getPosition();
        List<Agent> neighbors = world.getNeighbors(currentPos, perceptionRadius);

        double totalX = 0;
        double totalY = 0;

        if (neighbors != null && !neighbors.isEmpty()) {
            for (Agent neighbor : neighbors) {
                totalX += (currentPos.x() - neighbor.getPosition().x()) * socialForceWeight;
                totalY += (currentPos.y() - neighbor.getPosition().y()) * socialForceWeight;
            }
        }

        ForceVector boundaryForce = calculateBoundaryRepulsion(currentPos, world);
        totalX += boundaryForce.x() * boundaryForceWeight;
        totalY += boundaryForce.y() * boundaryForceWeight;

        int dx = 0;
        int dy = 0;

        if (Math.abs(totalX) > 0.001 || Math.abs(totalY) > 0.001) {
            dx = (int) Math.signum(totalX);
            dy = (int) Math.signum(totalY);
        } else {
            dx = ThreadLocalRandom.current().nextInt(3) - 1;
            dy = ThreadLocalRandom.current().nextInt(3) - 1;
        }

        Point2D targetPos = new Point2D(currentPos.x() + dx, currentPos.y() + dy);
        return clampToWorldBounds(targetPos, world);
    }

    /*
     * Oblicza siłę odpychającą od granic mapy na podstawie zdefiniowanego marginesu.
     * Im agent znajduje się bliżej krawędzi, tym siła repulsji staje się silniejsza,
     * zapobiegając wychodzeniu poza dozwolony obszar.
     */
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

    /*
     * Funkcja pomocnicza przycinająca (clamp) ostateczne koordynaty do bezwzględnych granic mapy,
     * co zapewnia bezpieczeństwo przed błędami indeksowania.
     */
    private Point2D clampToWorldBounds(Point2D pos, WorldMap world) {
        int x = Math.max(0, Math.min(world.getWidth() - 1, pos.x()));
        int y = Math.max(0, Math.min(world.getHeight() - 1, pos.y()));
        return new Point2D(x, y);
    }
}