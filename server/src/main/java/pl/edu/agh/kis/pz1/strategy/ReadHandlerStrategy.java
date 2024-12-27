package pl.edu.agh.kis.pz1.strategy;

import pl.edu.agh.kis.pz1.ClientAttachment;
import pl.edu.agh.kis.pz1.PokerServer;

import java.io.IOException;
import java.nio.channels.SocketChannel;

/**
 * Interfejs reprezentujący strategię obsługi wiadomości od klienta w serwerze pokerowym wykorzystujący wzorzec Strategy.
 * Metoda `handle` w każdej implementacji przyjmuje wiadomość, klienta, załącznik klienta
 * (zawierający dodatkowe dane, takie jak bufor) oraz obiekt serwera, co pozwala na
 * wykonywanie odpowiednich operacji w odpowiedzi na komendę klienta.
 */
public interface ReadHandlerStrategy {
    void handle(String message, SocketChannel client, ClientAttachment attachment, PokerServer server) throws IOException;
}

