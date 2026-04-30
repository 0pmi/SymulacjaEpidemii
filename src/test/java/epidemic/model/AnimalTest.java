package epidemic.model;

import epidemic.strategies.movement.MovementStrategy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class AnimalTest {

    @Test
    void shouldInitializeAnimalCorrectly() {
        Point2D position = new Point2D(10, 10);
        MovementStrategy mockMovement = mock(MovementStrategy.class);

        Animal dog = new Animal(position, 3, SpeciesType.DOG, 5.0, mockMovement);

        assertEquals(SpeciesType.DOG, dog.getSpeciesType());
        assertEquals(5.0, dog.getBaseSpeed());
        assertEquals(3, dog.getAge());
        assertTrue(dog.getDetailedInfo().contains("Instynkt Zwierzęcy"), "Zwierzę powinno mieć specyficzne info w oknie inspektora");
    }
}