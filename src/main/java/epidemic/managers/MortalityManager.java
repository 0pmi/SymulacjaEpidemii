package epidemic.managers;

import epidemic.model.Agent;
import epidemic.model.HealthStatus;
import epidemic.strategies.mortality.MortalityStrategy;

import java.util.List;

public class MortalityManager {

    private final MortalityStrategy mortalityStrategy;

    public MortalityManager(MortalityStrategy mortalityStrategy) {
        this.mortalityStrategy = mortalityStrategy;
    }

    public void processLifeCycles(List<Agent> agents) {
        for (Agent agent : agents) {
            if (agent.isDead()) continue;

            agent.incrementAge();

            if (agent.getHealthStatus() == HealthStatus.SICK) {
                processSickness(agent);
            }

            if (!agent.isDead() && mortalityStrategy.shouldDieNaturally(agent)) {
                agent.setDead(true);
            }
        }
    }

    private void processSickness(Agent agent) {
        agent.decrementInfectionTimer();

        if (mortalityStrategy.shouldDieFromDisease(agent)) {
            agent.setDead(true);
            return;
        }

        if (agent.getRemainingInfectionEpochs() <= 0) {
            agent.setHealthStatus(HealthStatus.RECOVERED);
        }
    }
}