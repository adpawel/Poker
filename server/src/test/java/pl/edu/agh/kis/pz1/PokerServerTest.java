package pl.edu.agh.kis.pz1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.agh.kis.pz1.player_utils.Player;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static pl.edu.agh.kis.pz1.Constants.SET_PLAYERS;

/**
 * Klasa testowa `PokerServerTest` służy do testowania funkcjonalności serwera pokera
 * za pomocą JUnit i Mockito. Testy weryfikują poprawność obsługi klientów, gier oraz
 * komunikacji na serwerze PokerServer.
 */
class PokerServerTest {

    private PokerServer pokerServer;
    private SocketChannel clientChannel;
    private Player player;
    private Game game;

    /**
     * Inicjalizuje testowe środowisko przed każdym testem.
     * Tworzy obiekty `PokerServer`, `SocketChannel`, `Player` oraz `Game`.
     *
     * @throws IOException jeśli wystąpi problem przy inicjalizacji obiektu `PokerServer`.
     */
    @BeforeEach
    void setUp() throws IOException {
        // używamy spy dzięki któremu możemy wywoływać prawdziwe metody i jednocześnie działa verify
        pokerServer = spy(new PokerServer(4, "localhost", 1234));

        clientChannel = mock(SocketChannel.class);
        player = mock(Player.class);
        game = new Game(1, 2, mock(Selector.class));

        pokerServer.activeGames = new HashMap<>();
        Game.playersMap = new HashMap<>();
    }

    /**
     * Testuje scenariusz, w którym użytkownik podaje nieprawidłowy identyfikator gry
     * podczas próby dołączenia. Weryfikuje, czy klient otrzymuje odpowiednią wiadomość
     * o błędzie.
     */
    @Test
    void testInvalidHandleJoinGame(){
        ClientAttachment attachment = mock(ClientAttachment.class);
        doNothing().when(pokerServer).sendMessage(any(SocketChannel.class), anyString());
        pokerServer.handleJoinGame("abcd", clientChannel, attachment);
        assertNull(attachment.getState());
        verify(pokerServer).sendMessage(eq(clientChannel), contains("Nieprawidłowy ID gry"));
    }

    /**
     * Testuje scenariusz, w którym użytkownik podaje prawidłowy identyfikator gry
     * podczas próby dołączenia. Weryfikuje, czy odpowiednia metoda `joinExistingGame`
     * zostaje wywołana.
     */
    @Test
    void testValidHandleJoinGame(){
        ClientAttachment attachment = mock(ClientAttachment.class);
        doNothing().when(pokerServer).sendMessage(any(SocketChannel.class), anyString());
        doNothing().when(pokerServer).joinExistingGame(any(SocketChannel.class), eq(2));
        pokerServer.handleJoinGame("2", clientChannel, attachment);
        assertNull(attachment.getState());
        verify(pokerServer, times(1)).joinExistingGame(any(SocketChannel.class), eq(2));
    }

    /**
     * Testuje, czy po odebraniu żądania dołączenia przez serwer, użytkownik
     * otrzymuje odpowiedni komunikat z prośbą o podanie identyfikatora gry,
     * a stan klienta zmienia się na "join".
     */
    @Test
    void testHandleJoinRequest(){
        ClientAttachment attachment = spy(new ClientAttachment(mock(ByteBuffer.class)));
        doNothing().when(pokerServer).sendMessage(any(SocketChannel.class), anyString());
        pokerServer.handleJoinRequest(clientChannel, attachment);

        verify(pokerServer).sendMessage(clientChannel, "Podaj ID gry, do której chcesz dołączyć:\n");
        assertEquals("join", attachment.getState());
    }

