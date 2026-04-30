package epidemic.strategies.mortality;

import epidemic.model.Agent;

/**
 * Prosta strategia śmiertelności oparta na sztywnym progu wiekowym.
 * Agent umiera natychmiast po osiągnięciu określonego wieku.
 * Strategia ta zakłada całkowitą odporność na śmierć w wyniku choroby (przydatne np. dla zwierząt-nosicieli).
 */
public class ThresholdMortalityStrategy implements MortalityStrategy {

    private final int maxAge;

    public ThresholdMortalityStrategy(int maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    public boolean shouldDieFromDisease(Agent agent) {
        // Ta konkretna strategia ignoruje wpływ wirusa na śmiertelność
        return false;
    }

    @Override
    public boolean shouldDieNaturally(Agent agent) {
        return agent.getAge() >= maxAge;
    }
}