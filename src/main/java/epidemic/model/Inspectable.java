package epidemic.model;

import java.util.List;

/**
 * Interfejs oznaczający obiekty, z których GUI potrafi wyciągnąć
 * dynamiczną listę cech i ich stanu w czasie rzeczywistym.
 */
public interface Inspectable {
    /**
     * Zwraca główny nagłówek/tytuł inspekcji.
     */
    String getObjectName();

    /**
     * Zwraca listę właściwości, które Panel Inspektora ma wygenerować.
     */
    List<InspectionProperty> getInspectionProperties();
}