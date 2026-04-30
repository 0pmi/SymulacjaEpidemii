package epidemic.strategies.mortality;

import epidemic.model.Agent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ThresholdMortalityStrategyTest {

    @Test
    void shouldDieNaturallyIfAgeExceedsThreshold() {
        ThresholdMortalityStrategy strategy = new ThresholdMortalityStrategy(100);
        Agent mockAgent = mock(Agent.class);

        when(mockAgent.getAge()).thenReturn(99);
        assertFalse(strategy.shouldDieNaturally(mockAgent));

        when(mockAgent.getAge()).thenReturn(100);
        assertTrue(strategy.shouldDieNaturally(mockAgent));

        when(mockAgent.getAge()).thenReturn(150);
        assertTrue(strategy.shouldDieNaturally(mockAgent));
    }

    @Test
    void shouldNeverDieFromDisease() {
        ThresholdMortalityStrategy strategy = new ThresholdMortalityStrategy(100);
        Agent mockAgent = mock(Agent.class);

        assertFalse(strategy.shouldDieFromDisease(mockAgent));
    }
}