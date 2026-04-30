package epidemic.managers;

import epidemic.model.HealthStatus;
import epidemic.model.Hospital;
import epidemic.model.Human;
import epidemic.model.WorldContext;
import epidemic.model.WorldMap;
import epidemic.service.Config;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MedicalManagerTest {

    private MockedStatic<Config> mockedConfig;
    private MedicalManager manager;
    private WorldMap mockWorld;
    private Hospital mockHospital;
    private WorldContext mockContext;

    @BeforeEach
    void setUp() {
        mockedConfig = Mockito.mockStatic(Config.class);
        mockedConfig.when(() -> Config.getInt("medical.hospitalHealingBoost", 2)).thenReturn(2);

        manager = new MedicalManager();
        mockWorld = mock(WorldMap.class);
        mockHospital = mock(Hospital.class);
        mockContext = mock(WorldContext.class);

        when(mockWorld.getHospitals()).thenReturn(List.of(mockHospital));
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    @Test
    void shouldDischargePatientIfNoLongerWantsHospital() {
        Human mockPatient = mock(Human.class);
        when(mockPatient.isWantsHospital()).thenReturn(false);

        List<epidemic.model.HospitalUser> patients = new ArrayList<>();
        patients.add(mockPatient);
        when(mockHospital.getPatients()).thenReturn(patients);

        manager.processMedicalCare(mockWorld, mockContext);

        verify(mockPatient).setIsInHospital(false);
        assertEquals(0, patients.size(), "Pacjent powinien zostać usunięty z listy szpitala");
    }

    @Test
    void shouldVaccinateAndDischargeHealthyPatient() {
        Human mockPatient = mock(Human.class);
        when(mockPatient.isWantsHospital()).thenReturn(true);
        when(mockPatient.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);
        when(mockPatient.isVaccinated()).thenReturn(false);

        when(mockContext.isVaccineAvailable()).thenReturn(true);

        List<epidemic.model.HospitalUser> patients = new ArrayList<>();
        patients.add(mockPatient);
        when(mockHospital.getPatients()).thenReturn(patients);

        manager.processMedicalCare(mockWorld, mockContext);

        verify(mockPatient).setVaccinated(true);
        verify(mockPatient).setWantsHospital(false);
        verify(mockPatient).setIsInHospital(false);
        assertEquals(0, patients.size());
    }

    @Test
    void shouldHealSickPatientAndKeepThemIfStillSick() {
        Human mockPatient = mock(Human.class);
        when(mockPatient.isWantsHospital()).thenReturn(true);
        when(mockPatient.getHealthStatus()).thenReturn(HealthStatus.SICK);
        when(mockPatient.getRemainingInfectionEpochs()).thenReturn(5);

        List<epidemic.model.HospitalUser> patients = new ArrayList<>();
        patients.add(mockPatient);
        when(mockHospital.getPatients()).thenReturn(patients);

        manager.processMedicalCare(mockWorld, mockContext);

        // Zboostowane leczenie obniża czas trwania choroby o 2 (zostało 3)
        verify(mockPatient).setRemainingInfectionEpochs(3);
        verify(mockPatient, never()).setIsInHospital(false);
        assertEquals(1, patients.size(), "Pacjent nie wyleczył się w pełni, zostaje w szpitalu");
    }
}