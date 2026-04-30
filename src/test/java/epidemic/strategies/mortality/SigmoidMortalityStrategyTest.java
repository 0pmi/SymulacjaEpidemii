package epidemic.strategies.mortality;

import epidemic.model.Agent;
import epidemic.model.Human;
import epidemic.service.Config;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SigmoidMortalityStrategyTest {

    private MockedStatic<Config> mockedConfig;
    private Agent mockStandardAgent;

    @BeforeEach
    void setUp() {
        mockedConfig = Mockito.mockStatic(Config.class);
        mockStandardAgent = mock(Agent.class);
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    @Test
    void shouldAlwaysDieFromDiseaseIfLethalityIsOneHundredPercent() {
        mockedConfig.when(() -> Config.getDouble("sigmoid.baseLethality", 0.02)).thenReturn(1.0);
        mockedConfig.when(() -> Config.getDouble("sigmoid.k", 0.15)).thenReturn(0.15);
        mockedConfig.when(() -> Config.getInt("sigmoid.midpointAge", 80)).thenReturn(80);

        SigmoidMortalityStrategy strategy = new SigmoidMortalityStrategy();
        assertTrue(strategy.shouldDieFromDisease(mockStandardAgent));
    }

    @Test
    void shouldSurviveDiseaseIfInHospitalAndMultiplierIsZero() {
        mockedConfig.when(() -> Config.getDouble("sigmoid.baseLethality", 0.02)).thenReturn(1.0);
        mockedConfig.when(() -> Config.getDouble("sigmoid.hospitalMultiplier", 0.1)).thenReturn(0.0);
        mockedConfig.when(() -> Config.getDouble("sigmoid.k", 0.15)).thenReturn(0.15);
        mockedConfig.when(() -> Config.getInt("sigmoid.midpointAge", 80)).thenReturn(80);

        SigmoidMortalityStrategy strategy = new SigmoidMortalityStrategy();

        Human mockPatient = mock(Human.class);
        when(mockPatient.isInHospital()).thenReturn(true);

        assertFalse(strategy.shouldDieFromDisease(mockPatient), "Pacjent w szpitalu z mnożnikiem 0.0 nigdy nie powinien umrzeć");
    }

    @Test
    void shouldSurviveNaturallyIfAgeIsZero() {
        mockedConfig.when(() -> Config.getDouble("sigmoid.baseLethality", 0.02)).thenReturn(0.02);
        mockedConfig.when(() -> Config.getDouble("sigmoid.k", 0.15)).thenReturn(10.0);
        mockedConfig.when(() -> Config.getInt("sigmoid.midpointAge", 80)).thenReturn(80);

        SigmoidMortalityStrategy strategy = new SigmoidMortalityStrategy();
        when(mockStandardAgent.getAge()).thenReturn(0);

        assertFalse(strategy.shouldDieNaturally(mockStandardAgent), "Młody agent niemal na pewno przeżyje");
    }

    @Test
    void shouldDieNaturallyIfAgeIsExtremelyHigh() {
        mockedConfig.when(() -> Config.getDouble("sigmoid.baseLethality", 0.02)).thenReturn(0.02);
        mockedConfig.when(() -> Config.getDouble("sigmoid.k", 0.15)).thenReturn(10.0);
        mockedConfig.when(() -> Config.getInt("sigmoid.midpointAge", 80)).thenReturn(80);

        SigmoidMortalityStrategy strategy = new SigmoidMortalityStrategy();
        when(mockStandardAgent.getAge()).thenReturn(200);

        assertTrue(strategy.shouldDieNaturally(mockStandardAgent), "Nienaturalnie stary agent zawsze umrze na skutek krzywej");
    }
}