package pl.edu.agh.kis.pz1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.agh.kis.pz1.card_utils.Card;
import pl.edu.agh.kis.pz1.card_utils.Rank;
import pl.edu.agh.kis.pz1.card_utils.Suit;
import pl.edu.agh.kis.pz1.commands.Command;
import pl.edu.agh.kis.pz1.commands.CommandFactory;
import pl.edu.agh.kis.pz1.player_utils.Hand;
import pl.edu.agh.kis.pz1.player_utils.Player;

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static pl.edu.agh.kis.pz1.Constants.NUMBER_OF_CHIPS;

/**
 * Klasa testująca funkcjonalności klasy `Game`.
 * Obejmuje scenariusze związane z rozgrywką, obsługą graczy, oraz wykonaniem komend w grze.
 */
class GameTest {
    private Game game;

    /**
     * Przygotowanie nowego obiektu `Game` przed każdym testem.
     */
    @BeforeEach
    void setUp() {
        Selector selector = mock(Selector.class);
        game = spy(new Game(1, 4, selector));
    }

    /**
     * Testuje metodę `dealCards`, która rozdziela karty między graczy.
     * Sprawdza, czy zwracana lista kart nie jest pusta oraz czy rozdane karty
     * zostały usunięte z talii gry.
     */
    @Test
    void testDealCards() {
        List<Card> dealtCards = game.dealCards();

        assertNotNull(dealtCards);
        List<Card> remainingCards = game.getDeck().getCards();
        for (Card dealtCard : dealtCards) {
            assertFalse(remainingCards.contains(dealtCard));
        }
    }

    /**
     * Testuje przypadek, gdy komenda przekazana do gry jest nieznana (`null`).
     * Sprawdza, czy odpowiedni komunikat o błędzie jest wysyłany do klienta.
     */
    @Test
    void testExecuteCommandWhenCommandIsNull() {
        doNothing().when(game).sendMessage(any(SocketChannel.class), anyString());
        SocketChannel socketChannel = mock(SocketChannel.class);
        Player player = spy(new Player(socketChannel));
        Game.playersMap = new HashMap<>();
        Game.playersMap.put(socketChannel, player);

        game.executeCommand(socketChannel, List.of("1", "abcd", "20"));
        verify(game).sendMessage(socketChannel, "Nie ma takiej komendy. Spróbuj ponownie.");
    }

    /**
     * Testuje przypadek, gdy gracz próbuje wykonać akcję w turze innego gracza.
     * Weryfikuje, czy odpowiedni komunikat jest wysyłany do klienta.
     */
    @Test
    void testExecuteCommandWhenNotPLayersTurn() {
        doNothing().when(game).sendMessage(any(SocketChannel.class), anyString());
        SocketChannel socketChannel = mock(SocketChannel.class);
        Player player = spy(new Player(socketChannel));
        player.setPlayerId(1);
        Game.playersMap = new HashMap<>();
        Game.playersMap.put(socketChannel, player);

        game.executeCommand(socketChannel, List.of("1", "bet", "20"));
        verify(game).sendMessage(socketChannel, "To nie jest Twoja tura!");
    }

    /**
     * Testuje przypadek, gdy gracz próbuje wykonać akcję, ale nie jest aktywny
     * (np. spasował w rundzie). Sprawdza, czy odpowiedni komunikat jest wysyłany.
     */
    @Test
    void testExecuteCommandWhenPlayerNotActive() {
        doNothing().when(game).sendMessage(any(SocketChannel.class), anyString());
        SocketChannel socketChannel = mock(SocketChannel.class);
        Player player = spy(new Player(socketChannel));
        player.setPlayerId(0);
        player.setActive(false);
        Game.playersMap = new HashMap<>();
        Game.playersMap.put(socketChannel, player);

        game.executeCommand(socketChannel, List.of("1", "bet", "20"));
        verify(game).sendMessage(socketChannel, "Spasowałeś w tej rundzie.");
    }

