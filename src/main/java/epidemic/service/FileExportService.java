package epidemic.service;

import java.io.File;
import java.io.IOException;

/**
 * Narzędziowy serwis (Utility Service) odpowiedzialny za bezpieczne operacje wejścia/wyjścia (I/O).
 * Posiada mechanizmy weryfikacji uprawnień systemu operacyjnego, gwarantujące
 * ciągłość działania aplikacji niezależnie od katalogu jej instalacji.
 */
public class FileExportService {

    /**
     * Dynamicznie wyznacza bezpieczną ścieżkę do zapisu pliku na urządzeniu końcowym.
     * <p>
     * Przeprowadza tzw. *dry-run* (próbę zapisu) w bieżącym katalogu roboczym (np. w folderze aplikacji).
     * Jeśli system operacyjny (np. Windows w katalogu {@code Program Files}) zablokuje operację,
     * serwis automatycznie stosuje strategię fallback, przekierowując zapis do bezpiecznego
     * folderu domowego użytkownika.
     * </p>
     *
     * @param defaultFileName Sugerowana nazwa pliku wyjściowego ("wyniki.csv").
     * @return Całkowita, absolutna ścieżka systemowa gwarantująca uprawnienia do zapisu.
     */
    public static String getSafeExportPath(String defaultFileName) {
        File localFile = new File(defaultFileName);

        try {
            if (localFile.createNewFile() || localFile.canWrite()) {
                System.out.println("Zapis w katalogu lokalnym powiódł się.");
                return localFile.getAbsolutePath();
            }
        } catch (IOException e) {
            System.out.println("Brak uprawnień do zapisu w katalogu lokalnym.");
        }

        // Fallback
        String userHome = System.getProperty("user.home");
        File fallbackFile = new File(userHome, defaultFileName);
        System.out.println("Używam ścieżki awaryjnej (fallback): " + fallbackFile.getAbsolutePath());

        return fallbackFile.getAbsolutePath();
    }
}