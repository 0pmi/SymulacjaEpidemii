package epidemic.charts;

import epidemic.service.FileExportService;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Klasa narzędziowa odpowiedzialna za post-procesing i wizualizację danych symulacyjnych.
 * Wykorzystuje bibliotekę XChart do generowania interaktywnych wykresów XY na podstawie
 * historycznych danych wyeksportowanych do formatu CSV.
 */
public class SimulationChartGenerator {

    /**
     * Wczytuje wyniki z pliku tekstowego i inicjuje wyświetlenie okien z wykresami.
     * Metoda parsuje dane o stanach zdrowia agentów oraz skumulowanych statystykach zgonów.
     *
     * @param csvFilePath Ścieżka do pliku CSV zawierającego dane z obiektów EpochData.
     */
    public static void showResults(String csvFilePath) {
        List<Double> epochs = new ArrayList<>();
        List<Double> healthy = new ArrayList<>();
        List<Double> sick = new ArrayList<>();
        List<Double> recovered = new ArrayList<>();
        List<Double> virusDeaths = new ArrayList<>();
        List<Double> naturalDeaths = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] v = line.split(",");

                epochs.add(Double.parseDouble(v[0]));
                healthy.add(Double.parseDouble(v[1]));
                sick.add(Double.parseDouble(v[2]));
                recovered.add(Double.parseDouble(v[3]));
                naturalDeaths.add(Double.parseDouble(v[4]));
                virusDeaths.add(Double.parseDouble(v[5]));
            }
        } catch (IOException e) {
            System.err.println("Błąd podczas wczytywania danych do wykresów: " + e.getMessage());
            return;
        }

        XYChart populationChart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title("Dynamika stanów zdrowia populacji")
                .xAxisTitle("Epoka")
                .yAxisTitle("Liczba jednostek")
                .build();

        populationChart.getStyler().setMarkerSize(0);
        populationChart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);

        populationChart.addSeries("Zdrowi", epochs, healthy);
        populationChart.addSeries("Chorzy", epochs, sick);
        populationChart.addSeries("Ozdrowieńcy", epochs, recovered);

        XYChart deathChart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title("Skumulowana liczba zgonów")
                .xAxisTitle("Epoka")
                .yAxisTitle("Suma ofiar")
                .build();

        deathChart.getStyler().setMarkerSize(0);
        deathChart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);

        deathChart.addSeries("Z powodu infekcji", epochs, virusDeaths);
        deathChart.addSeries("Przyczyny naturalne", epochs, naturalDeaths);

        try {
            String safePopPathBase = FileExportService.getSafeExportPath("populacja_wykres");
            String safeDeathPathBase = FileExportService.getSafeExportPath("zgony_wykres");

            VectorGraphicsEncoder.saveVectorGraphic(populationChart, safePopPathBase, VectorGraphicsEncoder.VectorGraphicsFormat.PDF);
            VectorGraphicsEncoder.saveVectorGraphic(deathChart, safeDeathPathBase, VectorGraphicsEncoder.VectorGraphicsFormat.PDF);

            System.out.println("Pomyślnie wyeksportowano wykresy symulacji do plików PDF.");
        } catch (IOException e) {
            System.err.println("Błąd krytyczny I/O podczas utrwalania wykresów: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Błąd konfiguracji eksportera wektorowego (sprawdź zależności maven/gradle): " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Wystąpił nieoczekiwany błąd podczas generowania raportu PDF: " + e.getMessage());
        }


        javax.swing.JFrame popFrame = new SwingWrapper<>(populationChart).displayChart();
        javax.swing.JFrame deathFrame = new SwingWrapper<>(deathChart).displayChart();

        popFrame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);
        deathFrame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);

        java.awt.event.WindowAdapter chartCloseListener = new java.awt.event.WindowAdapter() {
            private int openWindows = 2;

            @Override
            public synchronized void windowClosed(java.awt.event.WindowEvent e) {
                openWindows--;
                if (openWindows <= 0) {
                    System.out.println("Zamknięto wszystkie wykresy. Definitywny koniec programu.");
                    System.exit(0);
                }
            }
        };

        popFrame.addWindowListener(chartCloseListener);
        deathFrame.addWindowListener(chartCloseListener);
    }
}