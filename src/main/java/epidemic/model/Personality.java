package epidemic.model;

import epidemic.strategies.decision.DecisionStrategy;

/**
 * Kapsułkuje profil psychologiczny agenta ludzkiego.
 * Pełni rolę Kontekstu we wzorcu projektowym Strategia,
 * wstrzymując się od samodzielnej ewaluacji zachowań i delegując proces decyzyjny
 * do wstrzykniętej instancji {@link DecisionStrategy}.
 */
public class Personality {

    private DecisionStrategy decisionStrategy;

    /**
     * Konstruuje nowy profil psychologiczny.
     *
     * @param decisionStrategy Konkretna implementacja strategii podejmowania decyzji
     *                         (np. racjonalna, spanikowana, mściwa).
     */
    public Personality(DecisionStrategy decisionStrategy) {
        this.decisionStrategy = decisionStrategy;
    }

    /**
     * Aktualizuje stan umysłowy i behawioralny agenta na podstawie telemetrii otaczającego go świata.
     * Wywołanie tej metody skutkuje ewaluacją wstrzykniętej strategii decyzyjnej,
     * co może prowadzić do zmiany stanu wyposażenia agenta (maski) lub zmiany jego wektora ruchu.
     *
     * @param human Agent docelowy poddawany procesom kognitywnym.
     * @param context Globalny zbiór informacji o środowisku symulacji dostarczany przez silnik.
     */
    public void updateMentalState(Human human, WorldContext context) {
        decisionStrategy.makeDecision(human, context);
    }

    /**
     * Pobiera instancję strategii decyzyjnej sterującą daną osobowością.
     *
     * @return Obiekt implementujący interfejs DecisionStrategy.
     */
    public DecisionStrategy getDecisionStrategy() {
        return decisionStrategy;
    }
}