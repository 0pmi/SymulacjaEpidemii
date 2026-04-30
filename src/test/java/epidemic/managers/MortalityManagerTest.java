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

        // Dopasowanie do 3 wywołań w kodzie Menedżera:
        // 1. Na starcie pętli -> musi żyć (false)
        // 2. Przed sprawdzeniem śmierci naturalnej -> musi żyć (false)
        // 3. Po wywołaniu setDead(true), przy ściąganiu z mapy -> uznajemy go za martwego (true)
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

        // Dopasowanie do 3 wywołań w kodzie Menedżera:
        // 1. Na starcie pętli -> musi żyć (false)
        // 2. Po wejściu w `processSickness` i śmierci od choroby -> jest już martwy (true)
        // 3. Przy ściąganiu z mapy -> uznajemy go za martwego (true)
        when(sickAgent.isDead()).thenReturn(false, true, true);
        when(sickAgent.getHealthStatus()).thenReturn(HealthStatus.SICK);
        when(mockStrategy.shouldDieFromDisease(sickAgent)).thenReturn(true);

        manager.processLifeCycles(mockWorld, List.of(sickAgent));

        verify(sickAgent).decrementInfectionTimer();
        verify(sickAgent).setDead(true);
        verify(mockWorld).removeAgent(sickAgent);
    }
}