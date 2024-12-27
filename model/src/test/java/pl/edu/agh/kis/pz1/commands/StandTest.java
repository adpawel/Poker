package pl.edu.agh.kis.pz1.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.agh.kis.pz1.Game;
import pl.edu.agh.kis.pz1.player_utils.Player;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.edu.agh.kis.pz1.Constants.NOT_DRAW_ROUND;

class StandTest {

    private Stand standCommand;

    @BeforeEach
    void setUp() {
        standCommand = new Stand();
    }

    @Test
    void testProcessCommand_InvalidPhase() {
        Game gameMock = mock(Game.class);
        Player playerMock = mock(Player.class);
        when(gameMock.getPhase()).thenReturn("bet");

        String result = standCommand.processCommand(gameMock, playerMock, Arrays.asList("1", "stand"));
        assertEquals(NOT_DRAW_ROUND, result);
    }

    @Test
    void testValidStand(){
        Game game = new Game();
        game.setPhase("draw");
        Player player = new Player();

        String result = standCommand.processCommand(game, player, Arrays.asList("1", "stand"));
        assertEquals("Pomyślnie dokonałeś 'stand'", result);
        assertTrue(player.isPlayed());
        assertTrue(standCommand.isSucceeded());
    }
}