package epidemic.engine;

import epidemic.factory.AgentFactory;
import epidemic.model.*;
import epidemic.service.Config;
import epidemic.service.SpatialManager;
import epidemic.statistics.EpochData;
import epidemic.statistics.Observer;
import epidemic.strategies.mortality.MortalityStrategy;
import epidemic.strategies.movement.MovementStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimulationEngineTest {

    private MockedStatic<Config> mockedConfig;
    private WorldMap mockWorld;
    private Virus mockVirus;
    private MortalityStrategy mockMortalityStrategy;
    private AgentFactory mockFactory;
    private SimulationEngine engine;

    @BeforeEach
    void setUp() {
        mockedConfig = Mockito.mockStatic(Config.class);
        mockedConfig.when(() -> Config.getInt("simulation.ageRate", 12)).thenReturn(2); // Agent starzeje się co 2 epoki
        mockedConfig.when(() -> Config.getDouble("simulation.baseContextValue", 0.01)).thenReturn(0.01);

        mockWorld = mock(WorldMap.class);
        mockVirus = mock(Virus.class);
        mockMortalityStrategy = mock(MortalityStrategy.class);
        mockFactory = mock(AgentFactory.class);

        SpatialManager mockSpatialManager = mock(SpatialManager.class);
        when(mockWorld.getSpatialManager()).thenReturn(mockSpatialManager);

        engine = new SimulationEngine(mockWorld, mockVirus, mockMortalityStrategy, mockFactory);
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    @Test
    void shouldNotifyObserversOnRunNextEpoch() {
        Observer mockObserver = mock(Observer.class);
        engine.addObserver(mockObserver);

        // Zapobiegamy wywalaniu się na agentach podczas logiki notifyObservers
        when(mockWorld.getAgents()).thenReturn(new ArrayList<>());

        engine.runNextEpoch();

        // Powinno utworzyć i powiadomić o zerowej populacji w 0 epoce
        verify(mockObserver, times(1)).update(any(EpochData.class));
        verify(mockWorld, times(1)).applyChanges(); // Menedżer zmian powinien też zostać zawołany
    }

    @Test
    void shouldIncrementAgentsAgeWhenRateReached() {
        Agent mockAgent = mock(Agent.class);
        when(mockWorld.getAgents()).thenReturn(List.of(mockAgent));

        // --- DODANE: Uzbrojenie mocka, by przetrwał przejście przez Menedżerów ---
        MovementStrategy mockStrategy = mock(MovementStrategy.class);
        when(mockAgent.getMovementStrategy()).thenReturn(mockStrategy);
        when(mockStrategy.calculateNextPosition(any(), any())).thenReturn(new Point2D(0, 0));
        when(mockAgent.getPosition()).thenReturn(new Point2D(0, 0));
        when(mockAgent.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);
        when(mockAgent.getSpeciesType()).thenReturn(SpeciesType.HUMAN);
        when(mockAgent.isDead()).thenReturn(false);
        // --------------------------------------------------------------------------

        // Epoka 0 (po wykonaniu runNextEpoch, currentEpoch rośnie do 1)
        engine.runNextEpoch();
        verify(mockAgent, never()).incrementAge(); // 1 % 2 != 0

        // Epoka 1 (po wykonaniu runNextEpoch, currentEpoch rośnie do 2)
        engine.runNextEpoch();
        verify(mockAgent, times(1)).incrementAge(); // 2 % 2 == 0 -> starzenie!
    }

    @Test
    void shouldManagePauseAndVaccineState() {
        assertTrue(engine.isPaused(), "Domyślnie silnik powinien być spauzowany");

        engine.setPaused(false);
        assertFalse(engine.isPaused());

        // Możemy po prostu upewnić się, że nie rzuca błędu
        engine.setVaccineAvailable(true);
        // Gdy uruchomi epokę i wyliczy kontekst, nie wyrzuci błędu o szczepionce.
        engine.runNextEpoch();
    }
}