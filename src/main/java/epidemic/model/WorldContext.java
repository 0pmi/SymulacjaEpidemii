package epidemic.model;

/**
 * Niemutowalny obiekt transferu danych (DTO) kapsułkujący globalny stan symulacji w konkretnej epoce czasowej.
 * Obiekt ten jest przekazywany do agentów podczas fazy decyzyjnej (w metodzie think), co skutecznie
 * zapobiega ich bezpośredniemu, ciasnemu sprzężeniu z głównym silnikiem symulacji.
 * Zapewnia to bezpieczny, jednokierunkowy przepływ informacji.
 */
public class WorldContext {
    private final double infectionPercentage;
    private final boolean vaccineAvailable;
    private final int currentEpoch;
    private final double mortalityRatio;

    /**
     * Konstruuje nowy "zrzut" stanu świata dla danej epoki.
     *
     * @param infectionPercentage Procentowy udział osób chorych w stosunku do całej żywej populacji.
     * @param vaccineAvailable Flaga określająca, czy administracja medyczna udostępniła już szczepionki dla populacji.
     * @param currentEpoch Bieżący krok czasowy symulacji.
     * @param mortalityRatio Aktualny wskaźnik śmiertelności wywołanej przez wirusa.
     */
    public WorldContext(double infectionPercentage, boolean vaccineAvailable, int currentEpoch, double mortalityRatio) {
        this.infectionPercentage = infectionPercentage;
        this.vaccineAvailable = vaccineAvailable;
        this.currentEpoch = currentEpoch;
        this.mortalityRatio = mortalityRatio;
    }

    /**
     * @return Ułamek określający stopień zaawansowania pandemii.
     */
    public double getInfectionPercentage() { return infectionPercentage; }

    /**
     * @return {@code true}, jeśli globalny program szczepień został uruchomiony.
     */
    public boolean isVaccineAvailable() { return vaccineAvailable; }

    /**
     * @return Numer aktualnie przetwarzanej epoki.
     */
    public int getCurrentEpoch() { return currentEpoch; }

    /**
     * @return Zagregowany wskaźnik śmiertelności dla celów analitycznych i decyzyjnych agentów.
     */
    public double getMortalityRatio() { return mortalityRatio; }
}