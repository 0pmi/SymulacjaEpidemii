package epidemic.strategies.decision;

import epidemic.model.Human;
import epidemic.model.WorldContext;

/**
 * Interfejs definiujący kontrakt dla strategii decyzyjnych ludzkich agentów.
 * Implementacje tego interfejsu modelują różne typy zachowań (np. racjonalne, w panice)
 * w odpowiedzi na zmieniające się warunki środowiskowe.
 */
public interface DecisionStrategy {

    /**
     * Oblicza i aplikuje decyzje do stanu agenta (np. założenie maski, chęć udania się do szpitala, zmiana stylu poruszania się).
     *
     * @param human Agent, który podejmuje decyzję.
     * @param world Aktualny stan symulacji (np. odsetek zakażeń, dostępność szczepionek).
     */
    void makeDecision(Human human, WorldContext world);
}