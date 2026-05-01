package epidemic.managers;

import epidemic.factory.AgentFactory;
import epidemic.model.*;
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
        mockedConfig.when(() -> Config.getDouble("reproduction.chance", 0.1)).thenReturn(1.0); // 100% chance for tests
        mockedConfig.when(() -> Config.getDouble("reproduction.matingRange", 0.1)).thenReturn(0.1);

        mockedConfig.when(() -> Config.getInt("reproduction.cooldownMin", 30)).thenReturn(30);
        mockedConfig.when(() -> Config.getInt("reproduction.cooldownMax", 50)).thenReturn(50);

        mockFactory = mock(AgentFactory.class);
        mockWorld = mock(WorldMap.class);
        mockSpatialManager = mock(SpatialManager.class);

        manager = new ReproductionManager(mockFactory);
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    @Test
    void shouldProduceOffspringIfConditionsAreMet() {
        int currentEpoch = 100;

        Agent parentA = mock(Agent.class);
        when(parentA.isDead()).thenReturn(false);
        when(parentA.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);
        when(parentA.getLastReproductionEpoch()).thenReturn(10); // Dawno temu
        when(parentA.getSpeciesType()).thenReturn(SpeciesType.HUMAN);
        when(parentA.getAge()).thenReturn(25);

        Agent parentB = mock(Agent.class);
        when(parentB.isDead()).thenReturn(false);
        when(parentB.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);
        when(parentB.getLastReproductionEpoch()).thenReturn(10); // Dawno temu
        when(parentB.getSpeciesType()).thenReturn(SpeciesType.HUMAN);
        when(parentB.getAge()).thenReturn(25);

        Agent baby = mock(Agent.class);

        when(mockWorld.getAgents()).thenReturn(List.of(parentA, parentB));
        when(mockSpatialManager.getNearbyAgents(parentA, 0.1)).thenReturn(List.of(parentB));
        when(mockFactory.createOffspring(parentA, parentB)).thenReturn(baby);

        manager.handleReproduction(mockWorld, mockSpatialManager, currentEpoch);

        // Verify that baby was created and added
        verify(mockFactory).createOffspring(parentA, parentB);
        verify(mockWorld).addAgent(baby);
        // Verify cooldown was applied
        verify(parentA).setLastReproductionEpoch(currentEpoch);
        verify(parentB).setLastReproductionEpoch(currentEpoch);
    }

    @Test
    void shouldNotReproduceIfInCooldown() {
        int currentEpoch = 100;

        Agent parentA = mock(Agent.class);
        when(parentA.isDead()).thenReturn(false);
        when(parentA.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);
        //Modyfikuje tak, by epoch difference (100 - 90 = 10) było na pewno mniejsze niż cooldownMin (30)
        when(parentA.getLastReproductionEpoch()).thenReturn(90);
        when(parentA.getSpeciesType()).thenReturn(SpeciesType.HUMAN);

        when(mockWorld.getAgents()).thenReturn(List.of(parentA));

        manager.handleReproduction(mockWorld, mockSpatialManager, currentEpoch);

        // Upewniam się, że nie sprawdzano nawet otoczenia ani nie powołano nowego życia
        verify(mockSpatialManager, never()).getNearbyAgents(any(), anyDouble());
        verify(mockFactory, never()).createOffspring(any(), any());
    }
}