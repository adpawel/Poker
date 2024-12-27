package pl.edu.agh.kis.pz1;


import lombok.Data;
import pl.edu.agh.kis.pz1.strategy.*;
import pl.edu.agh.kis.pz1.player_utils.Player;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static pl.edu.agh.kis.pz1.Constants.*;
import static pl.edu.agh.kis.pz1.Game.*;

/**
 * Klasa **PokerServer** jest serwerem pokera, który obsługuje komunikację z klientami za pomocą protokołu NIO.
 * Umożliwia tworzenie nowych gier, dołączanie do istniejących, obsługę poleceń związanych z grą, oraz zarządzanie połączeniami
 * i komunikacją z graczami. Używa selektora do obsługi wielu połączeń i asynchronicznego przetwarzania żądań.
 */
@Data
public class PokerServer {
    private Selector selector;
    private int numberOfPlayers;
    private int gameIdCounter = 0;
    Map<Integer, Game> activeGames = new ConcurrentHashMap<>();
    private ExecutorService gameExecutor = Executors.newCachedThreadPool();
    private boolean running = true;
    Map<String, ReadHandlerStrategy> strategyMap = new HashMap<>();
    private int port;
    private String address;

    /**
     * Konstruktor serwera pokera, który inicjalizuje liczbę graczy i selektor.
     *
     * @param nOfPlayers Liczba graczy w grze.
     * @throws IOException Jeśli wystąpi błąd przy otwieraniu selektora.
     */
    public PokerServer(int nOfPlayers, String address, int port) throws IOException {
        numberOfPlayers = nOfPlayers;
        selector = Selector.open();
        this.port = port;
        this.address = address;
    }

