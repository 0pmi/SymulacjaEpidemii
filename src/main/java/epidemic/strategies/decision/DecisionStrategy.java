package epidemic.strategies.decision;

import epidemic.model.Agent;
import epidemic.engine.WorldMap;

public interface DecisionStrategy {
    void makeDecision(Agent agent, WorldMap world);
}