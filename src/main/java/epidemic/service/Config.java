package epidemic.service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Statyczny punkt dostępu do globalnej konfiguracji symulacji.
 * Klasa implementuje strategię wieloetapowego ładowania (fallback) w następującej kolejności:
 * <ol>
 *   <li>Próba odczytu pliku zewnętrznego z głównego katalogu roboczego (na podstawie przekazanej nazwy).</li>
 *   <li>Próba odczytu z podkatalogu instalatora ({@code app/[nazwa_pliku]}).</li>
 *   <li>Załadowanie domyślnych ustawień zaszytych w zasobach wewnętrznych aplikacji (classpath).</li>
 * </ol>
 * Takie podejście pozwala na łatwą modyfikację parametrów symulacji po zbudowaniu
 * projektu do postaci wykonywalnej (JAR/EXE), gwarantując jednocześnie stabilność działania.
 */
public class Config {
    private static final Properties props = new Properties();

    /**
     * Inicjalizuje konfigurację poprzez wczytanie danych z plików właściwości.
     * Metoda wykorzystuje interfejs NIO do poprawnej obsługi ścieżek niezależnie od systemu (Windows/Linux)
     * i automatycznie przechodzi do kolejnego poziomu ładowania w przypadku niepowodzenia.
     * Oczekuje plików zapisanych w kodowaniu UTF-8.
     *
     * @param fileName Nazwa pliku konfiguracyjnego do wczytania (np. "config.properties").
     */
    public static void load(String fileName) {
        // Path.of automatycznie dobiera odpowiedni separator (np. \ dla Windows)
        Path rootPath = Path.of(fileName);
        Path appPath = Path.of("app", fileName);

        // Wybór pierwszej istniejącej lokalizacji zewnętrznej
        Path externalPath = Files.exists(rootPath) ? rootPath : (Files.exists(appPath) ? appPath : null);

        if (externalPath != null) {
            try (BufferedReader reader = Files.newBufferedReader(externalPath, StandardCharsets.UTF_8)) {
                props.load(reader);
                System.out.println("Załadowano konfigurację zewnętrzną: " + externalPath.toAbsolutePath());
                return;
            } catch (IOException e) {
                System.err.println("Błąd podczas odczytu pliku: " + e.getMessage());
            }
        }

        // Próba wczytania z zasobów wewnętrznych (classpath)[cite: 1]
        try (InputStream is = Config.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                System.err.println("BŁĄD: Nie znaleziono pliku " + fileName + " w zasobach.");
                return;
            }
            try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                props.load(reader);
                System.out.println("Załadowano konfigurację domyślną z zasobów (UTF-8).");
            }
        } catch (IOException e) {
            System.err.println("Krytyczny błąd odczytu konfiguracji domyślnej.");
            e.printStackTrace();
        }
    }

    /**
     * Pobiera wartość zmiennoprzecinkową dla podanego klucza.
     * @param key Klucz właściwości.
     * @param defaultValue Wartość zwracana w przypadku braku klucza.
     * @return Wartość double z konfiguracji lub wartość domyślna.[cite: 1]
     */
    public static double getDouble(String key, double defaultValue) {
        String value = props.getProperty(key);
        try {
            return (value != null) ? Double.parseDouble(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Pobiera wartość całkowitą dla podanego klucza.
     * <p>
     * Jeśli klucz nie istnieje w konfiguracji lub jego wartość nie może zostać
     * poprawnie rzutowana na liczbę całkowitą, metoda bezpiecznie zwróci wartość domyślną.
     * </p>
     * @param key Klucz właściwości do odszukania w konfiguracji.
     * @param defaultValue Wartość zwracana w przypadku braku klucza lub błędu parsowania.
     * @return Wartość {@code int} z konfiguracji lub wartość domyślna.[cite: 1]
     */
    public static int getInt(String key, int defaultValue) {
        String value = props.getProperty(key);
        try {
            return (value != null) ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Pobiera wartość tekstową dla podanego klucza.
     * @param key Klucz właściwości.
     * @param defaultValue Wartość zwracana w przypadku braku klucza.
     * @return Wartość {@code String} z konfiguracji lub podana wartość domyślna.[cite: 1]
     */
    public static String getString(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
}