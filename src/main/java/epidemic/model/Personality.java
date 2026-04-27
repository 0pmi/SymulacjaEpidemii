package epidemic.model;

import epidemic.strategies.decision.DecisionStrategy;
import epidemic.strategies.movement.MovementStrategy;

public class Personality {

    private MovementStrategy movementStrategy;
    private DecisionStrategy decisionStrategy;

    public Personality(MovementStrategy movementStrategy, DecisionStrategy decisionStrategy) {
        this.movementStrategy = movementStrategy;
        this.decisionStrategy = decisionStrategy;
    }

    public MovementStrategy getMovementStrategy() {
        return movementStrategy;
    }

    public DecisionStrategy getDecisionStrategy() {
        return decisionStrategy;
    }
}