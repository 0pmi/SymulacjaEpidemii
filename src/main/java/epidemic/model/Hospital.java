package epidemic.model;

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
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Inspektor Szpitala ===\n");
        sb.append("Pozycja: ").append(position).append("\n");
        sb.append("Obłożenie: ").append(getPatients().size()).append(" / ").append(capacity).append("\n");
        if (!getPatients().isEmpty()) {
            sb.append("\nLista pacjentów (ID):\n");
            for (HospitalUser user : getPatients()) {
                sb.append("- ").append(Integer.toHexString(user.hashCode())).append("\n");
            }
        } else {
            sb.append("\nSzpital jest pusty.\n");
        }
        return sb.toString();
    }

}