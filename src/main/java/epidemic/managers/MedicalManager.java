package epidemic.managers;

import epidemic.model.*;
import epidemic.service.Config;

import java.util.Iterator;

/**
 * Moduł zarządzający infrastrukturą ochrony zdrowia (szpitalami).
 * Przetwarza cykl leczenia pacjentów stacjonarnych, przyspieszając ich powrót
 * do zdrowia oraz koordynując akcje profilaktyczne, takie jak dystrybucja szczepionek.
 */
public class MedicalManager {

    /**
     * Uruchamia iterację procesu medycznego dla każdej placówki na mapie.
     * Metoda zarządza łóżkami szpitalnymi, iterując po listach pacjentów
     * i zwalniając miejsca zajmowane przez osoby uleczone, zaszczepione,
     * lub te, które przestały wyrażać chęć hospitalizacji.
     *
     * @param world Stan przestrzenny mapy zawierający infrastrukturę (szpitale).
     * @param context Globalny kontekst środowiska decydujący m.in. o dostępności szczepionek.
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

    /*
     * Aplikuje odpowiednią formę leczenia lub prewencji dla pojedynczego pacjenta.
     * Prowadzi aktywną terapię dla jednostek SICK (dodatkowa redukcja czasu trwania choroby),
     * podaje szczepienia osobom HEALTHY, jeśli są dostępne w kontekście, oraz zwalnia
     * tzw. "hipochondryków", którzy udali się do szpitala na wyrost.
     */
    private boolean handleTreatment(HospitalUser patient, WorldContext context) {
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