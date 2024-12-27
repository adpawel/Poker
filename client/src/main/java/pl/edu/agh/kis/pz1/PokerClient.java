package pl.edu.agh.kis.pz1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * Klasa reprezentująca klienta pokera, który łączy się z serwerem i umożliwia interakcję
 * z użytkownikiem poprzez konsolę.
 * Klient może wysyłać dane do serwera i odbierać odpowiedzi, a także zakończyć połączenie
 * po wpisaniu komendy "exit".
 */
public class PokerClient {

    /**
     * Rozpoczyna działanie klienta. Tworzy połączenie z serwerem, uruchamia wątek do
     * odbierania wiadomości z serwera i umożliwia użytkownikowi wprowadzanie komend.
     * Klient pozostaje aktywny dopóki użytkownik nie zdecyduje się zakończyć pracy
     * za pomocą komendy "exit".
     */
    public void start(String address, int port){
        // otwieramy kanał klienta
        try(SocketChannel client = SocketChannel.open(new InetSocketAddress(address, port))) {
            client.configureBlocking(false);
            // tworzymy nowy wątek do niezależnego odczytu
            Thread readerThread = new Thread(() -> {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                try {
                    while (true) {
                        int bytesRead = client.read(buffer);
                        if (bytesRead > 0) {
                            buffer.flip();
                            String message = new String(buffer.array(), 0, buffer.limit());
                            System.out.println(message.trim());  //.trim()
                            buffer.clear();
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Rozłączono od serwera.");
                }
            });

            readerThread.start();

            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();

                if ("exit".equalsIgnoreCase(input.trim())) {  // Warunek zakończenia
                    System.out.println("Wychodzenie...");
                    break;
                }

                client.write(ByteBuffer.wrap(input.getBytes()));
            }

            readerThread.interrupt();  // Po exit przerywamy wątek
            System.out.println("Klient zamknięty");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}