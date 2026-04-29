package epidemic.strategies.mortality;

import epidemic.model.Agent;

public class ThresholdMortalityStrategy implements MortalityStrategy {

    private final int maxAge;

    public ThresholdMortalityStrategy(int maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    public boolean shouldDieFromDisease(Agent agent) {
        return false;
    }

    @Override
    public boolean shouldDieNaturally(Agent agent) {
        return agent.getAge() >= maxAge;
    }
}