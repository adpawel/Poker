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

class CallTest {
    private Call callCommand;
    private Game mockGame;
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        // Tworzymy instancję Bet
        callCommand = new Call();

        // Tworzymy mocki obiektów Game i Player
        mockGame = mock(Game.class);
        mockPlayer = mock(Player.class);
    }

    @Test
    void testProcessCommand_InvalidPhase() {
        // Konfigurujemy, że faza gry nie jest "bet"
        when(mockGame.getPhase()).thenReturn("draw");

        // Testujemy, czy metoda zwróci odpowiedni komunikat
        String result = callCommand.processCommand(mockGame, mockPlayer, Arrays.asList("1", "call"));
        assertEquals(NOT_BET_ROUND, result);
    }

    @Test
    void testInactivePlayer(){
        when(mockGame.getPhase()).thenReturn("bet");
        when(mockPlayer.isActive()).thenReturn(false);
        String result = callCommand.processCommand(mockGame, mockPlayer, Arrays.asList("1", "call"));
        assertEquals(ALREADY_FOLD, result);
    }

    @Test
    void testCallWhenNotEnoughChips() {
        when(mockGame.getPhase()).thenReturn("bet");
        when(mockPlayer.isActive()).thenReturn(true);
        when(mockGame.getCurrentMaxBet()).thenReturn(30);
        when(mockPlayer.getContributedChips()).thenReturn(0);
        when(mockPlayer.getRemainingChips()).thenReturn(20);
        String result = callCommand.processCommand(mockGame, mockPlayer, Arrays.asList("1", "call"));
        assertEquals(NOT_ENOUGH_CHIPS, result);
    }

    @Test
    void testCallWhenNothingToCall(){
        when(mockGame.getPhase()).thenReturn("bet");
        when(mockPlayer.isActive()).thenReturn(true);
        when(mockGame.getCurrentMaxBet()).thenReturn(10);
        when(mockPlayer.getContributedChips()).thenReturn(10);
        when(mockPlayer.getRemainingChips()).thenReturn(100);
        String result = callCommand.processCommand(mockGame, mockPlayer, Arrays.asList("1", "call"));
        assertEquals(NO_BET_TO_CALL, result);
    }

    @Test
    void testValidCallCommand(){
        Game game = new Game();
        Player player = new Player();
        game.setPhase("bet");
        player.setActive(true);
        game.setCurrentMaxBet(30);
        player.setContributedChips(10);
        player.setRemainingChips(100);
        game.setPot(80);

        String result = callCommand.processCommand(game, player, Arrays.asList("1", "call"));
        assertEquals("Pomyślnie dokonałeś 'call'", result);
        assertTrue(player.isPlayed());
        assertEquals(0, game.getCheckCount());
        assertEquals(80, player.getRemainingChips());
        assertEquals(100, game.getPot());
        assertEquals(30, player.getContributedChips());
        assertTrue(callCommand.isSucceeded());
    }
}