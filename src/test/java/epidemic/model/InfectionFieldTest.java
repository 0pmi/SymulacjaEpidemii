package epidemic.model;

import epidemic.service.Config;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class InfectionFieldTest {

    private MockedStatic<Config> mockedConfig;

    @BeforeEach
    void setUp() {
        mockedConfig = Mockito.mockStatic(Config.class);
        mockedConfig.when(() -> Config.getInt("infectionField.defaultExpiration", 10)).thenReturn(10);
        mockedConfig.when(() -> Config.getDouble("infectionField.dissipationRate", 0.05)).thenReturn(0.1); // 10% spadku dla ułatwienia obliczeń
        mockedConfig.when(() -> Config.getDouble("infectionField.minInfectivity", 0.01)).thenReturn(0.01);
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    @Test
    void shouldInitializeWithCorrectValues() {
        Point2D pos = new Point2D(5, 5);
        InfectionField field = new InfectionField(pos, 0.5);

        assertEquals(pos, field.getPosition());
        assertEquals(0.5, field.getInfectivity());
        assertFalse(field.isExpired());
    }

    @Test
    void shouldRefreshWithMaxInfectivityAndResetTimer() {
        InfectionField field = new InfectionField(new Point2D(0, 0), 0.3);

        // Symuluje upływ czasu (spadek siły i czasu)
        field.decay();

        // Odświeża silniejszym wirusem
        field.refresh(0.8);

        assertEquals(0.8, field.getInfectivity(), "Powinien przyjąć silniejszą wartość infekcji");
        assertFalse(field.isExpired());
    }

    @Test
    void shouldDecayProperly() {
        InfectionField field = new InfectionField(new Point2D(0, 0), 1.0);
        field.decay();

        // Skoro dissipationRate w mocku = 0.1, to 1.0 * (1 - 0.1) = 0.9
        assertEquals(0.9, field.getInfectivity(), 0.001);
    }

    @Test
    void shouldExpireWhenTimeRunsOutOrInfectivityDrops() {
        InfectionField field = new InfectionField(new Point2D(0, 0), 0.011);

        // Zbija infectivity poniżej 0.01
        field.decay();

        assertTrue(field.isExpired(), "Pole powinno wygasnąć, gdy infectivity spadnie poniżej progu");
    }
}