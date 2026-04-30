package epidemic.managers;

import epidemic.factory.AgentFactory;
import epidemic.model.Agent;
import epidemic.model.HealthStatus;
import epidemic.model.SpeciesType;
import epidemic.model.WorldMap;
import epidemic.service.Config;
import epidemic.service.SpatialManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.*;

class ReproductionManagerTest {

    private MockedStatic<Config> mockedConfig;
    private AgentFactory mockFactory;
    private ReproductionManager manager;
    private WorldMap mockWorld;
    private SpatialManager mockSpatialManager;

    @BeforeEach
    void setUp() {
        mockedConfig = Mockito.mockStatic(Config.class);
        // Wymusza 100% szans na rozmnażanie i zasięg
        mockedConfig.when(() -> Config.getDouble("reproduction.chance", 0.1)).thenReturn(1.0);
        mockedConfig.when(() -> Config.getInt("reproduction.cooldown", 40)).thenReturn(10);
        mockedConfig.when(() -> Config.getDouble("reproduction.matingRange", 0.1)).thenReturn(5.0);

        mockFactory = mock(AgentFactory.class);
        manager = new ReproductionManager(mockFactory);
        mockWorld = mock(WorldMap.class);
        mockSpatialManager = mock(SpatialManager.class);
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    @Test
    void shouldProduceOffspringIfConditionsAreMet() {
        int currentEpoch = 50;

        Agent parentA = mock(Agent.class);
        when(parentA.isDead()).thenReturn(false);
        when(parentA.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);
        when(parentA.getLastReproductionEpoch()).thenReturn(0); // Cooldown minął
        when(parentA.getSpeciesType()).thenReturn(SpeciesType.HUMAN);
        when(parentA.getAge()).thenReturn(25); // Pełnoletni

        Agent parentB = mock(Agent.class);
        when(parentB.isDead()).thenReturn(false);
        when(parentB.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);
        when(parentB.getLastReproductionEpoch()).thenReturn(0);
        when(parentB.getSpeciesType()).thenReturn(SpeciesType.HUMAN);
        when(parentB.getAge()).thenReturn(24);

        Agent baby = mock(Agent.class);

        when(mockWorld.getAgents()).thenReturn(List.of(parentA, parentB));
        when(mockSpatialManager.getNearbyAgents(parentA, 5.0)).thenReturn(List.of(parentB));
        when(mockFactory.createOffspring(parentA, parentB)).thenReturn(baby);

        manager.handleReproduction(mockWorld, mockSpatialManager, currentEpoch);

        // Weryfikacja efektów udanego rozrodu
        verify(mockFactory).createOffspring(parentA, parentB);
        verify(mockWorld).addAgent(baby);
        verify(parentA).setLastReproductionEpoch(currentEpoch);
        verify(parentB).setLastReproductionEpoch(currentEpoch);
    }

    @Test
    void shouldNotReproduceIfInCooldown() {
        int currentEpoch = 50;

        Agent parentA = mock(Agent.class);
        when(parentA.isDead()).thenReturn(false);
        when(parentA.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);
        when(parentA.getLastReproductionEpoch()).thenReturn(45); // Ostatni rozród 5 epok temu (cooldown to 10)
        when(parentA.getSpeciesType()).thenReturn(SpeciesType.HUMAN);

        when(mockWorld.getAgents()).thenReturn(List.of(parentA));

        manager.handleReproduction(mockWorld, mockSpatialManager, currentEpoch);

        verify(mockSpatialManager, never()).getNearbyAgents(any(), anyDouble());
        verify(mockFactory, never()).createOffspring(any(), any());
    }
}