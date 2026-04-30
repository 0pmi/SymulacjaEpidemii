package epidemic.strategies.decision;

import epidemic.model.HealthStatus;
import epidemic.model.Human;
import epidemic.model.WorldContext;

public class RationalDecisionStrategy implements DecisionStrategy {

    @Override
    public void makeDecision(Human human, WorldContext world) {

        human.setWearingMask(world.getInfectionPercentage() > 0.20);

        if (world.isVaccineAvailable() && !human.isVaccinated()) {
            human.setVaccinated(true);
        }

        if (human.getHealthStatus() == HealthStatus.SICK && human.getRemainingInfectionEpochs() < 5) {
            human.setWantsHospital(true);
        }
    }
}