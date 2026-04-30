package epidemic.model;

/**
 * Interfejs oznaczający obiekty, które potrafią zwrócić czytelne dla człowieka
 * podsumowanie swojego stanu. Wykorzystywany głównie przez system GUI (Inspektor Obiektów)
 * przy kliknięciu w dany element na mapie.
 */
public interface Inspectable {

    /**
     * @return Sformatowany, wielolinijkowy ciąg znaków zawierający statystyki i stan obiektu.
     */
    String getDetailedInfo();
}