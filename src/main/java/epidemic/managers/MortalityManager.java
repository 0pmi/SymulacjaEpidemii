package epidemic.managers;

import epidemic.model.Agent;
import epidemic.model.HealthStatus;
import epidemic.model.WorldMap;
import epidemic.strategies.mortality.MortalityStrategy;

import java.util.List;

/**
 * Moduł zarządzający cyklem życia i śmierci agentów.
 * Wykorzystuje wstrzykniętą z zewnątrz strategię śmiertelności (wzorzec Strategy,
 * np. krzywa sigmoidalna wieku) do obiektywnej ewaluacji ryzyka zgonu.
 * Odpowiada za zarządzanie czasem trwania infekcji oraz proces usuwania
 * martwych jednostek ze środowiska.
 */
public class MortalityManager {

    private final MortalityStrategy mortalityStrategy;

    /**
     * Inicjalizuje menedżera z podaną strategią obliczania śmiertelności.
     *
     * @param mortalityStrategy Implementacja interfejsu ewaluującego szanse na śmierć
     *                          naturalną oraz wirusową (wzorzec Dependency Injection).
     */
    public MortalityManager(MortalityStrategy mortalityStrategy) {
        this.mortalityStrategy = mortalityStrategy;
    }

    /**
     * Główna metoda przetwarzająca stan biologiczny agentów w obrębie jednej epoki.
     * Zleca usunięcie z pamięci mapy ciał martwych agentów oraz zlicza zgony
     * spowodowane bezpośrednio przez wirusa w bieżącym kroku czasowym.
     *
     * @param world Stan mapy udostępniający metody modyfikacji kolekcji agentów.
     * @param agents Lista agentów zakwalifikowanych do przetworzenia w bieżącym kroku.
     * @return Liczba agentów, którzy zmarli na skutek wirusa w trakcie wykonywania tej metody.
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

    /*
     * Przetwarza cykl trwającej infekcji poprzez redukcję jej licznika.
     * Ewaluuje ryzyko zgonu wywołanego chorobą (dotyczy wyłącznie agentów SICK,
     * nosiciele CARRIER są odporni na objawy śmiertelne). Jeśli agent przetrwa
     * do końca czasu trwania infekcji, otrzymuje status ozdrowieńca (RECOVERED).
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