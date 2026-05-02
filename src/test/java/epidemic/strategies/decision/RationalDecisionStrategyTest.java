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

class RationalDecisionStrategyTest {

    private MockedStatic<Config> mockedConfig;
    private MovementStrategy mockHospitalMovement;
    private MovementStrategy mockDistancingMovement;
    private MovementStrategy mockNormalMovement;
    private MovementStrategy mockSeekMateMovement;
    private RationalDecisionStrategy strategy;
    private Human mockHuman;

    @BeforeEach
    void setUp() {
        mockedConfig = Mockito.mockStatic(Config.class);
        mockedConfig.when(() -> Config.getDouble("rational.infectionThreshold", 0.20)).thenReturn(0.20);
        mockedConfig.when(() -> Config.getInt("rational.hospitalEpochThreshold", 5)).thenReturn(5);
        mockedConfig.when(() -> Config.getDouble("reproduction.seekMateProbability", 0.2)).thenReturn(1.0); // 100% pewności dla celów testu!

        mockedConfig.when(() -> Config.getInt("species.human.maturity", 18)).thenReturn(18);
        SpeciesType.initAllFromConfig(); // Wymusza załadowanie wartości 18 do stałej HUMAN

        mockHospitalMovement = mock(MovementStrategy.class);
        mockDistancingMovement = mock(MovementStrategy.class);
        mockNormalMovement = mock(MovementStrategy.class);
        mockSeekMateMovement = mock(MovementStrategy.class);

        // ZMIANA: Konstruktor przyjmuje teraz 4 argumenty
        strategy = new RationalDecisionStrategy(
                mockHospitalMovement, mockDistancingMovement, mockNormalMovement, mockSeekMateMovement);

        mockHuman = mock(Human.class);
        // Konfigurujemy gatunek do testowania wieku rozrodczego
        when(mockHuman.getSpeciesType()).thenReturn(SpeciesType.HUMAN);
        when(mockHuman.getAge()).thenReturn(25); // Dorosły
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    @Test
    void shouldSeekMateWhenInfectionIsLowAndHealthyAndAdult() {
        WorldContext context = new WorldContext(0.10, false, 100, 10);
        when(mockHuman.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);

        strategy.makeDecision(mockHuman, context);

        verify(mockHuman).setWearingMask(false);
        verify(mockHuman).setWantsHospital(false);
        // Sprawdzamy, czy przypisano nową strategię SeekMate
        verify(mockHuman).setMovementStrategy(mockSeekMateMovement);
    }

    @Test
    void shouldDistanceAndWearMaskWhenInfectionIsHigh() {
        WorldContext context = new WorldContext(0.30, false, 100, 30);
        when(mockHuman.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);

        strategy.makeDecision(mockHuman, context);

        verify(mockHuman).setWearingMask(true);
        verify(mockHuman).setWantsHospital(false);
        verify(mockHuman).setMovementStrategy(mockDistancingMovement);
    }

    @Test
    void shouldGoToHospitalWhenSickAndNearEpochThreshold() {
        WorldContext context = new WorldContext(0.10, false, 100, 10);
        when(mockHuman.getHealthStatus()).thenReturn(HealthStatus.SICK);
        when(mockHuman.getRemainingInfectionEpochs()).thenReturn(3); // Poniżej progu 5

        strategy.makeDecision(mockHuman, context);

        verify(mockHuman).setWantsHospital(true);
        verify(mockHuman).setMovementStrategy(mockHospitalMovement);
    }

    @Test
    void shouldGoGetVaccinatedIfHealthyAndVaccineAvailable() {
        WorldContext context = new WorldContext(0.10, true, 100, 10);
        when(mockHuman.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);
        when(mockHuman.isVaccinated()).thenReturn(false);

        strategy.makeDecision(mockHuman, context);

        verify(mockHuman).setWantsHospital(true);
        verify(mockHuman).setMovementStrategy(mockHospitalMovement);
    }
    @Test
    void shouldBehaveNormallyIfRecoveredRegardlessOfInfectionRate() {
        // Symuluje katastrofalny wskaźnik infekcji (99%)
        WorldContext context = new WorldContext(0.99, false, 100, 99);
        when(mockHuman.getHealthStatus()).thenReturn(HealthStatus.RECOVERED);

        strategy.makeDecision(mockHuman, context);

        // Ozdrowieniec ignoruje pandemię
        verify(mockHuman).setWearingMask(false);
        verify(mockHuman).setWantsHospital(false);

        verify(mockHuman, never()).setMovementStrategy(mockDistancingMovement);
        verify(mockHuman, never()).setMovementStrategy(mockHospitalMovement);
    }
    /**
     * Weryfikuje wymóg bycia dorosłym przy poszukiwaniu partnera.
     */
    @Test
    void shouldNotSeekMateIfUnderageEvenIfHealthyAndLowInfection() {
        // Arrange
        WorldContext context = new WorldContext(0.10, false, 100, 10);
        when(mockHuman.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);
        // Nadpisujemy przygotowanie z @BeforeEach i ustalamy wiek na bardzo młody
        when(mockHuman.getAge()).thenReturn(5);

        // Act
        strategy.makeDecision(mockHuman, context);

        // Assert
        verify(mockHuman).setMovementStrategy(mockNormalMovement); // Powinien użyć normalnego ruchu, a nie prokreacyjnego
    }
}