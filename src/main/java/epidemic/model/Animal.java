package epidemic.model;

public class Animal extends Agent {

    private boolean isCarrierOnly;
    private double speciesVirulence;

    public Animal(Point2D position, int age, double naturalMortalityRate, boolean isCarrierOnly, double speciesVirulence) {
        super(position, age, naturalMortalityRate);

        this.isCarrierOnly = isCarrierOnly;
        this.speciesVirulence = speciesVirulence;
    }
}