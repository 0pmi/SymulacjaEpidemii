package epidemic.model;

import java.util.ArrayList;
import java.util.List;

public class Hospital {
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
}