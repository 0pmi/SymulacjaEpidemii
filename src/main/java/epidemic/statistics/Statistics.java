package epidemic.statistics;

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
        // TODO
    }

    public List<EpochData> getHistory() {
        return history;
    }
}