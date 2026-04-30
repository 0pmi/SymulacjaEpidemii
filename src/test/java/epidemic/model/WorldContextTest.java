package epidemic.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WorldContextTest {

    @Test
    void shouldInitializeAndReturnCorrectValues() {
        WorldContext context = new WorldContext(0.35, true, 100, 0.05);

        assertEquals(0.35, context.getInfectionPercentage());
        assertTrue(context.isVaccineAvailable());
        assertEquals(100, context.getCurrentEpoch());
        assertEquals(0.05, context.getMortalityRatio());
    }
}