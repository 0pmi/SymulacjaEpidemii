package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.HealthStatus;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;
import epidemic.service.Config;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Złośliwa i agresywna implementacja strategii ruchu.
 * Agent przypisany do tej strategii aktywnie skanuje swoje otoczenie w poszukiwaniu
 * zdrowych jednostek, a następnie modyfikuje swój wektor ruchu tak, aby maksymalnie
 * skrócić do nich dystans i doprowadzić do zakażenia kropelkowego.
 */
public class MaliciousPursuitStrategy implements MovementStrategy {

    /**
     * Wyznacza wektor ruchu skierowany w stronę najbliższej, zdrowej ofiary.
     * Skanuje otoczenie w promieniu określonym w konfiguracji ({@code movement.malicious.radius}).
     * Mściciel obiera na celownik wyłącznie agentów o statusie {@link HealthStatus#HEALTHY}.
     * W przypadku braku potencjalnych ofiar w zasięgu wzroku, strategia aktywuje mechanizm
     * zapasowy (fallback), nakazując agentowi losowe błądzenie (oczekiwanie na cel).
     *
     * @param agent Złośliwy agent ścigający ofiary.
     * @param world Aktualny stan mapy symulacyjnej służący do weryfikacji sąsiedztwa.
     * @return Skalkulowany punkt przybliżający agenta do ofiary lub losowy krok w stanie oczekiwania.
     */
    @Override
    public Point2D calculateNextPosition(Agent agent, WorldMap world) {
        double searchRadius = Config.getDouble("movement.malicious.radius", 10.0);
        List<Agent> neighbors = world.getNeighbors(agent.getPosition(), searchRadius);

        Agent closestVictim = null;
        double minDistance = Double.MAX_VALUE;

        for (Agent neighbor : neighbors) {
            if (neighbor != agent && neighbor.getHealthStatus() == HealthStatus.HEALTHY) {
                double dist = agent.getPosition().distanceTo(neighbor.getPosition());
                if (dist < minDistance) {
                    minDistance = dist;
                    closestVictim = neighbor;
                }
            }
        }

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