package pl.edu.agh.kis.pz1.strategy;

import pl.edu.agh.kis.pz1.ClientAttachment;
import pl.edu.agh.kis.pz1.PokerServer;

import java.io.IOException;
import java.nio.channels.SocketChannel;


/**
 * Strategia obsługi komendy 'exit' w serwerze pokera.
 * Klasa implementuje interfejs `ReadHandlerStrategy` i odpowiada za realizację
 * logiki zamknięcia połączenia z serwerem, kiedy klient wyśle komendę 'exit'.
 * @see ReadHandlerStrategy
 */
public class ExitHandlerStrategy implements ReadHandlerStrategy {

    /**
     * Obsługuje komendę 'exit' wysłaną przez klienta.
     * @param message wiadomość wysłaną przez klienta
     * @param client socket klienta
     * @param attachment załącznik zawierający dodatkowe informacje o kliencie
     * @param server obiekt serwera, który wykonuje operacje związane z zakończeniem połączenia
     * @throws IOException jeśli wystąpi błąd podczas komunikacji z klientem
     */
    @Override
    public void handle(String message, SocketChannel client, ClientAttachment attachment, PokerServer server) throws IOException {
        server.handleExit(client);
    }
}
