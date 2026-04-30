package epidemic.model;

import epidemic.service.Config;
import epidemic.strategies.movement.MovementStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HumanTest {

    private MockedStatic<Config> mockedConfig;
    private Personality mockPersonality;
    private MovementStrategy mockMovement;
    private Human human;

    @BeforeEach
    void setUp() {
        mockedConfig = Mockito.mockStatic(Config.class);
        // Ustawienie konfiguracji pod wzór testów Agenta (z którego Human dziedziczy) i podatności
        mockedConfig.when(() -> Config.getDouble("agent.defaultNaturalMortality", 0.01)).thenReturn(0.01);
        mockedConfig.when(() -> Config.getDouble("vulnerability.maskMultiplier", 0.3)).thenReturn(0.5); // Użyjemy 0.5 dla łatwiejszych obliczeń
        mockedConfig.when(() -> Config.getDouble("vulnerability.vaccineMultiplier", 0.1)).thenReturn(0.2); // Użyjemy 0.2

        mockPersonality = mock(Personality.class);
        mockMovement = mock(MovementStrategy.class);

        human = new Human(new Point2D(0, 0), 25, 2.0, 0.1, mockPersonality, mockMovement);
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    @Test
    void shouldCalculateVulnerabilityProperly_NoProtection() {
        // Odporność 0.1 (czyli 10%). Brak maski, brak szczepionki.
        // Oczekiwany wynik: 1.0 * (1.0 - 0.1) = 0.9
        assertEquals(0.9, human.getVulnerabilityMultiplier(), 0.001);
    }

    @Test
    void shouldCalculateVulnerabilityProperly_WithMask() {
        human.setWearingMask(true);
        // Oczekiwany wynik: 1.0 * 0.5 (maska) * (1.0 - 0.1) = 0.45
        assertEquals(0.45, human.getVulnerabilityMultiplier(), 0.001);
    }

    @Test
    void shouldCalculateVulnerabilityProperly_WithVaccine() {
        human.setVaccinated(true);
        // Oczekiwany wynik: 1.0 * 0.2 (szczepionka) * (1.0 - 0.1) = 0.18
        assertEquals(0.18, human.getVulnerabilityMultiplier(), 0.001);
    }

    @Test
    void shouldCalculateVulnerabilityProperly_WithMaskAndVaccine() {
        human.setWearingMask(true);
        human.setVaccinated(true);
        // Oczekiwany wynik: 1.0 * 0.5 (maska) * 0.2 (szczepionka) * (1.0 - 0.1) = 0.09
        assertEquals(0.09, human.getVulnerabilityMultiplier(), 0.001);
    }

    @Test
    void shouldDelegateThinkToPersonality() {
        WorldContext mockContext = mock(WorldContext.class);

        human.think(mockContext);

        // Weryfikacja czy metoda updateMentalState została zawołana dokładnie jeden raz
        verify(mockPersonality, times(1)).updateMentalState(human, mockContext);
    }

    @Test
    void shouldManageHospitalUserStatus() {
        assertFalse(human.isWantsHospital());
        assertFalse(human.isInHospital());

        human.setWantsHospital(true);
        human.setIsInHospital(true);

        assertTrue(human.isWantsHospital());
        assertTrue(human.isInHospital());
    }
}