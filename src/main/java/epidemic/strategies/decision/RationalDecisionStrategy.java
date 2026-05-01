package epidemic.strategies.decision;

import epidemic.model.HealthStatus;
import epidemic.model.Human;
import epidemic.model.WorldContext;
import epidemic.service.Config;
import epidemic.strategies.movement.MovementStrategy;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Strategia decyzyjna modelująca racjonalne podejście.
 * Agent reaguje na zagrożenie proporcjonalnie, stosuje dystansowanie społeczne
 * oraz udaje się do placówek medycznych na szczepienia lub leczenie zaawansowanej infekcji.
 * Dodatkowo przewiduje aktywne poszukiwanie partnerów w stanie pełnego zdrowia.
 */
public class RationalDecisionStrategy implements DecisionStrategy {

    private final MovementStrategy hospitalMovementStrategy;
    private final MovementStrategy distancingMovementStrategy;
    private final MovementStrategy normalMovementStrategy;
    private final MovementStrategy seekMateMovementStrategy;

    public RationalDecisionStrategy(
            MovementStrategy hospitalMovementStrategy,
            MovementStrategy distancingMovementStrategy,
            MovementStrategy normalMovementStrategy,
            MovementStrategy seekMateMovementStrategy) {
        this.hospitalMovementStrategy = hospitalMovementStrategy;
        this.distancingMovementStrategy = distancingMovementStrategy;
        this.normalMovementStrategy = normalMovementStrategy;
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
        boolean highInfectionRate = world.getInfectionPercentage() > Config.getDouble("rational.infectionThreshold", 0.20);
        human.setWearingMask(highInfectionRate);

        if (human.getHealthStatus() == HealthStatus.SICK &&
                human.getRemainingInfectionEpochs() < Config.getInt("rational.hospitalEpochThreshold", 5)) {
            human.setWantsHospital(true);
            human.setMovementStrategy(hospitalMovementStrategy);
        }
        else if (world.isVaccineAvailable() && !human.isVaccinated() && human.getHealthStatus() == HealthStatus.HEALTHY) {
            human.setWantsHospital(true);
            human.setMovementStrategy(hospitalMovementStrategy);
        }
        else {
            human.setWantsHospital(false);
            if (highInfectionRate) {
                human.setMovementStrategy(distancingMovementStrategy);
            } else {
                human.setMovementStrategy(determinePassiveMovement(human));
            }
        }
    }

    /**
     * Wyznacza strategię ruchu w przypadku braku aktywnych zagrożeń lub potrzeb medycznych.
     * Weryfikuje gotowość agenta do podjęcia aktywnego poszukiwania partnera na podstawie
     * wieku oraz statusu zdrowotnego.
     *
     * @param human Oczeniany agent.
     * @return Pasywna strategia ruchu lub strategia prokreacyjna.
     */
    private MovementStrategy determinePassiveMovement(Human human) {
        boolean isAdult = human.getAge() >= human.getSpeciesType().getMaturityAge();
        double seekMateProb = Config.getDouble("reproduction.seekMateProbability", 0.2);

        if (isAdult && human.getHealthStatus() == HealthStatus.HEALTHY &&
                ThreadLocalRandom.current().nextDouble() < seekMateProb) {
            return seekMateMovementStrategy;
        }

        return normalMovementStrategy;
    }
}