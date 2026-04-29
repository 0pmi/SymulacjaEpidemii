package epidemic.managers;

import epidemic.model.*;
import epidemic.service.SpatialManager;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class InfectionManager {

    private final double baseInfectionProbability;
    private final double infectionRadius;
    private final int defaultInfectionDuration;

    public InfectionManager(double baseInfectionProbability, double infectionRadius, int defaultInfectionDuration) {
        this.baseInfectionProbability = baseInfectionProbability;
        this.infectionRadius = infectionRadius;
        this.defaultInfectionDuration = defaultInfectionDuration;
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
        List<Agent> nearbyAgents = spatialManager.getNearbyAgents(spreader, infectionRadius);

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
        double prob = baseInfectionProbability;

        if (spreader.getHealthStatus() == HealthStatus.CARRIER) {
            prob *= 0.5;
        }

        double distance = spreader.getPosition().distanceTo(victim.getPosition());
        double distanceFactor = 1.0 - (distance / infectionRadius);
        prob *= distanceFactor;

        if (victim instanceof Human h) {
            if (h.isWearingMask()) prob *= 0.3;
            if (h.isVaccinated()) prob *= 0.1;
            prob *= (1.0 - h.getResistance());
        }

        return Math.max(0, prob);
    }

    private void infect(Agent victim) {
        victim.setHealthStatus(HealthStatus.SICK);
        victim.setRemainingInfectionEpochs(defaultInfectionDuration);
    }
}