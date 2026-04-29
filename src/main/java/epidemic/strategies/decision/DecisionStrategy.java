package epidemic.strategies.decision;

import epidemic.model.Agent;
import epidemic.model.WorldContext;

public interface DecisionStrategy {
    void makeDecision(Agent agent, WorldContext world);
}