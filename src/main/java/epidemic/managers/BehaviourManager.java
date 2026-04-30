package epidemic.managers;

import epidemic.model.Agent;
import epidemic.model.WorldContext;
import epidemic.model.WorldMap;

public class BehaviourManager {
    public void updateBehaviours(WorldMap world, WorldContext context) {
        for (Agent agent : world.getAgents()) {
            agent.think(context);
        }
    }
}
