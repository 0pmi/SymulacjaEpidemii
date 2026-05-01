package epidemic.strategies.decision;

import epidemic.model.HealthStatus;
import epidemic.model.Human;
import epidemic.model.WorldContext;
import epidemic.service.Config;
import epidemic.strategies.movement.MovementStrategy;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Strategia decyzyjna modelująca zachowanie w panice.
 * Agent wykazuje bardzo niski próg tolerancji na infekcję w środowisku,
 * co objawia się natychmiastowym chaotycznym ruchem (ucieczką) oraz nakładaniem maseczki.
 */
public class PanickedDecisionStrategy implements DecisionStrategy {

    private final MovementStrategy panicMovementStrategy;
    private final MovementStrategy calmMovementStrategy;
    private final MovementStrategy hospitalMovementStrategy;
    private final MovementStrategy seekMateMovementStrategy;

    public PanickedDecisionStrategy(
            MovementStrategy panicMovementStrategy,
            MovementStrategy calmMovementStrategy,
            MovementStrategy hospitalMovementStrategy,
            MovementStrategy seekMateMovementStrategy) {
        this.panicMovementStrategy = panicMovementStrategy;
        this.calmMovementStrategy = calmMovementStrategy;
        this.hospitalMovementStrategy = hospitalMovementStrategy;
        this.seekMateMovementStrategy = seekMateMovementStrategy;
    }

    @Override
    public void makeDecision(Human human, WorldContext world) {
        if (human.getHealthStatus() == HealthStatus.RECOVERED) {
            human.setWearingMask(false);
            human.setWantsHospital(false);
            human.setMovementStrategy(determinePassiveMovement(human));
            return;
        }
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
                human.setMovementStrategy(determinePassiveMovement(human));
            }
        }
    }

    /**
     * Wyznacza strategię ruchu w stanie spoczynku (brak paniki i choroby).
     * Uwzględnia weryfikację wymagań do rozrodu.
     *
     * @param human Oczeniany agent.
     * @return Uspokojona strategia ruchu lub aktywna strategia prokreacyjna.
     */
    private MovementStrategy determinePassiveMovement(Human human) {
        boolean isAdult = human.getAge() >= human.getSpeciesType().getMaturityAge();
        double seekMateProb = Config.getDouble("reproduction.seekMateProbability", 0.2);

        if (isAdult && human.getHealthStatus() == HealthStatus.HEALTHY &&
                ThreadLocalRandom.current().nextDouble() < seekMateProb) {
            return seekMateMovementStrategy;
        }

        return calmMovementStrategy;
    }
}