package epidemic.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Zestaw testów weryfikujący poprawność działania infrastruktury medycznej.
 */
class HospitalTest {

    private Hospital hospital;

    @BeforeEach
    void setUp() {
        hospital = new Hospital(2, new Point2D(10, 10)); // Bardzo mały szpital na 2 miejsca
    }

    @Test
    void shouldInitializeProperly() {
        assertEquals(2, hospital.getCapacity());
        assertEquals(10, hospital.getPosition().x());
        assertEquals(10, hospital.getPosition().y());
        assertTrue(hospital.getPatients().isEmpty(), "Nowy szpital nie powinien posiadać pacjentów");
    }

    @Test
    void shouldAcceptPatientsUntilFull() {
        HospitalUser patient1 = mock(HospitalUser.class);
        HospitalUser patient2 = mock(HospitalUser.class);
        HospitalUser patient3 = mock(HospitalUser.class); // Pacjent, który się nie zmieści

        assertTrue(hospital.addPatient(patient1), "Szpital z wolnymi łóżkami powinien przyjąć pierwszego pacjenta");
        assertTrue(hospital.addPatient(patient2), "Szpital z wolnymi łóżkami powinien przyjąć drugiego pacjenta");
        assertFalse(hospital.addPatient(patient3), "Pełny szpital musi odrzucić nowego pacjenta");

        assertEquals(2, hospital.getPatients().size());
    }

    @Test
    void shouldGenerateCorrectInspectionProperties() {
        // Arrange
        HospitalUser patient = mock(HospitalUser.class);
        hospital.addPatient(patient);

        // Act
        assertEquals("Szpital Polowy", hospital.getObjectName());
        List<InspectionProperty> props = hospital.getInspectionProperties();

        // Assert
        assertEquals(2, props.size(), "Szpital powinien zwracać dokładnie dwie właściwości do GUI");

        // Weryfikacja pierwszej cechy
        assertEquals("Pozycja", props.get(0).label());
        assertEquals("[10, 10]", props.get(0).stringValue());

        // Weryfikacja paska zajętości. Szpital jest w połowie pełny (1/2), więc kolor nie powinien być czerwony
        InspectionProperty bar = props.get(1);
        assertEquals("Obłożenie Łóżek", bar.label());
        assertEquals(1, bar.progressValue());
        assertEquals(2, bar.progressMax());
        assertNotEquals(Color.RED, bar.highlightColor(), "Szpital w połowie pusty nie powinien być alarmujący");
    }
}