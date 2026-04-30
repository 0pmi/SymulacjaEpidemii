package epidemic.managers;

import epidemic.model.Agent;
import epidemic.model.HealthStatus;
import epidemic.model.WorldMap;
import epidemic.strategies.mortality.MortalityStrategy;

import java.util.List;

/**
 * Menedżer nadzorujący stan zdrowia i cykl życia agentów (narodziny i zgon).
 * Przetwarza postęp infekcji u chorych, egzekwuje strategie śmiertelności
 * i zdejmuje martwych agentów z mapy.
 */
public class MortalityManager {

    private final MortalityStrategy mortalityStrategy;

    public MortalityManager(MortalityStrategy mortalityStrategy) {
        this.mortalityStrategy = mortalityStrategy;
    }

    /**
     * Główna metoda ewaluująca stan biologiczny agentów w danej epoce.
     *
     * @param world Stan mapy, służący m.in. do zlecania usunięcia ciał.
     * @param agents Lista agentów do przetworzenia.
     */
    public void processLifeCycles(WorldMap world, List<Agent> agents) {
        for (Agent agent : agents) {
            if (agent.isDead()) continue;

            if (agent.getHealthStatus() == HealthStatus.SICK) {
                processSickness(agent);
            }

            if (!agent.isDead() && mortalityStrategy.shouldDieNaturally(agent)) {
                agent.setDead(true);
            }

            if (agent.isDead()) {
                world.removeAgent(agent);
            }
        }
    }

    private void processSickness(Agent agent) {
        agent.decrementInfectionTimer();

        // Jeśli choroba okazała się śmiertelna, przerywa dalsze sprawdzanie stanu
        if (mortalityStrategy.shouldDieFromDisease(agent)) {
            agent.setDead(true);
            return;
        }

        // Jeśli agent przeżył, ale skończył mu się czas trwania infekcji - zyskuje odporność
        if (agent.getRemainingInfectionEpochs() <= 0) {
            agent.setHealthStatus(HealthStatus.RECOVERED);
        }
    }
}