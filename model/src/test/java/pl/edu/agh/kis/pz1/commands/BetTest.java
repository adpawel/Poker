package pl.edu.agh.kis.pz1.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.agh.kis.pz1.Game;
import pl.edu.agh.kis.pz1.player_utils.Player;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.edu.agh.kis.pz1.Constants.*;

class BetTest {

    private Bet betCommand;
    private Game mockGame;
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        // Tworzymy instancję Bet
        betCommand = new Bet();

        // Tworzymy mocki obiektów Game i Player
        mockGame = mock(Game.class);
        mockPlayer = mock(Player.class);
    }

    @Test
    void testProcessCommandWithoutSumOfBet(){
        String result = betCommand.processCommand(mockGame, mockPlayer, Arrays.asList("1", "bet"));
        assertEquals("Nieprawidłowy format parametrów. Spróbuj ponownie.\n", result);
    }

    @Test
    void testProcessCommand_InvalidPhase() {
        // Konfigurujemy, że faza gry nie jest "bet"
        when(mockGame.getPhase()).thenReturn("draw");

        // Testujemy, czy metoda zwróci odpowiedni komunikat
        String result = betCommand.processCommand(mockGame, mockPlayer, Arrays.asList("1", "bet", "100"));
        assertEquals(NOT_BET_ROUND, result);
    }

    @Test
    void testInactivePlayer(){
       when(mockGame.getPhase()).thenReturn("bet");
       when(mockPlayer.isActive()).thenReturn(false);
       String result = betCommand.processCommand(mockGame, mockPlayer, Arrays.asList("1", "bet", "100"));
        assertEquals(ALREADY_FOLD, result);
    }

    @Test
   void testBetWhenMaxBetIsNotZero() {
        // Ustawiamy fazę na 'bet' i maksymalny zakład na wartość inną niż 0
        when(mockGame.getPhase()).thenReturn("bet");
        when(mockGame.getCurrentMaxBet()).thenReturn(20);
        when(mockPlayer.isActive()).thenReturn(true);

        // Gracz próbuje postawić zakład
        String result = betCommand.processCommand(mockGame, mockPlayer, List.of("1", "bet", "10"));

        // Oczekujemy komunikatu, że już w tej rundzie był zakład
        assertEquals(BET_EXIST, result);
    }


    @Test
    void testBetWhenNotEnoughChips() {
        // Ustawiamy fazę gry na 'bet' i żetony gracza na 5
        when(mockGame.getPhase()).thenReturn("bet");
        when(mockPlayer.getRemainingChips()).thenReturn(5);
        when(mockPlayer.isActive()).thenReturn(true);

        // Gracz próbuje postawić zakład 10
        String result = betCommand.processCommand(mockGame, mockPlayer, List.of("1", "bet", "10"));

        // Oczekujemy komunikatu, że gracz nie ma wystarczającej liczby żetonów
        assertEquals(NOT_ENOUGH_CHIPS, result);
    }

    @Test
    void testBetWhenValid() {
        // Ustawiamy fazę gry na 'bet' i graczowi przydzielamy wystarczającą liczbę żetonów
        Game game = new Game();
        game.setPhase("bet");
        Player player = new Player();
        player.setActive(true);
        player.setRemainingChips(100);

        // Gracz stawia zakład 50
        String result = betCommand.processCommand(game, player, List.of("1", "bet", "50"));

        // Oczekujemy komunikatu, że zakład został przyjęty
        assertEquals("Poprawnie przyjęto zakład", result);

        // Sprawdzamy, czy zmiany w grze i graczu są odpowiednie
        assertEquals(50, game.getPot());
        assertEquals(50, game.getCurrentMaxBet());
        assertEquals(50, player.getContributedChips());
        assertEquals(50, player.getRemainingChips());
    }
}
