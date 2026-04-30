package epidemic.model;

import epidemic.service.Config;

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
        double rate = Config.getDouble("infectionField.dissipationRate", 0.05);
        this.intensity = Math.max(0, this.intensity - rate);

        if (this.expirationTime > 0) {
            this.expirationTime--;
        }
    }
}