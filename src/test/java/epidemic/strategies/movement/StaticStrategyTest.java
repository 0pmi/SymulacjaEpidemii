package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StaticStrategyTest {

    @Test
    void shouldReturnSamePosition() {
        StaticStrategy strategy = new StaticStrategy();
        Agent mockAgent = mock(Agent.class);
        WorldMap mockWorld = mock(WorldMap.class);

        Point2D currentPosition = new Point2D(42, 15);
        when(mockAgent.getPosition()).thenReturn(currentPosition);

        Point2D nextPosition = strategy.calculateNextPosition(mockAgent, mockWorld);

        assertEquals(currentPosition, nextPosition, "Statyczna strategia nie powinna zmieniać pozycji agenta");
    }
}