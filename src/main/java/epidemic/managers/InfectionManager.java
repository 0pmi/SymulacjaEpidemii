package epidemic.managers;

import epidemic.model.*;
import epidemic.service.Config;
import epidemic.service.SpatialManager;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Moduł odpowiedzialny za mechanikę rozprzestrzeniania się patogenu.
 * Ocenia interakcje między agentami oraz środowiskiem (chmury zakaźne),
 * przeliczając szanse na transmisję wirusa na podstawie dystansu przestrzennego
 * i indywidualnej podatności organizmu (Vulnerability).
 */
public class InfectionManager {

    private final Virus virus;

    /**
     * Inicjalizuje menedżera infekcji.
     *
     * @param virus Referencja do globalnego patogenu definiującego bazowe statystyki zakażeń.
     */
    public InfectionManager(Virus virus) {
        this.virus = virus;
    }

    /**
     * Przeprowadza pełną iterację procesu zakażania dla całej populacji na mapie.
     * Przetwarza wektory transmisji dwutorowo:
     * 1. Bezpośrednie zakażenia kropelkowe między nosicielami a podatnymi ofiarami w promieniu rażenia.
     * 2. Zakażenia środowiskowe (aerozolowe) poprzez kontakt ze stacjonarnymi polami skażenia (InfectionField).
     *
     * @param world Aktualny stan mapy symulacyjnej udostępniający listę agentów i indeks przestrzenny.
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

            // WEKTOR 2: Zarażenie drogą powietrzną
            if (agent.canBeInfected()) {
                InfectionField field = world.getFieldAt(agent.getPosition());
                if (field != null) {
                    double prob = field.getInfectivity() * agent.getVulnerabilityMultiplier();
                    if (ThreadLocalRandom.current().nextDouble() < prob) {
                        infect(agent);
                    }
                }
            }
        }
    }

    /*
     * Weryfikuje, czy agent jest żywy i posiada status umożliwiający transmisję patogenu.
     */
    private boolean canSpreadVirus(Agent agent) {
        if (agent.isDead()) return false;
        HealthStatus status = agent.getHealthStatus();
        return status == HealthStatus.SICK || status == HealthStatus.CARRIER;
    }

    /*
     * Wyszukuje potencjalne ofiary w otoczeniu nosiciela i przeprowadza próbę infekcji.
     */
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

    /*
     * Oblicza finalne prawdopodobieństwo transmisji wirusa między dwoma agentami,
     * biorąc pod uwagę bazową zjadliwość, asymptomatyczność nosiciela (CARRIER),
     * spadek szansy wraz z odległością oraz środki ochrony osobistej ofiary (maski, szczepionki).
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

    /*
     * Aplikuje skutki pomyślnej infekcji na agencie, z uwzględnieniem szansy
     * na bezobjawowy przebieg choroby.
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