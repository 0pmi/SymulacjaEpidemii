package epidemic.statistics;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Statistics implements Observer {

    private List<EpochData> history;

    public Statistics() {
        this.history = new ArrayList<>();
    }

    @Override
    public void update(EpochData data) {
        history.add(data);
    }

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