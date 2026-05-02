package epidemic.strategies.movement;

import epidemic.model.Agent;
import epidemic.model.Point2D;
import epidemic.model.WorldMap;
import epidemic.service.Config;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Testy jednostkowe weryfikujące poprawność wektorów ucieczki w SocialDistancingStrategy.
 *
 * Klasa sprawdza poprawność matematyczną algorytmu pól potencjałów oraz upewnia się,
 * że agenci nie przenikają przez granice świata i nie grupują się na krawędziach.
 */
class SocialDistancingStrategyTest {

    private MockedStatic<Config> mockedConfig;
    private Agent mockAgent;
    private WorldMap mockWorld;
    private SocialDistancingStrategy strategy;

    /**
     * Przygotowanie środowiska testowego.
     * Mockowanie Config musi nastąpić przed instancjacją strategii,
     * ponieważ wartości są przypisywane do pól finalnych w konstruktorze.
     */
    @BeforeEach
    void setUp() {
        mockedConfig = Mockito.mockStatic(Config.class);

        // Mockowanie wszystkich parametrów, których używa konstruktor/pola klasy
        mockedConfig.when(() -> Config.getDouble("movement.distancing.radius", 5.0)).thenReturn(5.0);
        mockedConfig.when(() -> Config.getDouble("movement.boundary.margin", 3.0)).thenReturn(3.0);
        mockedConfig.when(() -> Config.getDouble("movement.social.weight", 1.0)).thenReturn(1.0);
        mockedConfig.when(() -> Config.getDouble("movement.boundary.weight", 2.5)).thenReturn(2.5);

        strategy = new SocialDistancingStrategy();
        mockAgent = mock(Agent.class);
        mockWorld = mock(WorldMap.class);

        // Ustawienie wymiarów, aby clamp() nie sprowadzał wyniku do 0
        when(mockWorld.getWidth()).thenReturn(100);
        when(mockWorld.getHeight()).thenReturn(100);
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    /**
     * Weryfikuje ucieczkę od pojedynczego źródła zagrożenia.
     * Agent na (10,10) przy sąsiedzie na (12,10) musi wygenerować wektor ujemny (dx = -1).
     */
    @Test
    void shouldFleeDirectlyAwayFromSingleNeighbor() {
        Point2D agentPos = new Point2D(10, 10);
        when(mockAgent.getPosition()).thenReturn(agentPos);

        Agent mockNeighbor = mock(Agent.class);
        when(mockNeighbor.getPosition()).thenReturn(new Point2D(12, 10));

        // Użycie any() zapewnia, że Mockito dopasuje wywołanie niezależnie od instancji Point2D
        when(mockWorld.getNeighbors(any(), anyDouble())).thenReturn(List.of(mockNeighbor));

        Point2D nextPos = strategy.calculateNextPosition(mockAgent, mockWorld);

        assertEquals(9, nextPos.x(), "Agent powinien uciekać na zachód (X: 10 -> 9)");
        assertEquals(10, nextPos.y());
    }

    /**
     * Sprawdza wektor wypadkowy przy wielu sąsiadach.
     * Siły ucieczki powinny się sumować, prowadząc agenta na wolną przestrzeń.
     */
    @Test
    void shouldCalculateVectorAwayFromMultipleNeighbors() {
        Point2D agentPos = new Point2D(10, 10);
        when(mockAgent.getPosition()).thenReturn(agentPos);

        Agent n1 = mock(Agent.class); // Północ
        when(n1.getPosition()).thenReturn(new Point2D(10, 12));
        Agent n2 = mock(Agent.class); // Wschód
        when(n2.getPosition()).thenReturn(new Point2D(12, 10));

        when(mockWorld.getNeighbors(any(), anyDouble())).thenReturn(List.of(n1, n2));

        Point2D nextPos = strategy.calculateNextPosition(mockAgent, mockWorld);

        assertEquals(9, nextPos.x(), "Ucieczka od wschodniego sąsiada");
        assertEquals(9, nextPos.y(), "Ucieczka od północnego sąsiada");
    }

    /**
     * Weryfikuje system ochrony granic.
     * Nawet przy braku sąsiadów, krawędź mapy powinna generować siłę spychającą do wnętrza.
     */
    @Test
    void shouldAvoidBoundaryWhenCloseToEdge() {
        // Agent przy lewej krawędzi (X=1). Margines to 3.0, więc siła odpychania musi zadziałać.
        Point2D agentPos = new Point2D(1, 50);
        when(mockAgent.getPosition()).thenReturn(agentPos);
        when(mockWorld.getNeighbors(any(), anyDouble())).thenReturn(Collections.emptyList());

        Point2D nextPos = strategy.calculateNextPosition(mockAgent, mockWorld);

        assertTrue(nextPos.x() > agentPos.x(), "Wektor odpychania od ściany powinien przesunąć agenta w prawo");
    }

    /**
     * Sprawdza mechanizm bezpieczeństwa błądzenia losowego.
     */
    @Test
    void shouldFallbackToRandomWhenNoStimuli() {
        // Środek mapy, brak sąsiadów - pełna swoboda ruchu
        Point2D agentPos = new Point2D(50, 50);
        when(mockAgent.getPosition()).thenReturn(agentPos);
        when(mockWorld.getNeighbors(any(), anyDouble())).thenReturn(Collections.emptyList());

        Point2D nextPos = strategy.calculateNextPosition(mockAgent, mockWorld);

        int dx = Math.abs(nextPos.x() - agentPos.x());
        int dy = Math.abs(nextPos.y() - agentPos.y());

        assertTrue(dx <= 1 && dy <= 1, "Ruch losowy nie może przekroczyć jednego pola");
    }
}