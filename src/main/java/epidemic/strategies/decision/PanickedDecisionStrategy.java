package epidemic.strategies.decision;

import epidemic.model.HealthStatus;
import epidemic.model.Human;
import epidemic.model.WorldContext;
import epidemic.strategies.movement.MovementStrategy;

public class PanickedDecisionStrategy implements DecisionStrategy {

    private final MovementStrategy panicMovementStrategy;
    private final MovementStrategy calmMovementStrategy;
    private final MovementStrategy hospitalMovementStrategy;

    public PanickedDecisionStrategy(
            MovementStrategy panicMovementStrategy,
            MovementStrategy calmMovementStrategy,
            MovementStrategy hospitalMovementStrategy) {
        this.panicMovementStrategy = panicMovementStrategy;
        this.calmMovementStrategy = calmMovementStrategy;
        this.hospitalMovementStrategy = hospitalMovementStrategy;
    }

    @Override
    public void makeDecision(Human human, WorldContext world) {

        boolean isPanicking = world.getInfectionPercentage() > 0.05;
        human.setWearingMask(isPanicking);


        if (human.getHealthStatus() == HealthStatus.SICK) {
            human.setWantsHospital(true);
            human.setMovementStrategy(hospitalMovementStrategy);
        }
        else if (isPanicking) {
            human.setMovementStrategy(panicMovementStrategy);
        }
        else {
            human.setMovementStrategy(calmMovementStrategy);
        }
    }
}