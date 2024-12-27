package pl.edu.agh.kis.pz1.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.agh.kis.pz1.Game;
import pl.edu.agh.kis.pz1.player_utils.Player;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.edu.agh.kis.pz1.Constants.*;

class RaiseTest {

    private Raise raiseCommand;
    private Game mockGame;
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        raiseCommand = new Raise();

        mockGame = mock(Game.class);
        mockPlayer = mock(Player.class);
    }

    @Test
    void testProcessCommandWithoutSumOfRaise(){
        String result = raiseCommand.processCommand(mockGame, mockPlayer, Arrays.asList("1", "raise"));
        assertEquals("Nieprawidłowy format parametrów. Spróbuj ponownie.\n", result);
    }

    @Test
    void testProcessCommand_InvalidPhase() {
        when(mockGame.getPhase()).thenReturn("draw");

        String result = raiseCommand.processCommand(mockGame, mockPlayer, Arrays.asList("1", "raise", "100"));
        assertEquals(NOT_BET_ROUND, result);
    }

    @Test
    void testIfPlayerInactive(){
        when(mockGame.getPhase()).thenReturn("bet");
        when(mockPlayer.isActive()).thenReturn(false);

        String result = raiseCommand.processCommand(mockGame, mockPlayer, Arrays.asList("1", "raise", "100"));
        assertEquals(ALREADY_FOLD, result);
    }

    @Test
    void testEnoughRemainingChipsToRaise(){
        when(mockGame.getPhase()).thenReturn("bet");
        when(mockPlayer.isActive()).thenReturn(true);
        when(mockPlayer.getRemainingChips()).thenReturn(200);
        String result = raiseCommand.processCommand(mockGame, mockPlayer, Arrays.asList("1", "raise", "250"));
        assertEquals(NOT_ENOUGH_CHIPS, result);
    }

    @Test
    void testEnoughChipsToRaise(){
        when(mockGame.getPhase()).thenReturn("bet");
        when(mockPlayer.isActive()).thenReturn(true);
        when(mockPlayer.getRemainingChips()).thenReturn(200);
        when(mockPlayer.getContributedChips()).thenReturn(50);
        when(mockGame.getCurrentMaxBet()).thenReturn(200);
        String result = raiseCommand.processCommand(mockGame, mockPlayer, Arrays.asList("1", "raise", "100"));
        assertEquals( "Podana liczba żetonów dodana do twojego wkładu(50) musi być większa niż obecny zakład (200)", result);
    }

    @Test
    void testValidRaise(){
        Game game = new Game();
        Player player = new Player();
        game.setPhase("bet");
        player.setActive(true);
        player.setRemainingChips(200);
        player.setContributedChips(150);
        game.setCurrentMaxBet(200);
        game.setPot(400);

        String result = raiseCommand.processCommand(game, player, Arrays.asList("1", "raise", "100"));
        assertEquals( "Pomyślnie dokonałeś 'raise'", result);
        assertTrue(player.isPlayed());
        assertTrue(raiseCommand.isSucceeded());
        assertEquals(0, game.getCheckCount());
        assertEquals(250, game.getCurrentMaxBet());
        assertEquals(100, player.getRemainingChips());
        assertEquals(250, player.getContributedChips());
        assertEquals(500, game.getPot());
    }
}