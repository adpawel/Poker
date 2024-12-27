package pl.edu.agh.kis.pz1.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.agh.kis.pz1.Game;
import pl.edu.agh.kis.pz1.player_utils.Player;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.edu.agh.kis.pz1.Constants.*;

class CheckTest {
    private Check checkCommand;
    private Game mockGame;
    private Player mockPlayer;
    @BeforeEach
    void setUp() {
        checkCommand = new Check();
        mockGame = mock(Game.class);
        mockPlayer = mock(Player.class);
    }

    @Test
    void testProcessCommand_InvalidPhase() {
        // Konfigurujemy, że faza gry nie jest "bet"
        when(mockGame.getPhase()).thenReturn("draw");

        // Testujemy, czy metoda zwróci odpowiedni komunikat
        String result = checkCommand.processCommand(mockGame, mockPlayer, Arrays.asList("1", "check"));
        assertEquals(NOT_BET_ROUND, result);
    }

    @Test
    void testInactivePlayer(){
        when(mockGame.getPhase()).thenReturn("bet");
        when(mockPlayer.isActive()).thenReturn(false);
        String result = checkCommand.processCommand(mockGame, mockPlayer, Arrays.asList("1", "check"));
        assertEquals(ALREADY_FOLD, result);
    }

    @Test
    void testCheckWhenThereIsBetToCall(){
        when(mockGame.getPhase()).thenReturn("bet");
        when(mockPlayer.isActive()).thenReturn(true);
        when(mockGame.getCurrentMaxBet()).thenReturn(50);
        when(mockPlayer.getContributedChips()).thenReturn(20);

        String result = checkCommand.processCommand(mockGame, mockPlayer, Arrays.asList("1", "check"));
        assertEquals(EXISTS_BET_TO_CALL, result);
    }

    @Test
    void testValidCheck(){
        Game game = new Game();
        Player player = new Player();
        game.setPhase("bet");
        game.setCurrentMaxBet(50);
        player.setContributedChips(50);
        player.setActive(true);
        game.setCheckCount(1);

        String result = checkCommand.processCommand(game, player, Arrays.asList("1", "check"));
        assertEquals("Pomyślnie wykonano ruch: 'check'", result);
        assertTrue(player.isPlayed());
        assertTrue(checkCommand.isSucceeded());
        assertEquals(2, game.getCheckCount());
    }
}