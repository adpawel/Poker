package pl.edu.agh.kis.pz1.strategy;

import pl.edu.agh.kis.pz1.ClientAttachment;
import pl.edu.agh.kis.pz1.PokerServer;

import java.nio.channels.SocketChannel;

/**
 * Strategia obsługi żądania dołączenia do gry w serwerze pokera.
 * Klasa implementuje interfejs `ReadHandlerStrategy` i odpowiada za realizację
 * logiki, gdy klient wysyła komendę 'join', sygnalizując chęć dołączenia do gry.
 * @see ReadHandlerStrategy
 */
public class JoinRequestHandlerStrategy implements ReadHandlerStrategy {
    /**
     * Obsługuje komendę 'join' wysłaną przez klienta.
     * @param message wiadomość wysłaną przez klienta (komenda 'join')
     * @param client socket klienta
     * @param attachment załącznik zawierający dodatkowe informacje o kliencie
     * @param server obiekt serwera, który obsługuje żądanie dołączenia do gry
     */
    @Override
    public void handle(String message, SocketChannel client, ClientAttachment attachment, PokerServer server) {
        server.handleJoinRequest(client, attachment);
    }
}

