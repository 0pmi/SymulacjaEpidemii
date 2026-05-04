package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;

/**
 * Pasywna implementacja strategii ruchu (wzorzec Strategy).
 * Agent z przypisaną tą strategią pozostaje w absolutnym bezruchu, ignorując
 * wszelkie bodźce środowiskowe. Strategia ta jest optymalna dla jednostek
 * martwych, poddanych rygorystycznej kwarantannie lub znajdujących się
 * w zaawansowanym stadium choroby uniemożliwiającym lokomocję.
 */
public class StaticStrategy implements MovementStrategy {

    /**
     * Zwraca aktualną pozycję agenta bez dokonywania jakichkolwiek modyfikacji.
     *
     * @param agent Agent, dla którego ewaluowana jest pozycja.
     * @param world Stan mapy symulacyjnej.
     * @return Bieżące współrzędne agenta, gwarantujące pozostanie w miejscu.
     */
    @Override
    public Point2D calculateNextPosition(Agent agent, WorldMap world) {
        return agent.getPosition();
    }
}