Poker 5-Card Draw

Wstęp
Gra oparta jest na popularnej wersji pokera o nazwie 5-Card Draw, z dodatkowymi elementami dostosowanymi do
implementacji klient-serwer. Gracze mogą łączyć się z serwerem, rozpoczynać gry, wymieniać karty oraz kończyć rozgrywkę.


Podstawowe paramtery gry:
- Liczba graczy: od 2 do n
- Liczba kart: 5
- Liczba żetonów na starcie do dyspozycji dla każdego gracza: 500

Etapy gry:
1. Rozpoczęcie gry
2. Runda zakładów
3. Runda wymiany kart
4. Runda zakładów
5. Ewaluacja i ogłoszenie wyników.

1. Rozpoczęcie gry
    Gracze dołączają do gry i jeśli jest ich odpowidnia ilość (ustawiona na serwerze) to gra się zaczyna.
    Każdy gracz na początku otrzymuje 5 kart rozdanych z talii (standardowa talia 52 kart) oraz swoje ID gracza.

2. Runda zakładów
    Gracze mogą stawiać zakłady, czekać, spasować, podnieść stawkę albo sprawdzić (wyrównać).
    Dostępne komendy:
    {id_gry} {ruch} ({parametry_ruchu})
    gdzie ruch to:
    - bet {ilość_żetonów} : gracz może dokonać zakładu jeśli dysponuje odpowiednią liczbą żetonów i wcześniej nikt nie dokonał zakładu.
    - call : gracz może sprawdzić tzn. wyrównać aktualny zakład jeśli ma jakiś do wyrównania i ma wystarczające środki.
    - check: gracz może czekać jeśli nie było w rundzie jeszcze żadnego zakładu
    - raise {ilość_żetonów} : warunek: ilość_żetonów + aktualny_wkład > aktualny_zakład, tzn suma suma żetonów, które gracz już włożył do pulli
        oraz ilość, którą pisze w komendzie raise musi przwyższać aktualny zakład
    - fold : gracz może spasować i nie gra do końca gry
    Kończy się gdy:
    - wszyscy gracze, któzy nie spasowali wyrównają stawkę lub
    - wszyscy gracze dokonają check lub
    - wszyscy poza jednym graczem spasują

3. Runda wymian
    Gracze mogą wymienić dowolną ilość kart (ilość należy do przedziału [0;5]).
    Dostępne komendy:
    {id_gry} {ruch} ({parametry_ruchu})
    - stand : gracz nie wymienia żadnej karty i przechodzimy do następnego gracza
    - exchange {numery kart} : gracz odrzuca karty o wybranych numerach i dobiera karty z talii. Jeśli karty w talii się
        skończą odrzucone karty są tasowane i stają się nową talią.
    Kończy się, gdy każdy z graczy dostanie szanse wymiany kart.

4. Runda zakładów
    Jeśli po tej rundzie jest więcej niż jeden gracz, który nie spasował dokonywane jest porównywanie zestawów kart zawodników
    na podstawie (od najsłabszego do najlepszego):
    - Wysoka karta: Najwyższa karta w ręce (np. As)
    - Para: Dwie karty o tej samej wartości (np. dwa Króle)
    - Dwie pary: Dwa zestawy par (np. dwie 9-ki i dwie 7-ki)
    - Trójka: Trzy karty o tej samej wartości
    - Strit: Pięć kolejnych kart w różnych kolorach (np. 4, 5, 6, 7, 8)
    - Kolor: Pięć kart w tym samym kolorze (np. wszystkie w pikach)
    - Full: Para i trójka (np. dwie 10-ki i trzy Asy)
    - Kareta: Cztery karty o tej samej wartości
    - Poker: Pięć kolejnych kart w tym samym kolorze
    - Poker Królewski: Strit od 10 do Asa w tym samym kolorze

5. Serwer ogłasza zwycięzcę na podstawie najlepszego układu.

