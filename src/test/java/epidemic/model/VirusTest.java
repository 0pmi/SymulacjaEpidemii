package epidemic.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class VirusTest {

    @Test
    void shouldInitializeAndReturnCorrectValues() {
        Virus virus = new Virus(0.75, 5.0, 14);

        assertEquals(0.75, virus.getBaseInfectionProbability());
        assertEquals(5.0, virus.getInfectionRadius());
        assertEquals(14, virus.getDefaultInfectionDuration());
    }
}