package epidemic.factory;

import epidemic.model.Personality;
import epidemic.service.Config;
import epidemic.strategies.decision.*;
import epidemic.strategies.movement.MovementStrategy;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Fabryka odpowiedzialna za kreowanie osobowości (strategii decyzyjnych) agentów.
 * Centralizuje logikę losowania cech na podstawie proporcji zdefiniowanych w globalnej konfiguracji.
 */
public class PersonalityFactory {

    private final MovementStrategy seekHospital;
    private final MovementStrategy distancing;
    private final MovementStrategy normalMove;
    private final MovementStrategy seekMate;
    private final MovementStrategy maliciousPursuit;

    /**
     * Inicjalizuje fabrykę z zestawem bazowych strategii poruszania się.
     * Współdzielenie instancji strategii przez wszystkich agentów (wzorzec Pyłek / Flyweight)
     * znacząco optymalizuje zużycie pamięci operacyjnej.
     *
     * @param seekHospital Strategia udania się do placówki medycznej.
     * @param distancing Strategia zachowania dystansu społecznego i unikania tłumów.
     * @param normalMove Strategia standardowego poruszania się (np. błądzenie losowe).
     * @param seekMate Strategia poszukiwania partnera do rozrodu.
     * @param maliciousPursuit Strategia celowego podążania za ofiarami w celu ich zarażenia.
     */
    public PersonalityFactory(MovementStrategy seekHospital, MovementStrategy distancing,
                              MovementStrategy normalMove, MovementStrategy seekMate,
                              MovementStrategy maliciousPursuit) {
        this.seekHospital = seekHospital;
        this.distancing = distancing;
        this.normalMove = normalMove;
        this.seekMate = seekMate;
        this.maliciousPursuit = maliciousPursuit;
    }

    /**
     * Generuje nową osobowość bazując na rozkładzie prawdopodobieństwa z pliku Config.
     * <p>
     * Metoda uwzględnia parametry {@code human.rationalRatio} oraz {@code human.panickedRatio}.
     * Agenci niełapiący się w powyższe pule domyślnie otrzymują zachowanie mściwe (Vindictive).
     * </p>
     *
     * @return Nowa instancja Personality z odpowiednio przypisaną strategią decyzyjną.
     */
    public Personality generateRandomPersonality() {
        double rationalRatio = Config.getDouble("human.rationalRatio", 0.4);
        double panickedRatio = Config.getDouble("human.panickedRatio", 0.4);
        double rand = ThreadLocalRandom.current().nextDouble();

        if (rand < rationalRatio) {
            return new Personality(new RationalDecisionStrategy(
                    seekHospital, distancing, normalMove, seekMate));
        } else if (rand < rationalRatio + panickedRatio) {
            return new Personality(new PanickedDecisionStrategy(
                    distancing, normalMove, seekHospital, seekMate));
        } else {
            return new Personality(new VindictiveDecisionStrategy(
                    maliciousPursuit, seekHospital, normalMove));
        }
    }
}