Inne komendy:
    - Przed dołączeniem do gry:
        - new : tworzy nową grę na serwerze
        - join : uruchamia możliwość dołączenie do gry. Następnie należy podać ID gry do której chce się dołączyć.
        - exit: wyjście z gry.
    - set_players {liczba} : ustawia liczbę graczy. Wybrana liczba obowiązuje dla gier utworzonych po użyciu komendy set_players
        tzn. nie zmieni ilości graczy w trwających grach.
    - {id_gry} cards : jeśli trwa tura gracza może on przypomnieć swoje karty tym poleceniem.




Uruchomienie:
Po pobraniu projektu w zip należy:
 1. Przejść do głównego katalogu projektu czyli Poker
 2. Użyć komend: 'java -jar server-1.0-jar-with-dependencies.jar --port={nr_portu}' oraz
    'java -jar client-1.0-jar-with-dependencies.jar --host={adres_hosta} --port={nr_portu}'. Na przykład:
    'java -jar server-1.0-jar-with-dependencies.jar --port=5678',
    'java -jar client-1.0-jar-with-dependencies.jar --host=localhost --port=5678'
 3. Aby uruchomić serwer należy najpierw wpisać liczbę graczy po komunikacie.


Protokół komunikacyjny serwer-klient

- Serwer: wiadomość powitalna i przedstawienie komend
- Klient: 'new'
    Serwer: 'Utworzono nową grę z ID: {ID_gry}. Dołącz do gry wpisując ID.'
    Interakcja tworząca nową grę. Parametr ID_gry jest nadawany na serwerze.
- Klient: 'join'
    Serwer: 'Podaj ID gry, do której chcesz dołączyć:'
    Klient: {ID_gry}
    Serwer: 'Dołączono do gry o ID: 0'
    Proces dołączania do gry. Na serwerze tworzona jest nowa gra i trwa oczekiwanie na pozostałych graczy.
- Serwer: 'Gra się rozpoczyna! Liczba graczy: {ustawiona_liczba_graczy}
           Aby grać użyj następującego formatu komendy: '0 {ruch} {opcjonalnie:parametry ruchu}'
           Twoje id: {ID_gracza}. Twoje karty: <karty rozdane graczowi>'
    Komunikat informacyjny.
- Serwer: 'Twoja tura!'
    Komunikat informujący klienta o tym, że nastąpiła jego tura.
