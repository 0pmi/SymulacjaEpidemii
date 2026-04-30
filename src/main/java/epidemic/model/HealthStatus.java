package epidemic.model;
/**
 * Definiuje dopuszczalne stany epidemiczne agenta w systemie.
 * Wykorzystywane przez menedżerów infekcji do decydowania o mechanice zarażania i kwarantanny.
 */
public enum HealthStatus {
    HEALTHY,
    CARRIER,
    SICK,
    RECOVERED;
}
