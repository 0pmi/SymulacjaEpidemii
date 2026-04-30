package epidemic.model;

import epidemic.service.Config;
/**
 * Definiuje parametry biologiczne dla poszczególnych gatunków w symulacji.
 * UWAGA: Zmienne w enumie są domyślnie puste. Przed rozpoczęciem symulacji
 * należy bezwzględnie wywołać {@link #initAllFromConfig()}, aby załadować
 * wskaźniki zjadliwości i wieku dojrzałości z globalnej konfiguracji.
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
     * Inicjalizuje parametry wszystkich gatunków na podstawie właściwości systemu (Config).
     * Wywoływane raz podczas fazy bootstrapingu aplikacji.
     */
    public static void initAllFromConfig() {
        HUMAN.update(Config.getDouble("species.human.virulence", 1.0), Config.getInt("species.human.maturity", 18));
        BAT.update(Config.getDouble("species.bat.virulence", 2.5), Config.getInt("species.bat.maturity", 2));
        DOG.update(Config.getDouble("species.dog.virulence", 2.5), Config.getInt("species.dog.maturity", 2));
        RAT.update(Config.getDouble("species.rat.virulence", 1.8), Config.getInt("species.rat.maturity", 1));
    }

    private void update(double virulence, int maturity) {
        this.baseVirulence = virulence;
        this.maturityAge = maturity;
    }
    public double getBaseVirulence() { return baseVirulence; }
    public int getMaturityAge() { return maturityAge; }
}
