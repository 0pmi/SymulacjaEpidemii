package epidemic.model;

public class InfectionField {
    private Point2D position;
    private double intensity;
    private int expirationTime;

    public InfectionField(Point2D position, double intensity, int expirationTime) {
        this.position = position;
        this.intensity = intensity;
        this.expirationTime = expirationTime;
    }

    public void dissipate() {
        // TODO:
    }
}