    /**
     * Testuje, czy po wyjściu klienta z serwera jego dane są usuwane z `playersMap`,
     * a odpowiednie wiadomości są wysyłane. Weryfikuje również, czy połączenie
     * klienta zostaje zamknięte.
     *
     * @throws IOException jeśli wystąpi problem z zamknięciem kanału klienta.
     */
    @Test
    void testHandleExit() throws IOException {
        Game.playersMap = new HashMap<>();
        Game.playersMap.put(clientChannel, new Player(clientChannel));
        doNothing().when(pokerServer).sendMessage(any(SocketChannel.class), anyString());
        pokerServer.handleExit(clientChannel);
        assertFalse(Game.playersMap.containsKey(clientChannel));
        verify(pokerServer).sendMessage(clientChannel, "Dziękujemy za korzystanie z serwera pokera! Do zobaczenia!\n"); verify(clientChannel).close();
    }

    /**
     * Testuje przypadek, gdy wiadomość od klienta jest zbyt krótka, aby była poprawną komendą gry.
     * Weryfikuje, czy klient otrzymuje komunikat o niepoprawnej wiadomości.
     */
    @Test
    void testHandleGameCommandWhenMessageTooShort(){
        doNothing().when(pokerServer).sendMessage(any(SocketChannel.class), anyString());
        pokerServer.handleGameCommand("abcd", clientChannel);
        verify(pokerServer).sendMessage(eq(clientChannel), contains("Niepoprawna wiadomość"));
    }

    /**
     * Testuje przypadek, gdy gra o podanym ID nie istnieje.
     * Weryfikuje, czy klient otrzymuje odpowiedni komunikat o braku gry.
     */
    @Test
    void testHandleGameCommandWhenGameNotExists(){
        pokerServer.activeGames = new HashMap<>();
        pokerServer.activeGames.put(2, mock(Game.class));
        doNothing().when(pokerServer).sendMessage(any(SocketChannel.class), anyString());
        pokerServer.handleGameCommand("1 call", clientChannel);
        verify(pokerServer).sendMessage(eq(clientChannel), contains("Gra o podanym ID nie istnieje. Spróbuj ponownie."));
    }

    /**
     * Testuje przypadek, gdy gra o podanym ID istnieje, ale nie jest już aktywna.
     * Weryfikuje, czy klient otrzymuje odpowiedni komunikat o zakończeniu gry.
     */
    @Test
    void testHandleGameCommandWhenGameNotRunning(){
        pokerServer.activeGames = new HashMap<>();
        Game game1 = mock(Game.class);
        when(game1.isGameRunning()).thenReturn(false);
        pokerServer.activeGames.put(2, game1);

        doNothing().when(pokerServer).sendMessage(any(SocketChannel.class), anyString());
        pokerServer.handleGameCommand("2 call", clientChannel);
        verify(pokerServer).sendMessage(clientChannel, "Gra o podanym ID została zakończona. Spróbuj ponownie.");
    }

    /**
     * Testuje przypadek, gdy klient podaje ID gry, która nie jest przypisana do niego.
     * Weryfikuje, czy klient otrzymuje odpowiedni komunikat o niewłaściwej grze.
     */
    @Test
    void testHandleGameCommandWhenGameNotCorrespondToPlayer(){
        Game.playersMap.put(clientChannel, player);
        Game game1 = mock(Game.class);
        when(game1.isGameRunning()).thenReturn(true);
        when(game1.getGameId()).thenReturn(2);
        pokerServer.activeGames.put(2, game1);
        Game game2 = mock(Game.class);
        when(game2.isGameRunning()).thenReturn(true);
        when(game2.getGameId()).thenReturn(1);
        pokerServer.activeGames.put(1, game2);
        when(player.getGame()).thenReturn(game1);
        doNothing().when(pokerServer).sendMessage(any(SocketChannel.class), anyString());
        pokerServer.handleGameCommand("1 call", clientChannel);
        verify(pokerServer).sendMessage(clientChannel, "Podałeś ID nie swojej gry!");
    }

