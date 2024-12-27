package pl.edu.agh.kis.pz1.commands;

import pl.edu.agh.kis.pz1.exceptions.CommandCreationException;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {
    private final Map<String, Class<? extends Command>> commandMap = new HashMap<>();

    /**
     * Rejestruje klasę komendy dla danego identyfikatora.
     *
     * @param commandName  unikalna nazwa komendy
     * @param commandClass klasa komendy implementująca Command
     * @throws IllegalArgumentException jeśli commandClass jest null
     */
    public void registerCommand(String commandName, Class<? extends Command> commandClass) {
        if (commandClass == null) {
            throw new IllegalArgumentException("Command class cannot be null");
        }
        commandMap.put(commandName, commandClass);
    }

    /**
     * Tworzy instancję komendy na podstawie jej nazwy.
     *
     * @param commandName nazwa zarejestrowanej komendy
     * @return nowa instancja komendy lub null, jeśli nie znaleziono nazwy
     * @throws RuntimeException jeśli tworzenie instancji nie powiedzie się
     */
    public Command createCommand(String commandName) {
        Class<? extends Command> commandClass = commandMap.get(commandName);

        if (commandClass == null) {
            return null;
        }

        try {
            return commandClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new CommandCreationException("Failed to create command instance: " + commandName, e);
        }
    }
}

