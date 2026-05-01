package epidemic.model;

import epidemic.service.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

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

    @Test
    void shouldAddAndRetrieveInfectionField() {
        WorldMap map = new WorldMap(100, 100, 5.0);
        Point2D pos = new Point2D(10, 20); // Zmiana na wartości całkowite (int)

        map.addOrRefreshInfectionField(pos, 0.8);

        InfectionField retrieved = map.getFieldAt(new Point2D(10, 20));
        assertNotNull(retrieved);
        assertEquals(0.8, retrieved.getInfectivity());
        assertEquals(1, map.getActiveFields().size());
    }

    @Test
    void shouldDecayAndRemoveExpiredFields() {
        try (MockedStatic<Config> mockedConfig = Mockito.mockStatic(Config.class)) {
            mockedConfig.when(() -> Config.getInt("infectionField.defaultExpiration", 10)).thenReturn(1);
            mockedConfig.when(() -> Config.getDouble("infectionField.dissipationRate", 0.05)).thenReturn(0.5);
            mockedConfig.when(() -> Config.getDouble("infectionField.minInfectivity", 0.01)).thenReturn(0.01);

            WorldMap map = new WorldMap(100, 100, 5.0);
            map.addOrRefreshInfectionField(new Point2D(5, 5), 0.5);

            assertEquals(1, map.getActiveFields().size());

            // Ponieważ expiration wynosi 1, po jednym decay() spadnie do 0 i powinno zniknąć
            map.decayInfectionFields();

            assertEquals(0, map.getActiveFields().size(), "Wygasłe pola powinny zostać usunięte z mapy");
        }
    }
}