    /**
     * Testuje poprawne wykonanie komendy przez gracza.
     * Weryfikuje, czy komenda została przetworzona, a wynik został przesłany do klienta.
     */
    @Test
    void testExecuteCommand() {
        SocketChannel clientChannel = mock(SocketChannel.class);
        Player player = mock(Player.class);
        CommandFactory commandFactory = mock(CommandFactory.class);
        Command command = mock(Command.class);
        game.setCommandFactory(commandFactory);
        Game.playersMap = new HashMap<>();
        Game.playersMap.put(clientChannel, player);
        List<String> parts = Arrays.asList("1", "CALL");
        when(commandFactory.createCommand("CALL")).thenReturn(command);
        when(command.processCommand(game, player, parts)).thenReturn("Success");
        when(command.isSucceeded()).thenReturn(true);
        when(game.isTurn(player)).thenReturn(true);
        when(player.isActive()).thenReturn(true);
        doNothing().when(game).sendMessage(any(SocketChannel.class), anyString());
        doNothing().when(game).handleSuccessfulCommand(anyList());

        game.executeCommand(clientChannel, parts);
        verify(command).processCommand(game, player, parts);
        verify(game).sendMessage(clientChannel, "Success");
        verify(command).isSucceeded();
        verify(game).handleSuccessfulCommand(parts);
    }

    /**
     * Testuje dodanie nowego gracza do gry.
     * Sprawdza, czy gracz został poprawnie dodany do listy graczy w grze.
     */
    @Test
    void testAddPlayer() {
        Player player = new Player(mock(SocketChannel.class));
        game.addPlayer(player);
        assertEquals(1, game.getPlayers().size());
        assertEquals(player, game.getPlayers().get(0));
    }

    /**
     * Testuje metodę `getCurrentPlayer`, która zwraca aktualnie grającego gracza.
     * Sprawdza, czy metoda zwraca poprawnego gracza.
     */
    @Test
    void testGetCurrentPlayer() {
        Player player = new Player(mock(SocketChannel.class));
        game.addPlayer(player);
        assertEquals(player, game.getCurrentPlayer());
    }

    /**
     * Testuje metodę `isTurn`, która sprawdza, czy jest tura danego gracza.
     * Weryfikuje poprawność działania dla różnych indeksów aktualnego gracza.
     */
    @Test
    void testIsTurn() {
        Player player = new Player(mock(SocketChannel.class));
        player.setPlayerId(0);
        game.setCurrentPlayerIndex(0);
        assertTrue(game.isTurn(player));
        game.setCurrentPlayerIndex(1);
        assertFalse(game.isTurn(player));
    }

    /**
     * Testuje przejście do następnej tury w grze.
     * Weryfikuje, czy aktualny gracz zmienia się na kolejnego w kolejności.
     */
    @Test
    void testNextTurn() {
        Player player1 = new Player(mock(SocketChannel.class));
        Player player2 = new Player(mock(SocketChannel.class));
        game.addPlayer(player1);
        game.addPlayer(player2);

        game.nextTurn();
        assertEquals(player2, game.getCurrentPlayer());
    }

    /**
     * Testuje sprawdzanie, czy runda licytacji została zakończona.
     * Obejmuje różne scenariusze:
     * - Wszystkich graczy spasowało.
     * - Liczba checków jest równa liczbie graczy.
     * - Wszyscy gracze wyrównali zakład.
     */
    @Test
    void testIsBetRoundFinished() {
        Player player1 = new Player(mock(SocketChannel.class));
        Player player2 = new Player(mock(SocketChannel.class));

        game.addPlayer(player1);
        game.addPlayer(player2);
        player1.setActive(true);
        player2.setActive(true);

        assertFalse(game.isBetRoundFinished());
        player1.setActive(false);
        assertTrue(game.isBetRoundFinished());
        player1.setActive(true);
        game.setCheckCount(4);
        game.setNumberOfPlayers(4);
        assertTrue(game.isBetRoundFinished());
        game.setCheckCount(3);

        game.setCurrentMaxBet(20);
        for (Player player : game.getPlayers()) {
            player.setContributedChips(20);
            player.setPlayed(true);
        }
        assertTrue(game.isBetRoundFinished());

        for (Player player : game.getPlayers()) {
            player.setContributedChips(10);
        }
        assertFalse(game.isBetRoundFinished());
    }

