package pl.edu.agh.kis.pz1.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.agh.kis.pz1.Game;
import pl.edu.agh.kis.pz1.player_utils.Player;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.edu.agh.kis.pz1.Constants.ALREADY_FOLD;
import static pl.edu.agh.kis.pz1.Constants.NOT_BET_ROUND;

class FoldTest {

    private Fold foldCommand;
    private Game mockGame;
    private Player mockPlayer;

    @BeforeEach
    void setUp() {
        foldCommand = new Fold();

        mockGame = mock(Game.class);
        mockPlayer = mock(Player.class);
    }

    @Test
    void testProcessCommand_InvalidPhase() {
        when(mockGame.getPhase()).thenReturn("draw");

        String result = foldCommand.processCommand(mockGame, mockPlayer, Arrays.asList("1", "fold"));
        assertEquals(NOT_BET_ROUND, result);
    }

    @Test
    void testIfPlayerInactive(){
        when(mockGame.getPhase()).thenReturn("bet");
        when(mockPlayer.isActive()).thenReturn(false);

        String result = foldCommand.processCommand(mockGame, mockPlayer, Arrays.asList("1", "fold"));
        assertEquals(ALREADY_FOLD, result);
    }

    @Test
    void testValidFold(){
        Game game = new Game();
        Player player = new Player();
        game.setPhase("bet");
        player.setActive(true);

        String result = foldCommand.processCommand(game, player, Arrays.asList("1", "fold"));
        assertTrue(player.isPlayed());
        assertTrue(foldCommand.isSucceeded());
        assertFalse(player.isActive());
        assertEquals(0, game.getCheckCount());
        assertEquals("Pomyślnie spasowałeś!", result);
    }


}