package epidemic.strategies.mortality;

import epidemic.model.Agent;

/**
 * Interfejs definiujący zasady śmiertelności dla agentów w symulacji.
 * Pozwala na odseparowanie biologicznych i losowych czynników zgonu
 * od głównej logiki cyklu życia agenta.
 */
public interface MortalityStrategy {

    /**
     * Weryfikuje, czy agent powinien umrzeć w wyniku trwającej infekcji.
     * @param agent Agent podlegający ocenie.
     * @return true, jeśli choroba okazała się śmiertelna, false w przeciwnym razie.
     */
    boolean shouldDieFromDisease(Agent agent);

    /**
     * Weryfikuje, czy agent osiągnął kres swojego naturalnego cyklu życia.
     * @param agent Agent podlegający ocenie.
     * @return true, jeśli agent zmarł ze starości, false w przeciwnym razie.
     */
    boolean shouldDieNaturally(Agent agent);
}