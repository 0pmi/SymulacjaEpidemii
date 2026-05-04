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
 * Główne okno aplikacji oparte na bibliotece Swing.
 * Pełni rolę kontrolera (Controller) oraz głównego kontenera widoku (View).
 * Odpowiada za cykl życia głównego timera symulacji, synchronizację stanu silnika
 * z interfejsem graficznym oraz bezpieczne zamykanie i eksportowanie wyników.
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

    /**
     * Buduje główny interfejs graficzny symulacji.
     * Inicjalizuje panele statystyk, kontrolki sterujące oraz płótno mapy.
     * Konfiguruje akcje przycisków oraz zachowanie aplikacji przy zamykaniu okna.
     *
     * @param engine Główny silnik symulacji zarządzający logiką domenową.
     * @param world Stan mapy świata wykorzystywany do renderowania wizualizacji.
     */
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

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.out.println("Zamykanie GUI... Zapisywanie statystyk.");

                if (timer != null && timer.isRunning()) {
                    timer.stop();
                }

                String baseName = Config.getString("stats.exportFilename", "wyniki_symulacji.csv");
                String safePath = epidemic.service.FileExportService.getSafeExportPath(baseName);
                engine.getStats().exportToCSV(safePath);

                new Thread(() -> {
                    epidemic.charts.SimulationChartGenerator.showResults(safePath);
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
     * Centralny punkt synchronizacji między warstwą logiki a warstwą widoku.
     * Wywoływany cyklicznie przez Timer w wątku Event Dispatch Thread (EDT).
     * Wymusza przeliczenie kolejnej epoki, aktualizuje etykiety telemetryczne
     * i zleca przerysowanie płótna. Automatycznie zatrzymuje symulację w przypadku wygaśnięcia epidemii.
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
     * Obsługuje proces zakończenia symulacji, wyświetlając podsumowanie
     * i oferując użytkownikowi możliwość wygenerowania wykresów analitycznych.
     * Zapewnia bezpieczny fallback ścieżki eksportu w przypadku braku uprawnień administratora.
     *
     * @param reason Komunikat wyjaśniający powód przerwania symulacji.
     */
    private void handleSimulationEnd(String reason) {
        engine.setPaused(true);

        List<EpochData> history = engine.getStats().getHistory();
        EpochData finalData = history.get(history.size() - 1);

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
            String baseName = Config.getString("stats.exportFilename", "wyniki_symulacji.csv");
            String safePath = epidemic.service.FileExportService.getSafeExportPath(baseName);
            engine.getStats().exportToCSV(safePath);

            new Thread(() -> {
                epidemic.charts.SimulationChartGenerator.showResults(safePath);
            }).start();

            this.dispose();
        } else {
            System.out.println("Kontynuacja pauzy.");
        }
    }
    /**
     * Upowszechnia okno na ekranie i uruchamia główną pętlę zdarzeń symulacji.
     */
    public void start() {
        setVisible(true);
        timer.start();
    }

}