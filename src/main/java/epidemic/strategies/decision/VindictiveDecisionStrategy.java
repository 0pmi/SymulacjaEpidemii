package epidemic.strategies.decision;

import epidemic.model.HealthStatus;
import epidemic.model.Human;
import epidemic.model.WorldContext;
import epidemic.service.Config;
import epidemic.strategies.movement.MovementStrategy;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Złośliwa strategia decyzyjna (wzorzec Strategy) oparta na rosnącym wskaźniku frustracji jednostki.
 * Agent zachowuje się zgodnie ze standardami społecznymi do momentu, w którym system ochrony zdrowia
 * odmawia mu dostępu do leczenia (np. ze względu na przepełnione szpitale).
 * Przekroczenie progu cierpliwości skutkuje wpadnięciem w furię, odrzuceniem środków ochronnych
 * i celowym rozsiewaniem patogenu na innych agentów.
 */
public class VindictiveDecisionStrategy implements DecisionStrategy {

    private final MovementStrategy maliciousMovementStrategy;
    private final MovementStrategy hospitalMovementStrategy;
    private final MovementStrategy normalMovementStrategy;

    /**
     * Mapa śledząca licznik frustracji przypisany do konkretnego agenta.
     * Wykorzystanie struktury {@link WeakHashMap} gwarantuje, że wpisy dla zmarłych
     * lub usuniętych z mapy agentów zostaną automatycznie zebrane przez Garbage Collector,
     * zapobiegając wyciekom pamięci w długo trwających symulacjach.
     */
    private final Map<Human, Integer> frustrationMap = new WeakHashMap<>();

    /**
     * Inicjalizuje mściwą strategię decyzyjną.
     *
     * @param maliciousMovementStrategy Wzorzec ruchu aktywowany po wpadnięciu w furię (agresywne podążanie za ofiarami).
     * @param hospitalMovementStrategy Strategia wyznaczająca trasę do placówki medycznej.
     * @param normalMovementStrategy Standardowy wzorzec ruchu stosowany przed wybuchem frustracji lub po wyleczeniu.
     */
    public VindictiveDecisionStrategy(
            MovementStrategy maliciousMovementStrategy,
            MovementStrategy hospitalMovementStrategy,
            MovementStrategy normalMovementStrategy) {
        this.maliciousMovementStrategy = maliciousMovementStrategy;
        this.hospitalMovementStrategy = hospitalMovementStrategy;
        this.normalMovementStrategy = normalMovementStrategy;
    }

    /**
     * Weryfikuje cierpliwość chorego agenta oczekującego na przyjęcie do placówki medycznej.
     * Każda epoka spędzona poza oddziałem zwiększa licznik frustracji. Osiągnięcie limitu
     * wyzwala status wrogości (Hostile). Udane wejście do szpitala lub całkowite wyleczenie
     * automatycznie resetuje ten stan i uspokaja agenta.
     *
     * @param human Agent poddawany ewaluacji i presji psychologicznej.
     * @param world Stan środowiska symulacji.
     */
    @Override
    public void makeDecision(Human human, WorldContext world) {
        if (human.getHealthStatus() == HealthStatus.SICK) {
            human.setWantsHospital(true);

            if (!human.isInHospital()) {
                int currentFrustration = frustrationMap.getOrDefault(human, 0) + 1;
                frustrationMap.put(human, currentFrustration);

                int maxFrustration = Config.getInt("vindictive.frustrationThreshold", 10);

                if (currentFrustration >= maxFrustration) {
                    human.setWearingMask(false);
                    human.setMovementStrategy(maliciousMovementStrategy);
                    human.setHostile(true);
                    return;
                }
            } else {
                frustrationMap.put(human, 0);
                human.setHostile(false);
            }

            human.setMovementStrategy(hospitalMovementStrategy);
        } else {
            frustrationMap.remove(human);
            human.setWantsHospital(false);
            human.setHostile(false); //
            human.setMovementStrategy(normalMovementStrategy);
        }
    }
}