package epidemic.managers;

import epidemic.factory.AgentFactory;
import epidemic.model.*;
import epidemic.service.Config;
import epidemic.service.SpatialManager;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Menedżer nadzorujący biologiczny proces rozmnażania populacji.
 * Poszukuje potencjalnych partnerów w bliskim sąsiedztwie i generuje potomstwo
 * na podstawie wskaźników płodności oraz ograniczeń wiekowych.
 */
public class ReproductionManager {

    private final AgentFactory agentFactory;
    private final double REPRODUCTION_CHANCE;
    private final int COOLDOWN_MIN;
    private final int COOLDOWN_MAX;

    public ReproductionManager(AgentFactory agentFactory) {
        this.agentFactory = agentFactory;
        this.REPRODUCTION_CHANCE = Config.getDouble("reproduction.chance", 0.1);

        this.COOLDOWN_MIN = Config.getInt("reproduction.cooldownMin", 30);
        this.COOLDOWN_MAX = Config.getInt("reproduction.cooldownMax", 50);
    }

    /**
     * Główny cykl reprodukcyjny przetwarzany w każdej epoce.
     */
    public void handleReproduction(WorldMap world, SpatialManager spatialManager, int currentEpoch) {
        List<Agent> agents = world.getAgents();

        for (Agent parentA : agents) {
            if (!canParticipateInReproduction(parentA, currentEpoch)) {
                continue;
            }

            double matingRange = Config.getDouble("reproduction.matingRange", 0.1);
            List<Agent> partnersAtSameSpot = spatialManager.getNearbyAgents(parentA, matingRange);

            for (Agent parentB : partnersAtSameSpot) {
                // Partner musi być innym agentem tego samego gatunku, gotowym do rozrodu
                if (parentB != parentA &&
                        parentB.getSpeciesType() == parentA.getSpeciesType() &&
                        canParticipateInReproduction(parentB, currentEpoch)) {

                    if (ThreadLocalRandom.current().nextDouble() < REPRODUCTION_CHANCE) {
                        spawnOffspring(world, parentA, parentB, currentEpoch);
                        break; // Tylko jedno potomstwo na epokę z danym partnerem
                    }
                }
            }
        }
    }

    /**
     * Sprawdza, czy agent spełnia warunki zdrowotne, wiekowe oraz czasowe do reprodukcji.
     * Wymagany czas odpoczynku jest losowany w locie.
     */
    private boolean canParticipateInReproduction(Agent agent, int currentEpoch) {
        if (agent.isDead() || agent.getHealthStatus() == HealthStatus.SICK) {
            return false;
        }

        int requiredCooldown = ThreadLocalRandom.current().nextInt(COOLDOWN_MIN, COOLDOWN_MAX + 1);

        if (currentEpoch - agent.getLastReproductionEpoch() < requiredCooldown) {
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