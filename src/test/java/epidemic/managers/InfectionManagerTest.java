package epidemic.managers;

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

class InfectionManagerTest {

    private MockedStatic<Config> mockedConfig;
    private Virus mockVirus;
    private InfectionManager manager;
    private SpatialManager mockSpatialManager;

    @BeforeEach
    void setUp() {
        mockedConfig = Mockito.mockStatic(Config.class);
        mockedConfig.when(() -> Config.getDouble("infection.carrierMultiplier", 0.5)).thenReturn(0.5);

        mockVirus = mock(Virus.class);
        when(mockVirus.getInfectionRadius()).thenReturn(10.0);
        // Ustalamy bazowe prawdobopodobieństwo na tak wysokie, aby zawsze zarażało dla celów testu
        when(mockVirus.getBaseInfectionProbability()).thenReturn(1000.0);
        when(mockVirus.getDefaultInfectionDuration()).thenReturn(14);

        manager = new InfectionManager(mockVirus);
        mockSpatialManager = mock(SpatialManager.class);
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    @Test
    void shouldNotSpreadIfAgentIsDeadOrHealthy() {
        Agent deadAgent = mock(Agent.class);
        when(deadAgent.isDead()).thenReturn(true);

        Agent healthyAgent = mock(Agent.class);
        when(healthyAgent.isDead()).thenReturn(false);
        when(healthyAgent.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);

        manager.processInfections(List.of(deadAgent, healthyAgent), mockSpatialManager);

        verify(mockSpatialManager, never()).getNearbyAgents(any(), anyDouble());
    }

    @Test
    void shouldInfectVulnerableNeighbor() {
        Agent spreader = mock(Agent.class);
        when(spreader.isDead()).thenReturn(false);
        when(spreader.getHealthStatus()).thenReturn(HealthStatus.SICK);
        when(spreader.getPosition()).thenReturn(new Point2D(0, 0));
        when(spreader.getVirulence()).thenReturn(1.0);

        Agent victim = mock(Agent.class);
        when(victim.canBeInfected()).thenReturn(true);
        when(victim.getPosition()).thenReturn(new Point2D(5, 5)); // Dystans < 10
        when(victim.getVulnerabilityMultiplier()).thenReturn(1.0);

        when(mockSpatialManager.getNearbyAgents(spreader, 10.0)).thenReturn(List.of(victim));

        manager.processInfections(List.of(spreader), mockSpatialManager);

        verify(victim).setHealthStatus(HealthStatus.SICK);
        verify(victim).setRemainingInfectionEpochs(14);
    }
}