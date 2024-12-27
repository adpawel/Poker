package pl.edu.agh.kis.pz1.strategy;

import org.junit.Before;
import org.junit.Test;
import pl.edu.agh.kis.pz1.ClientAttachment;
import pl.edu.agh.kis.pz1.PokerServer;

import java.nio.channels.SocketChannel;

import static org.mockito.Mockito.*;

public class JoinRequestHandlerStrategyTest {
    private PokerServer mockServer;
    private SocketChannel mockClient;
    private ClientAttachment mockAttachment;
    private String message;
    private JoinRequestHandlerStrategy joinRequestHandlerStrategy;

    @Before
    public void setUp() {
        mockServer = mock(PokerServer.class);
        mockClient = mock(SocketChannel.class);
        mockAttachment = mock(ClientAttachment.class);
        message = "";
        joinRequestHandlerStrategy = new JoinRequestHandlerStrategy();
    }

    @Test
    public void testHandleExit() {
        joinRequestHandlerStrategy.handle(message, mockClient, mockAttachment, mockServer);

        // Weryfikujemy, czy metoda została wywołana na obiekcie mockServer
        verify(mockServer, times(1)).handleJoinRequest(mockClient, mockAttachment);
    }
}