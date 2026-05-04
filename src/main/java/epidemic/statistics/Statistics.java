package epidemic.statistics;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Główny, stanowy agregator logów telemetrycznych i statystycznych.
 * Implementuje interfejs {@link Observer}, dzięki czemu automatycznie subskrybuje
 * i archiwizuje migawki środowiska ({@link EpochData}) po zakończeniu każdego kroku symulacji.
 * Udostępnia również wbudowane mechanizmy eksportu zebranych danych do formatów analitycznych.
 */
public class Statistics implements Observer {

    private List<EpochData> history;

    /**
     * Inicjalizuje pusty agregator gotowy do zbierania danych telemetrycznych.
     */
    public Statistics() {
        this.history = new ArrayList<>();
    }

    /**
     * Odbiera zjawisko (event) z silnika i odkłada nową paczkę danych
     * na koniec wewnętrznej listy historycznej.
     *
     * @param data Struktura danych reprezentująca podsumowanie właśnie zakończonej epoki.
     */
    @Override
    public void update(EpochData data) {
        history.add(data);
    }

    /**
     * Eksportuje zebraną dotychczas historię symulacji do płaskiego pliku w formacie CSV.
     * Używa bezpiecznych bloków try-with-resources do zarządzania strumieniem wejścia/wyjścia (I/O),
     * a ewentualne wyjątki związane z brakiem uprawnień do zapisu są logowane do strumienia błędów.
     *
     * @param filename Ścieżka (np. wygenerowana przez FileExportService) do pliku docelowego.
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

    /**
     * Pobiera całą wewnętrzną kolekcję zebranych danych analitycznych.
     *
     * @return Uporządkowana chronologicznie lista zarchiwizowanych epok.
     */
    public List<EpochData> getHistory() {
        return history;
    }
}