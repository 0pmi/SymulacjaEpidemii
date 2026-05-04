package epidemic.strategies.decision;

import epidemic.model.HealthStatus;
import epidemic.model.Human;
import epidemic.model.WorldContext;
import epidemic.service.Config;
import epidemic.strategies.movement.MovementStrategy;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Strategia decyzyjna (wzorzec Strategy) modelująca racjonalne i zrównoważone podejście agenta.
 * Jednostka adaptuje się do sytuacji w sposób analityczny: stosuje dystansowanie społeczne
 * przy wysokim odsetku zakażeń, udaje się do placówek medycznych na szczepienia prewencyjne
 * lub leczenie w zaawansowanym stadium infekcji, a w czasie spokoju wykazuje naturalne
 * potrzeby prokreacyjne.
 */
public class RationalDecisionStrategy implements DecisionStrategy {

    private final MovementStrategy hospitalMovementStrategy;
    private final MovementStrategy distancingMovementStrategy;
    private final MovementStrategy normalMovementStrategy;
    private final MovementStrategy seekMateMovementStrategy;

    /**
     * Inicjalizuje strategię racjonalną z odpowiednim zestawem wstrzykniętych zachowań ruchowych.

     * @param hospitalMovementStrategy Strategia wyznaczająca trasę do najbliższej placówki medycznej.
     * @param distancingMovementStrategy Strategia zachowania dystansu i unikania skupisk ludzkich.
     * @param normalMovementStrategy Pasywny wzorzec ruchu stosowany w bezpiecznym środowisku.
     * @param seekMateMovementStrategy Prokreacyjna strategia poszukiwania partnera do rozrodu.
     */
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

    /**
     * Przeprowadza wieloetapową ewaluację stanu agenta w oparciu o hierarchię potrzeb i logikę racjonalną.
     * Ozdrowieńcy odrzucają obostrzenia i wracają do normy. Agenci w zaawansowanym stadium choroby
     * lub chętni na szczepienia priorytetyzują wizytę w szpitalu. W przypadku wysokiego wskaźnika
     * infekcji w populacji agent zakłada maskę i aktywuje protokół dystansowania społecznego.
     *
     * @param human Agent poddawany procesom decyzyjnym.
     * @param world Aktualna telemetria świata (współczynnik zakażeń, dostępność szczepień).
     */
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

    /*
     * Wyznacza strategię ruchu w przypadku braku aktywnych zagrożeń lub nagłych potrzeb medycznych.
     * Weryfikuje gotowość zdrowego agenta do podjęcia aktywnego poszukiwania partnera na podstawie
     * jego wieku i prawdopodobieństwa zdefiniowanego w konfiguracji.
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