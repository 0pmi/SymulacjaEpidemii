package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.WorldMap;

public interface MovementStrategy {
    void move(Agent agent, WorldMap world);
}