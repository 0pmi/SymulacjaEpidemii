package epidemic.managers;

import epidemic.model.*;
import epidemic.service.Config;
import epidemic.service.SpatialManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.*;

class InfectionManagerTest {

    private MockedStatic<Config> mockedConfig;
    private InfectionManager manager;
    private Virus mockVirus;
    private WorldMap mockWorld;
    private SpatialManager mockSpatialManager;

    @BeforeEach
    void setUp() {
        mockedConfig = Mockito.mockStatic(Config.class);
        // Parametry promujące zakażenia dla celów testu (100% szans)
        mockedConfig.when(() -> Config.getDouble("infectionField.aerosolMultiplier", 0.3)).thenReturn(1.0);
        mockedConfig.when(() -> Config.getDouble("infection.carrierMultiplier", 0.5)).thenReturn(1.0);
        mockedConfig.when(() -> Config.getDouble("infection.carrierProbability", 0.2)).thenReturn(0.0); // Zawsze SICK
        mockedConfig.when(() -> Config.getInt("virus.defaultDuration", 30)).thenReturn(30);

        mockVirus = mock(Virus.class);
        when(mockVirus.getBaseInfectionProbability()).thenReturn(1.0);
        when(mockVirus.getInfectionRadius()).thenReturn(5.0);
        when(mockVirus.getDefaultInfectionDuration()).thenReturn(30);

        mockWorld = mock(WorldMap.class);
        mockSpatialManager = mock(SpatialManager.class);
        when(mockWorld.getSpatialManager()).thenReturn(mockSpatialManager);

        manager = new InfectionManager(mockVirus);
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    @Test
    void shouldLeaveInfectionFieldAndInfectDirectly() {
        Agent sickAgent = mock(Agent.class);
        when(sickAgent.isDead()).thenReturn(false);
        when(sickAgent.getHealthStatus()).thenReturn(HealthStatus.SICK);
        when(sickAgent.getPosition()).thenReturn(new Point2D(10, 10));
        when(sickAgent.getVirulence()).thenReturn(1.0);

        Agent victim = mock(Agent.class);
        when(victim.canBeInfected()).thenReturn(true);
        when(victim.getPosition()).thenReturn(new Point2D(10, 10)); // Na tym samym polu
        when(victim.getVulnerabilityMultiplier()).thenReturn(1.0);

        when(mockWorld.getAgents()).thenReturn(List.of(sickAgent, victim));
        when(mockSpatialManager.getNearbyAgents(sickAgent, 5.0)).thenReturn(List.of(victim));

        manager.processInfections(mockWorld);

        // Chory agent powienien zostawić chmurę wirusa na mapie
        verify(mockWorld).addOrRefreshInfectionField(eq(new Point2D(10, 10)), anyDouble());

        // Ofiara powinna zostać zakażona bezpośrednio
        verify(victim).setHealthStatus(HealthStatus.SICK);
        verify(victim).setRemainingInfectionEpochs(30);
    }

    @Test
    void shouldInfectFromAirborneField() {
        Agent healthyAgent = mock(Agent.class);
        when(healthyAgent.isDead()).thenReturn(false);
        when(healthyAgent.getHealthStatus()).thenReturn(HealthStatus.HEALTHY);
        when(healthyAgent.canBeInfected()).thenReturn(true);
        when(healthyAgent.getPosition()).thenReturn(new Point2D(20, 20));
        when(healthyAgent.getVulnerabilityMultiplier()).thenReturn(1.0); // Brak maski

        // Tworzymy silną chmurę zakaźną na polu agenta
        InfectionField mockField = mock(InfectionField.class);
        when(mockField.getInfectivity()).thenReturn(1.0); // 100% szans

        when(mockWorld.getAgents()).thenReturn(List.of(healthyAgent));
        when(mockWorld.getFieldAt(any(Point2D.class))).thenReturn(mockField);

        manager.processInfections(mockWorld);

        // Weryfikacja: agent zdrowy, bez bezpośredniego kontaktu z chorym, zaraził się z powietrza
        verify(healthyAgent).setHealthStatus(HealthStatus.SICK);
        verify(healthyAgent).setRemainingInfectionEpochs(30);
    }
    /**
     * Weryfikuje czy martwi agenci są ignorowani jako wektor zakażeń.
     */
    @Test
    void shouldNotSpreadVirusIfSpreaderIsDead() {
        Agent deadAgent = mock(Agent.class);
        when(deadAgent.isDead()).thenReturn(true);
        when(deadAgent.getHealthStatus()).thenReturn(HealthStatus.SICK); // Był chory przed śmiercią

        when(mockWorld.getAgents()).thenReturn(List.of(deadAgent));

        manager.processInfections(mockWorld);

        // Upewniamy się, że trup nie skaził środowiska i nie wyszukiwał sąsiadów
        verify(mockWorld, never()).addOrRefreshInfectionField(any(), anyDouble());
        verify(mockSpatialManager, never()).getNearbyAgents(any(), anyDouble());
    }

    /**
     * Weryfikuje bezobjawowego nosiciela (CARRIER), upewniając się, że
     * jego wpływ na środowisko jest poprawnie osłabiany przez mnożniki.
     */
    @Test
    void shouldSpreadWeakerVirusIfSpreaderIsCarrier() {
        // Odtwarzamy domyślną konfigurację tylko na potrzeby tego testu
        mockedConfig.when(() -> Config.getDouble("infectionField.aerosolMultiplier", 0.3)).thenReturn(0.3);
        mockedConfig.when(() -> Config.getDouble("infection.carrierMultiplier", 0.5)).thenReturn(0.5);

        Agent carrier = mock(Agent.class);
        when(carrier.isDead()).thenReturn(false);
        when(carrier.getHealthStatus()).thenReturn(HealthStatus.CARRIER);
        Point2D carrierPos = new Point2D(10, 10);
        when(carrier.getPosition()).thenReturn(carrierPos);

        when(mockWorld.getAgents()).thenReturn(List.of(carrier));

        manager.processInfections(mockWorld);

        // Obliczenie siły z jaką nosiciel zanieczyszcza środowisko:
        // Prawdopodobieństwo wirusa (1.0) * Mnożnik Aerozolu (0.3) * Mnożnik Nosiciela (0.5) = 0.15
        verify(mockWorld).addOrRefreshInfectionField(eq(carrierPos), eq(0.15));
    }
}