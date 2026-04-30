package epidemic.managers;

import epidemic.model.*;

/**
 * Menedżer odpowiedzialny za koordynowanie fizycznego przemieszczania się agentów na mapie.
 * Weryfikuje granice świata, przetwarza kolizje z infrastrukturą (np. wejście do szpitala)
 * oraz deleguje samo wyliczenie kroku do strategii ruchu przypisanej do konkretnego agenta.
 */
public class MovementManager {

    /**
     * Główna metoda przetwarzająca ruch wszystkich agentów w pojedynczej epoce.
     * Na koniec aktualizuje indeks przestrzenny na mapie, aby odzwierciedlić nowe pozycje.
     *
     * @param world Stan mapy symulacyjnej.
     */
    public void moveAgents(WorldMap world) {
        for (Agent agent : world.getAgents()) {
            // Agenci znajdujący się na oddziale szpitalnym są wyłączeni z mechaniki ruchu
            if (agent instanceof HospitalUser user && user.isInHospital()) {
                continue;
            }

            Point2D nextPos = agent.getMovementStrategy().calculateNextPosition(agent, world);

            // Weryfikacja, czy strategia nie wygenerowała kroku poza dozwolony obszar
            if (world.isWithinBounds(nextPos)) {
                agent.setPosition(nextPos);
            }

            checkHospitalInteraction(agent, world);
        }

        // Krytyczny krok: po aktualizacji wszystkich pozycji należy przebudować drzewo/siatkę wyszukiwań
        world.rebuildSpatialIndex();
    }

    private void checkHospitalInteraction(Agent agent, WorldMap world) {
        if (agent instanceof HospitalUser user) {
            Hospital hospital = world.getHospitalAt(user.getPosition());

            // Agent wchodzi do szpitala tylko, jeśli na jego polu jest placówka i ma taką chęć
            if (hospital != null && user.isWantsHospital()) {
                if (hospital.addPatient(user)) {
                    user.setIsInHospital(true);
                }
            }
        }
    }
}