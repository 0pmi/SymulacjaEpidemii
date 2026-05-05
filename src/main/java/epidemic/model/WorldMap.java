package epidemic.model;

import epidemic.service.SpatialManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Centralne repozytorium reprezentujące dyskretne środowisko przestrzenne symulacji.
 * Zarządza cyklem życia oraz kolekcjami agentów i infrastruktury medycznej (szpitali).
 * Realizuje opóźnione modyfikacje list (wzorzec podwójnego buforowania) w celu zapewnienia
 * bezpieczeństwa wątkowego i uniknięcia błędów podczas iteracji, a także deleguje
 * złożone zapytania przestrzenne do dedykowanego komponentu {@link SpatialManager}.
 */
public class WorldMap {
    private List<Agent> agents;
    private List<Hospital> hospitals;
    private int width;
    private int height;
    private SpatialManager spatialManager;
    private List<Agent> agentsToAdd;
    private List<Agent> agentsToRemove;
    private final java.util.Map<String, InfectionField> airborneFields = new java.util.concurrent.ConcurrentHashMap<>();

    /**
     * Inicjalizuje nową przestrzeń o zadanych wymiarach.
     *
     * @param width Szerokość mapy w jednostkach logicznych.
     * @param height Wysokość mapy w jednostkach logicznych.
     * @param cellSize Rozmiar pojedynczej komórki siatki używanej przez menedżera przestrzennego.
     */
    public WorldMap(int width, int height, double cellSize) {
        this.width = width;
        this.height = height;
        this.agents = new ArrayList<>();
        this.hospitals = new ArrayList<>();
        this.agentsToAdd = new ArrayList<>();
        this.agentsToRemove = new ArrayList<>();
        this.spatialManager = new SpatialManager(width, height, cellSize);
    }

    /**
     * Zleca dodanie nowego agenta do środowiska.
     * Wykorzystuje mechanizm buforowania – agent zostanie faktycznie udostępniony
     * dla logiki silnika dopiero po wywołaniu metody {@link #applyChanges()}.
     *
     * @param agent Jednostka do zaaplikowania na mapie.
     */
    public void addAgent(Agent agent) {
        agentsToAdd.add(agent);
    }

    /**
     * Zleca usunięcie istniejącego agenta ze środowiska (np. w wyniku zgonu).
     * Podobnie jak przy dodawaniu, usunięcie zostaje zbuforowane.
     *
     * @param agent Jednostka do zaaplikowania na mapie.
     */
    public void removeAgent(Agent agent) {
        agentsToRemove.add(agent);
    }

    /**
     * Aplikuje wszystkie zakolejkowane zmiany z buforów do głównej listy agentów.
     * Metoda ta zapobiega wyjątkom typu {@code ConcurrentModificationException} podczas
     * iterowania po głównej kolekcji w poszczególnych fazach epoki.
     */
    public void applyChanges() {
        agents.addAll(agentsToAdd);
        agentsToAdd.clear();
        agents.removeAll(agentsToRemove);
        agentsToRemove.clear();
    }

    /**
     * Wyszukuje agentów znajdujących się w określonym promieniu od zadanego punktu przestrzennego.
     *
     * @param pos Centralny punkt wyszukiwania.
     * @param radius Zasięg wyszukiwania w jednostkach mapy.
     * @return Lista jednostek znajdujących się w strefie.
     */
    public List<Agent> getNeighbors(Point2D pos, double radius) {
        return spatialManager.getNearbyAgentsAtPos(pos, radius);
    }

    /**
     * Wyszukuje bezpośrednich sąsiadów dla wskazanego agenta.
     *
     * @param agent Agent stanowiący centrum obszaru poszukiwań.
     * @param radius Promień wyszukiwania.
     * @return Lista jednostek przebywających w pobliżu.
     */
    public List<Agent> getNeighborsForAgent(Agent agent, double radius) {
        return spatialManager.getNearbyAgents(agent, radius);
    }

