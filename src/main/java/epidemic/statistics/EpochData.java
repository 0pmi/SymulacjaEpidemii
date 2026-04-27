package epidemic.statistics;

public record EpochData(
        int epochNumber,
        int healthyCount,
        int sickCount,
        int recoveredCount,
        int naturalDeadCount,
        int virusDeadCount,
        int totalPopulation
){}