    /**
     * Testuje przejście do kolejnej fazy gry (rundy).
     * Weryfikuje reset stanu graczy oraz przejście przez fazy "draw" i "bet".
     */
    @Test
    void testChangeRound() {
        for (int i = 0; i < 4; i++) {
            Player player = new Player(mock(SocketChannel.class));
            player.setPlayed(true);
            player.setContributedChips(100);
            game.addPlayer(player);
        }

        game.changeRound();
        assertEquals("draw", game.getPhase());
        for (Player player : game.getPlayers()) {
            assertEquals(0, player.getContributedChips());
            assertFalse(player.isPlayed());
        }

        game.changeRound();
        assertEquals("bet", game.getPhase());
    }

    /**
     * Testuje sprawdzanie, czy runda wymiany kart została zakończona.
     * Oczekuje, że wszyscy gracze muszą wykonać swoją akcję, aby runda była zakończona.
     */
    @Test
    void testIsDrawRoundFinished() {
        for (int i = 0; i < 4; i++) {
            Player player = new Player(mock(SocketChannel.class));
            player.setPlayed(true);
            player.setContributedChips(100);
            game.addPlayer(player);
        }
        assertTrue(game.isDrawRoundFinished());
        game.getPlayers().get(0).setPlayed(false);
        assertFalse(game.isDrawRoundFinished());
    }

    /**
     * Testuje obliczanie zwycięzcy, gdy jest tylko jeden wygrywający gracz.
     * Sprawdza, czy gracz z najlepszym układem kart zostaje wyłoniony jako zwycięzca.
     */
    @Test
    void testCalculateWinner_SingleWinner() {
        Player player1 = createMockPlayer(1, List.of(new Card(Suit.HEARTS, Rank.TWO), new Card(Suit.HEARTS, Rank.THREE)));
        Player player2 = createMockPlayer(2, List.of(new Card(Suit.CLUBS, Rank.FOUR), new Card(Suit.CLUBS, Rank.FIVE)));

        game.addPlayer(player1);
        game.addPlayer(player2);
        player1.setActive(true);
        player2.setActive(true);
        Game.playersMap = mockPlayersMap(player1, player2);
        game.calculateWinner();

        assertEquals(1, game.getWinners().size());
        assertTrue(game.getWinners().contains(player2));
    }

    /**
     * Testuje obliczanie zwycięzców, gdy jest ich więcej niż jeden.
     * Sprawdza, czy gracze z równorzędnymi najlepszymi układami kart są poprawnie wyłonieni jako zwycięzcy.
     */
    @Test
    void testCalculateWinner_MultipleWinners() {
        Player player1 = createMockPlayer(1, List.of(new Card(Suit.HEARTS, Rank.TEN),
                new Card(Suit.HEARTS, Rank.JACK), new Card(Suit.HEARTS, Rank.KING),
                new Card(Suit.HEARTS, Rank.QUEEN), new Card(Suit.HEARTS, Rank.ACE)));
        Player player2 = createMockPlayer(2, List.of(new Card(Suit.CLUBS, Rank.TEN),
                new Card(Suit.CLUBS, Rank.JACK), new Card(Suit.CLUBS, Rank.KING),
                new Card(Suit.CLUBS, Rank.QUEEN), new Card(Suit.CLUBS, Rank.ACE)));
        Player player3 = createMockPlayer(3, List.of(new Card(Suit.CLUBS, Rank.FOUR),
                new Card(Suit.SPADES, Rank.FIVE), new Card(Suit.CLUBS, Rank.TWO),
                new Card(Suit.DIAMONDS, Rank.FIVE), new Card(Suit.HEARTS, Rank.FIVE)));

        game.addPlayer(player1);
        game.addPlayer(player2);
        game.addPlayer(player3);

        player1.setActive(true);
        player2.setActive(true);
        player3.setActive(true);

        Game.playersMap = mockPlayersMap(player1, player2, player3);
        game.calculateWinner();

        assertEquals(2, game.getWinners().size());
        assertTrue(game.getWinners().contains(player1));
        assertTrue(game.getWinners().contains(player2));
    }


