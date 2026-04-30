package epidemic.managers;

import epidemic.factory.AgentFactory;
import epidemic.model.*;
import epidemic.service.SpatialManager;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ReproductionManager {

    private final AgentFactory agentFactory;
    private final double REPRODUCTION_CHANCE = 0.1;
    private final int REPRODUCTION_COOLDOWN = 40;

    public ReproductionManager(AgentFactory agentFactory) {
        this.agentFactory = agentFactory;
    }

    public void handleReproduction(WorldMap world, SpatialManager spatialManager, int currentEpoch) {
        List<Agent> agents = world.getAgents();

        for (Agent parentA : agents) {
            if (!canParticipateInReproduction(parentA, currentEpoch)) {
                continue;
            }

            List<Agent> partnersAtSameSpot = spatialManager.getNearbyAgents(parentA, 0.1);

            for (Agent parentB : partnersAtSameSpot) {
                if (parentB != parentA &&
                        parentB.getSpeciesType() == parentA.getSpeciesType() &&
                        canParticipateInReproduction(parentB, currentEpoch)) {

                    if (ThreadLocalRandom.current().nextDouble() < REPRODUCTION_CHANCE) {
                        spawnOffspring(world, parentA, parentB, currentEpoch);
                        break;
                    }
                }
            }
        }
    }

    private boolean canParticipateInReproduction(Agent agent, int currentEpoch) {
        if (agent.isDead() || agent.getHealthStatus() == HealthStatus.SICK) {
            return false;
        }

        if (currentEpoch - agent.getLastReproductionEpoch() < REPRODUCTION_COOLDOWN) {
            return false;
        }

        int requiredAge = agent.getSpeciesType().getMaturityAge();

        return agent.getAge() >= requiredAge;
    }

    private void spawnOffspring(WorldMap world, Agent a, Agent b, int currentEpoch) {
        Agent baby = agentFactory.createOffspring(a, b);
        if (baby != null) {
            a.setLastReproductionEpoch(currentEpoch);
            b.setLastReproductionEpoch(currentEpoch);

            world.addAgent(baby);
        }
    }
}