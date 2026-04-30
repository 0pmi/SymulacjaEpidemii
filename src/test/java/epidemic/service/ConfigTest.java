package epidemic.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigTest {

    @Test
    void shouldReturnDefaultDoubleWhenKeyIsMissing() {
        double value = Config.getDouble("missing.double.key", 3.14);
        assertEquals(3.14, value, 0.001);
    }

    @Test
    void shouldReturnDefaultIntWhenKeyIsMissing() {
        int value = Config.getInt("missing.int.key", 42);
        assertEquals(42, value);
    }

    @Test
    void shouldReturnDefaultStringWhenKeyIsMissing() {
        String value = Config.getString("missing.string.key", "default_val");
        assertEquals("default_val", value);
    }
}