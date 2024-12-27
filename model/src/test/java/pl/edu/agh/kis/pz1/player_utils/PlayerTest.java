package pl.edu.agh.kis.pz1.player_utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.agh.kis.pz1.card_utils.Card;
import pl.edu.agh.kis.pz1.card_utils.Rank;
import pl.edu.agh.kis.pz1.card_utils.Suit;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static pl.edu.agh.kis.pz1.Constants.NUMBER_OF_CHIPS;

class PlayerTest {
    Player player;

    @BeforeEach
    void setUp() {
        SocketChannel channelMock = mock(SocketChannel.class);
        player = new Player(channelMock);
    }

    @Test
    void printCards() {
        List<Card> cards = new ArrayList<>(List.of(
                new Card(Suit.CLUBS, Rank.FIVE),
                new Card(Suit.CLUBS, Rank.SIX),
                new Card(Suit.SPADES, Rank.FIVE),
                new Card(Suit.CLUBS, Rank.ACE)
        ));
        player.setCards(cards);

        String result = player.printCards();
        assertEquals("""
                CLUBS FIVE
                CLUBS SIX
                SPADES FIVE
                CLUBS ACE
                """, result);
    }

    @Test
    void reset() {
        player.setRemainingChips(0);
        player.setContributedChips(150);
        player.setActive(false);
        player.setPlayed(true);
        player.reset();
        assertEquals(NUMBER_OF_CHIPS, player.getRemainingChips());
        assertEquals(0, player.getContributedChips());
        assertTrue(player.isActive());
        assertFalse(player.isPlayed());
    }
}