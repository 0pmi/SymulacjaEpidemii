package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;

/**
 * Główny interfejs dla wszystkich algorytmów poruszania się agentów (Wzorzec Strategii).
 * Pozwala na polimorficzne wyliczanie kolejnej pozycji agenta w środowisku,
 * bazując na jego aktualnym stanie i otoczeniu.
 */
public interface MovementStrategy {

    /**
     * Oblicza docelową pozycję agenta w następnej epoce symulacji.
     * UWAGA: Metoda nie modyfikuje stanu agenta, a jedynie zwraca proponowane współrzędne.
     *
     * @param agent Agent, dla którego obliczany jest ruch.
     * @param world Stan mapy symulacyjnej, dostarczający wiedzy o przeszkodach i innych agentach.
     * @return Nowa pozycja (Punkt w 2D), na którą agent powinien się przemieścić.
     */
    Point2D calculateNextPosition(Agent agent, WorldMap world);
}