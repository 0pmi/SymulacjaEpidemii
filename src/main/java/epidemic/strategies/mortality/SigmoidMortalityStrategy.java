package epidemic.strategies.mortality;

import epidemic.model.Agent;
import epidemic.model.HospitalUser;
import java.util.concurrent.ThreadLocalRandom;

public class SigmoidMortalityStrategy implements MortalityStrategy {

    private final double baseDiseaseLethality = 0.02;
    private final double k = 0.15; // Stromość krzywej starzenia
    private final int x0 = 80;     // Wiek, w którym szansa na śmierć to 50%

    @Override
    public boolean shouldDieFromDisease(Agent agent) {
        double currentLethality = baseDiseaseLethality;

        if (agent instanceof HospitalUser user && user.isInHospital()) {
            currentLethality *= 0.1;
        }

        return ThreadLocalRandom.current().nextDouble() < currentLethality;
    }

    @Override
    public boolean shouldDieNaturally(Agent agent) {

        double exponent = -k * (agent.getAge() - x0);
        double deathProbability = 1.0 / (1.0 + Math.exp(exponent));

        return ThreadLocalRandom.current().nextDouble() < deathProbability;
    }
}