package epidemic.factory;

import epidemic.model.*;
import epidemic.service.Config;
import epidemic.strategies.movement.MovementStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AgentFactoryTest {

    private MockedStatic<Config> mockedConfig;
    private AgentFactory factory;
    private MovementStrategy mockStrategy;
    private Personality mockPersonality;

    @BeforeEach
    void setUp() {
        mockedConfig = Mockito.mockStatic(Config.class);
        mockedConfig.when(() -> Config.getDouble("human.initialResistance", 0.1)).thenReturn(0.5);
        mockedConfig.when(() -> Config.getDouble("human.offspringResistance", 1.0)).thenReturn(0.9);

        factory = new AgentFactory();
        mockStrategy = mock(MovementStrategy.class);
        mockPersonality = mock(Personality.class);
    }

    @AfterEach
    void tearDown() {
        mockedConfig.close();
    }

    @Test
    void shouldCreateHumanWithCorrectAttributes() {
        Point2D pos = new Point2D(10, 10);
        Human human = factory.createHuman(pos, 30, 2.5, mockPersonality, mockStrategy);

        assertNotNull(human);
        assertEquals(SpeciesType.HUMAN, human.getSpeciesType());
        assertEquals(10, human.getPosition().x());
        assertEquals(30, human.getAge());
        assertEquals(2.5, human.getBaseSpeed());
        assertEquals(0.5, human.getResistance());
    }

    @Test
    void shouldCreateAnimalWithCorrectAttributes() {
        Point2D pos = new Point2D(5, 5);
        Animal animal = factory.createAnimal(pos, 5, 4.0, SpeciesType.DOG, mockStrategy);

        assertNotNull(animal);
        assertEquals(SpeciesType.DOG, animal.getSpeciesType());
        assertEquals(5, animal.getAge());
        assertEquals(4.0, animal.getBaseSpeed());
    }

    @Test
    void shouldCreateHumanOffspring() {
        Point2D parentPos = new Point2D(100, 100);
        Human parentA = mock(Human.class);
        when(parentA.getPosition()).thenReturn(parentPos);
        when(parentA.getSpeciesType()).thenReturn(SpeciesType.HUMAN);
        when(parentA.getMovementStrategy()).thenReturn(mockStrategy);
        when(parentA.getBaseSpeed()).thenReturn(2.5);
        when(parentA.getPersonality()).thenReturn(mockPersonality);

        Human parentB = mock(Human.class);
        when(parentB.getPersonality()).thenReturn(mockPersonality);

        Agent offspring = factory.createOffspring(parentA, parentB);

        assertNotNull(offspring);
        assertTrue(offspring instanceof Human);
        Human humanBaby = (Human) offspring;

        assertEquals(parentPos, humanBaby.getPosition());
        assertEquals(0, humanBaby.getAge()); // Noworodek ma wiek 0
        assertEquals(2.5, humanBaby.getBaseSpeed());
        assertEquals(0.9, humanBaby.getResistance()); // Wartość z mockowanego configu dla dziecka
    }
}