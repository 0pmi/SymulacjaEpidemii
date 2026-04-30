package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.Hospital;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;

/**
 * Strategia kierująca agenta do najbliższej dostępnej placówki medycznej.
 * Agent w każdej epoce przelicza dystans do wszystkich szpitali i wykonuje
 * jeden krok w stronę tego, który znajduje się najbliżej w linii prostej.
 */
public class SeekHospitalStrategy implements MovementStrategy {

    /**
     * Oblicza kolejny krok w kierunku najbliższego szpitala.
     * Jeśli na mapie nie ma szpitali, agent pozostaje w miejscu.
     *
     * @param agent Agent, dla którego obliczany jest ruch.
     * @param world Mapa świata dostarczająca listę dostępnych szpitali.
     * @return Nowa pozycja przesunięta o maksymalnie 1 pole w stronę celu.
     */
    @Override
    public Point2D calculateNextPosition(Agent agent, WorldMap world) {
        Point2D currentPos = agent.getPosition();
        Hospital nearestHospital = null;
        double minDistance = Double.MAX_VALUE;

        // Znalezienie najbliższego szpitala
        for (Hospital hospital : world.getHospitals()) {
            double dist = currentPos.distanceTo(hospital.getPosition());
            if (dist < minDistance) {
                minDistance = dist;
                nearestHospital = hospital;
            }
        }

        // Zabezpieczenie na wypadek braku szpitali na mapie
        if (nearestHospital == null) {
            return currentPos;
        }

        Point2D targetPos = nearestHospital.getPosition();

        // Integer.compare naturalnie sprowadza różnicę odległości do wartości z puli {-1, 0, 1}
        int dx = Integer.compare(targetPos.x(), currentPos.x());
        int dy = Integer.compare(targetPos.y(), currentPos.y());

        return new Point2D(currentPos.x() + dx, currentPos.y() + dy);
    }
}