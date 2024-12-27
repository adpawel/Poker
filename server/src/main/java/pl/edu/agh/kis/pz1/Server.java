package pl.edu.agh.kis.pz1;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Główna klasa serwera, odpowiedzialna za uruchomienie serwera pokera.
 * Użytkownik jest proszony o podanie liczby graczy, po czym uruchamiany jest serwer pokera,
 * który nasłuchuje na połączenia przychodzące i obsługuje gry.
 */
public class Server {

    /**
     * Główna metoda aplikacji serwera.
     * Inicjalizuje serwer pokera i uruchamia go na podstawie liczby graczy wprowadzonej przez użytkownika.
     *
     * @param args Argumenty wiersza poleceń (nieużywane w tej klasie).
     * @throws IOException Jeśli wystąpi błąd wejścia/wyjścia przy uruchamianiu serwera.
     */
    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port = 1234;
        Map<String, String> arguments = parseArguments(args);

        if (arguments.containsKey("host")) {
            host = arguments.get("host");
        }
        if (arguments.containsKey("port")) {
            try {
                port = Integer.parseInt(arguments.get("port"));
            } catch (NumberFormatException e) {
                System.err.println("Nieprawidłowy numer portu. Używany będzie domyślny port: " + port);
            }
        }
        Scanner sc = new Scanner(System.in);
        System.out.println("Podaj liczbę graczy: ");
        int numberOfPlayers = sc.nextInt();
        PokerServer server = new PokerServer(numberOfPlayers, host, port);
        server.start();
    }

    private static Map<String, String> parseArguments(String[] args) {
        Map<String, String> arguments = new HashMap<>();
        for (String arg : args) {
            if (arg.startsWith("--")) {
                String[] parts = arg.substring(2).split("=", 2);
                if (parts.length == 2) {
                    arguments.put(parts[0], parts[1]);
                }
            }
        }
        return arguments;
    }
}
