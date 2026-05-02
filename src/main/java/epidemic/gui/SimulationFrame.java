package epidemic.gui;

import epidemic.charts.SimulationChartGenerator;
import epidemic.engine.SimulationEngine;
import epidemic.model.WorldMap;
import epidemic.service.Config;
import epidemic.statistics.EpochData;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Główne okno aplikacji (JFrame).
 * Spina wszystkie elementy sterujące (przyciski, suwaki), paski informacji oraz płótno mapy.
 * Zarządza głównym timerem odświeżania symulacji.
 */
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
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel infoPanel = new JPanel(new GridLayout(1, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Statystyki Populacji"));
        infoPanel.add(healthyLabel);
        infoPanel.add(sickLabel);
        infoPanel.add(recoveredLabel);
        infoPanel.add(totalLabel);
        infoPanel.add(epochLabel);

        JPanel controlPanel = new JPanel();
        JButton playPauseButton = new JButton("Pauza");
        JButton nextStepButton = new JButton("Następny Krok");
        JButton stopButton = new JButton("Zakończ Symulację");
        JSlider speedSlider = new JSlider(
                Config.getInt("gui.timerDelayMin", 10),
                Config.getInt("gui.timerDelayMax", 1000),
                Config.getInt("gui.timerDelayInitial", 100)
        );

        mapPanel.setupMouseListener(engine);

        this.timer = new Timer(Config.getInt("gui.timerDelayInitial", 100), e -> step());
        stopButton.addActionListener(e -> {
            timer.stop();
            handleSimulationEnd("Symulacja przerwana ręcznie przez użytkownika.");
        });

        // Bezpieczne zapisywanie statystyk przed zamknięciem programu
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.out.println("Zamykanie GUI... Zapisywanie statystyk.");
                String fileName = Config.getString("stats.exportFilename", "wyniki_symulacji.csv");
                engine.getStats().exportToCSV(fileName);

                new Thread(() -> {
                    SimulationChartGenerator.showResults(fileName);
                }).start();
            }
        });

        playPauseButton.addActionListener(e -> {
            if (timer.isRunning()) {
                timer.stop();
                engine.setPaused(true);
                playPauseButton.setText("Start");
            } else {
                timer.start();
                engine.setPaused(false);
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
        controlPanel.add(stopButton);
        controlPanel.add(new JLabel("Opóźnienie (ms):"));
        controlPanel.add(speedSlider);
        controlPanel.add(epochLabel);

        add(infoPanel, BorderLayout.NORTH);
        add(mapPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Krok synchronizujący. Przesuwa czas w silniku o jedną epokę,
     * pobiera zaktualizowane statystyki i wymusza przemalowanie widoku mapy.
     */
    private void step() {
        engine.runNextEpoch();

        List<EpochData> history = engine.getStats().getHistory();
        epochLabel.setText("Epoka: " + history.size());

        if (!history.isEmpty()) {
            EpochData last = history.get(history.size() - 1);
            healthyLabel.setText("Zdrowi: " + last.healthyCount());
            sickLabel.setText("Chorzy: " + last.sickCount());
            recoveredLabel.setText("Ozdrowieńcy: " + last.recoveredCount());
            totalLabel.setText("Populacja: " + last.totalPopulation());

            boolean noInfectionsLeft = (last.healthyCount() + last.recoveredCount()) == last.totalPopulation();

            if (noInfectionsLeft && history.size() > 5) {
                timer.stop();
                handleSimulationEnd("Epidemia wygasła. Brak aktywnych infekcji w populacji.");
            }
        }

        mapPanel.repaint();
    }

    /**
     * Zatrzymuje działanie i wywołuje okno podsumowania z wykresem.
     */
    private void handleSimulationEnd(String reason) {
        engine.setPaused(true);

        List<EpochData> history = engine.getStats().getHistory();
        EpochData finalData = history.get(history.size() - 1);

        // POPRAWIONE: Usunięto czwarty znacznik %d, który powodował MissingFormatArgumentException
        String report = String.format(
                "<html><h3>%s</h3>" +
                        "<b>Czas trwania:</b> %d epok<br>" +
                        "<b>Ocaleni (Zdrowi + Ozdrowieńcy):</b> %d<br><br>" +
                        "Czy chcesz wygenerować wykresy i zamknąć program?</html>",
                reason,
                history.size(),
                (finalData.healthyCount() + finalData.recoveredCount())
        );

        int choice = JOptionPane.showConfirmDialog(
                this,
                report,
                "Koniec Symulacji",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            String fileName = Config.getString("stats.exportFilename", "wyniki_symulacji.csv");
            engine.getStats().exportToCSV(fileName);

            new Thread(() -> {
                epidemic.charts.SimulationChartGenerator.showResults(fileName);
            }).start();

            this.dispose();
        } else {
            System.out.println("Kontynuacja pauzy.");
        }
    }
    /**
     * Metoda inicjująca wyświetlanie okna i uruchamiająca pętlę symulacji.
     */
    public void start() {
        setVisible(true);
        timer.start();
    }

}