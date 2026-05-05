# Symulacja Epidemii w Populacji Mieszanej

## Opis Projektu
System symulacyjny napisany w języku Java 21, modelujący dynamikę epidemii w złożonym ekosystemie. Aplikacja analizuje interakcje między ludźmi a różnymi gatunkami zwierząt (nietoperze, psy, szczury), uwzględniając zaawansowane modele behawioralne oraz infrastrukturę medyczną.

## Legenda i Interfejs Symulacji

Wizualizacja symulacji odbywa się w czasie rzeczywistym. Poniżej znajduje się zestawienie elementów widocznych na mapie oraz opis interfejsu graficznego:

### Agenci (Populacja)
Każda kropka poruszająca się po planszy reprezentuje pojedynczego agenta. Jego wygląd niesie kluczowe informacje o gatunku, stanie zdrowia oraz aktualnym zachowaniu:

* **Rozmiar**:
  * **Duże kropki**: Reprezentują ludzi.
  * **Małe kropki**: Reprezentują zwierzęta (różne gatunki w ekosystemie).
* **Kolor (Stan zdrowia)**:
  * 🟢 **Zielony**: Agent zdrowy.
  * 🔴 **Czerwony**: Agent chory (zakażony i objawowy).
  * 🟠 **Pomarańczowy**: Nosiciel bezobjawowy.
  * 🔵 **Niebieski**: Ozdrowieniec (posiada nabytą odporność po przejściu choroby).
* **Zachowanie (Obwódka)**:
  * **Gruba czarna obwódka**: Człowiek korzystający aktualnie ze strategii `MalliciousPursuitStrategy`. Oznacza to, że jego próg frustracji został przekroczony i celowo poszukuje on zdrowych agentów w swoim zasięgu, aby ich zarazić.

### Infrastruktura i Środowisko
*  **Niebieskie kwadraty (Szpitale)**: Obiekty infrastruktury medycznej. Szpitale można kliknąć, aby sprawdzić ich aktualne obłożenie.
*  **Czerwone pola (Miazma)**: Zakaźna chmura pozostawiana przez chorych agentów na trasie ich przemarszu. Pola te zanikają z upływem czasu, co symbolizuje rozpraszanie się wirusa i spadek ryzyka zakażenia z powietrza.

### Interfejs Użytkownika
* **Inspektor Obiektu (prawy panel)**: Interaktywny panel wyświetlający szczegółowe parametry wybranego elementu na mapie (np. wiek, podatność na zakażenie, aktualna strategia decyzyjna/ruchu agenta lub statystyki szpitala). Aktualnie analizowany obiekt jest wyróżniony na mapie cienkim czarnym okręgiem.
* **Statystyki Populacji (górny pasek)**: Podgląd na żywo rozkładu sił w populacji – liczby osób zdrowych, chorych, ozdrowieńców oraz całkowitej wielkości populacji.
* **Panel Sterowania (dolny pasek)**:
  * Przyciski kontrolne (**Start**, **Następny Krok**, **Zakończ Symulację**) do zarządzania cyklem życia programu.
  * **Suwak opóźnienia (ms)**: Pozwala na płynną regulację prędkości upływu kolejnych epok symulacji.

## Analiza Wyników (Dane i Wykresy)

Po zakończeniu symulacji system automatycznie agreguje zebrane dane statystyczne. Na ich podstawie generowane jest podsumowanie wizualne w osobnych oknach (z wykorzystaniem biblioteki XChart), a same wyniki są eksportowane na dysk:

