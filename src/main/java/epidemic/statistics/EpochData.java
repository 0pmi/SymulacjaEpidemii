package epidemic.statistics;

/**
 * Niemutowalny rekord przechowujący migawkę stanu symulacji w konkretnej epoce.
 * Służy do bezpiecznego przekazywania danych pomiędzy silnikiem a modułami statystycznymi.
 */
public record EpochData(
        int epochNumber,
        int healthyCount,
        int sickCount,
        int recoveredCount,
        int naturalDeadCount,
        int virusDeadCount,
        int totalPopulation
){}