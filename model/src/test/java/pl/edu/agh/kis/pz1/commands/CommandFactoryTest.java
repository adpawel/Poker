package pl.edu.agh.kis.pz1.commands;

import org.junit.jupiter.api.Test;
import pl.edu.agh.kis.pz1.Game;
import pl.edu.agh.kis.pz1.player_utils.Player;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommandFactoryTest {

    // Przykładowa klasa implementująca Command
    public static class TestCommand extends Command {
        public TestCommand() {
            // Publiczny konstruktor
        }

        @Override
        public String processCommand(Game game, Player player, List<String> params) {
            return "TestCommand executed";
        }
    }

    private static class AnotherTestCommand extends Command {
        public AnotherTestCommand() {
            // Publiczny konstruktor
        }

        @Override
        public String processCommand(Game game, Player player, List<String> params) {
            return "AnotherTestCommand executed";
        }
    }

    @Test
    void testRegisterAndCreateCommand() {
        CommandFactory factory = new CommandFactory();

        // Rejestrujemy komendę
        factory.registerCommand("test", TestCommand.class);

        // Tworzymy instancję
        Command command = factory.createCommand("test");

        assertNotNull(command, "Command should not be null after creation");
        assertTrue(command instanceof TestCommand, "Created command should be an instance of TestCommand");
    }

    @Test
    void testCreateCommandNotRegistered() {
        CommandFactory factory = new CommandFactory();

        // Próba stworzenia instancji dla nieistniejącej komendy
        Command command = factory.createCommand("nonexistent");

        assertNull(command, "Command should be null if not registered");
    }

    @Test
    void testRegisterNullCommandClass() {
        CommandFactory factory = new CommandFactory();

        // Próba zarejestrowania null jako klasy komendy
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> factory.registerCommand("test", null),
                "Registering a null command class should throw IllegalArgumentException"
        );

        assertEquals("Command class cannot be null", exception.getMessage());
    }

    @Test
    void testCreateCommandWithException() {
        CommandFactory factory = new CommandFactory();

        // Klasa komendy bez domyślnego konstruktora
        class InvalidCommand extends Command {
            private InvalidCommand() {} // Prywatny konstruktor
        }

        factory.registerCommand("invalid", InvalidCommand.class);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> factory.createCommand("invalid"),
                "Creating a command with no accessible constructor should throw RuntimeException"
        );

        assertTrue(exception.getMessage().contains("Failed to create command instance"), "Exception message should indicate failure to create command instance");
    }

    @Test
    void testRegisterMultipleCommands() {
        CommandFactory factory = new CommandFactory();

        // Rejestrujemy różne komendy
        factory.registerCommand("test1", TestCommand.class);
        factory.registerCommand("test2", AnotherTestCommand.class);

        // Tworzymy pierwszą komendę
        Command command1 = factory.createCommand("test1");
        assertNotNull(command1, "Command1 should not be null after creation");
        assertTrue(command1 instanceof TestCommand, "Command1 should be an instance of TestCommand");

        // Tworzymy drugą komendę
        Command command2 = factory.createCommand("test2");
        assertNotNull(command2, "Command2 should not be null after creation");
        assertTrue(command2 instanceof AnotherTestCommand, "Command2 should be an instance of AnotherTestCommand");
    }
}