    /**
     * Testuje poprawny przypadek, gdy klient podaje prawidłowe ID gry oraz poprawną komendę.
     * Weryfikuje, czy metoda `executeCommand` dla gry jest wywoływana z odpowiednimi parametrami.
     */
    @Test
    void testValidHandleGameCommand(){
        Game.playersMap.put(clientChannel, player);
        Game game1 = spy(new Game(1, 2, mock(Selector.class)));
        when(game1.isGameRunning()).thenReturn(true);
        pokerServer.activeGames.put(1, game1);
        when(player.getGame()).thenReturn(game1);
        doNothing().when(game1).executeCommand(any(SocketChannel.class), anyList());
        pokerServer.handleGameCommand("1 call", clientChannel);
        verify(game1, times(1)).executeCommand(clientChannel, List.of("1", "call"));
    }


    /**
     * Testuje przypadek, gdy komenda gry ma niepoprawny format.
     * Weryfikuje, czy klient otrzymuje odpowiedni komunikat o błędnym formacie.
     */
    @Test
    void testHandleGameCommandWhenWrongFormat(){
        doNothing().when(pokerServer).sendMessage(any(SocketChannel.class), anyString());
        pokerServer.handleGameCommand("abcd call", clientChannel);
        verify(pokerServer).sendMessage(clientChannel, "Niepoprawny format. Spróbuj ponownie.");
    }

    /**
     * Testuje przypadek, gdy komenda ustawienia liczby graczy ma zbyt mało argumentów.
     * Weryfikuje, czy klient otrzymuje odpowiedni komunikat o błędzie.
     */
    @Test
    void testHandleSetPlayersCommandWhenTooFewArguments(){
        doNothing().when(pokerServer).sendMessage(any(SocketChannel.class), anyString());
        pokerServer.handleSetPlayersCommand("abcd", clientChannel);
        verify(pokerServer).sendMessage(eq(clientChannel), contains("Niepoprawna komenda"));
    }

    /**
     * Testuje przypadek, gdy liczba graczy jest ustawiana poprawnie.
     * Weryfikuje, czy liczba graczy na serwerze zostaje zmieniona i klient jest o tym informowany.
     */
    @Test
    void testHandleSetPlayersCommand(){
        int n = 2;
        doNothing().when(pokerServer).sendMessage(any(SocketChannel.class), anyString());
        pokerServer.handleSetPlayersCommand(SET_PLAYERS + " " + n, clientChannel);
        assertEquals(n, pokerServer.getNumberOfPlayers());
        verify(pokerServer).sendMessage(clientChannel, "Liczba graczy została ustawiona na: " + n);
    }

    /**
     * Testuje przypadek, gdy klient próbuje ustawić liczbę graczy mniejszą niż 2.
     * Weryfikuje, czy klient otrzymuje odpowiedni komunikat o błędzie.
     */
    @Test
    void testHandleSetPlayersCommandWhenNumberOfPlayersIsLessThanTwo(){
        int n = 1;
        doNothing().when(pokerServer).sendMessage(any(SocketChannel.class), anyString());
        pokerServer.handleSetPlayersCommand(SET_PLAYERS + " " + n, clientChannel);
        verify(pokerServer).sendMessage(clientChannel, "Liczba graczy musi być większa od 1.");
    }

    /**
     * Testuje przypadek, gdy klient podaje nieprawidłowy (nieliczbowy) argument dla liczby graczy.
     * Weryfikuje, czy klient otrzymuje odpowiedni komunikat o błędnym formacie.
     */
    @Test
    void testHandleSetPlayersCommandWhenNumberIsNotANumber(){
        String s = "abcd";
        doNothing().when(pokerServer).sendMessage(any(SocketChannel.class), anyString());
        pokerServer.handleSetPlayersCommand(SET_PLAYERS + " " + s, clientChannel);
        verify(pokerServer).sendMessage(clientChannel, "Niepoprawny format liczby. Użyj: set_players <liczba>");
    }

