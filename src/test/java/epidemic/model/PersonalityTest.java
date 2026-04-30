package epidemic.model;

import epidemic.strategies.decision.DecisionStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PersonalityTest {

    @Test
    void shouldDelegateMentalStateUpdateToStrategy() {
        DecisionStrategy mockStrategy = mock(DecisionStrategy.class);
        Personality personality = new Personality(mockStrategy);

        Human mockHuman = mock(Human.class);
        WorldContext mockContext = mock(WorldContext.class);

        personality.updateMentalState(mockHuman, mockContext);

        verify(mockStrategy, times(1)).makeDecision(mockHuman, mockContext);
        assertEquals(mockStrategy, personality.getDecisionStrategy());
    }
}