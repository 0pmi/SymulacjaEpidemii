package epidemic.model;

/**
 * Interfejs definiujący kontrakt dla encji mogących korzystać z infrastruktury medycznej (szpitali).
 * Pozwala na odseparowanie złożonej logiki Agenta od wymagań Menedżera Medycznego.
 */
public interface HospitalUser {

    /** @return true, jeśli pacjent wyraża chęć udania się do szpitala. */
    boolean isWantsHospital();
    void setWantsHospital(boolean status);

    /** @return true, jeśli pacjent został przyjęty i aktualnie znajduje się na oddziale. */
    boolean isInHospital();
    void setIsInHospital(boolean status);

    /** @return Aktualne współrzędne pacjenta (niezbędne do weryfikacji wejścia do budynku). */
    Point2D getPosition();

    HealthStatus getHealthStatus();
    void setHealthStatus(HealthStatus status);

    boolean isVaccinated();
    void setVaccinated(boolean vaccinated);

    int getRemainingInfectionEpochs();
    void setRemainingInfectionEpochs(int epochs);
}