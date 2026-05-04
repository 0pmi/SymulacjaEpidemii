package epidemic.managers;

import epidemic.model.*;

/**
 * Moduł odpowiadający za przemieszczanie podmiotów w przestrzeni symulacji.
 * Wykorzystuje polimorfizm i wzorzec Strategii (Strategy) przypisany do każdego agenta
 * do wyznaczenia optymalnego wektora przesunięcia, z zachowaniem spójności
 * i granic obszaru mapy.
 */
public class MovementManager {

    /**
     * Główna metoda przetwarzająca ruch wszystkich agentów w pojedynczej epoce.
     * Ignoruje agentów aktualnie hospitalizowanych. Zabezpiecza przed opuszczeniem
     * dozwolonego obszaru mapy, a po przeliczeniu wszystkich wektorów wymusza
     * krytyczną aktualizację indeksu przestrzennego (np. drzewa/siatki wyszukiwań).
     *
     * @param world Stan mapy symulacyjnej udostępniający kolekcję agentów oraz granice.
     */
    public void moveAgents(WorldMap world) {
        for (Agent agent : world.getAgents()) {
            // Agenci znajdujący się na oddziale szpitalnym są wyłączeni z mechaniki ruchu
            if (agent instanceof HospitalUser user && user.isInHospital()) {
                continue;
            }

            Point2D nextPos = agent.getMovementStrategy().calculateNextPosition(agent, world);

            if (world.isWithinBounds(nextPos)) {
                agent.setPosition(nextPos);
            }

            checkHospitalInteraction(agent, world);
        }

        world.rebuildSpatialIndex();
    }

    /*
     * Sprawdza, czy po wykonaniu ruchu agent znajduje się na polu placówki medycznej
     * i posiada flagę chęci hospitalizacji. Jeśli warunki są spełnione, podejmuje próbę
     * wpisania pacjenta na oddział.
     */
    private void checkHospitalInteraction(Agent agent, WorldMap world) {
        if (agent instanceof HospitalUser user) {
            Hospital hospital = world.getHospitalAt(user.getPosition());

            if (hospital != null && user.isWantsHospital()) {
                if (hospital.addPatient(user)) {
                    user.setIsInHospital(true);
                }
            }
        }
    }
}