package epidemic.model;

import epidemic.service.Config;
import epidemic.strategies.movement.MovementStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AgentTest {

    private MockedStatic<Config> mockedConfig;
    private MovementStrategy mockMovementStrategy;
    private SpeciesType mockSpeciesType;
    private TestAgent agent;

    // Klasa pomocnicza (Dummy) do testowania klasy abstrakcyjnej
    private static class TestAgent extends Agent {
        public TestAgent(Point2D position, int age, SpeciesType speciesType, double baseSpeed, MovementStrategy movementStrategy) {
            super(position, age, speciesType, baseSpeed, movementStrategy);
        }
    }

    @BeforeEach
    void setUp() {
        // Mockowanie konfiguracji statycznej, która odpala się w konstruktorze Agenta
        mockedConfig = Mockito.mockStatic(Config.class);
        mockedConfig.when(() -> Config.getDouble("agent.defaultNaturalMortality", 0.01)).thenReturn(0.01);
        mockedConfig.when(() -> Config.getDouble("agent.defaultVulnerability", 1.0)).thenReturn(1.0);

        mockMovementStrategy = mock(MovementStrategy.class);
        mockSpeciesType = mock(SpeciesType.class);
        when(mockSpeciesType.getBaseVirulence()).thenReturn(0.5);

        Point2D startPosition = new Point2D(0, 0);
        agent = new TestAgent(startPosition, 20, mockSpeciesType, 2.5, mockMovementStrategy);
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    @Test
    void shouldIncrementAgeByOne() {
        agent.incrementAge();
        assertEquals(21, agent.getAge(), "Wiek agenta powinien się zwiększyć z 20 do 21");
    }

    @Test
    void shouldNotBeInfectableIfDead() {
        agent.setDead(true);
        agent.setHealthStatus(HealthStatus.HEALTHY);

        assertFalse(agent.canBeInfected(), "Martwy agent nie może zostać zainfekowany");
    }

    @Test
    void shouldNotBeInfectableIfAlreadySick() {
        agent.setDead(false);
        agent.setHealthStatus(HealthStatus.SICK);

        assertFalse(agent.canBeInfected(), "Chory agent nie może zostać ponownie zainfekowany");
    }

    @Test
    void shouldBeInfectableIfHealthyAndAlive() {
        agent.setDead(false);
        agent.setHealthStatus(HealthStatus.HEALTHY);

        assertTrue(agent.canBeInfected(), "Zdrowy i żywy agent powinien być podatny na infekcję");
    }

    @Test
    void shouldDecrementInfectionTimer() {
        agent.setRemainingInfectionEpochs(3);

        agent.decrementInfectionTimer();
        assertEquals(2, agent.getRemainingInfectionEpochs());

        agent.decrementInfectionTimer();
        agent.decrementInfectionTimer();
        assertEquals(0, agent.getRemainingInfectionEpochs());

        // Test zabezpieczenia przed wejściem na wartości ujemne
        agent.decrementInfectionTimer();
        assertEquals(0, agent.getRemainingInfectionEpochs(), "Licznik epok infekcji nie powinien spaść poniżej 0");
    }

    @Test
    void shouldReturnCorrectVirulenceFromSpeciesType() {
        assertEquals(0.5, agent.getVirulence(), "Metoda getVirulence powinna delegować wywołanie do przypisanego SpeciesType");
    }
}