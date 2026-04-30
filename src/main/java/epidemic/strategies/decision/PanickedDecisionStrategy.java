package epidemic.strategies.decision;

import epidemic.model.HealthStatus;
import epidemic.model.Human;
import epidemic.model.WorldContext;
import epidemic.service.Config;
import epidemic.strategies.movement.MovementStrategy;

/**
 * Strategia decyzyjna modelująca zachowanie w panice.
 * Agent z tą strategią ma bardzo niski próg tolerancji na infekcję w populacji.
 * Szybko wpada w panikę, zakłada maskę i zaczyna poruszać się chaotycznie.
 */
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
        // Agent wpada w panikę przy stosunkowo niskim wskaźniku infekcji (domyślnie 5%)
        boolean isPanicking = world.getInfectionPercentage() > Config.getDouble("panicked.infectionThreshold", 0.05);
        human.setWearingMask(isPanicking);

        if (human.getHealthStatus() == HealthStatus.SICK) {
            human.setWantsHospital(true);
            human.setMovementStrategy(hospitalMovementStrategy);
        } else {
            human.setWantsHospital(false);

            if (isPanicking) {
                human.setMovementStrategy(panicMovementStrategy);
            } else {
                human.setMovementStrategy(calmMovementStrategy);
            }
        }
    }
}