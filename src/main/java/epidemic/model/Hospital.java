package epidemic.model;

import java.util.ArrayList;
import java.util.List;

public class Hospital {
    private Point2D position;
    private int capacity;
    private List<Agent> patients;

    public Hospital(int capacity) {
        this.capacity = capacity;
        this.patients = new ArrayList<>();
    }

    public void applyTreatment(Human human) {
        // TODO
    }

    public boolean addPatient(Agent a) {
        if (patients.size() < capacity) {
            patients.add(a);
            return true;
        }
        return false;
    }

    public void releasePatient(Agent a) {
        patients.remove(a);
    }
    public Point2D getPosition() { return position; }
    public List<Agent> getPatients() {
        return patients;
    }
}