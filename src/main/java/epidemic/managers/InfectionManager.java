package epidemic.managers;

import epidemic.model.*;
import epidemic.service.Config;
import epidemic.service.SpatialManager;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Złożony menedżer odpowiadający za mechanikę rozprzestrzeniania się patogenu.
 * Analizuje siatkę przestrzenną, aby odszukać sąsiadów wokół nosicieli i chorych,
 * a następnie ewaluuje prawdopodobieństwo zarażenia na podstawie wektorów odległości i podatności.
 */
public class InfectionManager {

    private final Virus virus;

    public InfectionManager(Virus virus) {
        this.virus = virus;
    }

    /**
     * Główna metoda wywoływana w pętli symulacji. Przeszuka całą populację w celu znalezienia siewców,
     * a następnie podejmie próbę rozsiania wirusa w ich pobliżu.
     *
     * @param agents Lista wszystkich agentów w symulacji.
     * @param spatialManager Moduł udostępniający szybkie zapytania przestrzenne.
     */
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

    /**
     * Kompiluje ostateczne prawdopodobieństwo zakażenia w danym kontakcie.
     * Składa się na nie: bazowa siła wirusa, modyfikatory nosiciela (np. bezobjawowi rozsiewają słabiej),
     * fizyczna odległość na siatce oraz własna podatność ofiary (np. maseczki, odporność).
     */
    private double calculateFinalProbability(Agent spreader, Agent victim) {
        double prob = virus.getBaseInfectionProbability();

        if (spreader.getHealthStatus() == HealthStatus.CARRIER) {
            prob *= Config.getDouble("infection.carrierMultiplier", 0.5);
        }

        prob *= spreader.getVirulence();

        double distance = spreader.getPosition().distanceTo(victim.getPosition());
        // Zabezpieczenie logiczne - zakłada, że ofiara jest znaleziona w promieniu wirusa
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