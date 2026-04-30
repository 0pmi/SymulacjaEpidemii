package epidemic.model;

import epidemic.strategies.decision.DecisionStrategy;
import epidemic.strategies.movement.MovementStrategy;

public class Personality {

    private DecisionStrategy decisionStrategy;

    public Personality(DecisionStrategy decisionStrategy) {
        this.decisionStrategy = decisionStrategy;
    }

    public void updateMentalState(Human human, WorldContext context) {
        decisionStrategy.makeDecision(human, context);
    }

    // --- Gettery i Settery ---

    public DecisionStrategy getDecisionStrategy() {
        return decisionStrategy;
    }
}