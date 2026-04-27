package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.engine.WorldMap;

public interface MovementStrategy {
    void move(Agent agent, WorldMap world);
}