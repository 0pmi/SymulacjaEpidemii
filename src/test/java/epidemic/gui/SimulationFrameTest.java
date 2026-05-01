package epidemic.gui;

import epidemic.engine.SimulationEngine;
import epidemic.model.WorldMap;
import epidemic.service.Config;
import epidemic.statistics.Statistics;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimulationFrameTest {

    private MockedStatic<Config> mockedConfig;
    private SimulationEngine mockEngine;
    private WorldMap mockWorld;

    @BeforeEach
    void setUp() {
        mockedConfig = Mockito.mockStatic(Config.class);
        mockedConfig.when(() -> Config.getInt("gui.timerDelayMin", 10)).thenReturn(10);
        mockedConfig.when(() -> Config.getInt("gui.timerDelayMax", 1000)).thenReturn(1000);
        mockedConfig.when(() -> Config.getInt("gui.timerDelayInitial", 100)).thenReturn(100);
        mockedConfig.when(() -> Config.getInt("gui.mapScale", 7)).thenReturn(7);

        mockEngine = mock(SimulationEngine.class);
        mockWorld = mock(WorldMap.class);

        // Zabezpieczenie dla metody step() i zamknięcia okna
        when(mockEngine.getStats()).thenReturn(new Statistics());
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    @Test
    void shouldInitializeFrameWithCorrectTitle() {
        SimulationFrame frame = new SimulationFrame(mockEngine, mockWorld);

        assertNotNull(frame);
        assertEquals("Sterowanie Symulacją Epidemii", frame.getTitle());
        assertEquals(WindowConstants.DISPOSE_ON_CLOSE, frame.getDefaultCloseOperation());
    }
}