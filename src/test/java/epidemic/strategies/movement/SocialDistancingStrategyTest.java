package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;
import epidemic.service.Config;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SocialDistancingStrategyTest {

    private MockedStatic<Config> mockedConfig;
    private Agent mockAgent;
    private WorldMap mockWorld;
    private SocialDistancingStrategy strategy;

    @BeforeEach
    void setUp() {
        mockedConfig = Mockito.mockStatic(Config.class);
        // Konfiguruje testowy promień przed inicjalizacją strategii (ze względu na przypisanie do pola finalnego)
        mockedConfig.when(() -> Config.getDouble("movement.distancing.radius", 5.0)).thenReturn(5.0);

        strategy = new SocialDistancingStrategy();
        mockAgent = mock(Agent.class);
        mockWorld = mock(WorldMap.class);
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    @Test
    void shouldFleeDirectlyAwayFromSingleNeighbor() {
        Point2D agentPos = new Point2D(5, 5);
        when(mockAgent.getPosition()).thenReturn(agentPos);

        // Symulacja: Sąsiad znajduje się po prawej stronie (X + 2)
        Agent mockNeighbor = mock(Agent.class);
        when(mockNeighbor.getPosition()).thenReturn(new Point2D(7, 5));

        when(mockWorld.getNeighbors(eq(agentPos), eq(5.0))).thenReturn(List.of(mockNeighbor));

        Point2D nextPos = strategy.calculateNextPosition(mockAgent, mockWorld);

        // Agent powinien uciekać w lewo, z dala od sąsiada. Oczekuję kroku dx = -1.
        assertEquals(4, nextPos.x());
        assertEquals(5, nextPos.y());
    }

    @Test
    void shouldCalculateVectorAwayFromMultipleNeighbors() {
        Point2D agentPos = new Point2D(10, 10);
        when(mockAgent.getPosition()).thenReturn(agentPos);

        // Dwóch sąsiadów na północy i wschodzie. Agent powinien uciekać na południowy-zachód (-1, -1)
        Agent neighborNorth = mock(Agent.class);
        when(neighborNorth.getPosition()).thenReturn(new Point2D(10, 12));

        Agent neighborEast = mock(Agent.class);
        when(neighborEast.getPosition()).thenReturn(new Point2D(12, 10));

        when(mockWorld.getNeighbors(eq(agentPos), eq(5.0))).thenReturn(List.of(neighborNorth, neighborEast));

        Point2D nextPos = strategy.calculateNextPosition(mockAgent, mockWorld);

        assertEquals(9, nextPos.x());
        assertEquals(9, nextPos.y());
    }

    @Test
    void shouldFallbackToRandomWhenNoNeighbors() {
        Point2D agentPos = new Point2D(0, 0);
        when(mockAgent.getPosition()).thenReturn(agentPos);
        when(mockWorld.getNeighbors(any(), any(Double.class))).thenReturn(Collections.emptyList());

        // Wykonuje zapytanie wielokrotnie ze względu na losowość
        for(int i = 0; i < 50; i++) {
            Point2D nextPos = strategy.calculateNextPosition(mockAgent, mockWorld);
            assertTrue(nextPos.x() >= -1 && nextPos.x() <= 1);
            assertTrue(nextPos.y() >= -1 && nextPos.y() <= 1);
        }
    }
}