    /**
     * Uruchamia serwer i nasłuchuje na połączenia przychodzące.
     * Obsługuje różne operacje związane z połączeniami, jak akceptowanie nowych połączeń, odczyt danych,
     * oraz zapis komunikatów do klientów.
     */
    public void start(){
        // otwieramy kanał serwera
        try(ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()){
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.bind(new InetSocketAddress(port));
            // rejestrujemy selektor w serwerze
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("PokerServer started on port" + port);
            while (running) {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();

                // sprawdzamy jakiego typu jest klucz i wykonujemy: akceptację, odczyt lub zapis do klientów
                for (Iterator<SelectionKey> it = keys.iterator(); it.hasNext();) {
                    SelectionKey key = it.next();
                    it.remove();

                    if (key.isAcceptable()) {
                        handleAccept(key);
                    } else if (key.isReadable()) {
                        handleRead(key);
                    } else if (key.isWritable()) {
                        handleWrite(key);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Obsługuje nowe połączenie od klienta, rejestruje je w selektorze oraz wysyła powitalną wiadomość.
     *
     * @param key Klucz selektora do przetwarzania połączenia.
     * @throws IOException W przypadku błędów przy akceptowaniu połączenia.
     */
    void handleAccept(SelectionKey key) throws IOException {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel client = serverSocketChannel.accept();

            if (client != null) {
                // kanał klienta nieblokujący
                client.configureBlocking(false);
                // tworzymy bufor dla kanału klienta
                ClientAttachment attachment = new ClientAttachment(ByteBuffer.allocate(256));
                // rejestrujemy selektor oraz bufor
                client.register(selector, SelectionKey.OP_READ, attachment);
                System.out.println("Nowe połączenie: " + client.getRemoteAddress());
                // tworzymy gracza i zapisujemy go w mapie graczy
                Player player = new Player(client);
                playersMap.put(client, player);
                // utworzenie nowego wpisu w w mapie pending data aby komunikaty wysyłane do klienta mogły być w kolejce
                pendingData.put(client, new LinkedList<>());
                sendMessage(client, "Witaj na serwerze pokera! Dostępne opcje: \n1. 'new' - Nowa gra \n2. 'join' - Dołącz do istniejącej gry \n3. 'exit' - Wyjście\n");
            }
    }

    /**
     * Obsługuje odczyt danych od klienta, analizując różne polecenia, takie jak 'new', 'join', 'exit'.
     *
     * @param key Klucz selektora do przetwarzania danych z klienta.
     * @throws IOException W przypadku błędów przy odczycie danych.
     */
    void handleRead(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ClientAttachment attachment = (ClientAttachment) key.attachment();
        ByteBuffer buffer = attachment.getBuffer();

        // odczyt danych z bufora
        int bytesRead = client.read(buffer);
        if (bytesRead == -1) {
            System.out.println("Klient rozłączył się: " + client.getRemoteAddress());
            client.close();
            return;
        }

        buffer.flip();
        String message = new String(buffer.array(), 0, buffer.limit()).trim();
        buffer.clear();
        System.out.println("Otrzymano od klienta: " + message);

        // stan pomaga w rozpoznaniu czy jesteśmy w instrukcji join czy poza
        String state = attachment.getState();
        createStrategy();
        if ("join".equals(state)) { // kiedy jesteśmy już w trakcie instrukcji join
            handleJoinGame(message, client, attachment);
        } else if (message.startsWith(SET_PLAYERS)) {
            strategyMap.get(SET_PLAYERS).handle(message, client, attachment, this);
        } else if (strategyMap.containsKey(message.toLowerCase())) {
            strategyMap.get(message.toLowerCase()).handle(message, client, attachment, this);
        } else {
            handleGameCommand(message, client);
        }

    }

    /**
     * Obsługuje dołączanie do istniejącej gry, wykonując odpowiednie operacje na podstawie ID gry.
     *
     * @param message Wiadomość z ID gry, do której gracz chce dołączyć.
     * @param client Klient, który chce dołączyć do gry.
     * @param attachment Załącznik klienta, zawierający stan gry.
     */
    public void handleJoinGame(String message, SocketChannel client, ClientAttachment attachment) {
        try {
            int gameId = Integer.parseInt(message);
            joinExistingGame(client, gameId);
        } catch (NumberFormatException e) {
            sendMessage(client, "Nieprawidłowy ID gry. Spróbuj ponownie.\n");
        }
        attachment.setState(null); // Resetowanie stanu żeby można było znowu utworzyć nową grę lub dołączyć do innej
    }

    /**
     * Obsługuje żądanie dołączenia do istniejącej gry.
     * Po otrzymaniu komendy 'join' od klienta, serwer wysyła klientowi wiadomość
     * z prośbą o podanie ID gry, do której chce dołączyć.
     * Zmienia również stan klienta na "join", aby wskazać, że klient oczekuje na podanie ID gry.
     *
     * @param client Kanał komunikacyjny klienta, który wysłał żądanie dołączenia do gry.
     * @param attachment Załącznik klienta, przechowujący jego stan (tutaj zmienia stan na "join").
     */
    public void handleJoinRequest(SocketChannel client, ClientAttachment attachment) {
        // po otrzymaniu 'join' powiadamiamy, że komenda poprawna i pytamy do jakiej gry dołączyć
        sendMessage(client, "Podaj ID gry, do której chcesz dołączyć:\n");
        attachment.setState("join");
    }

    /**
     * Obsługuje żądanie wyjścia klienta z serwera.
     * Usuwa klienta z mapy graczy, wysyła mu wiadomość pożegnalną i zamyka połączenie.
     *
     * @param client Kanał komunikacyjny klienta, który chce opuścić serwer.
     * @throws IOException W przypadku problemów z zamknięciem połączenia klienta.
     */
    public void handleExit(SocketChannel client) throws IOException {
        playersMap.remove(client);
        sendMessage(client, "Dziękujemy za korzystanie z serwera pokera! Do zobaczenia!\n");
        client.close();
    }

    /**
     * Obsługuje wykonanie komend związanych z grą, takich jak zakłady, podbicia, itp.
     *
     * @param message Wiadomość zawierająca komendę do wykonania.
     * @param client Klient, który wysłał komendę.
     */
    void handleGameCommand(String message, SocketChannel client) {
        List<String> parts = Arrays.asList(message.split(" "));
        // obsługa sytuacji gdy komunikat nie pasuje do wzorca poprawnej komendy
        if (parts.size() < 2) {
            sendMessage(client, "Niepoprawna wiadomość. Spróbuj ponownie.");
            return;
        }
        try {
            int gameId = Integer.parseInt(parts.get(0));
            Game chosenGame = activeGames.get(gameId);
            if (chosenGame == null) {
                sendMessage(client, "Gra o podanym ID nie istnieje. Spróbuj ponownie.");
            } else if(!chosenGame.isGameRunning()){
                sendMessage(client, "Gra o podanym ID została zakończona. Spróbuj ponownie.");
            } else if(chosenGame.getGameId() != playersMap.get(client).getGame().getGameId()){
                sendMessage(client, "Podałeś ID nie swojej gry!");
            }
            else {
                chosenGame.executeCommand(client, parts);
            }
        } catch (NumberFormatException e) {
            // jeśli użytkownik zamiast liczby oznaczającej id_gry wpisał coś innego
            sendMessage(client, "Niepoprawny format. Spróbuj ponownie.");
        }
    }


    /**
     * Obsługuje komendę 'set_players', która pozwala na zmianę liczby graczy w grze.
     * Metoda analizuje wiadomość od klienta, sprawdza jej poprawność i ustawia nową liczbę graczy,
     * jeżeli komenda jest poprawna, w przeciwnym razie informuje klienta o błędzie.
     *
     * @param message Wiadomość zawierająca komendę i liczbę graczy do ustawienia.
     * @param client Kanał komunikacyjny klienta, który wysłał komendę.
     */
    public void handleSetPlayersCommand(String message, SocketChannel client){
        String[] parts = message.split(" ");
        if (parts.length != 2) {
            sendMessage(client, "Niepoprawna komenda. Użyj: set_players {liczba}");
            return;
        }

        try {
            int newNumberOfPlayers = Integer.parseInt(parts[1]);
            if (newNumberOfPlayers >= 2 ) {
                numberOfPlayers = newNumberOfPlayers;
                sendMessage(client, "Liczba graczy została ustawiona na: " + numberOfPlayers);
            } else {
                sendMessage(client, "Liczba graczy musi być większa od 1.");
            }
        } catch (NumberFormatException e) {
            sendMessage(client, "Niepoprawny format liczby. Użyj: set_players <liczba>");
        }
    }

    /**
     * Tworzy nową grę z unikalnym ID i przypisuje ją do odpowiednich struktur serwera.
     *
     * @param clientChannel Kanał klienta, który tworzy nową grę.
     */
    public void createNewGame(SocketChannel clientChannel) {
        int gameId = gameIdCounter++;
        // tworzymy nową grę i wpisujemy ją do aktywnych gier
        Game game = new Game(gameId, numberOfPlayers, selector);
        activeGames.put(gameId, game);
        // rejestrujemy wątek nowej gry w executorze
        gameExecutor.submit(game);

        sendMessage(clientChannel, "Utworzono nową grę z ID: " + gameId + ". Dołącz do gry wpisując ID.\n");
    }

    void joinExistingGame(SocketChannel clientChannel, int gameId) {
        Game game = activeGames.get(gameId);
        if (game != null) {
            playersMap.get(clientChannel).setGame(game);
            game.addPlayer(playersMap.get(clientChannel));
            sendMessage(clientChannel, "Dołączono do gry o ID: " + gameId + "\n");

        } else {
            sendMessage(clientChannel, "Gra o podanym ID nie istnieje.\n");
        }
    }

    /**
     * Obsługuje zapisywanie danych do klienta (np. odpowiada na polecenia związane z grą).
     *
     * @param key Klucz selektora do zapisu danych.
     * @throws IOException W przypadku błędów przy zapisie.
     */
    void handleWrite(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();

        // sprawdzamy czy kanał klienta jest nadal otwarty
        if (!client.isOpen() || !key.isValid()) {
            return;
        }
        Queue<ByteBuffer> queue = pendingData.get(client);

        // dopóki nie opróżnimy kolejki
        while (!queue.isEmpty()) {
            // pobieramy bufor, którego kolej nadeszłą
            ByteBuffer buffer = queue.peek();
            client.write(buffer);

            if (buffer.hasRemaining()) {
                return;
            }
            // usuwamy element z kolejki
            queue.remove();
        }
        // zmieniamy klucz na
        key.interestOps(SelectionKey.OP_READ);
    }

    /**
     * Obsługuje wysyłanie wiadomości do klienta.
     *
     * @param client Klient, do którego ma zostać wysłana wiadomość.
     * @param message Wiadomość do wysłania.
     */
    void sendMessage(SocketChannel client, String message) {
        if (client == null || !client.isOpen()) {
            return;
        }
        // tworzymy bufor z wiadomością
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        // dodajemy go do kolejki
        pendingData.get(client).add(buffer);
        client.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
    }

    /**
     * Tworzy i dodaje do mapy `strategyMap` różne strategie obsługi komend.
     * Każda strategia odpowiada za obsługę określonej komendy, która może zostać otrzymana
     * przez serwer w trakcie komunikacji z klientem.
     * Strategie są mapowane na odpowiednie komendy, takie jak "join", "set_players", "new", "exit",
     * oraz potencjalnie inne.
     */
    void createStrategy(){
        strategyMap.put("join", new JoinRequestHandlerStrategy());
        strategyMap.put("", new JoinHandlerStrategy());
        strategyMap.put(SET_PLAYERS, new SetPlayersHandlerStrategy());
        strategyMap.put("new", new NewGameHandlerStrategy());
        strategyMap.put("exit", new ExitHandlerStrategy());
    }
}