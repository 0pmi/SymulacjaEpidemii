package epidemic.statistics;

/**
 * Niemutowalna struktura danych (wzorzec Data Transfer Object - DTO) reprezentująca
 * telemetryczną migawkę stanu symulacji dla konkretnej epoki czasowej.
 * Zapewnia bezpieczne (wątkowo i architektonicznie) przekazywanie zagregowanych danych
 * pomiędzy silnikiem symulacji a modułami analitycznymi i statystycznymi.
 *
 * @param epochNumber Numer zarchiwizowanej epoki (kroku czasowego) symulacji.
 * @param healthyCount Liczba agentów w pełni zdrowych (potencjalnie podatnych na infekcję).
 * @param sickCount Liczba aktywnych i pełnoobjawowych nosicieli patogenu.
 * @param recoveredCount Liczba agentów, którzy pomyślnie zwalczyli infekcję.
 * @param naturalDeadCount Skumulowana liczba zgonów wynikających z uwarunkowań biologicznych (np. wiek).
 * @param virusDeadCount Skumulowana liczba ofiar śmiertelnych wywołanych powikłaniami infekcji.
 * @param totalPopulation Aktualna liczba całkowita żywych osobników na mapie.
 */
public record EpochData(
        int epochNumber,
        int healthyCount,
        int sickCount,
        int recoveredCount,
        int naturalDeadCount,
        int virusDeadCount,
        int totalPopulation
){}