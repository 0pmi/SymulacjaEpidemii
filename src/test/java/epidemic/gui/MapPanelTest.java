package epidemic.gui;

import epidemic.engine.SimulationEngine;
import epidemic.model.WorldMap;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MapPanelTest {

    @Test
    void shouldInitializePanelWithoutExceptions() {
        WorldMap mockWorld = mock(WorldMap.class);
        when(mockWorld.getWidth()).thenReturn(100);
        when(mockWorld.getHeight()).thenReturn(100);

        MapPanel panel = new MapPanel(mockWorld);

        assertNotNull(panel);
        // Sprawdzenie, czy struktura Layoutu została ustawiona
        assertTrue(panel.getLayout() instanceof BorderLayout);
    }

    @Test
    void shouldAttachMouseListenerSafely() {
        WorldMap mockWorld = mock(WorldMap.class);
        SimulationEngine mockEngine = mock(SimulationEngine.class);
        MapPanel panel = new MapPanel(mockWorld);

        // Upewniamy się, że rejestracja nasłuchiwania nie zgłasza błędów
        panel.setupMouseListener(mockEngine);
    }
}