package epidemic.strategies.decision;

import epidemic.model.HealthStatus;
import epidemic.model.Human;
import epidemic.model.WorldContext;
import epidemic.service.Config;
import epidemic.strategies.movement.MovementStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

/**
 * Zestaw testów jednostkowych weryfikujący logikę "mściwej" strategii decyzyjnej.
 * Koncentruje się na sprawdzaniu maszyny stanów: od normalnego oczekiwania na leczenie,
 * poprzez eskalację frustracji, aż po wejście w tryb agresywny (Hostile).
 */
class VindictiveDecisionStrategyTest {

    private MockedStatic<Config> mockedConfig;
    private MovementStrategy mockMaliciousMovement;
    private MovementStrategy mockHospitalMovement;
    private MovementStrategy mockNormalMovement;

    private VindictiveDecisionStrategy strategy;
    private Human mockHuman;
    private WorldContext mockContext;

    @BeforeEach
    void setUp() {
        mockedConfig = Mockito.mockStatic(Config.class);
        // Ustawiamy próg frustracji na bardzo niski poziom (3 epoki), aby przyspieszyć test
        mockedConfig.when(() -> Config.getInt("vindictive.frustrationThreshold", 10)).thenReturn(3);

        mockMaliciousMovement = mock(MovementStrategy.class);
        mockHospitalMovement = mock(MovementStrategy.class);
        mockNormalMovement = mock(MovementStrategy.class);

        strategy = new VindictiveDecisionStrategy(
                mockMaliciousMovement, mockHospitalMovement, mockNormalMovement);

        mockHuman = mock(Human.class);
        mockContext = mock(WorldContext.class);
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    /**
     * Weryfikuje prawidłowe zachowanie agenta, gdy po prostu udaje się do szpitala
     * i frustracja nie osiągnęła jeszcze punktu krytycznego.
     */
    @Test
    void shouldWaitPatientlyForHospitalIfBelowFrustrationThreshold() {
        // Arrange
        when(mockHuman.getHealthStatus()).thenReturn(HealthStatus.SICK);
        when(mockHuman.isInHospital()).thenReturn(false);

        // Act - Symulujemy 2 epoki czekania pod szpitalem (próg to 3)
        strategy.makeDecision(mockHuman, mockContext);
        strategy.makeDecision(mockHuman, mockContext);

        // Assert
        verify(mockHuman, times(2)).setWantsHospital(true);
        verify(mockHuman, times(2)).setMovementStrategy(mockHospitalMovement);
        verify(mockHuman, never()).setHostile(true); // Agent jest cierpliwy
    }

    /**
     * Główny test maszyny stanów. Agent, który nie został przyjęty do szpitala
     * przez N epok, musi wpaść w furię, zdjąć maskę i zmienić wektor ruchu na złośliwy.
     */
    @Test
    void shouldBecomeHostileAndMaliciousWhenFrustrationThresholdIsReached() {
        // Arrange
        when(mockHuman.getHealthStatus()).thenReturn(HealthStatus.SICK);
        when(mockHuman.isInHospital()).thenReturn(false);

        // Act - Symulujemy 3 epoki czekania pod szpitalem (równo z progiem)
        strategy.makeDecision(mockHuman, mockContext); // Epoka 1
        strategy.makeDecision(mockHuman, mockContext); // Epoka 2
        strategy.makeDecision(mockHuman, mockContext); // Epoka 3 - pęknięcie

        // Assert
        verify(mockHuman).setWearingMask(false); // Zdejmuje maseczkę
        verify(mockHuman).setHostile(true); // Ustawia flagę zagrożenia
        verify(mockHuman).setMovementStrategy(mockMaliciousMovement); // Zmienia cel na atak
    }

    /**
     * Sprawdza, czy wejście do szpitala poprawnie resetuje mechanizm złości agenta.
     */
    @Test
    void shouldCalmDownIfSuccessfullyAdmittedToHospital() {
        // Arrange
        when(mockHuman.getHealthStatus()).thenReturn(HealthStatus.SICK);

        // Act 1 - Agent czeka pod szpitalem, rośnie mu frustracja
        when(mockHuman.isInHospital()).thenReturn(false);
        strategy.makeDecision(mockHuman, mockContext);

        // Act 2 - Agentowi udaje się dostać łóżko przed wybuchem paniki
        when(mockHuman.isInHospital()).thenReturn(true);
        strategy.makeDecision(mockHuman, mockContext);

        // Assert
        verify(mockHuman).setHostile(false); // Uspokaja się
        verify(mockHuman, times(2)).setMovementStrategy(mockHospitalMovement); // Wciąż trzyma się strategii medycznej
    }

    /**
     * Sprawdza powrót agenta do normalności po pomyślnym przebyciu choroby.
     * Frustracja powinna zostać wyczyszczona z pamięci (Mapy).
     */
    @Test
    void shouldBehaveNormallyWhenRecoveredOrHealthy() {
        // Arrange
        when(mockHuman.getHealthStatus()).thenReturn(HealthStatus.RECOVERED);

        // Act
        strategy.makeDecision(mockHuman, mockContext);

        // Assert
        verify(mockHuman).setWantsHospital(false);
        verify(mockHuman).setHostile(false);
        verify(mockHuman).setMovementStrategy(mockNormalMovement);
    }
}