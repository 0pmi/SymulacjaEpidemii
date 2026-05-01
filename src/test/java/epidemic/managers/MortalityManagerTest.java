package epidemic.managers;

import epidemic.model.Agent;
import epidemic.model.HealthStatus;
import epidemic.model.WorldMap;
import epidemic.strategies.mortality.MortalityStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class MortalityManagerTest {

    private MortalityStrategy mockStrategy;
    private MortalityManager manager;
    private WorldMap mockWorld;

    @BeforeEach
    void setUp() {
        mockStrategy = mock(MortalityStrategy.class);
        manager = new MortalityManager(mockStrategy);
        mockWorld = mock(WorldMap.class);
    }

    @Test
    void shouldIgnoreAlreadyDeadAgents() {
        Agent deadAgent = mock(Agent.class);
        when(deadAgent.isDead()).thenReturn(true);

        manager.processLifeCycles(mockWorld, List.of(deadAgent));

        verify(deadAgent, never()).getHealthStatus();
        verify(mockWorld, never()).removeAgent(deadAgent);
    }

    @Test
    void shouldKillAndRemoveAgentIfNaturallyDead() {
        Agent healthyAgent = mock(Agent.class);
        when(healthyAgent.isDead()).thenReturn(false, false, true);
        when(healthyAgent.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);
        when(mockStrategy.shouldDieNaturally(healthyAgent)).thenReturn(true);

        manager.processLifeCycles(mockWorld, List.of(healthyAgent));

        verify(healthyAgent).setDead(true);
        verify(mockWorld).removeAgent(healthyAgent);
    }

    @Test
    void shouldRecoverIfTimerReachesZeroAndSurvivesDisease() {
        Agent sickAgent = mock(Agent.class);
        when(sickAgent.isDead()).thenReturn(false);
        when(sickAgent.getHealthStatus()).thenReturn(HealthStatus.SICK);
        when(mockStrategy.shouldDieFromDisease(sickAgent)).thenReturn(false);
        when(sickAgent.getRemainingInfectionEpochs()).thenReturn(0);

        manager.processLifeCycles(mockWorld, List.of(sickAgent));

        verify(sickAgent).decrementInfectionTimer();
        verify(sickAgent).setHealthStatus(HealthStatus.RECOVERED);
        verify(sickAgent, never()).setDead(true);
    }

    @Test
    void shouldKillFromDisease() {
        Agent sickAgent = mock(Agent.class);
        when(sickAgent.isDead()).thenReturn(false, true, true);
        when(sickAgent.getHealthStatus()).thenReturn(HealthStatus.SICK);
        when(mockStrategy.shouldDieFromDisease(sickAgent)).thenReturn(true);

        manager.processLifeCycles(mockWorld, List.of(sickAgent));

        verify(sickAgent).decrementInfectionTimer();
        verify(sickAgent).setDead(true);
        verify(mockWorld).removeAgent(sickAgent);
    }

    @Test
    void shouldRecoverCarrierWithoutTestingForDiseaseDeath() {
        Agent carrierAgent = mock(Agent.class);
        when(carrierAgent.isDead()).thenReturn(false);
        when(carrierAgent.getHealthStatus()).thenReturn(HealthStatus.CARRIER);
        when(carrierAgent.getRemainingInfectionEpochs()).thenReturn(0);

        manager.processLifeCycles(mockWorld, List.of(carrierAgent));

        verify(carrierAgent).decrementInfectionTimer();
        verify(carrierAgent).setHealthStatus(HealthStatus.RECOVERED);
        // Upewniam się, że dla nosicieli nie jest wołana ocena zgonu z powodu choroby
        verify(mockStrategy, never()).shouldDieFromDisease(carrierAgent);
        verify(carrierAgent, never()).setDead(true);
    }
}