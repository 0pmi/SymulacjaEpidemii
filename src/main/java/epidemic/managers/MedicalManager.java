package epidemic.managers;

import epidemic.model.*;
import epidemic.service.Config;

import java.util.Iterator;

/**
 * Koordynuje działanie infrastruktury medycznej w symulacji.
 * Zarządza procesem leczenia w szpitalach, aplikacją szczepień
 * oraz wypisywaniem pacjentów po zakończeniu terapii.
 */
public class MedicalManager {

    /**
     * Przetwarza cykl medyczny dla wszystkich szpitali i ich pacjentów.
     * Pacjenci uleczeni, zaszczepieni lub niekwalifikujący się do pobytu są zwalniani.
     *
     * @param world Mapa zawierająca placówki medyczne.
     * @param context Globalny kontekst środowiska.
     */
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

    /**
     * Ewaluuje i aplikuje leczenie lub profilaktykę dla pojedynczego pacjenta.
     *
     * @param patient Pacjent przebywający w szpitalu.
     * @param context Kontekst środowiska.
     * @return true, jeśli pacjent powinien opuścić szpital (terapia zakończona); false w przeciwnym razie.
     */
    private boolean handleTreatment(HospitalUser patient, WorldContext context) {
        // Jeśli pacjent utracił chęć przebywania w szpitalu (np. zmiana strategii decyzji)
        if (!patient.isWantsHospital()) {
            return true;
        }

        if (patient.getHealthStatus() == HealthStatus.RECOVERED) {
            patient.setWantsHospital(false);
            return true;
        }

        // Podanie szczepionki zdrowym ochotnikom
        if (context.isVaccineAvailable() && !patient.isVaccinated() && patient.getHealthStatus() == HealthStatus.HEALTHY) {
            patient.setVaccinated(true);
            patient.setWantsHospital(false);
            return true;
        }

        // Aktywne leczenie objawowe dla chorych (redukcja czasu trwania infekcji)
        if (patient.getHealthStatus() == HealthStatus.SICK) {
            int remaining = patient.getRemainingInfectionEpochs();
            int healingBoost = Config.getInt("medical.hospitalHealingBoost", 2);
            patient.setRemainingInfectionEpochs(Math.max(0, remaining - healingBoost));

            if (patient.getRemainingInfectionEpochs() <= 0) {
                patient.setHealthStatus(HealthStatus.RECOVERED);
                patient.setWantsHospital(false);
                return true;
            }
        }

        // Zwolnienie łóżek przez zdrowych hipochondryków, gdy nie ma szczepionek
        if (patient.getHealthStatus() == HealthStatus.HEALTHY && !context.isVaccineAvailable()) {
            patient.setWantsHospital(false);
            return true;
        }

        return false;
    }
}