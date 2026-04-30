package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.Hospital;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SeekHospitalStrategyTest {

    private SeekHospitalStrategy strategy;
    private Agent mockAgent;
    private WorldMap mockWorld;

    @BeforeEach
    void setUp() {
        strategy = new SeekHospitalStrategy();
        mockAgent = mock(Agent.class);
        mockWorld = mock(WorldMap.class);
    }

    @Test
    void shouldStayInPlaceIfNoHospitalsOnMap() {
        Point2D agentPos = new Point2D(10, 10);
        when(mockAgent.getPosition()).thenReturn(agentPos);
        when(mockWorld.getHospitals()).thenReturn(Collections.emptyList());

        Point2D nextPos = strategy.calculateNextPosition(mockAgent, mockWorld);

        assertEquals(agentPos, nextPos, "Agent bez dostępu do szpitali powinien zostać w miejscu");
    }

    @Test
    void shouldMoveTowardsTheOnlyHospital() {
        Point2D agentPos = new Point2D(10, 10);
        when(mockAgent.getPosition()).thenReturn(agentPos);

        Hospital mockHospital = mock(Hospital.class);
        // Szpital jest na północny-wschód od agenta
        when(mockHospital.getPosition()).thenReturn(new Point2D(15, 5));
        when(mockWorld.getHospitals()).thenReturn(List.of(mockHospital));

        Point2D nextPos = strategy.calculateNextPosition(mockAgent, mockWorld);

        // Oczekuję ruchu x+1 (w stronę 15) oraz y-1 (w stronę 5)
        assertEquals(11, nextPos.x());
        assertEquals(9, nextPos.y());
    }

    @Test
    void shouldChooseTheNearestHospitalWhenMultipleAvailable() {
        Point2D agentPos = new Point2D(10, 10);
        when(mockAgent.getPosition()).thenReturn(agentPos);

        Hospital farHospital = mock(Hospital.class);
        when(farHospital.getPosition()).thenReturn(new Point2D(100, 100)); // Odległość ~127

        Hospital nearHospital = mock(Hospital.class);
        when(nearHospital.getPosition()).thenReturn(new Point2D(8, 10)); // Odległość 2 na zachód

        when(mockWorld.getHospitals()).thenReturn(List.of(farHospital, nearHospital));

        Point2D nextPos = strategy.calculateNextPosition(mockAgent, mockWorld);

        // Oczekuję kroku w stronę bliskiego szpitala: x-1, y bez zmian
        assertEquals(9, nextPos.x());
        assertEquals(10, nextPos.y());
    }

    @Test
    void shouldStayInPlaceIfAlreadyAtHospital() {
        Point2D agentPos = new Point2D(20, 20);
        when(mockAgent.getPosition()).thenReturn(agentPos);

        Hospital exactHospital = mock(Hospital.class);
        when(exactHospital.getPosition()).thenReturn(new Point2D(20, 20)); // Agent jest dokładnie na szpitalu
        when(mockWorld.getHospitals()).thenReturn(List.of(exactHospital));

        Point2D nextPos = strategy.calculateNextPosition(mockAgent, mockWorld);

        // Oczekuję braku zmiany współrzędnych
        assertEquals(20, nextPos.x());
        assertEquals(20, nextPos.y());
    }
}