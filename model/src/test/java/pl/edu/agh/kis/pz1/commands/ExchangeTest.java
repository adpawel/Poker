package pl.edu.agh.kis.pz1.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.agh.kis.pz1.Game;
import pl.edu.agh.kis.pz1.card_utils.Card;
import pl.edu.agh.kis.pz1.card_utils.Deck;
import pl.edu.agh.kis.pz1.card_utils.Rank;
import pl.edu.agh.kis.pz1.card_utils.Suit;
import pl.edu.agh.kis.pz1.player_utils.Player;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static pl.edu.agh.kis.pz1.Constants.NOT_DRAW_ROUND;
import static pl.edu.agh.kis.pz1.Constants.WRONG_INDEX;

class ExchangeTest {
    Exchange exchangeCommand;
    Game gameMock;
    Player player;

    @BeforeEach
    void setUp() {
        exchangeCommand = new Exchange();
        gameMock = mock(Game.class);
        player = new Player();
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(Suit.CLUBS, Rank.ACE));
        cards.add(new Card(Suit.DIAMONDS, Rank.EIGHT));
        cards.add(new Card(Suit.CLUBS, Rank.FIVE));
        cards.add(new Card(Suit.SPADES, Rank.ACE));
        cards.add(new Card(Suit.DIAMONDS, Rank.SIX));
        player.setCards(cards);
    }

    @Test
    void testInvalidExchange(){
        Player playerMock = mock(Player.class);
        when(gameMock.getPhase()).thenReturn("bet");
        String result = exchangeCommand.processCommand(gameMock, playerMock, List.of("1", "exchange", "2", "4"));
        assertEquals(NOT_DRAW_ROUND, result);
    }

    @Test
    void testInvalidCardIndex(){
        when(gameMock.getPhase()).thenReturn("draw");
        String result = exchangeCommand.processCommand(gameMock, player, List.of("1", "exchange", "0", "4"));
        assertEquals(WRONG_INDEX, result);
    }

    @Test
    void testAnotherInvalidCardIndex(){
        when(gameMock.getPhase()).thenReturn("draw");
        Deck deckMock = mock(Deck.class);
        when(gameMock.getDeck()).thenReturn(deckMock);
        when(deckMock.dealCard()).thenReturn(new Card(Suit.CLUBS, Rank.TWO));

        String result = exchangeCommand.processCommand(gameMock, player, List.of("1", "exchange", "1", "2", "3", "4", "6"));
        assertEquals(WRONG_INDEX, result);
    }

    @Test
    void testValidExchange() {
        when(gameMock.getPhase()).thenReturn("draw");
        Deck deckMock = mock(Deck.class);
        when(gameMock.getDeck()).thenReturn(deckMock);
        when(deckMock.dealCard()).thenReturn(new Card(Suit.CLUBS, Rank.TWO));

        List<Card> cards = new ArrayList<>();
        cards.add(new Card(Suit.CLUBS, Rank.ACE));
        cards.add(new Card(Suit.DIAMONDS, Rank.EIGHT));
        cards.add(new Card(Suit.CLUBS, Rank.FIVE));
        cards.add(new Card(Suit.SPADES, Rank.ACE));
        cards.add(new Card(Suit.DIAMONDS, Rank.SIX));
        player.setCards(cards);

        List<Card> removedCards = new ArrayList<>();
        when(deckMock.getRemovedCards()).thenReturn(removedCards);

        String result = exchangeCommand.processCommand(gameMock, player, List.of("1", "exchange", "2", "4"));

        assertEquals("Pomyślnie dokonałeś wymiany kart!", result);
        assertTrue(player.isPlayed());
        assertTrue(removedCards.contains(new Card(Suit.DIAMONDS, Rank.EIGHT)));
        assertTrue(removedCards.contains(new Card(Suit.SPADES, Rank.ACE)));
        assertTrue(exchangeCommand.isSucceeded());
        assertNotEquals("DIAMONDS EIGHT", player.getCards().get(1).toString());
        assertNotEquals("SPADES ACE", player.getCards().get(3).toString());
    }

}