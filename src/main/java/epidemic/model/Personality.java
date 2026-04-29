package epidemic.model;

import epidemic.strategies.decision.DecisionStrategy;
import epidemic.strategies.movement.MovementStrategy;

public class Personality {

    private DecisionStrategy decisionStrategy;
    private MovementStrategy movementStrategy;

    public Personality(DecisionStrategy decisionStrategy, MovementStrategy initialMovementStrategy) {
        this.decisionStrategy = decisionStrategy;
        this.movementStrategy = initialMovementStrategy;
    }

    public void updateMentalState(Human human, WorldContext context) {
        decisionStrategy.makeDecision(human, context);
    }

    // --- Gettery i Settery ---

    public DecisionStrategy getDecisionStrategy() {
        return decisionStrategy;
    }
    public MovementStrategy getMovementStrategy() {
        return movementStrategy;
    }

    public void setMovementStrategy(MovementStrategy movementStrategy) {
        this.movementStrategy = movementStrategy;
    }
}