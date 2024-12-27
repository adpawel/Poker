package pl.edu.agh.kis.pz1;

/**
 * Klasa zawierająca stałe używane w grze, takie jak adres serwera, port, liczba żetonów początkowych
 * oraz komunikaty dla graczy w różnych sytuacjach, takich jak brak zakładu, zbyt mała liczba żetonów,
 * czy nieprawidłowy indeks. Stałe te są wykorzystywane w różnych częściach gry, zapewniając spójność i
 * łatwiejsze zarządzanie komunikatami oraz ustawieniami.
 */
public class Constants {
    public static final String SERVER_ADDRESS = "localhost";
    public static final int SERVER_PORT = 1234;
    public static final int NUMBER_OF_CHIPS = 500;

    // komunikaty
    public static final String NOT_BET_ROUND = "Aktualnie nie trwa runda zakładów!";
    public static final String ALREADY_FOLD = "Spasowałeś już w tej rundzie!";
    public static final String BET_EXIST = "W aktualnej rundzie wystąpił już zakład. Użyj 'raise', 'fold' lub 'call'";
    public static final String NOT_ENOUGH_CHIPS = "Nie posiadasz wystarczającej liczby żetonów!";
    public static final String NO_BET_TO_CALL = "Nie masz zakładu do wyrównania";
    public static final String EXISTS_BET_TO_CALL = "W aktualnej rundzie masz zakład do wyrównania. Użyj 'raise', 'fold' lub 'call'";
    public static final String WRONG_INDEX = "Błędny indeks!";
    public static final String NOT_DRAW_ROUND = "Aktualnie nie trwa runda wymian!";
    public static final String SET_PLAYERS = "set_players";

    private Constants() {
        throw new UnsupportedOperationException("Nie można stworzyć instancji tej klasy!");
    }
}