* **Surowe dane (`wyniki_symulacji.csv`)**: Plik w formacie CSV zawierający szczegółowe odczyty statystyk z każdej epoki symulacji.
* **Dynamika stanów zdrowia populacji (`populacja_wykres.pdf`)**: Wykres liniowy prezentujący zmiany w liczebności poszczególnych grup na przestrzeni czasu. Obrazuje krzywe dla osób zdrowych, chorych oraz ozdrowieńców, co pozwala na wizualną analizę fal infekcji i tempa nabywania odporności stadnej.
* **Skumulowana liczba zgonów (`zgony_wykres.pdf`)**: Wykres przedstawiający narastającą sumę ofiar od początku trwania symulacji, z podziałem na zgony spowodowane bezpośrednio wirusem oraz zgony z przyczyn naturalnych.
*  Domyślnie wszystkie pliki (`.csv` oraz `.pdf`) są eksportowane do głównego katalogu, z którego uruchomiono aplikację. Jeśli program napotka brak uprawnień do zapisu w bieżącym katalogu (np. gdy aplikacja znajduje się w folderze systemowym), pliki zostaną automatycznie wygenerowane w bezpiecznej ścieżce – w folderze domowym aktualnego użytkownika.



### Kluczowe cechy systemu:
* **Zróżnicowane strategie decyzyjne**: Agenci typu Human posiadają osobowości (Rational, Panicked, Vindictive), które determinują ich reakcje na stan epidemii, takie jak zachowywanie dystansu społecznego, noszenie masek lub agresywne poszukiwanie kontaktu po przekroczeniu progu frustracji.
* **Zaawansowany silnik przemieszczania**: Implementacja różnych strategii ruchu (Random Walk, Social Distancing, Malicious Pursuit, Seek Hospital).
* **Optymalizacja przestrzenna**: Wykorzystanie klasy SpatialManager do wydajnego zarządzania zapytaniami o obiekty w zadanym promieniu, co pozwala na płynną symulację dużych populacji.
* **Model epidemiologiczny**: Uwzględnienie transmisji bezpośredniej oraz powietrznej (InfectionField), nosicielstwa bezobjawowego oraz wpływu szczepień i odporności wrodzonej na prawdopodobieństwo infekcji.

## Architektura i Technologie
* **Środowisko**: Java 21 (Temurin).
* **System budowania**: Maven.
* **Biblioteki**:
    * XChart - generowanie wykresów statystycznych w czasie rzeczywistym.
    * JUnit 5 & Mockito - testy jednostkowe i integracyjne.
    * JaCoCo - analiza pokrycia kodu testami.
* **GUI**: Swing.

## Formy Dystrybucji i Uruchomienie
> **⚠️ Uwaga dotycząca lokalizacji plików:**
> Zdecydowanie zaleca się rozpakowywanie i uruchamianie aplikacji w katalogach, do których zalogowany użytkownik ma pełne uprawnienia (np. `Pulpit`, `Dokumenty` czy dedykowany folder na innej partycji).
>
> Unikaj folderów systemowych typu `C:\Program Files` (folderów read-only). Umieszczenie tam symulacji sprawi, że:
> * Do edycji pliku konfiguracyjnego `config.properties` będą wymagane uprawnienia administratora.
> * Aplikacja nie będzie mogła zapisać plików wynikowych obok pliku wykonywalnego i zapisze je do Twojego folderu domowego.

Projekt udostępnia trzy metody uruchomienia, dostępne w sekcji **Releases** repozytorium:

### 1. Wersja Portable (Zalecana)
Kompletna paczka zawierająca dedykowane środowisko uruchomieniowe. Nie wymaga instalacji Javy w systemie.
*   **Archiwum**: `SymulacjaEpidemii_v1.0.10_Portable.zip`
*   **Uruchomienie**: Wypakuj ZIP i uruchom plik `SymulacjaEpidemii.exe`.
*    Plik `config.properties` znajduje się bezpośrednio w folderze głównym obok aplikacji.

### 2. Instalator EXE
Standardowy instalator Windows, który konfiguruje skróty w systemie.
*   **Plik**: `SymulacjaEpidemii_v1.0.10_Setup.exe`
*   **Wymagania**: System Windows.
*   Po instalacji pobierz i dodaj plik config.properties obok pliku wykonywalnego w celu możliwości edycji konfiguracji symulacji.

### 3. Plik Wykonywalny JAR
Dla systemów z zainstalowanym środowiskiem Java.
*   **Plik**: `SymulacjaEpidemii_v1.0.10.jar`
*   **Wymagania**: Środowisko JRE 21.
*   **Uruchomienie**: `java -jar SymulacjaEpidemii_v1.0.10.jar`

