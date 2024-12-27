package pl.edu.agh.kis.pz1.strategy;

import pl.edu.agh.kis.pz1.ClientAttachment;
import pl.edu.agh.kis.pz1.PokerServer;

import java.nio.channels.SocketChannel;

/**
 * Strategia obsługi komendy 'join' w serwerze pokera.
 * Klasa implementuje interfejs `ReadHandlerStrategy` i odpowiada za realizację
 * logiki dołączania klienta do istniejącej gry, kiedy klient wyśle komendę 'join'
 * wraz z ID gry.
 * @see ReadHandlerStrategy
 */
public class JoinHandlerStrategy implements ReadHandlerStrategy {
    /**
     * Obsługuje komendę kiedy klient wyśle ID gry do której chce dołączyć.
     * @param message wiadomość wysłaną przez klienta (zawierająca ID gry)
     * @param client socket klienta
     * @param attachment załącznik zawierający dodatkowe informacje o kliencie
     * @param server obiekt serwera, który obsługuje dołączenie klienta do gry
     */
    @Override
    public void handle(String message, SocketChannel client, ClientAttachment attachment, PokerServer server) {
        server.handleJoinGame(message, client, attachment);
    }
}