    /**
     * Testuje przypadek, gdy nowa gra jest tworzona przez klienta.
     * Weryfikuje, czy gra zostaje dodana do aktywnych gier i czy odpowiedni komunikat jest wysyłany do klienta.
     */
    @Test
    void testCreateNewGame(){
        Selector selector = mock(Selector.class);
        pokerServer.setSelector(selector);
        ExecutorService gameExecutor = mock(ExecutorService.class);
        pokerServer.setGameExecutor(gameExecutor);
        doNothing().when(pokerServer).sendMessage(any(SocketChannel.class), anyString());
        pokerServer.createNewGame(clientChannel);
        assertEquals(1, pokerServer.activeGames.size());
        Game createdGame = pokerServer.activeGames.values().iterator().next();
        assertNotNull(createdGame);
        verify(gameExecutor).submit(createdGame);
        verify(pokerServer).sendMessage(eq(clientChannel), contains("Utworzono nową grę z ID: "));
    }


    /**
     * Testuje przypadek, gdy klient dołącza do istniejącej gry.
     * Weryfikuje, czy gracz jest przypisywany do gry, a odpowiedni komunikat jest wysyłany.
     */
    @Test
    void testJoinExistingGameWhenGameExists() {
        int gameId = 1;
        Game.playersMap.put(clientChannel, player);
        pokerServer.activeGames.put(1, game);
        doNothing().when(pokerServer).sendMessage(clientChannel, "Dołączono do gry o ID: " + gameId + "\n");

        pokerServer.joinExistingGame(clientChannel, gameId);

        verify(player).setGame(game);

        assertTrue(game.getPlayers().contains(player));

        verify(pokerServer).sendMessage(clientChannel, "Dołączono do gry o ID: " + gameId + "\n");
    }

    /**
     * Testuje przypadek, gdy klient próbuje dołączyć do gry, która nie istnieje.
     * Weryfikuje, czy odpowiedni komunikat o błędzie jest wysyłany.
     */
    @Test
    void testJoinExistingGameWhenGameNotExists() {
        int gameId = 89;
        Game.playersMap.put(clientChannel, player);
        pokerServer.activeGames.put(1, game);
        doNothing().when(pokerServer).sendMessage(clientChannel, "Gra o podanym ID nie istnieje.\n");
        pokerServer.joinExistingGame(clientChannel, gameId);
        verify(pokerServer).sendMessage(clientChannel, "Gra o podanym ID nie istnieje.\n");
    }


    /**
     * Testuje przypadek, gdy klient jest nieaktywny (zamknięty).
     * Weryfikuje, czy dane nie są dodawane do kolejki i żadne inne operacje nie są wykonywane.
     */
    @Test void testSendMessage_ClientIsNotOpen(){
        Selector selector = mock(Selector.class);
        game = spy(new Game(1, 2, selector));
        Game.pendingData = new HashMap<>();
        Queue<ByteBuffer> pendingDataQueue = mock(Queue.class);
        Game.pendingData.put(clientChannel, pendingDataQueue);

        when(clientChannel.isOpen()).thenReturn(false);

        game.sendMessage(clientChannel, "Test message");
        verify(clientChannel).isOpen();
        verify(pendingDataQueue, never()).add(any(ByteBuffer.class));
    }

    /**
     * Testuje przypadek, gdy klient jest aktywny i otwarty.
     * Weryfikuje, czy wiadomość jest poprawnie dodawana do kolejki oraz czy ustawiany jest interesujący nas klucz `OP_WRITE`.
     */
    @Test void testSendMessage_ClientIsOpen() {
        Selector selector = mock(Selector.class);
        game = spy(new Game(1, 2, selector));
        Game.pendingData = new HashMap<>();
        Queue<ByteBuffer> pendingDataQueue = mock(Queue.class);
        Game.pendingData.put(clientChannel, pendingDataQueue);
        when(clientChannel.isOpen()).thenReturn(true);
        SelectionKey selectionKey = mock(SelectionKey.class);
        when(clientChannel.keyFor(selector)).thenReturn(selectionKey);
        game.sendMessage(clientChannel, "Test message");
        verify(clientChannel).isOpen();
        verify(pendingDataQueue).add(any(ByteBuffer.class));
        verify(selectionKey).interestOps(SelectionKey.OP_WRITE);
    }
}

