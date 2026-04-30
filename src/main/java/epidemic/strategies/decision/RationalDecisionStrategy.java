package epidemic.strategies.decision;

import epidemic.model.HealthStatus;
import epidemic.model.Human;
import epidemic.model.WorldContext;
import epidemic.service.Config;
import epidemic.strategies.movement.MovementStrategy;

/**
 * Strategia decyzyjna modelująca racjonalne podejście.
 * Agent reaguje na zagrożenie proporcjonalnie, stosuje dystansowanie społeczne,
 * gdy infekcje są wysokie, oraz udaje się do szpitala w celu szczepienia lub leczenia ciężkich objawów.
 */
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
        // Racjonalny agent reaguje, gdy wskaźnik infekcji przekroczy rozsądny próg (domyślnie 20%)
        boolean highInfectionRate = world.getInfectionPercentage() > Config.getDouble("rational.infectionThreshold", 0.20);
        human.setWearingMask(highInfectionRate);

        // Udaje się do szpitala tylko, gdy jest chory i jego stan wymaga nagłej interwencji (końcówka choroby)
        if (human.getHealthStatus() == HealthStatus.SICK &&
                human.getRemainingInfectionEpochs() < Config.getInt("rational.hospitalEpochThreshold", 5)) {
            human.setWantsHospital(true);
            human.setMovementStrategy(hospitalMovementStrategy);
        }
        // Udaje się do szpitala po szczepionkę, jeśli jest zdrowy, niesaszczepiony i szczepionka jest dostępna
        else if (world.isVaccineAvailable() && !human.isVaccinated() && human.getHealthStatus() == HealthStatus.HEALTHY) {
            human.setWantsHospital(true);
            human.setMovementStrategy(hospitalMovementStrategy);
        }
        else {
            human.setWantsHospital(false);
            if (highInfectionRate) {
                human.setMovementStrategy(distancingMovementStrategy);
            } else {
                human.setMovementStrategy(normalMovementStrategy);
            }
        }
    }
}