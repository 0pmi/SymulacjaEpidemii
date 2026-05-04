package epidemic.strategies.mortality;

import epidemic.model.Agent;

/**
 * Deterministyczna implementacja strategii śmiertelności oparta na sztywnym progu wiekowym.
 * Agent przypisany do tej strategii umiera natychmiastowo po osiągnięciu zdefiniowanego limitu epok.
 * <p>
 * Strategia ta zakłada również całkowitą odporność organizmu na zgon wywołany patogenem,
 * co czyni ją idealnym modelem dla bezobjawowych zwierząt stanowiących środowiskowe
 * wektory wirusa (np. nietoperze, szczury).
 * </p>
 */
public class ThresholdMortalityStrategy implements MortalityStrategy {

    private final int maxAge;

    /**
     * Inicjalizuje strategię z określonym, maksymalnym limitem wieku.
     *
     * @param maxAge Graniczny wiek, po którym następuje automatyczny zgon z przyczyn naturalnych.
     */
    public ThresholdMortalityStrategy(int maxAge) {
        this.maxAge = maxAge;
    }

    /**
     * Ignoruje wpływ wirusa na śmiertelność organizmu.
     *
     * @param agent Agent podlegający ocenie.
     * @return Zawsze {@code false}, gwarantując przetrwanie infekcji.
     */
    @Override
    public boolean shouldDieFromDisease(Agent agent) {
        // Ta konkretna strategia ignoruje wpływ wirusa na śmiertelność
        return false;
    }

    /**
     * Weryfikuje, czy wiek agenta zrównał się z maksymalnym dozwolonym progiem lub go przekroczył.
     *
     * @param agent Agent podlegający ocenie.
     * @return {@code true}, jeśli agent dożył wyznaczonego limitu.
     */
    @Override
    public boolean shouldDieNaturally(Agent agent) {
        return agent.getAge() >= maxAge;
    }
}