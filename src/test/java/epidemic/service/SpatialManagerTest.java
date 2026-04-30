package epidemic.service;

import epidemic.model.Agent;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpatialManagerTest {

    private SpatialManager manager;
    private WorldMap mockWorld;

    @BeforeEach
    void setUp() {
        // Mapa 100x100, rozmiar komórki to 10
        manager = new SpatialManager(100.0, 100.0, 10.0);
        mockWorld = mock(WorldMap.class);
    }

    @Test
    void shouldFindNearbyAgentsWithinRadius() {
        Agent centerAgent = mock(Agent.class);
        when(centerAgent.getPosition()).thenReturn(new Point2D(50, 50));
        when(centerAgent.isDead()).thenReturn(false);

        Agent closeAgent = mock(Agent.class);
        when(closeAgent.getPosition()).thenReturn(new Point2D(53, 54)); // Dystans = 5.0
        when(closeAgent.isDead()).thenReturn(false);

        Agent farAgent = mock(Agent.class);
        when(farAgent.getPosition()).thenReturn(new Point2D(10, 10)); // Poza promieniami
        when(farAgent.isDead()).thenReturn(false);

        when(mockWorld.getAgents()).thenReturn(List.of(centerAgent, closeAgent, farAgent));
        manager.rebuild(mockWorld);

        List<Agent> neighbors = manager.getNearbyAgents(centerAgent, 10.0);

        assertEquals(1, neighbors.size(), "Powinno znaleźć dokładnie jednego sąsiada w promieniu");
        assertTrue(neighbors.contains(closeAgent));
    }

    @Test
    void shouldNotIncludeCenterAgentInResults() {
        Agent centerAgent = mock(Agent.class);
        when(centerAgent.getPosition()).thenReturn(new Point2D(20, 20));
        when(centerAgent.isDead()).thenReturn(false);

        when(mockWorld.getAgents()).thenReturn(List.of(centerAgent));
        manager.rebuild(mockWorld);

        List<Agent> neighbors = manager.getNearbyAgents(centerAgent, 50.0);
        assertTrue(neighbors.isEmpty(), "Wynik nie powinien zawierać samego szukającego agenta");
    }

    @Test
    void shouldIgnoreDeadAgentsInRebuild() {
        Agent deadAgent = mock(Agent.class);
        when(deadAgent.getPosition()).thenReturn(new Point2D(15, 15));
        when(deadAgent.isDead()).thenReturn(true); // Agent jest martwy

        when(mockWorld.getAgents()).thenReturn(List.of(deadAgent));
        manager.rebuild(mockWorld);

        List<Agent> neighbors = manager.getNearbyAgentsAtPos(new Point2D(15, 15), 10.0);
        assertTrue(neighbors.isEmpty(), "Martwi agenci nie powinni być dodawani do struktury indeksu");
    }
}