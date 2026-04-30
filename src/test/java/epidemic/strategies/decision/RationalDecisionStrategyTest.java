package epidemic.strategies.decision;

import epidemic.model.HealthStatus;
import epidemic.model.Human;
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
    private RationalDecisionStrategy strategy;
    private Human mockHuman;

    @BeforeEach
    void setUp() {
        mockedConfig = Mockito.mockStatic(Config.class);
        mockedConfig.when(() -> Config.getDouble("rational.infectionThreshold", 0.20)).thenReturn(0.20);
        mockedConfig.when(() -> Config.getInt("rational.hospitalEpochThreshold", 5)).thenReturn(5);

        mockHospitalMovement = mock(MovementStrategy.class);
        mockDistancingMovement = mock(MovementStrategy.class);
        mockNormalMovement = mock(MovementStrategy.class);

        strategy = new RationalDecisionStrategy(mockHospitalMovement, mockDistancingMovement, mockNormalMovement);
        mockHuman = mock(Human.class);
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    @Test
    void shouldBehaveNormallyWhenInfectionIsLow() {
        WorldContext context = new WorldContext(0.10, false, 1, 0.01);
        when(mockHuman.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);

        strategy.makeDecision(mockHuman, context);

        verify(mockHuman).setWearingMask(false);
        verify(mockHuman).setWantsHospital(false);
        verify(mockHuman).setMovementStrategy(mockNormalMovement);
    }

    @Test
    void shouldDistanceAndWearMaskWhenInfectionIsHigh() {
        WorldContext context = new WorldContext(0.25, false, 1, 0.01);
        when(mockHuman.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);
        when(mockHuman.isVaccinated()).thenReturn(true); // Omija warunek pójścia po szczepionkę

        strategy.makeDecision(mockHuman, context);

        verify(mockHuman).setWearingMask(true);
        verify(mockHuman).setWantsHospital(false);
        verify(mockHuman).setMovementStrategy(mockDistancingMovement);
    }

    @Test
    void shouldGoToHospitalWhenSickAndNearEpochThreshold() {
        WorldContext context = new WorldContext(0.10, false, 1, 0.01);
        when(mockHuman.getHealthStatus()).thenReturn(HealthStatus.SICK);
        when(mockHuman.getRemainingInfectionEpochs()).thenReturn(3); // Poniżej progu 5

        strategy.makeDecision(mockHuman, context);

        verify(mockHuman).setWantsHospital(true);
        verify(mockHuman).setMovementStrategy(mockHospitalMovement);
    }

    @Test
    void shouldGoGetVaccinatedIfHealthyAndVaccineAvailable() {
        WorldContext context = new WorldContext(0.10, true, 1, 0.01);
        when(mockHuman.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);
        when(mockHuman.isVaccinated()).thenReturn(false);

        strategy.makeDecision(mockHuman, context);

        verify(mockHuman).setWantsHospital(true);
        verify(mockHuman).setMovementStrategy(mockHospitalMovement);
    }
}