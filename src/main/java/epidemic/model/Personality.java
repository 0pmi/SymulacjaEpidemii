package epidemic.model;

import epidemic.strategies.decision.DecisionStrategy;

/**
 * Kapsułkuje profil psychologiczny agenta ludzkiego.
 * Pełni rolę Kontekstu (Context) we wzorcu projektowym Strategia,
 * delegując właściwe podejmowanie decyzji do wstrzykniętej strategii decyzyjnej.
 */
public class Personality {

    private DecisionStrategy decisionStrategy;

    public Personality(DecisionStrategy decisionStrategy) {
        this.decisionStrategy = decisionStrategy;
    }

    /**
     * Aktualizuje stan umysłowy i behawioralny agenta na podstawie otaczającego go świata.
     * Metoda zazwyczaj wywoływana raz na epokę symulacyjną.
     *
     * @param human Agent, którego stan jest aktualizowany.
     * @param context Globalny kontekst środowiska.
     */
    public void updateMentalState(Human human, WorldContext context) {
        decisionStrategy.makeDecision(human, context);
    }

    public DecisionStrategy getDecisionStrategy() {
        return decisionStrategy;
    }
}