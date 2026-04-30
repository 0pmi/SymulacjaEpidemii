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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RandomWalkStrategyTest {

    private MockedStatic<Config> mockedConfig;
    private RandomWalkStrategy strategy;
    private Agent mockAgent;
    private WorldMap mockWorld;

    @BeforeEach
    void setUp() {
        mockedConfig = Mockito.mockStatic(Config.class);
        // Wymusza wartość "3", co oznacza przesunięcia {-1, 0, 1}
        mockedConfig.when(() -> Config.getInt("movement.random.stepRange", 3)).thenReturn(3);

        strategy = new RandomWalkStrategy();
        mockAgent = mock(Agent.class);
        mockWorld = mock(WorldMap.class);
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    @Test
    void shouldCalculatePositionWithinConfiguredRange() {
        Point2D startPosition = new Point2D(10, 10);
        when(mockAgent.getPosition()).thenReturn(startPosition);

        // Ponieważ algorytm jest losowy, wykonujemy go wielokrotnie, aby upewnić się, że nigdy nie wychodzi poza ramy
        for (int i = 0; i < 100; i++) {
            Point2D nextPos = strategy.calculateNextPosition(mockAgent, mockWorld);

            int dx = nextPos.x() - startPosition.x();
            int dy = nextPos.y() - startPosition.y();

            assertTrue(dx >= -1 && dx <= 1, "Przesunięcie X powinno zawierać się w przedziale [-1, 1]");
            assertTrue(dy >= -1 && dy <= 1, "Przesunięcie Y powinno zawierać się w przedziale [-1, 1]");
        }
    }
}