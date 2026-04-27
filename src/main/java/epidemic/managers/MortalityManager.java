package epidemic.managers;

import epidemic.model.Agent;
import epidemic.model.Virus;
import epidemic.strategies.mortality.MortalityStrategy;
import java.util.List;

public class MortalityManager {
    private MortalityStrategy strategy;

    public MortalityManager(MortalityStrategy strategy) {
        this.strategy = strategy;
    }

    public void updateLifeStatus(List<Agent> agents, Virus virus) {
        // TODO
    }
}