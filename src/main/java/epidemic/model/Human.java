package epidemic.model;

public class Human extends Agent{
    private double resistance;
    private int infectionDuration;
    private Personality personality;
    private boolean isVaccinated;
    private boolean isWearingMask;
    private boolean wantsMedicalCare;

    public Human(Point2D position, int age, double naturalMortalityRate, double resistance, Personality personality) {
        super(position, age, naturalMortalityRate);

        this.resistance = resistance;
        this.personality = personality;

        this.infectionDuration = 0;
        this.isVaccinated = false;
        this.isWearingMask = false;
        this.wantsMedicalCare = false;
    }

    @Override
    public boolean wantsHospital() {
        return this.wantsMedicalCare;
    }
}
