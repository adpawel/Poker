package pl.edu.agh.kis.pz1.exceptions;

public class CommandCreationException extends RuntimeException {
    public CommandCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
