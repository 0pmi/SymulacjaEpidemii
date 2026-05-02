package epidemic.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Hospital implements Inspectable {
    public int getCapacity() {
        return capacity;
    }

    private final int capacity;
    private final List<HospitalUser> patients;
    private final Point2D position;

    public Hospital(int capacity, Point2D position) {
        this.capacity = capacity;
        this.position = position;
        this.patients = new ArrayList<>();
    }

    public boolean addPatient(HospitalUser user) {
        if (patients.size() < capacity) {
            patients.add(user);
            return true;
        }
        return false;
    }
    public Point2D getPosition() {
        return position;
    }
    public List<HospitalUser> getPatients() {
        return patients;
    }
    @Override
    public String getObjectName() {
        return "Szpital Polowy";
    }

    @Override
    public List<InspectionProperty> getInspectionProperties() {
        List<InspectionProperty> props = new ArrayList<>();
        props.add(InspectionProperty.text("Pozycja", "[" + position.x() + ", " + position.y() + "]"));

        // Obliczenia kolorów paska postępu dla GUI przeniesione blisko domeny
        double fillRatio = (double) patients.size() / capacity;
        Color barColor = fillRatio > 0.9 ? Color.RED : (fillRatio > 0.5 ? Color.ORANGE : new Color(30, 144, 255));

        props.add(InspectionProperty.progressBar("Obłożenie Łóżek", patients.size(), capacity, barColor));

        return props;
    }

}