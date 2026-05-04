package epidemic.service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Statyczny punkt dostępu do globalnej konfiguracji symulacji.
 * Klasa implementuje strategię wieloetapowego ładowania (fallback) w następującej kolejności:
 * <ol>
 *   <li>Próba odczytu pliku zewnętrznego z głównego katalogu roboczego (obok pliku wykonywalnego).</li>
 *   <li>Próba odczytu z podkatalogu instalatora ({@code app/config.properties}).</li>
 *   <li>Załadowanie domyślnych ustawień zaszytych w zasobach wewnętrznych aplikacji (classpath).</li>
 * </ol>
 * Takie podejście pozwala na łatwą modyfikację parametrów symulacji po zbudowaniu
 * projektu do postaci wykonywalnej (JAR/EXE), gwarantując jednocześnie stabilność działania.
 */
public class Config {
    private static final Properties props = new Properties();
    private static final String FILE_NAME = "config.properties";


    /**
     * Inicjalizuje konfigurację poprzez wczytanie danych z plików właściwości.
     * Metoda bezpiecznie tłumi błędy odczytu (I/O) i automatycznie przechodzi
     * do kolejnego poziomu ładowania w przypadku niepowodzenia.
     * Oczekuje plików zapisanych w kodowaniu UTF-8.
     */
    public static void load() {
        File rootFile = new File(FILE_NAME);
        File appFolderFile = new File("app/" + FILE_NAME);
        File externalFile = rootFile.exists() ? rootFile : (appFolderFile.exists() ? appFolderFile : null);

        if (externalFile != null) {
            try (InputStream is = new FileInputStream(externalFile);
                 InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                props.load(reader);
                System.out.println("Załadowano konfigurację zewnętrzną: " + externalFile.getAbsolutePath());
                return;
            } catch (IOException e) {
                System.err.println("Błąd podczas odczytu: " + e.getMessage());
            }
        }

        try (InputStream is = Config.class.getClassLoader().getResourceAsStream(FILE_NAME)) {
            if (is == null) {
                System.err.println("BŁĄD: Nie znaleziono pliku " + FILE_NAME + " w zasobach.");
                return;
            }
            try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                props.load(reader);
                System.out.println("Załadowano konfigurację domyślną z zasobów (UTF-8).");
            }
        } catch (IOException e) {
            System.err.println("Krytyczny błąd odczytu konfiguracji.");
            e.printStackTrace();
        }
    }

    /**
     * Pobiera wartość zmiennoprzecinkową dla podanego klucza.
     * @param key Klucz właściwości.
     * @param defaultValue Wartość zwracana w przypadku braku klucza.
     * @return Wartość double z konfiguracji lub wartość domyślna.
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
     * poprawnie rzutowana na liczbę całkowitą (np. zawiera litery), metoda
     * bezpiecznie zwróci wartość domyślną.
     * </p>
     *
     * @param key Klucz właściwości do odszukania w konfiguracji.
     * @param defaultValue Wartość zwracana w przypadku braku klucza lub błędu parsowania.
     * @return Wartość {@code int} z konfiguracji lub wartość domyślna.
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
     * @return Wartość {@code String} z konfiguracji lub podana wartość domyślna.
     */
    public static String getString(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
}