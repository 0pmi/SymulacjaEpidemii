package epidemic.charts;

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
 * historycznych danych wyeksportowanych do formatu CSV[cite: 28, 29].
 */
public class SimulationChartGenerator {

    /**
     * Wczytuje wyniki z pliku tekstowego i inicjuje wyświetlenie okien z wykresami.
     * Metoda parsuje dane o stanach zdrowia agentów oraz skumulowanych statystykach zgonów[cite: 32].
     *
     * @param csvFilePath Ścieżka do pliku CSV zawierającego dane z obiektów EpochData.
     */
    public static void showResults(String csvFilePath) {
        // Listy przechowujące serie danych dla osi rzędnych i odciętych
        List<Double> epochs = new ArrayList<>();
        List<Double> healthy = new ArrayList<>();
        List<Double> sick = new ArrayList<>();
        List<Double> recovered = new ArrayList<>();
        List<Double> virusDeaths = new ArrayList<>();
        List<Double> naturalDeaths = new ArrayList<>();

        // 1. Proces wczytywania i parsowania danych z pliku CSV
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine(); // Pominięcie nagłówka dokumentu

            while ((line = br.readLine()) != null) {
                String[] v = line.split(",");
                // Mapowanie kolumn zgodnie ze strukturą eksportu SimulationEngine
                epochs.add(Double.parseDouble(v[0]));
                healthy.add(Double.parseDouble(v[1]));
                sick.add(Double.parseDouble(v[2]));
                recovered.add(Double.parseDouble(v[3]));
                naturalDeaths.add(Double.parseDouble(v[4])); // Zgony skumulowane (naturalne)
                virusDeaths.add(Double.parseDouble(v[5]));  // Zgony skumulowane (wirusowe)
            }
        } catch (IOException e) {
            System.err.println("Błąd podczas wczytywania danych do wykresów: " + e.getMessage());
            return;
        }

        // 2. Generowanie Wykresu 1 - Dynamika Populacji
        // Prezentuje zmiany w liczebności grup: zdrowych, chorych i ozdrowieńców w czasie
        XYChart populationChart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title("Dynamika stanów zdrowia populacji")
                .xAxisTitle("Epoka")
                .yAxisTitle("Liczba jednostek")
                .build();

        // Konfiguracja estetyki wykresu
        populationChart.getStyler().setMarkerSize(0);
        populationChart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);

        populationChart.addSeries("Zdrowi", epochs, healthy);
        populationChart.addSeries("Chorzy", epochs, sick);
        populationChart.addSeries("Ozdrowieńcy", epochs, recovered);

        // 3. Generowanie Wykresu 2 - Analiza Śmiertelności
        // Wizualizuje kumulatywne statystyki zgonów z rozbiciem na przyczyny
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

        // 4. Inicjalizacja kontenerów Swing i wyświetlenie wykresów w osobnych wątkach GUI
        new SwingWrapper<>(populationChart).displayChart();
        new SwingWrapper<>(deathChart).displayChart();
    }
}