package epidemic.model;

public interface HospitalUser {
    boolean isWantsHospital();
    boolean isInHospital();
    void setIsInHospital(boolean status);
    void setWantsHospital(boolean status);
    Point2D getPosition();

    HealthStatus getHealthStatus();
    void setHealthStatus(HealthStatus status);
    boolean isVaccinated();
    void setVaccinated(boolean vaccinated);
    int getRemainingInfectionEpochs();
    void setRemainingInfectionEpochs(int epochs);
}