package epidemic.managers;

import epidemic.model.Agent;
import epidemic.model.WorldContext;
import epidemic.model.WorldMap;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class BehaviourManagerTest {

    @Test
    void shouldInvokeThinkOnAllAgents() {
        BehaviourManager manager = new BehaviourManager();
        WorldMap mockWorld = mock(WorldMap.class);
        WorldContext mockContext = mock(WorldContext.class);

        Agent agent1 = mock(Agent.class);
        Agent agent2 = mock(Agent.class);

        when(mockWorld.getAgents()).thenReturn(List.of(agent1, agent2));

        manager.updateBehaviours(mockWorld, mockContext);

        verify(agent1, times(1)).think(mockContext);
        verify(agent2, times(1)).think(mockContext);
    }
}