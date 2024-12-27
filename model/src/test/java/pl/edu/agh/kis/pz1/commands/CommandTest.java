package pl.edu.agh.kis.pz1.commands;

import org.junit.jupiter.api.Test;
import pl.edu.agh.kis.pz1.Game;
import pl.edu.agh.kis.pz1.player_utils.Player;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandTest {

    // Klasa pochodna do testowania klasy Command
    private static class TestCommand extends Command {
        @Override
        public String processCommand(Game game, Player player, List<String> params) {
            return "TestCommand executed";
        }
    }

    @Test
    void testDefaultProcessCommand() {
        // Tworzymy anonimową klasę pochodną, aby przetestować domyślną implementację
        Command command = new Command() {};

        String result = command.processCommand(null, null, null);

        assertEquals("Komenda nie zaimplementowana", result, "Domyślna implementacja powinna zwrócić 'Komenda nie zaimplementowana'");
    }

    @Test
    void testSucceededDefaultValue() {
        Command command = new TestCommand();

        // Sprawdzamy domyślną wartość `succeeded`
        assertFalse(command.isSucceeded(), "Początkowa wartość succeeded powinna być false");
    }

    @Test
    void testSetSucceeded() {
        Command command = new TestCommand();

        // Ustawiamy succeeded na true
        command.setSucceeded(true);

        assertTrue(command.isSucceeded(), "Po ustawieniu succeeded na true, wartość powinna być true");
    }

    @Test
    void testProcessCommandOverride() {
        Command command = new TestCommand();

        // Wywołanie metody nadpisanej w klasie pochodnej
        String result = command.processCommand(null, null, null);

        assertEquals("TestCommand executed", result, "Nadpisana implementacja powinna zwrócić 'TestCommand executed'");
    }
}
