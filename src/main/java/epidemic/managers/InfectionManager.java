package epidemic.managers;

import epidemic.model.*;
import epidemic.service.SpatialManager;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class InfectionManager {

    private final Virus virus;

    public InfectionManager(Virus virus) {
        this.virus = virus;
    }

    public void processInfections(List<Agent> agents, SpatialManager spatialManager) {
        for (Agent agent : agents) {
            if (canSpreadVirus(agent)) {
                spreadToNeighbors(agent, spatialManager);
            }
        }
    }

    private boolean canSpreadVirus(Agent agent) {
        if (agent.isDead()) return false;
        HealthStatus status = agent.getHealthStatus();
        return status == HealthStatus.SICK || status == HealthStatus.CARRIER;
    }

    private void spreadToNeighbors(Agent spreader, SpatialManager spatialManager) {
        List<Agent> nearbyAgents = spatialManager.getNearbyAgents(spreader, virus.getInfectionRadius());

        for (Agent potentialVictim : nearbyAgents) {
            if (potentialVictim.canBeInfected()) {
                double finalProbability = calculateFinalProbability(spreader, potentialVictim);

                if (ThreadLocalRandom.current().nextDouble() < finalProbability) {
                    infect(potentialVictim);
                }
            }
        }
    }

    private double calculateFinalProbability(Agent spreader, Agent victim) {
        double prob = virus.getBaseInfectionProbability();

        if (spreader.getHealthStatus() == HealthStatus.CARRIER) {
            prob *= 0.5;
        }
        prob *= spreader.getVirulence();

        double distance = spreader.getPosition().distanceTo(victim.getPosition());
        double distanceFactor = 1.0 - (distance / virus.getInfectionRadius());
        prob *= distanceFactor;
        prob *= victim.getVulnerabilityMultiplier();

        return Math.max(0, prob);
    }

    private void infect(Agent victim) {
        victim.setHealthStatus(HealthStatus.SICK);
        victim.setRemainingInfectionEpochs(virus.getDefaultInfectionDuration());
    }
}