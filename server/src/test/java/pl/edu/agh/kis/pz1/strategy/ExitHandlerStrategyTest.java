package pl.edu.agh.kis.pz1.strategy;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import pl.edu.agh.kis.pz1.ClientAttachment;
import pl.edu.agh.kis.pz1.PokerServer;

public class ExitHandlerStrategyTest {
    @Mock
    private PokerServer mockServer;
    @Mock
    private SocketChannel mockClient;
    @Mock
    private ClientAttachment mockAttachment;
    private ExitHandlerStrategy exitHandlerStrategy;

    @Before
    public void setUp() {
        mockServer = mock(PokerServer.class);
        mockClient = mock(SocketChannel.class);
        mockAttachment = mock(ClientAttachment.class);
        exitHandlerStrategy = new ExitHandlerStrategy();
    }

    @Test
    public void testHandleExit() throws IOException {
        exitHandlerStrategy.handle("exit", mockClient, mockAttachment, mockServer);

        // Weryfikujemy, czy metoda handleExit została wywołana na obiekcie mockServer
        verify(mockServer, times(1)).handleExit(mockClient);
    }
}
