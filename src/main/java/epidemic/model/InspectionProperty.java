package epidemic.model;

import java.awt.Color;

/**
 * Struktura danych reprezentująca pojedynczą statystykę obiektu na mapie.
 * Zawiera parametry niezbędne do dynamicznego wygenerowania odpowiednich
 * komponentów Swing w Inspektorze (np. JLabel lub JProgressBar).
 */
public record InspectionProperty(
        String label,
        String stringValue,
        Integer progressValue,
        Integer progressMax,
        Color highlightColor
) {
    /** Używane do tworzenia standardowych etykiet tekstowych. */
    public static InspectionProperty text(String label, String value) {
        return new InspectionProperty(label, value, null, null, null);
    }

    /** Używane do tworzenia etykiet wyróżnionych odpowiednim kolorem. */
    public static InspectionProperty textColored(String label, String value, Color color) {
        return new InspectionProperty(label, value, null, null, color);
    }

    /** Używane do nakazywania GUI renderowania paska postępu. */
    public static InspectionProperty progressBar(String label, int value, int max, Color color) {
        return new InspectionProperty(label, null, value, max, color);
    }
}