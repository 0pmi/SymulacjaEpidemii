package epidemic.strategies.mortality;

import epidemic.model.Agent;

/**
 * Interfejs definiujący kontrakt dla strategii śmiertelności (wzorzec Strategy).
 * Pozwala na całkowite odseparowanie biologicznych i losowych uwarunkowań zgonu
 * od głównej logiki cyklu życia zarządzanej przez centralne menedżery silnika.
 */
public interface MortalityStrategy {

    /**
     * Weryfikuje, czy agent powinien umrzeć w wyniku powikłań trwającej infekcji.
     *
     * @param agent Agent podlegający ocenie klinicznej.
     * @return {@code true}, jeśli choroba okazała się śmiertelna w bieżącym kroku symulacji.
     */
    boolean shouldDieFromDisease(Agent agent);

    /**
     * Weryfikuje, czy agent osiągnął kres swojego naturalnego cyklu życia.
     *
     * @param agent Agent podlegający ocenie biologicznej.
     * @return {@code true}, jeśli agent zmarł ze starości lub z przyczyn niezwiązanych z epidemią.
     */
    boolean shouldDieNaturally(Agent agent);
}