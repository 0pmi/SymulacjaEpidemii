package epidemic.strategies.decision;

import epidemic.model.Human;
import epidemic.model.WorldContext;

/**
 * Interfejs definiujący kontrakt dla kognitywnych strategii decyzyjnych ludzkich agentów (wzorzec Strategy).
 * Implementacje tego interfejsu modelują różnorodne profile psychologiczne (np. racjonalne, podążające za tłumem, w panice),
 * które determinują adaptację jednostki w odpowiedzi na zmieniające się warunki środowiskowe.
 */
public interface DecisionStrategy {

    /**
     * Ewaluuje i aplikuje decyzje behawioralne bezpośrednio do stanu obiektu agenta.
     * Decyzje mogą obejmować założenie środków ochrony osobistej (maski), zgłoszenie chęci
     * hospitalizacji lub zmianę aktualnego wektora poruszania się.
     *
     * @param human Agent docelowy, którego stan i wyposażenie ulegną modyfikacji.
     * @param world Globalny zbiór informacji o świecie (telemetria, dostępność szczepionek), na którym agent opiera swój osąd.
     */
    void makeDecision(Human human, WorldContext world);
}