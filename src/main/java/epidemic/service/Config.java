package epidemic.service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Statyczny punkt dostępu do globalnej konfiguracji symulacji.
 * Klasa implementuje strategię dwuetapowego ładowania:
 * 1. Próba odczytu zewnętrznego pliku 'config.properties' z katalogu roboczego.
 * 2. W przypadku braku pliku zewnętrznego, ładowanie domyślnych zasobów z wnętrza pakietu (classpath).
 */
public class Config {
    private static final Properties props = new Properties();
    private static final String FILE_NAME = "config.properties";

    /**
     * Inicjalizuje konfigurację poprzez wczytanie danych z plików właściwości.
     * Priorytet ma plik zewnętrzny, co pozwala na modyfikację parametrów symulacji
     * po zbudowaniu projektu do postaci wykonywalnej (JAR/EXE).
     */
    public static void load() {
        File externalFile = new File(FILE_NAME);

        // 1. Próba ładowania z pliku zewnętrznego (UTF-8)
        if (externalFile.exists()) {
            try (InputStream is = new FileInputStream(externalFile);
                 InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                props.load(reader);
                System.out.println("Załadowano konfigurację zewnętrzną (UTF-8): " + externalFile.getAbsolutePath());
                return;
            } catch (IOException e) {
                System.err.println("Błąd podczas odczytu zewnętrznego pliku (UTF-8): " + e.getMessage());
            }
        }

        // 2. Próba ładowania z zasobów wewnętrznych (UTF-8)
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
     * @param key Klucz właściwości.
     * @param defaultValue Wartość zwracana w przypadku braku klucza.
     * @return Wartość int z konfiguracji lub wartość domyślna.
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
     * @return Wartość String z konfiguracji lub wartość domyślna.
     */
    public static String getString(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
}