package epidemic.model;

import epidemic.service.Config;
import epidemic.strategies.movement.MovementStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Klasa testowa odpowiedzialna za weryfikację logiki biznesowej obiektów klasy {@link Animal}.
 * Sprawdza poprawność inicjalizacji stanu początkowego oraz integrację z warstwą DTO
 * wykorzystywaną przez komponenty graficzne (Inspector).
 */
class AnimalTest {

    private MockedStatic<Config> mockedConfig;

    /**
     * Konfiguracja środowiska przed każdym testem.
     * Inicjalizuje statyczne atrapy (mocki) dla globalnej klasy konfiguracji,
     * aby odizolować testy od warstwy wejścia/wyjścia (pliku .properties).
     */
    @BeforeEach
    void setUp() {
        mockedConfig = mockStatic(Config.class);

        // Definiowanie zachowania atrapy dla parametrów wymaganych przez metodę getInspectionProperties() klasy bazowej Agent
        mockedConfig.when(() -> Config.getInt("mortality.maxAge", 100)).thenReturn(100);
        mockedConfig.when(() -> Config.getDouble("agent.defaultVulnerability", 1.0)).thenReturn(1.0);
    }

    /**
     * Sprzątanie po wykonaniu testu.
     * Zamknięcie atrapy statycznej zapobiega wyciekom zasobów i potencjalnej
     * interferencji (cross-talk) z innymi testami w ramach tego samego wątku.
     */
    @AfterEach
    void tearDown() {
        if (mockedConfig != null) {
            mockedConfig.close();
        }
    }

    /**
     * Weryfikuje poprawność przypisania argumentów konstruktora do wewnętrznego stanu obiektu.
     */
    @Test
    void shouldInitializeAnimalCorrectly() {
        // Arrange
        Point2D position = new Point2D(10, 10);
        MovementStrategy mockMovement = mock(MovementStrategy.class);

        // Act
        Animal dog = new Animal(position, 3, SpeciesType.DOG, 5.0, mockMovement);

        // Assert
        assertEquals(SpeciesType.DOG, dog.getSpeciesType(), "Typ gatunku powinien zostać poprawnie przypisany");
        assertEquals(5.0, dog.getBaseSpeed(), "Prędkość bazowa powinna zostać poprawnie przypisana");
        assertEquals(3, dog.getAge(), "Wiek początkowy powinien zostać poprawnie przypisany");
    }

    /**
     * Weryfikuje mechanizm generowania obiektów transferu danych (DTO) typu {@link InspectionProperty}.
     * Test upewnia się, że klasa dziedzicząca (Animal) zachowuje właściwości klasy bazowej
     * i skutecznie rozszerza zbiór o swoje unikalne atrybuty.
     */
    @Test
    void shouldReturnCorrectInspectionProperties() {
        // Arrange
        Point2D position = new Point2D(10, 10);
        MovementStrategy mockMovement = mock(MovementStrategy.class);
        Animal dog = new Animal(position, 3, SpeciesType.DOG, 5.0, mockMovement);

        // Act
        List<InspectionProperty> properties = dog.getInspectionProperties();
        String objectName = dog.getObjectName();

        // Assert
        assertEquals("Typ: DOG", objectName, "Nagłówek obiektu powinien zawierać poprawny typ gatunku");
        assertNotNull(properties, "Kolekcja właściwości inspekcji nie może być wartością null");
        assertFalse(properties.isEmpty(), "Kolekcja właściwości inspekcji nie powinna być pusta");

        // Weryfikacja obecności specyficznego dla zwierzęcia atrybutu (prędkość bazowa) w DTO
        boolean hasSpeedProperty = properties.stream()
                .anyMatch(p -> "Prędkość bazowa".equals(p.label()) && "5.0".equals(p.stringValue()));

        assertTrue(hasSpeedProperty, "Struktura DTO powinna zawierać właściwość 'Prędkość bazowa' o odpowiedniej wartości");
    }
}