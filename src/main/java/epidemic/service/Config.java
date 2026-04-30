package epidemic.service;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Statyczny punkt dostępu do globalnej konfiguracji symulacji.
 * Odpowiada za załadowanie parametrów z pliku konfiguracyjnego `config.properties`
 * i udostępnianie ich w sposób bezpieczny, z obsługą wartości domyślnych.
 */
public class Config {
    private static final Properties props = new Properties();

    public static void load() {
        String fileName = "config.properties";

        try (InputStream is = Config.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                System.err.println("Nie znaleziono pliku: " + fileName + " w resources!");
                return;
            }
            props.load(is);
            System.out.println("Konfiguracja wczytana pomyślnie.");
        } catch (IOException e) {
            System.err.println("Błąd podczas odczytu pliku konfiguracji.");
            e.printStackTrace();
        }
    }

    public static double getDouble(String key, double defaultValue) {
        String value = props.getProperty(key);
        return (value != null) ? Double.parseDouble(value) : defaultValue;
    }

    public static int getInt(String key, int defaultValue) {
        String value = props.getProperty(key);
        return (value != null) ? Integer.parseInt(value) : defaultValue;
    }

    public static String getString(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
}