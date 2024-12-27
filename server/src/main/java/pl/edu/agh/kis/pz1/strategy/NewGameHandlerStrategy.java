package pl.edu.agh.kis.pz1.strategy;

import pl.edu.agh.kis.pz1.ClientAttachment;
import pl.edu.agh.kis.pz1.PokerServer;

import java.nio.channels.SocketChannel;

/**
 * Strategia obsługi komendy utworzenia nowej gry w serwerze pokera.
 * Klasa implementuje interfejs `ReadHandlerStrategy` i odpowiada za realizację
 * logiki, gdy klient wysyła komendę 'new', sygnalizując chęć utworzenia nowej gry.
 * @see ReadHandlerStrategy
 */
public class NewGameHandlerStrategy implements ReadHandlerStrategy {

    /**
     * Obsługuje komendę 'new' wysłaną przez klienta, tworząc nową grę.
     * @param message wiadomość wysłaną przez klienta (komenda 'new')
     * @param client socket klienta
     * @param attachment załącznik zawierający dodatkowe informacje o kliencie
     * @param server obiekt serwera, który obsługuje utworzenie nowej gry
     */
    @Override
    public void handle(String message, SocketChannel client, ClientAttachment attachment, PokerServer server) {
        server.createNewGame(client);
    }
}
