# Symulacja Epidemii w Populacji Mieszanej

## Opis Projektu
System symulacyjny napisany w języku Java 21, modelujący dynamikę epidemii w złożonym ekosystemie. Aplikacja analizuje interakcje między ludźmi a różnymi gatunkami zwierząt (nietoperze, psy, szczury), uwzględniając zaawansowane modele behawioralne oraz infrastrukturę medyczną.

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

Projekt udostępnia trzy metody uruchomienia, dostępne w sekcji **Releases** repozytorium:

### 1. Wersja Portable (Zalecana)
Kompletna paczka zawierająca dedykowane środowisko uruchomieniowe. Nie wymaga instalacji Javy w systemie.
*   **Archiwum**: `Symulacja_v1.0.4_Portable.zip`
*   **Uruchomienie**: Wypakuj ZIP i uruchom plik `SymulacjaEpidemii.exe`.
*    Plik `config.properties` znajduje się bezpośrednio w folderze głównym obok aplikacji.

### 2. Instalator EXE
Standardowy instalator Windows, który konfiguruje skróty w systemie.
*   **Plik**: `SymulacjaEpidemii.exe`
*   **Wymagania**: System Windows.
*   Po instalacji pobierz i dodaj plik config.properties obok pliku wykonywalnego w celu możliwości edycji konfiguracji symulacji.

### 3. Plik Wykonywalny JAR
Dla systemów z zainstalowanym środowiskiem Java.
*   **Plik**: `SymulacjaEpidemii.jar`
*   **Wymagania**: Środowisko JRE 21.
*   **Uruchomienie**: `java -jar SymulacjaEpidemii.jar`

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


## Kompilacja i Jakość Kodu
Projekt wykorzystuje Maven do zarządzania zależnościami i cyklem życia.
*   **Budowanie**: `mvn clean package`
*   **Testy**: Kompleksowe testy jednostkowe oparte na JUnit 5 i Mockito.
*   **Pokrycie**: Raporty JaCoCo generowane są automatycznie do katalogu `target/site/jacoco/`.

---
*Projekt wykonany w ramach laboratorium Programowanie Obiektowe na kierunku Informatyka Techniczna na wydziale WIT Politechniki Wrocławskiej*

*Maj 2026*

*Wersja: 1.0.3*