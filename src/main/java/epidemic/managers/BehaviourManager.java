package epidemic.managers;

import epidemic.model.Agent;
import epidemic.model.WorldContext;
import epidemic.model.WorldMap;

/**
 * Menedżer odpowiedzialny za fazę decyzyjną agentów w symulacji.
 * Orkiestruje wywoływanie interakcji kognitywnych,
 * podczas których agenci analizują stan świata i planują swoje przyszłe akcje.
 */
public class BehaviourManager {

    /**
     * Aktualizuje stan mentalny i behawioralny każdego agenta na mapie.
     *
     * @param world Stan mapy symulacyjnej.
     * @param context Globalny kontekst środowiska (np. wskaźnik infekcji, dostępność szczepionek).
     */
    public void updateBehaviours(WorldMap world, WorldContext context) {
        for (Agent agent : world.getAgents()) {
            agent.think(context);
        }
    }
}