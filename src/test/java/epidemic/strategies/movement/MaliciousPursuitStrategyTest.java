package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.HealthStatus;
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
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Zestaw testów dla strategii złośliwego pościgu.
 * Weryfikuje wyznaczanie wektorów ataku na zdrowe ofiary oraz ignorowanie pozostałych.
 */
class MaliciousPursuitStrategyTest {

    private MockedStatic<Config> mockedConfig;
    private MaliciousPursuitStrategy strategy;
    private Agent mockAttacker;
    private WorldMap mockWorld;

    @BeforeEach
    void setUp() {
        mockedConfig = Mockito.mockStatic(Config.class);
        mockedConfig.when(() -> Config.getDouble("movement.malicious.radius", 10.0)).thenReturn(10.0);
        mockedConfig.when(() -> Config.getInt("movement.random.stepRange", 3)).thenReturn(3);

        strategy = new MaliciousPursuitStrategy();
        mockAttacker = mock(Agent.class);
        mockWorld = mock(WorldMap.class);

        when(mockAttacker.getPosition()).thenReturn(new Point2D(20, 20));
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    /**
     * Weryfikuje pościg w linii prostej na najbliższą ofiarę.
     */
    @Test
    void shouldMoveDirectlyTowardsHealthyVictim() {
        Agent victim = mock(Agent.class);
        when(victim.getPosition()).thenReturn(new Point2D(20, 15)); // Na północ
        when(victim.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);

        when(mockWorld.getNeighbors(any(), anyDouble())).thenReturn(List.of(victim));

        Point2D nextPos = strategy.calculateNextPosition(mockAttacker, mockWorld);

        assertEquals(20, nextPos.x(), "Pozycja X nie powinna ulec zmianie");
        assertEquals(19, nextPos.y(), "Agent powinien ruszyć na północ (Y-1) w stronę ofiary");
    }

    /**
     * Sprawdza mechanizm filtrowania - mściciel ignoruje innych chorych.
     */
    @Test
    void shouldIgnoreSickOrRecoveredAgents() {
        // Obiekty, które mściciel powinien zignorować
        Agent sickAgent = mock(Agent.class);
        when(sickAgent.getHealthStatus()).thenReturn(HealthStatus.SICK);

        Agent recoveredAgent = mock(Agent.class);
        when(recoveredAgent.getHealthStatus()).thenReturn(HealthStatus.RECOVERED);

        when(mockWorld.getNeighbors(any(), anyDouble())).thenReturn(List.of(sickAgent, recoveredAgent));

        Point2D startPos = mockAttacker.getPosition();
        Point2D nextPos = strategy.calculateNextPosition(mockAttacker, mockWorld);

        int dx = Math.abs(nextPos.x() - startPos.x());
        int dy = Math.abs(nextPos.y() - startPos.y());

        // Skoro nie ma ofiar, atakujący wpada w fallback (błądzenie losowe)
        assertTrue(dx <= 1 && dy <= 1, "Agent powinien wykonać ruch losowy w obliczu braku prawidłowych celów");
    }

    /**
     * Zabezpieczenie na wypadek pustej okolicy (brak sąsiadów w promieniu skanowania).
     */
    @Test
    void shouldFallbackToRandomWalkWhenNoTargetsInRadius() {
        when(mockWorld.getNeighbors(any(), anyDouble())).thenReturn(Collections.emptyList());

        Point2D startPos = mockAttacker.getPosition();
        Point2D nextPos = strategy.calculateNextPosition(mockAttacker, mockWorld);

        int dx = Math.abs(nextPos.x() - startPos.x());
        int dy = Math.abs(nextPos.y() - startPos.y());

        assertTrue(dx <= 1 && dy <= 1, "Złośliwy agent błąka się losowo szukając celu");
    }
}