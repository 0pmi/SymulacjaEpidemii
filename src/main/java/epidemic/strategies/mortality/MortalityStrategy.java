package epidemic.strategies.mortality;

import epidemic.model.Agent;

public interface MortalityStrategy {
    boolean shouldDieFromDisease(Agent agent);
    boolean shouldDieNaturally(Agent agent);
}