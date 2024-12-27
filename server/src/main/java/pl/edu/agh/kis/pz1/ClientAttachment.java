package pl.edu.agh.kis.pz1;

import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;

/**
 * Klasa reprezentująca załącznik dla klienta, który jest używany w komunikacji z serwerem.
 * Zawiera dane potrzebne do zarządzania połączeniem klienta, w tym bufor dla danych przychodzących,
 * stan klienta oraz bufor do budowania wiadomości.
 */
@Getter
public class ClientAttachment {
    private final ByteBuffer buffer;
    @Setter
    private String state;
    @Getter
    private final StringBuilder messageBuilder = new StringBuilder();

    /**
     * Konstruktor klasy ClientAttachment.
     * Inicjalizuje bufor i ustawia stan klienta na null.
     *
     * @param buffer Bufor do przechowywania danych przychodzących od klienta.
     */
    public ClientAttachment(ByteBuffer buffer) {
        this.buffer = buffer;
        this.state = null;
    }
}