- Klient: '{ID_GRY} {ruch} ({parametry_ruchu})'
    Serwer: odpowiedź dostosowana do akcji
    Przykłady możliwych ruchy i odpowiedzi (odpowiedzi serwera jeśli powtarzają się w wielu akcjach
    są opisane tylko raz - dla akcji występującej najwcześniej):
    1. Klient: {ID_GRY} bet {ilość_żetonów}
        a. Serwer: NOT_BET_ROUND    (komunikat błędu jeśli aktualnie nie trwa runda zakładów)
        b. Serwer: ALREADY_FOLD     (komunikat błędu jeśli gracz już spasował)
        c. Serwer: BET_EXIST        (komunikat błędu jeśli w aktualnej rundzie wystąpił już zakład))
        d. Serwer: NOT_ENOUGH_CHIPS (komunikat błędu jeśli gracz nie posiada wystarczającej liczby żetonów do wykonania ruchu)
        e. Serwer: 'Poprawnie przyjęto zakład' (komunikat o poprawnym wykonaniu akcji)
    2. Klient: {ID_GRY} call
        a. Serwer: NOT_BET_ROUND
        b. Serwer: ALREADY_FOLD
        c. Serwer: NOT_ENOUGH_CHIPS
        d. Serwer: NO_BET_TO_CALL   (komunikat błędu o tym że gracz nie ma zakładu do wyrównania)
        e. Serwer: 'Pomyślnie dokonałeś 'call'' (komunikat o poprawnym wykonaniu akcji)
    3. Klient: {ID_GRY} check
        a. Serwer: NOT_BET_ROUND
        b. Serwer: ALREADY_FOLD
        c. Serwer: EXISTS_BET_TO_CALL      (komunikat błędu występujący gdy gracz nie może czekać, ponieważ ma zakład do wyrównania)
        d. Serwer: 'Pomyślnie wykonano ruch: 'check'' (komunikat o poprawnym wykonaniu akcji)
    4. Klient: {ID_GRY} fold
        a. Serwer: NOT_BET_ROUND
        b. Serwer: ALREADY_FOLD
        c. Serwer: 'Pomyślnie spasowałeś!'  (komunikat o poprawnym wykonaniu akcji)
    5. Klient: {ID_GRY} raise {ilosc_zetonow}
        a. Serwer: NOT_BET_ROUND
        b. Serwer: ALREADY_FOLD
        c. Serwer: NOT_ENOUGH_CHIPS
        d. Serwer: 'Podana liczba żetonów dodana do twojego wkładu( {aktualny_wkład_gracza} ) musi być większa niż obecny zakład ({obecny_zakład})'
           (komunikat o zbyt małym podniesieniu)
        e. Serwer: 'Pomyślnie dokonałeś 'raise'"  (komunikat o poprawnym wykonaniu akcji)
    6. Klient: {ID_GRY} exchange {liczba1, (liczba2), ... (liczba5)}
        a. Serwer: NOT_BET_ROUND
        b. Serwer: WRONG_INDEX      (komunikat o podaniu błędnego indeksu karty do wymiany)
        c. Serwer: 'Pomyślnie dokonałeś wymiany kart!'  (komunikat o poprawnym wykonaniu akcji)
    7. Klient: {ID_GRY} stand
        a. Serwer: NOT_DRAW_ROUND
        b. Serwer: 'Pomyślnie dokonałeś 'stand''  (komunikat o poprawnym wykonaniu akcji)
    8. Klient: {ID_GRY} cards
            a. Serwer: {kolor_karty ranga_karty\n kolor_karty ranga_karty\n kolor_karty ranga_karty\n
                        kolor_karty ranga_karty\n kolor_karty ranga_karty\n}
                        (klient po zapytaniu dostaje odpowiedź w postaci aktualnie posiadanych kart)
    9. Klient: new
        a. Serwer: 'Utworzono nową grę z ID: {ID_GRY}. Dołącz do gry wpisując ID.\n' - odpowiada za informację klienta,
            że gra została poprawnie utworzona i można do niej dołączyć posługując się jej identyfikatorem.
    10. Klient: join
        a. Serwer: 'Podaj ID gry, do której chcesz dołączyć:\n' - po wysłaniu komendy join możemy spodziewać się komunikatu z pytaniem
            o ID_GRY do której chcemy dołączyć. Po czym następuje...
    11. Klient: {ID_GRY}
        a. Serwer: 'Dołączono do gry o ID: {ID_GRY}' - poprawnie dołązczono do gry i oczekujemy na kolejnych graczy lub
            zaczynamy rozgrywkę.
        b. Serwer: 'Gra o podanym ID nie istnieje.' - jeśli podane ID nie odpowiada żadnej grze.
    12. Klient: exit
        a. Serwer:  'Dziękujemy za korzystanie z serwera pokera! Do zobaczenia!\n' - interakcja przy opuszczaniu gry
    13. Klient: set_players {numer}
        a. Serwer: 'Liczba graczy została ustawiona na: {numer}' - poprawne ustawienie nowej liczby graczy, z którą zaczynać
            się będzie każda nowo stworzona (po komendzie ustawienia liczb graczy) gra
        b. Serwer: 'Liczba graczy musi być większa od 1.'   - po podaniu błędnej liczby
        c. Serwer: 'Niepoprawny format liczby. Użyj: set_players <liczba>'  - kiedy format wprowadzonej liczby graczy nie jest liczbowy


Użyte narzędzia:
- Java 17 
- Apache Maven do zarządzania zależnościami i budowaniem
- SonarQube do pokrycia testami i ograniczenia liczby błędów
- Javadoc do generowania dokumentacji.