    /**
     * Testuje resetowanie stanu graczy.
     * Weryfikuje, czy każdy gracz:
     * - Zostaje ustawiony jako aktywny.
     * - Ma zresetowany stan `played`.
     * - Liczba żetonów gracza zostaje przywrócona do wartości początkowej.
     */
    @Test
    void testResetPlayers() {
        for (int i = 0; i < 4; i++) {
            Player player = new Player(mock(SocketChannel.class));
            player.setPlayed(true);
            player.setContributedChips(100);
            game.addPlayer(player);
        }
        game.resetPlayers();
        for (Player player : game.getPlayers()) {
            assertFalse(player.isPlayed());
            assertTrue(player.isActive());
            assertEquals(NUMBER_OF_CHIPS, player.getRemainingChips());
        }
    }

    /**
     * Testuje rozpoczęcie gry.
     * Weryfikuje, czy:
     * - Wszyscy gracze otrzymują powiadomienia o rozpoczęciu gry.
     * - Każdemu graczowi przydzielane są karty.
     * - Każdy gracz otrzymuje wiadomość z informacją o swoim ID i przydzielonych kartach.
     * - Powiadamiany jest gracz, który zaczyna turę.
     */
    @Test
    void testStartGame() {
         SocketChannel clientChannel1 = mock(SocketChannel.class);
         SocketChannel clientChannel2 = mock(SocketChannel.class);
         Player player1 = spy(new Player(clientChannel1));
         Player player2 = spy(new Player(clientChannel2));
         player1.setPlayerId(1);
         player2.setPlayerId(2);
         List<Card> cards = List.of(new Card(Suit.HEARTS, Rank.TWO), new Card(Suit.SPADES, Rank.THREE),
                 new Card(Suit.DIAMONDS, Rank.FOUR), new Card(Suit.CLUBS, Rank.FIVE),
                 new Card(Suit.HEARTS, Rank.SIX));
        game.players.add(player1);
        game.players.add(player2);
        Game.playersMap = new HashMap<>();
        Game.playersMap.put(clientChannel1, player1);
        Game.playersMap.put(clientChannel2, player2);
        doReturn(cards).when(game).dealCards();
        doNothing().when(game).sendMessage(any(SocketChannel.class), anyString());
        doNothing().when(game).notifyAllPlayers(anyString());
        doNothing().when(game).notifyCurrentPlayer();
        game.startGame();

        verify(game).notifyAllPlayers("Gra się rozpoczyna! Liczba graczy: 2\n");
        verify(game).notifyAllPlayers("Aby grać użyj następującego formatu komendy: '1 {ruch} {opcjonalnie:parametry ruchu}'\n");
        verify(player1).setCards(cards);
        verify(game).sendMessage(clientChannel1, "Twoje id: " + player1.getPlayerId() + ". Twoje karty: \n" + player1.printCards() + "\n");
        verify(player2).setCards(cards);
        verify(game).sendMessage(clientChannel2, "Twoje id: " + player2.getPlayerId() + ". Twoje karty: \n" + player2.printCards() + "\n");
        verify(game).notifyCurrentPlayer();
    }

    /**
     * Testuje zamknięcie gry.
     * Weryfikuje, czy:
     * - Stan graczy zostaje zresetowany.
     * - Lista graczy w grze zostaje wyczyszczona.
     * - Gra przestaje działać.
     */
    @Test
    void testCloseGame(){
        for (int i = 0; i < 4; i++) {
            Player player = new Player(mock(SocketChannel.class));
            player.setPlayed(true);
            player.setContributedChips(100);
            game.addPlayer(player);
        }
        game.closeGame();
        for(Player player : game.getPlayers()) {
            assertFalse(player.isPlayed());
            assertTrue(player.isActive());
            assertEquals(NUMBER_OF_CHIPS, player.getRemainingChips());
        }
        assertEquals(0, game.getPlayers().size());
        assertFalse(game.isGameRunning());
    }

