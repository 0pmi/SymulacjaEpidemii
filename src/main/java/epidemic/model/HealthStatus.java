package epidemic.model;

/**
 * Definiuje dopuszczalne stany epidemiczne agenta w systemie.
 * Wykorzystywane przez menedżerów infekcji do decydowania o mechanice zarażania i kwarantanny.
 */
public enum HealthStatus {
    /** Organizmy zdrowe, w pełni podatne na zarażenie patogenem z zewnątrz. */
    HEALTHY,

    /** Bezobjawowi nosiciele patogenu. Przenoszą wirusa, ale sami są odporni na ryzyko zgonu wywołanego infekcją. */
    CARRIER,

    /** Pełnoobjawowi pacjenci, aktywnie rozprzestrzeniający wirusa oraz narażeni na bezpośrednie ryzyko śmiertelności. */
    SICK,

    /** Ozdrowieńcy, którzy przechorowali infekcję. Zależnie od ustawień konfiguracji mogą zyskiwać odporność. */
    RECOVERED;
}
