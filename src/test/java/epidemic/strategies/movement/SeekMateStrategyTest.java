package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.HealthStatus;
import epidemic.model.Point2D;
import epidemic.model.SpeciesType;
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
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Zestaw testów jednostkowych dla strategii poszukiwania partnera.
 * Weryfikuje poprawne filtrowanie kandydatów na podstawie gatunku, wieku i stanu zdrowia
 * oraz sprawdza fallback do ruchu losowego w przypadku braku partnerów.
 */
class SeekMateStrategyTest {

    private MockedStatic<Config> mockedConfig;
    private SeekMateStrategy strategy;
    private Agent mockAgent;
    private WorldMap mockWorld;

    @BeforeEach
    void setUp() {
        mockedConfig = Mockito.mockStatic(Config.class);
        // Parametry potrzebne do logiki strategii
        mockedConfig.when(() -> Config.getDouble("movement.seekMate.radius", 15.0)).thenReturn(15.0);
        mockedConfig.when(() -> Config.getInt("movement.random.stepRange", 3)).thenReturn(3);

        strategy = new SeekMateStrategy();
        mockAgent = mock(Agent.class);
        mockWorld = mock(WorldMap.class);

        // Ustalenie podstawowych atrybutów "naszego" agenta
        when(mockAgent.getPosition()).thenReturn(new Point2D(10, 10));
        when(mockAgent.getSpeciesType()).thenReturn(SpeciesType.HUMAN);
        when(mockAgent.getAge()).thenReturn(25);
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    /**
     * Weryfikuje czy agent zbliża się do odpowiedniego kandydata.
     */
    @Test
    void shouldMoveTowardsValidMate() {
        Agent validMate = mock(Agent.class);
        when(validMate.getPosition()).thenReturn(new Point2D(12, 10)); // Na wschód
        when(validMate.getSpeciesType()).thenReturn(SpeciesType.HUMAN);
        when(validMate.getAge()).thenReturn(20); // Dorosły
        when(validMate.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);

        when(mockWorld.getNeighbors(any(), anyDouble())).thenReturn(List.of(validMate));

        Point2D nextPos = strategy.calculateNextPosition(mockAgent, mockWorld);

        assertEquals(11, nextPos.x(), "Agent powinien ruszyć na wschód (X+1) w stronę partnera");
        assertEquals(10, nextPos.y(), "Pozycja Y nie powinna ulec zmianie");
    }

    /**
     * Weryfikuje czy algorytm odrzuca jednostki chore, innego gatunku lub niepełnoletnie.
     */
    @Test
    void shouldIgnoreInvalidMatesAndFallbackToRandomWalk() {
        // Kandydat 1: Chory
        Agent sickMate = mock(Agent.class);
        when(sickMate.getPosition()).thenReturn(new Point2D(11, 10)); // Zabezpieczenie przed NPE
        when(sickMate.getSpeciesType()).thenReturn(SpeciesType.HUMAN);
        when(sickMate.getAge()).thenReturn(25);
        when(sickMate.getHealthStatus()).thenReturn(HealthStatus.SICK);

        // Kandydat 2: Dziecko
        Agent childMate = mock(Agent.class);
        when(childMate.getPosition()).thenReturn(new Point2D(12, 10)); // Zabezpieczenie przed NPE
        when(childMate.getSpeciesType()).thenReturn(SpeciesType.HUMAN);
        when(childMate.getAge()).thenReturn(10);
        when(childMate.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);

        // Kandydat 3: Pies
        Agent dogMate = mock(Agent.class);
        when(dogMate.getPosition()).thenReturn(new Point2D(13, 10)); // Zabezpieczenie przed NPE
        when(dogMate.getSpeciesType()).thenReturn(SpeciesType.DOG);
        when(dogMate.getAge()).thenReturn(5);
        when(dogMate.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);

        when(mockWorld.getNeighbors(any(), anyDouble())).thenReturn(List.of(sickMate, childMate, dogMate));

        Point2D startPos = mockAgent.getPosition();
        Point2D nextPos = strategy.calculateNextPosition(mockAgent, mockWorld);

        int dx = Math.abs(nextPos.x() - startPos.x());
        int dy = Math.abs(nextPos.y() - startPos.y());

        // Powinien zignorować wszystkich i wykonać ruch losowy (max o 1 kratkę)
        assertTrue(dx <= 1 && dy <= 1, "Agent powinien wykonać ruch losowy w obliczu braku prawidłowych kandydatów");
    }

    /**
     * Weryfikuje mechanizm zabezpieczający w przypadku całkowicie pustej mapy.
     */
    @Test
    void shouldFallbackToRandomWalkWhenNoNeighbors() {
        when(mockWorld.getNeighbors(any(), anyDouble())).thenReturn(Collections.emptyList());

        Point2D startPos = mockAgent.getPosition();
        Point2D nextPos = strategy.calculateNextPosition(mockAgent, mockWorld);

        int dx = Math.abs(nextPos.x() - startPos.x());
        int dy = Math.abs(nextPos.y() - startPos.y());

        assertTrue(dx <= 1 && dy <= 1, "Agent musi mieć zdefiniowany wektor ruchu nawet na pustym polu");
    }
}