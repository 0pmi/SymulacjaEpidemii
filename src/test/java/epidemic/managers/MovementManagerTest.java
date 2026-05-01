package epidemic.managers;

import epidemic.model.*;
import epidemic.strategies.movement.MovementStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class MovementManagerTest {

    private MovementManager movementManager;
    private WorldMap mockWorld;

    // Pomocniczy interfejs symulujący agenta będącego użytkownikiem szpitala
    private interface MockHospitalUser extends HospitalUser {}
    private abstract static class TestAgent extends Agent implements MockHospitalUser {
        public TestAgent(Point2D pos, MovementStrategy strat) {
            super(pos, 20, SpeciesType.HUMAN, 1.0, strat);
        }
    }

    @BeforeEach
    void setUp() {
        movementManager = new MovementManager();
        mockWorld = mock(WorldMap.class);
    }

    @Test
    void shouldMoveAgentIfWithinBounds() {
        Agent mockAgent = mock(Agent.class);
        MovementStrategy mockStrategy = mock(MovementStrategy.class);
        Point2D targetPos = new Point2D(5, 5);

        when(mockWorld.getAgents()).thenReturn(List.of(mockAgent));
        when(mockAgent.getMovementStrategy()).thenReturn(mockStrategy);
        when(mockStrategy.calculateNextPosition(mockAgent, mockWorld)).thenReturn(targetPos);
        when(mockWorld.isWithinBounds(targetPos)).thenReturn(true);

        movementManager.moveAgents(mockWorld);

        verify(mockAgent).setPosition(targetPos);
        verify(mockWorld).rebuildSpatialIndex();
    }

    @Test
    void shouldNotMoveAgentIfOutOfBounds() {
        Agent mockAgent = mock(Agent.class);
        MovementStrategy mockStrategy = mock(MovementStrategy.class);
        Point2D outOfBoundsPos = new Point2D(-1, 5);

        when(mockWorld.getAgents()).thenReturn(List.of(mockAgent));
        when(mockAgent.getMovementStrategy()).thenReturn(mockStrategy);
        when(mockStrategy.calculateNextPosition(mockAgent, mockWorld)).thenReturn(outOfBoundsPos);
        when(mockWorld.isWithinBounds(outOfBoundsPos)).thenReturn(false);

        movementManager.moveAgents(mockWorld);

        verify(mockAgent, never()).setPosition(outOfBoundsPos);
    }

    @Test
    void shouldSkipMovementForAdmittedPatients() {
        TestAgent mockPatient = mock(TestAgent.class);

        when(mockWorld.getAgents()).thenReturn(List.of(mockPatient));
        when(mockPatient.isInHospital()).thenReturn(true);

        movementManager.moveAgents(mockWorld);

        // Upewniam się, że nie wywołano logiki ruchu dla pacjenta
        verify(mockPatient, never()).getMovementStrategy();
    }

    @Test
    void shouldEnterHospitalIfAvailableAndDesired() {
        TestAgent mockUser = mock(TestAgent.class);
        MovementStrategy mockStrategy = mock(MovementStrategy.class);
        Point2D hospitalPos = new Point2D(10, 10);
        Hospital mockHospital = mock(Hospital.class);

        when(mockWorld.getAgents()).thenReturn(List.of(mockUser));
        when(mockUser.isInHospital()).thenReturn(false);
        when(mockUser.getMovementStrategy()).thenReturn(mockStrategy);
        when(mockStrategy.calculateNextPosition(mockUser, mockWorld)).thenReturn(hospitalPos);
        when(mockWorld.isWithinBounds(hospitalPos)).thenReturn(true);
        when(mockUser.getPosition()).thenReturn(hospitalPos); // Po rzekomym przeniesieniu na pole

        when(mockWorld.getHospitalAt(hospitalPos)).thenReturn(mockHospital);
        when(mockUser.isWantsHospital()).thenReturn(true);
        when(mockHospital.addPatient(mockUser)).thenReturn(true);

        movementManager.moveAgents(mockWorld);

        verify(mockUser).setIsInHospital(true);
    }
}