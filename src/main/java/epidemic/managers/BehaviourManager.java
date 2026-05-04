package epidemic.managers;

import epidemic.model.Agent;
import epidemic.model.WorldContext;
import epidemic.model.WorldMap;

/**
 * Moduł odpowiedzialny za aktualizację stanu decyzyjnego agentów.
 * Pełni rolę centralnego wyzwalacza (Trigger) dla sztucznej inteligencji jednostek,
 * gwarantując ewaluację logiki behawioralnej w odpowiednim momencie cyklu życia epoki.
 */
public class BehaviourManager {

    /**
     * Wywołuje mechanizmy kognitywne dla każdego agenta obdarzonego inteligencją lub osobowością.
     * Zmiany stanu aplikowane w tej fazie (np. decyzja o poszukiwaniu szpitala, założenie maski)
     * bezpośrednio determinują zachowanie rozpatrywane w kolejnych etapach
     * (m.in. przemieszczanie się w {@code MovementManager}).
     *
     * @param world Repozytorium wszystkich agentów.
     * @param context Globalny kontekst informacyjny (telemetria świata) dla bieżącej epoki,
     *                służący agentom jako podstawa do oceny ryzyka.
     */
    public void updateBehaviours(WorldMap world, WorldContext context) {
        for (Agent agent : world.getAgents()) {
            agent.think(context);
        }
    }
}