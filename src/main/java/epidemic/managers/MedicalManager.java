package epidemic.managers;

import epidemic.model.*;
import java.util.Iterator;

public class MedicalManager {

    public void processMedicalCare(WorldMap world, WorldContext context) {
        for (Hospital hospital : world.getHospitals()) {
            Iterator<HospitalUser> iterator = hospital.getPatients().iterator();

            while (iterator.hasNext()) {
                HospitalUser patient = iterator.next();

                if (handleTreatment(patient, context)) {
                    patient.setIsInHospital(false);
                    iterator.remove();
                }
            }
        }
    }

    private boolean handleTreatment(HospitalUser patient, WorldContext context) {
        if (context.isVaccineAvailable() && !patient.isVaccinated() && patient.getHealthStatus() == HealthStatus.HEALTHY) {
            patient.setVaccinated(true);
            patient.setWantsHospital(false);
            return true;
        }

        if (patient.getHealthStatus() == HealthStatus.SICK) {
            int remaining = patient.getRemainingInfectionEpochs();
            patient.setRemainingInfectionEpochs(Math.max(0, remaining - 2));

            if (patient.getRemainingInfectionEpochs() <= 0) {
                patient.setHealthStatus(HealthStatus.RECOVERED);
                patient.setWantsHospital(false);
                return true;
            }
        }
        return false;
    }
}