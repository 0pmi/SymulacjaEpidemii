package epidemic.gui;

import epidemic.engine.SimulationEngine;
import epidemic.model.WorldMap;
import epidemic.statistics.EpochData;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SimulationFrame extends JFrame {
    private final SimulationEngine engine;
    private final MapPanel mapPanel;
    private final Timer timer;
    private final JLabel healthyLabel = new JLabel("Zdrowi: 0");
    private final JLabel sickLabel = new JLabel("Chorzy: 0");
    private final JLabel recoveredLabel = new JLabel("Ozdrowieńcy: 0");
    private final JLabel totalLabel = new JLabel("Populacja: 0");
    private final JLabel epochLabel = new JLabel("Epoka: 0");

    public SimulationFrame(SimulationEngine engine, WorldMap world) {
        this.engine = engine;
        this.mapPanel = new MapPanel(world);

        setTitle("Sterowanie Symulacją Epidemii");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        JPanel infoPanel = new JPanel(new GridLayout(1, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Statystyki Populacji"));

        // Dodajemy etykiety do panelu
        infoPanel.add(healthyLabel);
        infoPanel.add(sickLabel);
        infoPanel.add(recoveredLabel);
        infoPanel.add(totalLabel);
        infoPanel.add(epochLabel);
        JPanel controlPanel = new JPanel();

        JButton playPauseButton = new JButton("Pauza");
        JButton nextStepButton = new JButton("Następny Krok");
        JSlider speedSlider = new JSlider(10, 1000, 100); // od 10ms do 1000ms na epokę


        this.timer = new Timer(100, e -> step());

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.out.println("Zamykanie GUI... Zapisywanie statystyk.");

                // Wywołujemy eksport danych z silnika przed wyjściem[cite: 38, 42]
                engine.getStats().exportToCSV("wyniki_symulacji.csv");

                // Dopiero teraz kończymy proces
                System.exit(0);
            }
        });

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

        add(infoPanel, BorderLayout.NORTH);
        add(mapPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void step() {
        engine.runNextEpoch();
        epochLabel.setText("Epoka: " + engine.getStats().getHistory().size());
        List<EpochData> history = engine.getStats().getHistory();
        if (!history.isEmpty()) {
            EpochData last = history.get(history.size() - 1);
            healthyLabel.setText("Zdrowi: " + last.healthyCount());
            sickLabel.setText("Chorzy: " + last.sickCount());
            recoveredLabel.setText("Ozdrowieńcy: " + last.recoveredCount());
            totalLabel.setText("Populacja: " + last.totalPopulation());
        }
        mapPanel.repaint();
    }

    public void start() {
        setVisible(true);
        timer.start();
    }
}