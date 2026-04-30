package epidemic.strategies.decision;

import epidemic.model.Human;
import epidemic.model.WorldContext;

public interface DecisionStrategy {
    void makeDecision(Human human, WorldContext world);
}