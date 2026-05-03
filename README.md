# Symulacja Epidemii w Populacji Mieszanej

## Opis Projektu
System symulacyjny napisany w języku Java 21, modelujący dynamikę epidemii w złożonym ekosystemie. Aplikacja analizuje interakcje między ludźmi a różnymi gatunkami zwierząt (nietoperze, psy, szczury), uwzględniając zaawansowane modele behawioralne oraz infrastrukturę medyczną.

### Kluczowe cechy systemu:
* **Zróżnicowane strategie decyzyjne**: Agenci typu Human posiadają osobowości (Rational, Panicked, Vindictive), które determinują ich reakcje na stan epidemii, takie jak zachowywanie dystansu społecznego, noszenie masek lub agresywne poszukiwanie kontaktu po przekroczeniu progu frustracji.
* **Zaawansowany silnik przemieszczania**: Implementacja różnych strategii ruchu (Random Walk, Social Distancing, Malicious Pursuit, Seek Hospital) oparta na wektorach sił i analizie sąsiedztwa.
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

## Uruchomienie aplikacji
Aplikacja jest dystrybuowana w formie pliku wykonywalnego (.exe) dla systemu Windows oraz pliku .jar dla systemów wspierających środowisko JRE 21.

1. Pobierz paczkę z sekcji Releases.
2. Upewnij się, że plik `config.properties` znajduje się w tym samym katalogu co plik wykonywalny.
3. Uruchom `SymulacjaEpidemii.exe`.

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

## Kompilacja i Testy
Aby samodzielnie zbudować projekt i wygenerować raporty pokrycia testami:
```bash
mvn clean package