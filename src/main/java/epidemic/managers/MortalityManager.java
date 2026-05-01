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
    public int processLifeCycles(WorldMap world, List<Agent> agents) {
        int virusDeathsThisEpoch = 0;
        for (Agent agent : agents) {
            if (agent.isDead()) continue;

            HealthStatus status = agent.getHealthStatus();
            if (status == HealthStatus.SICK || status == HealthStatus.CARRIER) {
                if (processSickness(agent)) {
                    virusDeathsThisEpoch++;
                }
            }

            if (!agent.isDead() && mortalityStrategy.shouldDieNaturally(agent)) {
                agent.setDead(true);
            }

            if (agent.isDead()) {
                world.removeAgent(agent);
            }
        }
        return virusDeathsThisEpoch;
    }

    /**
     * Przetwarza cykl trwającej infekcji.
     * Uwaga: Nosiciele (CARRIER) zdrowieją, ale nie podlegają ryzyku zgonu z powodu choroby.
     */
    private boolean processSickness(Agent agent) {
        agent.decrementInfectionTimer();

        // Śmiertelność dotyczy tylko pełnoobjawowych (SICK) agentów
        if (agent.getHealthStatus() == HealthStatus.SICK && mortalityStrategy.shouldDieFromDisease(agent)) {
            agent.setDead(true);
            agent.setDiedFromVirus(true); // Flaga dla statystyk
            return true;
        }

        // Jeśli agent przeżył, ale skończył mu się czas trwania infekcji - zyskuje odporność
        if (agent.getRemainingInfectionEpochs() <= 0) {
            agent.setHealthStatus(HealthStatus.RECOVERED);
        }
        return false;
    }
}