## Konfiguracja (config.properties)
Większość parametrów symulacji można modyfikować bez rekompilacji kodu. Kluczowe sekcje pliku konfiguracyjnego obejmują:

| Parametr | Opis                                                                                   |
| :--- |:---------------------------------------------------------------------------------------|
| `world.width` / `height` | Wymiary obszaru symulacji.                                                             |
| `pop.humans` / `pop.bats`... | Początkowa liczebność poszczególnych gatunków.                                         |
| `virus.defaultProb` | Bazowa szansa na zarażenie przy kontakcie.                                             |
| `infection.carrierProbability` | Prawdopodobieństwo wystąpienia bezobjawowego nosicielstwa.                             |
| `rational.infectionThreshold` | Próg procentowy zakażeń w populacji, po którym agenci racjonalni zmieniają zachowanie. |
| `hospital.capacity` | Liczba miejsc w szpitalach.                                                            |
| `reproduction.chance` | Prawdopodobieństwo pojawienia się nowego agenta w wyniku kontaktu.                     |


## Kompilacja
Projekt wykorzystuje Maven do zarządzania zależnościami i cyklem życia.
*   **Budowanie**: `mvn clean package`


## Testy i Jakość Kodu
Projekt charakteryzuje się bardzo wysokim stopniem pokrycia testami jednostkowymi, co gwarantuje stabilność silnika symulacji. Do analizy wykorzystano narzędzie **JaCoCo**.

### Statystyki Pokrycia (Core Logic)
Łączne pokrycie instrukcji dla kluczowej logiki biznesowej wynosi **90%**, natomiast pokrycie rozgałęzień (branches) to **79%**.

| Pakiet | Pokrycie Instrukcji | Kluczowe aspekty                                                                |
| :--- | :--- |:--------------------------------------------------------------------------------|
| `epidemic.strategies.decision` | **100%** | Pełna weryfikacja logiki podejmowania decyzji (Rational, Panicked, Vindictive). |
| `epidemic.strategies.mortality` | **100%** | Modele śmiertelności naturalnej i chorobowej.                                   |
| `epidemic.managers` | **98%** | Zarządzanie infekcjami, leczeniem oraz procesami reprodukcji.                   |
| `epidemic.engine` | **95%** | Główny silnik sterujący epokami symulacji.                                      |
| `epidemic.statistics` | **95%** | Mechanizmy zbierania i eksportu danych epidemiologicznych                       |
| `epidemic.strategies.movement` | **93%** | Algorytmy przemieszczania się agentów w przestrzeni.                           |

### Metodologia testowania
* **Izolacja logiki**: Pakiety odpowiedzialne za interfejs graficzny (`epidemic.gui`) oraz wizualizację wykresów (`epidemic.charts`) zostały świadomie wyłączone z raportowania pokrycia. Pozwoliło to na skupienie się na rygorystycznym przetestowaniu algorytmów decyzyjnych i epidemiologicznych.
* **Testy stanowe i behawioralne**: Wykorzystanie biblioteki **Mockito** pozwoliło na symulowanie złożonych interakcji między agentami a środowiskiem.

Aby wygenerować aktualny raport, należy uruchomić:
```bash
mvn test
```
Pełny raport dostępny jest w: `target/site/jacoco/index.html`

## Dokumentacja Techniczna
Projekt posiada pełną dokumentacja Javadoc dostępną w dwóch formach:
*   **Online**: [Zobacz dokumentację techniczną projektu](https://0pmi.github.io/SymulacjaEpidemii/)
*   **Lokalna**: Możliwa do wygenerowania komendą `mvn javadoc:javadoc` (pliki HTML pojawią się w `target/site/apidocs/`).

---
*Projekt wykonany w ramach laboratorium Programowanie Obiektowe na kierunku Informatyka Techniczna na wydziale WIT Politechniki Wrocławskiej*

*Maj 2026*

*Wersja: 1.0.10*