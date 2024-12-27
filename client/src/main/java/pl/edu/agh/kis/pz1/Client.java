package pl.edu.agh.kis.pz1;

import java.util.HashMap;
import java.util.Map;

import static pl.edu.agh.kis.pz1.Constants.SERVER_PORT;
import static pl.edu.agh.kis.pz1.Constants.SERVER_ADDRESS;

/**
 * Klasa główna dla klienta pokera.
 * Uruchamia instancję klasy `PokerClient` i rozpoczyna jej działanie.
 * Klient łączy się z serwerem, odbiera wiadomości i umożliwia użytkownikowi interakcję
 * z serwerem pokera poprzez konsolę.
 */
public class Client {
    /**
     * Punkt wejścia do aplikacji klienta pokera.
     * Inicjuje działanie klienta, uruchamiając metodę `start` klasy `PokerClient`.
     *
     * @param args argumenty wiersza poleceń (nieużywane).
     */
    public static void main(String[] args) {
        Map<String, String> arguments = parseArguments(args);

        String address = "localhost";
        int port = 1234;
        if (arguments.containsKey("host")) {
            address = arguments.get("host");
        }
        if (arguments.containsKey("port")) {
            try {
                port = Integer.parseInt(arguments.get("port"));
            } catch (NumberFormatException e) {
                System.err.println("Nieprawidłowy numer portu. Używany będzie domyślny port: " + SERVER_PORT);
            }
        }

        PokerClient client = new PokerClient();
        client.start(address, port);
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
