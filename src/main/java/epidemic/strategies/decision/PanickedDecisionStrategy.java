package epidemic.strategies.decision;

import epidemic.model.HealthStatus;
import epidemic.model.Human;
import epidemic.model.WorldContext;
import epidemic.service.Config;
import epidemic.strategies.movement.MovementStrategy;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Strategia decyzyjna (wzorzec Strategy) modelująca zachowanie jednostek wykazujących panikę.
 * Agent z tym profilem posiada bardzo niski próg tolerancji na zagrożenie w otoczeniu.
 * Przekroczenie tego progu wyzwala natychmiastowe założenie maseczki ochronnej oraz
 * przejście w tryb chaotycznej ucieczki (bądź rygorystycznego dystansowania).
 */
public class PanickedDecisionStrategy implements DecisionStrategy {

    private final MovementStrategy panicMovementStrategy;
    private final MovementStrategy calmMovementStrategy;
    private final MovementStrategy hospitalMovementStrategy;
    private final MovementStrategy seekMateMovementStrategy;

    /**
     * Inicjalizuje strategię paniczną z odpowiednim zestawem wstrzykniętych zachowań ruchowych.
     *
     * @param panicMovementStrategy Wzorzec ruchu aktywowany po przekroczeniu progu paniki.
     * @param calmMovementStrategy Standardowy wzorzec ruchu w stanie spoczynku.
     * @param hospitalMovementStrategy Strategia wyznaczająca trasę do najbliższej placówki medycznej.
     * @param seekMateMovementStrategy Opcjonalna strategia poszukiwania partnera do rozrodu.
     */
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

    /**
     * Przeprowadza ewaluację stanu psychicznego agenta na podstawie progu tolerancji zdefiniowanego w konfiguracji.
     * Ozdrowieńcy natychmiastowo porzucają środki ochrony i wracają do stanu spoczynku.
     * Osobniki chore priorytetyzują udanie się do szpitala.
     * Zdrowi agenci, po przekroczeniu minimalnego progu zakażeń w społeczeństwie,
     * wpadają w panikę, nakładając maski i zmieniając wzorzec poruszania się na ucieczkę.
     *
     * @param human Agent podejmujący decyzję.
     * @param world Aktualny odczyt parametrów środowiskowych.
     */
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

    /*
     * Wyznacza optymalną strategię ruchu w stanie spoczynku (gdy agent nie panikuje i nie jest chory).
     * Weryfikuje kryteria biologiczne – jeśli agent osiągnął dojrzałość oraz zdrowie dopisuje,
     * istnieje określone prawdopodobieństwo przejścia w tryb poszukiwania partnera do rozrodu.
     * W przeciwnym razie przywracana jest strategia standardowa.
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