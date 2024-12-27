package pl.edu.agh.kis.pz1.strategy;

import pl.edu.agh.kis.pz1.ClientAttachment;
import pl.edu.agh.kis.pz1.PokerServer;

import java.nio.channels.SocketChannel;


/**
 * Strategia obsługi komendy ustawienia liczby graczy w serwerze pokera.
 * Klasa implementuje interfejs `ReadHandlerStrategy` i odpowiada za realizację
 * logiki, gdy klient wysyła komendę 'set_players', która służy do ustawienia liczby graczy w grze.

 * @see ReadHandlerStrategy
 */
public class SetPlayersHandlerStrategy implements ReadHandlerStrategy {
    /**
     * Obsługuje komendę 'set_players' wysłaną przez klienta, ustawiając liczbę graczy w grze.
     * @param message wiadomość wysłaną przez klienta (komenda 'set_players')
     * @param client socket klienta
     * @param attachment załącznik zawierający dodatkowe informacje o kliencie
     * @param server obiekt serwera, który obsługuje ustawienie liczby graczy
     */
    @Override
    public void handle(String message, SocketChannel client, ClientAttachment attachment, PokerServer server){
        server.handleSetPlayersCommand(message, client);
    }
}
