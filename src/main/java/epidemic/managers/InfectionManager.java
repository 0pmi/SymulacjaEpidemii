package epidemic.managers;

import epidemic.model.*;
import epidemic.service.Config;
import epidemic.service.SpatialManager;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Złożony menedżer odpowiadający za mechanikę rozprzestrzeniania się patogenu.
 * Realizuje dwa wektory zakażeń:
 * 1. Bezpośredni (kropelkowy) na podstawie odległości (SpatialManager).
 * 2. Pośredni (aerozolowy) na podstawie stacjonarnych pól infekcji na mapie (WorldMap).
 */
public class InfectionManager {

    private final Virus virus;

    public InfectionManager(Virus virus) {
        this.virus = virus;
    }

    /**
     * Przetwarza wektory zakażeń dla całej populacji w danej epoce.
     *
     * @param world Stan mapy zawierający agentów oraz strefy skażonego powietrza.
     */
    public void processInfections(WorldMap world) {
        List<Agent> agents = world.getAgents();
        SpatialManager spatialManager = world.getSpatialManager();

        for (Agent agent : agents) {

            // WEKTOR 1: Rozprzestrzenianie bezpośrednie i tworzenie aerozoli
            if (canSpreadVirus(agent)) {
                spreadToNeighbors(agent, spatialManager);

                // Tworzenie stacjonarnej chmury zakaźnej (InfectionField)
                double fieldStrength = virus.getBaseInfectionProbability() * Config.getDouble("infectionField.aerosolMultiplier", 0.3);
                if (agent.getHealthStatus() == HealthStatus.CARRIER) {
                    fieldStrength *= Config.getDouble("infection.carrierMultiplier", 0.5);
                }
                world.addOrRefreshInfectionField(agent.getPosition(), fieldStrength);
            }

            // WEKTOR 2: Zarażenie drogą powietrzną (bez kontaktu z chorym)
            if (agent.canBeInfected()) {
                InfectionField field = world.getFieldAt(agent.getPosition());
                if (field != null) {
                    // Szansa na zakażenie zależy od gęstości chmury i podatności agenta (np. czy nosi maskę)
                    double prob = field.getInfectivity() * agent.getVulnerabilityMultiplier();
                    if (ThreadLocalRandom.current().nextDouble() < prob) {
                        infect(agent);
                    }
                }
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

    /**
     * Kompiluje ostateczne prawdopodobieństwo zakażenia w danym kontakcie.
     */
    private double calculateFinalProbability(Agent spreader, Agent victim) {
        double prob = virus.getBaseInfectionProbability();

        if (spreader.getHealthStatus() == HealthStatus.CARRIER) {
            prob *= Config.getDouble("infection.carrierMultiplier", 0.5);
        }

        prob *= spreader.getVirulence();

        double distance = spreader.getPosition().distanceTo(victim.getPosition());
        double distanceFactor = 1.0 - (distance / virus.getInfectionRadius());
        prob *= distanceFactor;

        prob *= victim.getVulnerabilityMultiplier();

        return Math.max(0, prob);
    }

    /**
     * Zmienia status zdrowego agenta po udanej infekcji.
     * Losuje, czy infekcja przebiega objawowo (SICK), czy bezobjawowo (CARRIER).
     */
    private void infect(Agent victim) {
        double carrierProb = Config.getDouble("infection.carrierProbability", 0.2);

        if (ThreadLocalRandom.current().nextDouble() < carrierProb) {
            victim.setHealthStatus(HealthStatus.CARRIER);
        } else {
            victim.setHealthStatus(HealthStatus.SICK);
        }

        victim.setRemainingInfectionEpochs(virus.getDefaultInfectionDuration());
    }
}