    /**
     * Zleca przebudowę globalnego indeksu przestrzennego na podstawie aktualnych
     * pozycji wszystkich agentów w głównej kolekcji.
     */
    public void rebuildSpatialIndex() {
        spatialManager.rebuild(this);
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public List<Agent> getAgents() { return agents; }
    public List<Hospital> getHospitals() { return hospitals; }

    /**
     * Umieszcza nową placówkę medyczną w przestrzeni symulacji.
     *
     * @param hospital Skonfigurowany obiekt szpitala.
     */
    public void addHospital(Hospital hospital) {
        this.hospitals.add(hospital);
    }

    public SpatialManager getSpatialManager() { return spatialManager; }

    /**
     * Wyszukuje szpital znajdujący się dokładnie we wskazanych współrzędnych geograficznych siatki.
     *
     * @param pos Pozycja do weryfikacji.
     * @return Obiekt placówki lub {@code null}, jeśli infrastruktura nie istnieje w danym punkcie.
     */
    public Hospital getHospitalAt(Point2D pos) {
        return hospitals.stream()
                .filter(h -> h.getPosition().equals(pos))
                .findFirst()
                .orElse(null);
    }

    /**
     * Sprawdza geometryczną poprawność położenia na mapie.
     *
     * @param pos Punkt przestrzenny do weryfikacji.
     * @return {@code true}, jeśli punkt leży ściśle wewnątrz dozwolonych granic mapy.
     */
    public boolean isWithinBounds(Point2D pos) {
        return pos.x() >= 0 && pos.x() < width &&
                pos.y() >= 0 && pos.y() < height;
    }

    /**
     * Rejestruje nowe pole infekcji (aerozol) lub odświeża już istniejące na danej współrzędnej.
     * Mapowanie realizowane jest z wykorzystaniem słownika wielowątkowego (ConcurrentHashMap),
     * a klucz generowany jest ze złączenia współrzędnych (format "X,Y"), co gwarantuje
     * optymalizację wyszukiwania i aktualizacji w stałym czasie O(1).
     *
     * @param pos Dokładna pozycja źródła powstania infekcji środowiskowej.
     * @param infectivity Parametr określający siłę zostawionego wirusa.
     */
    public void addOrRefreshInfectionField(Point2D pos, double infectivity) {
        // Normalizacja pozycji do kratki na mapie
        String key = (int)pos.x() + "," + (int)pos.y();

        airborneFields.compute(key, (k, existingField) -> {
            if (existingField == null) {
                return new InfectionField(new Point2D((int)pos.x(), (int)pos.y()), infectivity);
            } else {
                existingField.refresh(infectivity);
                return existingField;
            }
        });
    }

    /**
     * Pobiera stan zakażenia środowiskowego z konkretnej komórki przestrzeni.
     *
     * @param pos Pozycja docelowa do przeanalizowania.
     * @return Odpowiedni obiekt chmury (InfectionField) lub {@code null}, jeśli powietrze w tym miejscu jest czyste.
     */
    public InfectionField getFieldAt(Point2D pos) {
        String key = (int)pos.x() + "," + (int)pos.y();
        return airborneFields.get(key);
    }

    /**
     * Udostępnia bezstanowy widok na wszystkie aktywne chmury zakaźne w środowisku.
     * Metoda używana głównie na potrzeby silnika renderującego (GUI).
     *
     * @return Kolekcja aktualnie istniejących stref skażenia.
     */
    public java.util.Collection<InfectionField> getActiveFields() {
        return airborneFields.values();
    }

    /**
     * Przetwarza cykl życia stref skażeń środowiskowych.
     * Redukuje siłę zakaźną wszystkich istniejących chmur w środowisku i automatycznie
     * usuwa z kolekcji (ewikcja) te, które osiągnęły próg wygaśnięcia.
     */
    public void decayInfectionFields() {
        airborneFields.values().removeIf(field -> {
            field.decay();
            return field.isExpired();
        });
    }
}