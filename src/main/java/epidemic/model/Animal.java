package epidemic.model;

public class Animal extends Agent {

    // --- Unikalne cechy zwierząt w kontekście epidemii ---
    //private boolean isCarrierOnly;
    private double speciesVirulence;

    public Animal(Point2D position, int age, SpeciesType speciesType, double baseSpeed, double naturalMortalityRate, boolean isCarrierOnly, double speciesVirulence) {
        super(position, age, speciesType, baseSpeed, naturalMortalityRate);

        //this.isCarrierOnly = isCarrierOnly;
        this.speciesVirulence = speciesVirulence;
    }

    // --- Gettery i Settery ---
    //public boolean isCarrierOnly() { return isCarrierOnly; }
    //public void setCarrierOnly(boolean carrierOnly) { this.isCarrierOnly = carrierOnly; }

    public double getSpeciesVirulence() { return speciesVirulence; }
    public void setSpeciesVirulence(double speciesVirulence) { this.speciesVirulence = speciesVirulence; }
}