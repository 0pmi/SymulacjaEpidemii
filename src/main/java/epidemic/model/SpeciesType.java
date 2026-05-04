package epidemic.model;

import epidemic.service.Config;

/**
 * Definiuje parametry biologiczne dla poszczególnych gatunków obecnych w symulacji.
 * <p>
 * UWAGA: Zmienne wewnętrzne w enumeracji są domyślnie niezainicjalizowane.
 * Przed rozpoczęciem głównej pętli symulacji należy bezwzględnie wywołać
 * metodę {@link #initAllFromConfig()}, aby bezpiecznie załadować wskaźniki
 * zjadliwości i wieku dojrzałości ze scentralizowanej konfiguracji.
 * </p>
 */
public enum SpeciesType {
    HUMAN,
    BAT,
    DOG,
    RAT;

    private double baseVirulence;
    private int maturityAge;

    SpeciesType() {
    }

    /**
     * Inicjalizuje parametry wszystkich gatunków na podstawie właściwości załadowanych
     * przez system konfiguracji (Config).
     * Metoda ta powinna być wywoływana jednorazowo podczas fazy bootstrapingu aplikacji,
     * przed wygenerowaniem pierwszych agentów.
     */
    public static void initAllFromConfig() {
        HUMAN.update(Config.getDouble("species.human.virulence", 1.0), Config.getInt("species.human.maturity", 18));
        BAT.update(Config.getDouble("species.bat.virulence", 2.5), Config.getInt("species.bat.maturity", 2));
        DOG.update(Config.getDouble("species.dog.virulence", 2.5), Config.getInt("species.dog.maturity", 2));
        RAT.update(Config.getDouble("species.rat.virulence", 1.8), Config.getInt("species.rat.maturity", 1));
    }

    /*
     * Wewnętrzna metoda aktualizująca stan pojedynczej instancji enumeracji.
     */
    private void update(double virulence, int maturity) {
        this.baseVirulence = virulence;
        this.maturityAge = maturity;
    }
    /**
     * Pobiera bazowy wskaźnik siły patogenu przenoszonego przez dany gatunek.
     *
     * @return Wartość zjadliwości wykorzystywana jako mnożnik przy wyliczaniu szans na infekcję.
     */
    public double getBaseVirulence() { return baseVirulence; }

    /**
     * Pobiera próg wiekowy wymagany do osiągnięcia dojrzałości rozrodczej gatunku.
     *
     * @return Minimalny wiek w epokach/latach uprawniający do udziału w procesie reprodukcji.
     */
    public int getMaturityAge() { return maturityAge; }
}
