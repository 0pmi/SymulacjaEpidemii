package epidemic.gui;

import epidemic.engine.SimulationEngine;
import epidemic.model.WorldMap;
import javax.swing.*;
import java.awt.*;

public class SimulationFrame extends JFrame {
    private final SimulationEngine engine;
    private final MapPanel mapPanel;
    private final Timer timer;
    private final JLabel epochLabel;

    public SimulationFrame(SimulationEngine engine, WorldMap world) {
        this.engine = engine;
        this.mapPanel = new MapPanel(world);

        setTitle("Sterowanie Symulacją Epidemii");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();

        JButton playPauseButton = new JButton("Pauza");
        JButton nextStepButton = new JButton("Następny Krok");
        JSlider speedSlider = new JSlider(10, 1000, 100); // od 10ms do 1000ms na epokę
        epochLabel = new JLabel("Epoka: 0");

        this.timer = new Timer(100, e -> step());

        playPauseButton.addActionListener(e -> {
            if (timer.isRunning()) {
                timer.stop();
                playPauseButton.setText("Start");
            } else {
                timer.start();
                playPauseButton.setText("Pauza");
            }
        });

        nextStepButton.addActionListener(e -> {
            if (!timer.isRunning()) {
                step();
            }
        });

        speedSlider.addChangeListener(e -> timer.setDelay(speedSlider.getValue()));

        controlPanel.add(playPauseButton);
        controlPanel.add(nextStepButton);
        controlPanel.add(new JLabel("Opóźnienie (ms):"));
        controlPanel.add(speedSlider);
        controlPanel.add(epochLabel);

        add(mapPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Wykonuje pojedynczą epokę i odświeża obraz.
     */
    private void step() {
        engine.runNextEpoch();
        epochLabel.setText("Epoka: " + engine.getStats().getHistory().size());
        mapPanel.repaint();
    }

    public void start() {
        setVisible(true);
        timer.start();
    }
}