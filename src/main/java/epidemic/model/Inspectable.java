package epidemic.model;

import java.util.List;

/**
 * Interfejs definiujący kontrakt dla obiektów udostępniających swój stan wewnętrzny
 * na potrzeby warstwy wizualnej (GUI).
 * Umożliwia dynamiczne generowanie paneli telemetrycznych bez ścisłego sprzęgania
 * komponentów widoku z konkretnymi klasami logiki biznesowej.
 */
public interface Inspectable {
    /**
     * Udostępnia nazwę lub tytuł obiektu, który posłuży jako główny nagłówek w inspektorze.
     *
     * @return Zrozumiały dla użytkownika ciąg znaków identyfikujący encję.
     */
    String getObjectName();

    /**
     * Konstruuje zbiór metadanych analitycznych odzwierciedlających bieżący stan obiektu.
     *
     * @return Uporządkowana lista właściwości telemetrycznych gotowych do wyrenderowania w interfejsie.
     */
    List<InspectionProperty> getInspectionProperties();
}