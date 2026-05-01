package epidemic.charts;

import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimulationChartGenerator {

    public static void showResults(String csvFilePath) {
        List<Double> epochs = new ArrayList<>();
        List<Double> healthy = new ArrayList<>();
        List<Double> sick = new ArrayList<>();
        List<Double> recovered = new ArrayList<>();
        List<Double> virusDeaths = new ArrayList<>();
        List<Double> naturalDeaths = new ArrayList<>();

        // 1. Wczytywanie danych
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine(); // Pomiń nagłówek
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
            e.printStackTrace();
            return;
        }

        // 2. Wykres 1: Dynamika Populacji (Healthy, Sick, Recovered)
        XYChart populationChart = new XYChartBuilder()
                .width(800).height(600).title("Stan populacji w czasie").xAxisTitle("Epoka").yAxisTitle("Liczba osób").build();

        // Stylizacja
        populationChart.getStyler().setMarkerSize(0);
        populationChart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);

        populationChart.addSeries("Zdrowi", epochs, healthy);
        populationChart.addSeries("Chorzy", epochs, sick);
        populationChart.addSeries("Ozdrowieńcy", epochs, recovered);

        // 3. Wykres 2: Przyczyny zgonów
        XYChart deathChart = new XYChartBuilder()
                .width(800).height(600).title("Zgony").xAxisTitle("Epoka").yAxisTitle("Suma zgonów").build();

        deathChart.getStyler().setMarkerSize(0);
        deathChart.addSeries("Wirus", epochs, virusDeaths);
        deathChart.addSeries("Naturalne", epochs, naturalDeaths);

        // 4. Wyświetlenie w osobnych oknach
        new SwingWrapper<>(populationChart).displayChart();
        new SwingWrapper<>(deathChart).displayChart();
    }
}