package epidemic.strategies.decision;

import epidemic.model.HealthStatus;
import epidemic.model.Human;
import epidemic.model.WorldContext;
import epidemic.strategies.movement.MovementStrategy;

public class RationalDecisionStrategy implements DecisionStrategy {

    private final MovementStrategy hospitalMovementStrategy;
    private final MovementStrategy distancingMovementStrategy;
    private final MovementStrategy normalMovementStrategy;

    public RationalDecisionStrategy(
            MovementStrategy hospitalMovementStrategy,
            MovementStrategy distancingMovementStrategy,
            MovementStrategy normalMovementStrategy) {
        this.hospitalMovementStrategy = hospitalMovementStrategy;
        this.distancingMovementStrategy = distancingMovementStrategy;
        this.normalMovementStrategy = normalMovementStrategy;
    }

    @Override
    public void makeDecision(Human human, WorldContext world) {

        boolean highInfectionRate = world.getInfectionPercentage() > 0.20;

        human.setWearingMask(highInfectionRate);

        if (human.getHealthStatus() == HealthStatus.SICK && human.getRemainingInfectionEpochs() < 5) {
            human.setWantsHospital(true);
            human.setMovementStrategy(hospitalMovementStrategy);
        }

        else if (world.isVaccineAvailable() && !human.isVaccinated() && human.getHealthStatus() == HealthStatus.HEALTHY) {
            human.setWantsHospital(true);
            human.setMovementStrategy(hospitalMovementStrategy);
        }

        else if (highInfectionRate) {
            human.setWantsHospital(false);
            human.setMovementStrategy(distancingMovementStrategy);
        }
        else {
            human.setWantsHospital(false);
            human.setMovementStrategy(normalMovementStrategy);
        }
    }
}