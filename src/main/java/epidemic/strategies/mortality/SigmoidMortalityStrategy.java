package epidemic.strategies.mortality;

import epidemic.model.Agent;
import epidemic.model.HospitalUser;
import epidemic.service.Config;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Zaawansowana strategia modelująca śmiertelność populacji przy użyciu krzywej sigmoidalnej (funkcji logistycznej).
 * Pozwala na realistyczne odzwierciedlenie procesu biologicznego starzenia się, gdzie szansa na zgon
 * jest niska w młodości i rośnie drastycznie po przekroczeniu zdefiniowanego punktu przegięcia (wieku podeszłego).
 * Strategia ta współpracuje z infrastrukturą ochrony zdrowia – aktywna hospitalizacja znacząco redukuje
 * śmiertelność wywołaną powikłaniami wirusowymi.
 */
public class SigmoidMortalityStrategy implements MortalityStrategy {

    private final double baseDiseaseLethality = Config.getDouble("sigmoid.baseLethality", 0.02);
    private final double k = Config.getDouble("sigmoid.k", 0.15);
    private final int x0 = Config.getInt("sigmoid.midpointAge", 80);

    /**
     * Przeprowadza stochastyczną ewaluację ryzyka śmierci na skutek infekcji.
     * Wyjściowa zjadliwość wirusa jest weryfikowana na tle statusu medycznego agenta;
     * przebywanie na oddziale szpitalnym aplikuje mnożnik ratujący życie, redukujący szansę na zgon.
     *
     * @param agent Agent zmagający się z chorobą.
     * @return {@code true}, jeśli losowa próba znalazła się w przedziale aktualnej zjadliwości, oznaczając zgon agenta.
     */
    @Override
    public boolean shouldDieFromDisease(Agent agent) {
        double currentLethality = baseDiseaseLethality;

        if (agent instanceof HospitalUser user && user.isInHospital()) {
            currentLethality *= Config.getDouble("sigmoid.hospitalMultiplier", 0.1);
        }

        return ThreadLocalRandom.current().nextDouble() < currentLethality;
    }

    /**
     * Oblicza prawdopodobieństwo naturalnego zgonu na podstawie wieku agenta dystrybuowanego
     * wzdłuż krzywej S-kształtnej. Wykorzystuje parametry konfiguracyjne określające stromość
     * krzywej (k) oraz punkt 50% prawdopodobieństwa zgonu (x0).
     *
     * @param agent Agent weryfikowany pod kątem naturalnej śmierci ze starości.
     * @return {@code true}, jeśli wygenerowana wartość losowa padła ofiarą obliczonego prawdopodobieństwa .
     */
    @Override
    public boolean shouldDieNaturally(Agent agent) {
        double exponent = -k * (agent.getAge() - x0);
        double deathProbability = 1.0 / (1.0 + Math.exp(exponent));

        return ThreadLocalRandom.current().nextDouble() < deathProbability;
    }
}