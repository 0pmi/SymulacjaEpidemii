package epidemic.statistics;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Główny agregator danych statystycznych.
 * Rejestruje historię całej symulacji epoka po epoce i udostępnia mechanizmy eksportu.
 */
public class Statistics implements Observer {

    private List<EpochData> history;

    public Statistics() {
        this.history = new ArrayList<>();
    }

    /**
     * Odbiera nową paczkę danych z silnika po zakończeniu epoki i zapisuje ją w historii.
     * @param data Podsumowanie zakończonej epoki.
     */
    @Override
    public void update(EpochData data) {
        history.add(data);
    }

    /**
     * Eksportuje zebraną historię symulacji do pliku w formacie CSV.
     * @param filename Ścieżka do pliku docelowego (np. "wyniki.csv").
     */
    public void exportToCSV(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Epoch,Healthy,Sick,Recovered,NaturalDeaths,VirusDeaths,TotalPopulation");

            for (EpochData data : history) {
                writer.printf("%d,%d,%d,%d,%d,%d,%d%n",
                        data.epochNumber(),
                        data.healthyCount(),
                        data.sickCount(),
                        data.recoveredCount(),
                        data.naturalDeadCount(),
                        data.virusDeadCount(),
                        data.totalPopulation());
            }
        } catch (IOException e) {
            System.err.println("Błąd podczas eksportu statystyk do pliku: " + e.getMessage());
        }
    }

    public List<EpochData> getHistory() {
        return history;
    }
}