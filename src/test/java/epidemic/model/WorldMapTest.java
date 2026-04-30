package epidemic.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WorldMapTest {

    private WorldMap worldMap;

    @BeforeEach
    void setUp() {
        worldMap = new WorldMap(100, 100, 10.0);
    }

    @Test
    void shouldNotModifyAgentsListBeforeApplyChanges() {
        Agent mockAgent = mock(Agent.class);

        worldMap.addAgent(mockAgent);

        // Przed zatwierdzeniem zmian, główna lista powinna być pusta
        assertTrue(worldMap.getAgents().isEmpty());
    }

    @Test
    void shouldApplyChangesAndModifyAgentsList() {
        Agent mockAgent1 = mock(Agent.class);
        Agent mockAgent2 = mock(Agent.class);

        worldMap.addAgent(mockAgent1);
        worldMap.addAgent(mockAgent2);
        worldMap.applyChanges();

        assertEquals(2, worldMap.getAgents().size());
        assertTrue(worldMap.getAgents().contains(mockAgent1));

        worldMap.removeAgent(mockAgent1);
        // Sprawdzenie, czy usunięcie nie działa od razu
        assertEquals(2, worldMap.getAgents().size());

        worldMap.applyChanges();
        assertEquals(1, worldMap.getAgents().size());
        assertFalse(worldMap.getAgents().contains(mockAgent1));
        assertTrue(worldMap.getAgents().contains(mockAgent2));
    }

    @Test
    void shouldFindHospitalAtGivenPosition() {
        Hospital mockHospital = mock(Hospital.class);
        Point2D hospitalPosition = new Point2D(50, 50);
        when(mockHospital.getPosition()).thenReturn(hospitalPosition);

        worldMap.addHospital(mockHospital);

        Hospital found = worldMap.getHospitalAt(new Point2D(50, 50));
        assertNotNull(found);
        assertEquals(mockHospital, found);

        Hospital notFound = worldMap.getHospitalAt(new Point2D(10, 10));
        assertNull(notFound);
    }

    @Test
    void shouldCorrectlyValidateBounds() {
        assertTrue(worldMap.isWithinBounds(new Point2D(0, 0)));
        assertTrue(worldMap.isWithinBounds(new Point2D(99, 99)));
        assertTrue(worldMap.isWithinBounds(new Point2D(50, 50)));

        assertFalse(worldMap.isWithinBounds(new Point2D(-1, 50)));
        assertFalse(worldMap.isWithinBounds(new Point2D(50, -1)));
        assertFalse(worldMap.isWithinBounds(new Point2D(100, 50))); // granica to 99
        assertFalse(worldMap.isWithinBounds(new Point2D(50, 100)));
    }
}