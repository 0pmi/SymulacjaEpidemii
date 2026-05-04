package epidemic.model;

import java.awt.Color;

/**
 * Niemutowalna struktura danych (wzorzec Data Transfer Object - DTO) reprezentująca pojedynczą
 * statystykę obiektu na mapie symulacji.
 * Agreguje parametry niezbędne do dynamicznego wygenerowania i ostylowania odpowiednich
 * komponentów biblioteki Swing w Inspektorze ({@code JLabel}, {@code JProgressBar}).
 *
 * @param label Etykieta opisująca statystykę.
 * @param stringValue Wartość tekstowa (używana dla standardowych etykiet).
 * @param progressValue Obecna wartość postępu (używana dla pasków ładowania).
 * @param progressMax Maksymalna dopuszczalna wartość postępu.
 * @param highlightColor Opcjonalny kolor akcentujący dla tekstu lub wypełnienia paska.
 */
public record InspectionProperty(
        String label,
        String stringValue,
        Integer progressValue,
        Integer progressMax,
        Color highlightColor
) {
    /**
     * Statyczna metoda fabrykująca do tworzenia standardowych etykiet tekstowych.
     *
     * @param label Nazwa metryki.
     * @param value Reprezentacja tekstowa odczytu.
     * @return Nowa instancja DTO skonfigurowana jako zwykły tekst.
     */
    public static InspectionProperty text(String label, String value) {
        return new InspectionProperty(label, value, null, null, null);
    }

    /**
     * Statyczna metoda fabrykująca do tworzenia etykiet z wyróżnieniem kolorystycznym.
     *
     * @param label Nazwa metryki.
     * @param value Reprezentacja tekstowa odczytu.
     * @param color Kolor użyty do renderowania tekstu wartości.
     * @return Nowa instancja DTO skonfigurowana jako kolorowy tekst.
     */
    public static InspectionProperty textColored(String label, String value, Color color) {
        return new InspectionProperty(label, value, null, null, color);
    }

    /**
     * Statyczna metoda fabrykująca nakazująca warstwie GUI wygenerowanie paska postępu.
     *
     * @param label Opis umieszczany nad lub na pasku postępu.
     * @param value Bieżący stan wypełnienia.
     * @param max Maksymalna wartość skali.
     * @param color Kolor wypełnienia paska.
     * @return Nowa instancja DTO skonfigurowana jako wskaźnik postępu.
     */
    public static InspectionProperty progressBar(String label, int value, int max, Color color) {
        return new InspectionProperty(label, null, value, max, color);
    }
}