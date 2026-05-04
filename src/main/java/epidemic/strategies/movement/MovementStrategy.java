package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;

/**
 * Główny interfejs definiujący kontrakt dla wszystkich algorytmów lokomocji agentów (wzorzec Strategy).
 * Pozwala na polimorficzne wyliczanie kolejnej pozycji agenta w środowisku,
 * bazując na jego aktualnym stanie psychofizycznym i odczytach z otoczenia,
 * zapewniając jednocześnie łatwą rozszerzalność o nowe wzorce zachowań.
 */
public interface MovementStrategy {

    /**
     * Oblicza docelową pozycję agenta w następnej epoce symulacji.
     * <p>
     * UWAGA: Zgodnie z dobrymi praktykami, metoda ta nie modyfikuje wewnętrznego
     * stanu agenta (np. nie nadpisuje jego koordynatów). Jej jedyną odpowiedzialnością
     * jest zwrócenie proponowanych współrzędnych, których walidacją i aplikacją
     * zajmuje się scentralizowany Menedżer Ruchu.
     * </p>
     *
     * @param agent Agent, dla którego ewaluowany jest wektor ruchu.
     * @param world Stan mapy symulacyjnej, dostarczający wiedzy o przeszkodach i innych jednostkach.
     * @return Nowa pozycja w dwuwymiarowej przestrzeni, na którą agent planuje się przemieścić.
     */
    Point2D calculateNextPosition(Agent agent, WorldMap world);
}