    /**
     * Testuje powiadamianie wszystkich graczy poza jednym.
     * Weryfikuje, czy odpowiednia wiadomość jest wysyłana do pozostałych graczy.
     */
    @Test
    void testNotifyOtherPlayers(){
        SocketChannel channel1 = mock(SocketChannel.class);
        SocketChannel channel2 = mock(SocketChannel.class);
        SocketChannel channel3 = mock(SocketChannel.class);
        Player player1 = spy(new Player(channel1));
        player1.setPlayerId(0);
        player1.setGame(game);
        game.addPlayer(player1);
        Player player2 = spy(new Player(channel2));
        player2.setPlayerId(1);
        player2.setGame(game);
        game.addPlayer(player2);
        Player player3 = spy(new Player(channel3));
        player3.setPlayerId(2);
        player3.setGame(game);
        game.addPlayer(player3);

        Game.playersMap = mockPlayersMap(player1, player2, player3);
        doNothing().when(game).sendMessage(any(SocketChannel.class), anyString());
        game.notifyOtherPlayers(List.of("1", "abcd"));
        verify(game, times(2)).sendMessage(any(SocketChannel.class), contains("abcd"));
    }

    /**
     * Testuje powiadamianie wszystkich graczy.
     * Weryfikuje, czy odpowiednia wiadomość jest wysyłana do wszystkich graczy w grze.
     */
    @Test
    void testNotifyAllPlayers(){
        SocketChannel channel1 = mock(SocketChannel.class);
        SocketChannel channel2 = mock(SocketChannel.class);
        Player player1 = spy(new Player(channel1));
        player1.setPlayerId(0);
        player1.setGame(game);
        game.addPlayer(player1);
        Player player2 = spy(new Player(channel2));
        player2.setPlayerId(1);
        player2.setGame(game);
        game.addPlayer(player2);

        Game.playersMap = mockPlayersMap(player1, player2);
        doNothing().when(game).sendMessage(any(SocketChannel.class), anyString());
        game.notifyAllPlayers("abcd");
        verify(game, times(2)).sendMessage(any(SocketChannel.class), eq("abcd"));
    }

    /**
     * Testuje powiadamianie aktualnego gracza o jego turze.
     * Weryfikuje, czy odpowiednia wiadomość jest wysyłana do gracza będącego aktualnie w turze.
     */
    @Test
    void testNotifyCurrentPlayer(){
        SocketChannel channel = mock(SocketChannel.class);
        Player player = spy(new Player(channel));
        player.setPlayerId(0);
        player.setGame(game);
        game.addPlayer(player);
        doNothing().when(game).sendMessage(any(SocketChannel.class), anyString());
        game.notifyCurrentPlayer();
        verify(game).sendMessage(channel, "Twoja tura!");
    }

    /**
     * Tworzy mock gracza z określonym ID, zestawem kart i układem ręki.
     *
     * @param playerId ID gracza.
     * @param cards Karty przypisane do gracza.
     * @return Mock gracza.
     */
    private Player createMockPlayer(int playerId, List<Card> cards) {
        Player player = new Player(mock(SocketChannel.class));
        player.setPlayerId(playerId);
        player.setCards(cards);
        player.setHand(new Hand(cards, playerId));
        return player;
    }

    /**
     * Tworzy mock mapy graczy przypisanych do kanałów.
     *
     * @param players Tablica graczy do przypisania.
     * @return Mock mapa graczy i kanałów.
     */
    private Map<SocketChannel, Player> mockPlayersMap(Player... players) {
        Map<SocketChannel, Player> mockMap = new HashMap<>();
        for (Player player : players) {
        mockMap.put(player.getChannel(), player);
    }
        return mockMap;
    }
}

