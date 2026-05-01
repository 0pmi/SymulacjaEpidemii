package epidemic.strategies.decision;

import epidemic.model.HealthStatus;
import epidemic.model.Human;
import epidemic.model.WorldContext;
import epidemic.service.Config;
import epidemic.strategies.movement.MovementStrategy;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Złośliwa strategia decyzyjna. Agent zachowuje się poprawnie, dopóki system ochrony zdrowia nie zawiedzie.
 * Gdy zostaje odrzucony przez pełny szpital, wpada w furię, zdejmuje maskę i zaczyna zarażać innych.
 */
public class VindictiveDecisionStrategy implements DecisionStrategy {

    private final MovementStrategy maliciousMovementStrategy;
    private final MovementStrategy hospitalMovementStrategy;
    private final MovementStrategy normalMovementStrategy;

    // Mapa śledząca licznik frustracji per agent.
    private final Map<Human, Integer> frustrationMap = new WeakHashMap<>();

    public VindictiveDecisionStrategy(
            MovementStrategy maliciousMovementStrategy,
            MovementStrategy hospitalMovementStrategy,
            MovementStrategy normalMovementStrategy) {
        this.maliciousMovementStrategy = maliciousMovementStrategy;
        this.hospitalMovementStrategy = hospitalMovementStrategy;
        this.normalMovementStrategy = normalMovementStrategy;
    }

    @Override
    public void makeDecision(Human human, WorldContext world) {
        if (human.getHealthStatus() == HealthStatus.SICK) {
            human.setWantsHospital(true);

            // Sprawdzam, czy agent czeka na wejście do szpitala
            if (!human.isInHospital()) {
                int currentFrustration = frustrationMap.getOrDefault(human, 0) + 1;
                frustrationMap.put(human, currentFrustration);

                int maxFrustration = Config.getInt("vindictive.frustrationThreshold", 10);

                if (currentFrustration >= maxFrustration) {
                    // Agent wpada w furię
                    human.setWearingMask(false);
                    human.setMovementStrategy(maliciousMovementStrategy);
                    human.setHostile(true);
                    return;
                }
            } else {
                // Udało się wejść do szpitala - uspokaja się
                frustrationMap.put(human, 0);
                human.setHostile(false);
            }

            human.setMovementStrategy(hospitalMovementStrategy);
        } else {
            // Zdrowy lub wyzdrowiały agent
            frustrationMap.remove(human);
            human.setWantsHospital(false);
            human.setHostile(false); //
            human.setMovementStrategy(normalMovementStrategy);
        }
    }
}