package epidemic.statistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StatisticsTest {

    private Statistics statistics;

    @BeforeEach
    void setUp() {
        statistics = new Statistics();
    }

    @Test
    void shouldStoreEpochDataCorrectly() {
        EpochData data1 = new EpochData(1, 10, 5, 2, 0, 1, 17);
        EpochData data2 = new EpochData(2, 8, 7, 2, 0, 1, 17);

        statistics.update(data1);
        statistics.update(data2);

        assertEquals(2, statistics.getHistory().size());
        assertEquals(data1, statistics.getHistory().get(0));
        assertEquals(data2, statistics.getHistory().get(1));
    }

    @Test
    void shouldExportToCSVCorrectly(@TempDir Path tempDir) throws Exception {
        EpochData data = new EpochData(1, 100, 20, 5, 1, 4, 125);
        statistics.update(data);

        Path csvFile = tempDir.resolve("test_stats.csv");
        statistics.exportToCSV(csvFile.toString());

        assertTrue(Files.exists(csvFile), "Plik CSV powinien zostać utworzony");

        List<String> lines = Files.readAllLines(csvFile);
        assertEquals(2, lines.size(), "Plik powinien mieć nagłówek i jeden wiersz z danymi");
        assertEquals("Epoch,Healthy,Sick,Recovered,NaturalDeaths,VirusDeaths,TotalPopulation", lines.get(0));
        assertEquals("1,100,20,5,1,4,125", lines.get(1));
    }
}