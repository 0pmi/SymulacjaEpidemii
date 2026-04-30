package epidemic.model;

import epidemic.service.Config;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpeciesTypeTest {

    private MockedStatic<Config> mockedConfig;

    @BeforeEach
    void setUp() {
        mockedConfig = Mockito.mockStatic(Config.class);
        // Symulacja wczytywania wartości z pliku konfiguracyjnego
        mockedConfig.when(() -> Config.getDouble("species.human.virulence", 1.0)).thenReturn(1.2);
        mockedConfig.when(() -> Config.getInt("species.human.maturity", 18)).thenReturn(18);
        mockedConfig.when(() -> Config.getDouble("species.bat.virulence", 2.5)).thenReturn(3.0);
        mockedConfig.when(() -> Config.getInt("species.bat.maturity", 2)).thenReturn(2);
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    @Test
    void shouldInitializeSpeciesPropertiesFromConfig() {
        // Kiedy wywołujemy metodę inicjalizacyjną
        SpeciesType.initAllFromConfig();

        // Wtedy wartości Enumów są poprawnie nadpisane
        assertEquals(1.2, SpeciesType.HUMAN.getBaseVirulence(), 0.01);
        assertEquals(18, SpeciesType.HUMAN.getMaturityAge());
        assertEquals(3.0, SpeciesType.BAT.getBaseVirulence(), 0.01);
        assertEquals(2, SpeciesType.BAT.getMaturityAge());
    }
}