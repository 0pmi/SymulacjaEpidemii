package epidemic.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Reprezentuje infrastrukturę medyczną na mapie.
 * Odpowiada za rejestrację i przechowywanie pacjentów, limitowanie przepustowości usług
 * opieki zdrowotnej oraz eksponowanie swoich wskaźników do widoków telemetrycznych.
 */
public class Hospital implements Inspectable {

    /**
     * Pobiera zdefiniowany limit dostępnych miejsc w placówce.
     *
     * @return Maksymalna dopuszczalna liczba pacjentów.
     */
    public int getCapacity() {
        return capacity;
    }

    private final int capacity;
    private final List<HospitalUser> patients;
    private final Point2D position;

    /**
     * Konstruuje nową placówkę medyczną gotową do przyjmowania pacjentów.
     *
     * @param capacity Ograniczenie liczby pacjentów leczonych w tym samym czasie.
     * @param position Statyczne koordynaty obiektu w świecie symulacji     .
     */
    public Hospital(int capacity, Point2D position) {
        this.capacity = capacity;
        this.position = position;
        this.patients = new ArrayList<>();
    }

    /**
     * Podejmuje próbę zapisu pacjenta na oddział pod warunkiem dostępności łóżek.
     *
     * @param user Encja wyrażająca chęć i uprawnienia do hospitalizacji.
     * @return {@code true}, jeśli pacjent został skutecznie wpisany; {@code false}, jeśli brak wolnych miejsc.
     */
    public boolean addPatient(HospitalUser user) {
        if (patients.size() < capacity) {
            patients.add(user);
            return true;
        }
        return false;
    }

    /**
     * Pobiera dokładną pozycję obiektu.
     *
     * @return Wektor położenia szpitala.
     */
    public Point2D getPosition() {
        return position;
    }

    /**
     * Udostępnia listę pacjentów, którzy aktualnie są w trakcie hospitalizacji.
     *
     * @return Kolekcja podopiecznych szpitala.
     */
    public List<HospitalUser> getPatients() {
        return patients;
    }

    /**
     * Zwraca etykietę nazwy podmiotu wykorzystywaną w warstwie gui.
     */
    @Override
    public String getObjectName() {
        return "Szpital Polowy";
    }

    /**
     * Generuje zbiór metadanych analitycznych dotyczących bieżącego obciążenia szpitala.
     * Kalkuluje estetykę paska postępu, dostosowując kolor do gęstości wykorzystania infrastruktury:
     * od standardowego niebieskiego, przez alarmowy pomarańczowy (powyżej 50%), aż po krytyczny czerwony (powyżej 90%).
     *
     * @return Dynamicznie generowana lista atrybutów dla inspektora.
     */
    @Override
    public List<InspectionProperty> getInspectionProperties() {
        List<InspectionProperty> props = new ArrayList<>();
        props.add(InspectionProperty.text("Pozycja", "[" + position.x() + ", " + position.y() + "]"));

        double fillRatio = (double) patients.size() / capacity;
        Color barColor = fillRatio > 0.9 ? Color.RED : (fillRatio > 0.5 ? Color.ORANGE : new Color(30, 144, 255));

        props.add(InspectionProperty.progressBar("Obłożenie Łóżek", patients.size(), capacity, barColor));

        return props;
    }

}