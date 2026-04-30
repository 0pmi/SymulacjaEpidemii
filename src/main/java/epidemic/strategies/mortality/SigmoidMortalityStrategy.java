package epidemic.strategies.mortality;

import epidemic.model.Agent;
import epidemic.model.HospitalUser;
import epidemic.service.Config;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Zaawansowana strategia modelująca śmiertelność przy użyciu krzywej sigmoidalnej.
 * Pozwala na realistyczne odzwierciedlenie procesu starzenia, gdzie szansa na zgon
 * rośnie gwałtownie po przekroczeniu pewnego progu wiekowego.
 * Uwzględnia również system ochrony zdrowia - pobyt w szpitalu redukuje śmiertelność wirusa.
 */
public class SigmoidMortalityStrategy implements MortalityStrategy {

    private final double baseDiseaseLethality = Config.getDouble("sigmoid.baseLethality", 0.02);
    private final double k = Config.getDouble("sigmoid.k", 0.15);
    private final int x0 = Config.getInt("sigmoid.midpointAge", 80);

    @Override
    public boolean shouldDieFromDisease(Agent agent) {
        double currentLethality = baseDiseaseLethality;

        // Redukcja zjadliwości wirusa, jeśli agent jest pacjentem placówki medycznej
        if (agent instanceof HospitalUser user && user.isInHospital()) {
            currentLethality *= Config.getDouble("sigmoid.hospitalMultiplier", 0.1);
        }

        return ThreadLocalRandom.current().nextDouble() < currentLethality;
    }

    @Override
    public boolean shouldDieNaturally(Agent agent) {
        // Kalkulacja prawdopodobieństwa zgonu w oparciu o dystrybucję logistyczną
        double exponent = -k * (agent.getAge() - x0);
        double deathProbability = 1.0 / (1.0 + Math.exp(exponent));

        return ThreadLocalRandom.current().nextDouble() < deathProbability;
    }
}