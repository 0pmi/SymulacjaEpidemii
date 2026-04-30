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

class PanickedDecisionStrategyTest {

    private MockedStatic<Config> mockedConfig;
    private MovementStrategy mockPanicMovement;
    private MovementStrategy mockCalmMovement;
    private MovementStrategy mockHospitalMovement;
    private PanickedDecisionStrategy strategy;
    private Human mockHuman;

    @BeforeEach
    void setUp() {
        mockedConfig = Mockito.mockStatic(Config.class);
        mockedConfig.when(() -> Config.getDouble("panicked.infectionThreshold", 0.05)).thenReturn(0.05);

        mockPanicMovement = mock(MovementStrategy.class);
        mockCalmMovement = mock(MovementStrategy.class);
        mockHospitalMovement = mock(MovementStrategy.class);

        strategy = new PanickedDecisionStrategy(mockPanicMovement, mockCalmMovement, mockHospitalMovement);
        mockHuman = mock(Human.class);
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    @Test
    void shouldStayCalmIfInfectionBelowThresholdAndHealthy() {
        WorldContext context = new WorldContext(0.03, false, 1, 0.01);
        when(mockHuman.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);

        strategy.makeDecision(mockHuman, context);

        verify(mockHuman).setWearingMask(false);
        verify(mockHuman).setWantsHospital(false);
        verify(mockHuman).setMovementStrategy(mockCalmMovement);
    }

    @Test
    void shouldPanicIfInfectionAboveThresholdAndHealthy() {
        WorldContext context = new WorldContext(0.06, false, 1, 0.01);
        when(mockHuman.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);

        strategy.makeDecision(mockHuman, context);

        verify(mockHuman).setWearingMask(true);
        verify(mockHuman).setWantsHospital(false);
        verify(mockHuman).setMovementStrategy(mockPanicMovement);
    }

    @Test
    void shouldGoToHospitalIfSickRegardlessOfPanicLevel() {
        WorldContext context = new WorldContext(0.02, false, 1, 0.01);
        when(mockHuman.getHealthStatus()).thenReturn(HealthStatus.SICK);

        strategy.makeDecision(mockHuman, context);

        verify(mockHuman).setWearingMask(false); // Ponieważ < 0.05
        verify(mockHuman).setWantsHospital(true);
        verify(mockHuman).setMovementStrategy(mockHospitalMovement);
    }
}