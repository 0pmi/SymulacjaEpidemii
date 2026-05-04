package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.Hospital;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;

/**
 * Strategia ruchu (wzorzec Strategy) ukierunkowująca agenta na najbliższą dostępną placówkę medyczną.
 * Wykorzystuje podejście zachłanne (greedy search) w celu identyfikacji szpitala
 * o najmniejszej odległości euklidesowej i generuje wektor ruchu bezpośrednio w jego stronę.
 */
public class SeekHospitalStrategy implements MovementStrategy {

    /**
     * Oblicza kolejny krok w kierunku najbliższej placówki szpitalnej.
     * Algorytm w każdej epoce przelicza na nowo dystans do wszystkich szpitali na mapie,
     * co pozwala na dynamiczną adaptację w przypadku pojawienia się w środowisku nowych placówek.
     * W przypadku braku jakichkolwiek szpitali, strategia nakazuje agentowi pozostanie w miejscu.
     *
     * @param agent Agent, dla którego ewaluowany jest wektor ucieczki medycznej.
     * @param world Stan mapy symulacyjnej dostarczający listę zarejestrowanych szpitali.
     * @return Nowa pozycja przesunięta o maksymalnie 1 jednostkę w osi X i Y w stronę celu.
     */
    @Override
    public Point2D calculateNextPosition(Agent agent, WorldMap world) {
        Point2D currentPos = agent.getPosition();
        Hospital nearestHospital = null;
        double minDistance = Double.MAX_VALUE;

        /* Wyszukiwanie najbliższej placówki na podstawie odległości euklidesowej. */
        for (Hospital hospital : world.getHospitals()) {
            double dist = currentPos.distanceTo(hospital.getPosition());
            if (dist < minDistance) {
                minDistance = dist;
                nearestHospital = hospital;
            }
        }

        if (nearestHospital == null) {
            return currentPos;
        }

        Point2D targetPos = nearestHospital.getPosition();

        /*
         * Wykorzystanie metody Integer.compare naturalnie sprowadza różnicę koordynatów
         * do znormalizowanego wektora ruchu o wartościach z zamkniętej puli {-1, 0, 1}.
         */
        int dx = Integer.compare(targetPos.x(), currentPos.x());
        int dy = Integer.compare(targetPos.y(), currentPos.y());

        return new Point2D(currentPos.x() + dx, currentPos.y() + dy);
    }
}