package epidemic.strategies.decision;

import epidemic.model.HealthStatus;
import epidemic.model.Human;
import epidemic.model.SpeciesType;
import epidemic.model.WorldContext;
import epidemic.service.Config;
import epidemic.strategies.movement.MovementStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class PanickedDecisionStrategyTest {

    private MockedStatic<Config> mockedConfig;
    private MovementStrategy mockPanicMovement;
    private MovementStrategy mockCalmMovement;
    private MovementStrategy mockHospitalMovement;
    private MovementStrategy mockSeekMateMovement;
    private PanickedDecisionStrategy strategy;
    private Human mockHuman;

    @BeforeEach
    void setUp() {
        mockedConfig = Mockito.mockStatic(Config.class);
        mockedConfig.when(() -> Config.getDouble("panicked.infectionThreshold", 0.05)).thenReturn(0.05);
        // Forsujemy poszukiwanie partnera dla testu w stanie spokoju
        mockedConfig.when(() -> Config.getDouble("reproduction.seekMateProbability", 0.2)).thenReturn(1.0);

        mockPanicMovement = mock(MovementStrategy.class);
        mockCalmMovement = mock(MovementStrategy.class);
        mockHospitalMovement = mock(MovementStrategy.class);
        mockSeekMateMovement = mock(MovementStrategy.class);

        // ZMIANA: Konstruktor przyjmuje teraz 4 argumenty
        strategy = new PanickedDecisionStrategy(
                mockPanicMovement, mockCalmMovement, mockHospitalMovement, mockSeekMateMovement);

        mockHuman = mock(Human.class);
        when(mockHuman.getSpeciesType()).thenReturn(SpeciesType.HUMAN);
        when(mockHuman.getAge()).thenReturn(30); // Dorosły agent
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    @Test
    void shouldSeekMateIfInfectionBelowThresholdAndHealthyAndAdult() {
        WorldContext context = new WorldContext(0.02, false, 100, 2);
        when(mockHuman.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);

        strategy.makeDecision(mockHuman, context);

        verify(mockHuman).setWearingMask(false);
        verify(mockHuman).setWantsHospital(false);
        // Skoro infekcja < 0.05, age > maturity i prob == 1.0 -> biega za partnerem!
        verify(mockHuman).setMovementStrategy(mockSeekMateMovement);
    }

    @Test
    void shouldPanicIfInfectionAboveThresholdAndHealthy() {
        WorldContext context = new WorldContext(0.10, false, 100, 10);
        when(mockHuman.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);

        strategy.makeDecision(mockHuman, context);

        verify(mockHuman).setWearingMask(true);
        verify(mockHuman).setWantsHospital(false);
        verify(mockHuman).setMovementStrategy(mockPanicMovement);
    }

    @Test
    void shouldGoToHospitalIfSickRegardlessOfPanicLevel() {
        WorldContext context = new WorldContext(0.01, false, 100, 1);
        when(mockHuman.getHealthStatus()).thenReturn(HealthStatus.SICK);

        strategy.makeDecision(mockHuman, context);

        // Przykrywamy maską i kierujemy do szpitala
        verify(mockHuman).setWantsHospital(true);
        verify(mockHuman).setMovementStrategy(mockHospitalMovement);
    }
    @Test
    void shouldBehaveNormallyIfRecoveredRegardlessOfPanicLevel() {
        // Symulujemy katastrofalny wskaźnik infekcji (99%)
        WorldContext context = new WorldContext(0.99, false, 100, 99);
        when(mockHuman.getHealthStatus()).thenReturn(HealthStatus.RECOVERED);

        strategy.makeDecision(mockHuman, context);

        // Ozdrowieniec nie wpada w panikę
        verify(mockHuman).setWearingMask(false);
        verify(mockHuman).setWantsHospital(false);

        verify(mockHuman, never()).setMovementStrategy(mockPanicMovement);
        verify(mockHuman, never()).setMovementStrategy(mockHospitalMovement);
    }
}