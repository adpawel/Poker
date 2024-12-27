package pl.edu.agh.kis.pz1.commands;

import org.junit.jupiter.api.Test;
import pl.edu.agh.kis.pz1.*;
import pl.edu.agh.kis.pz1.card_utils.Card;
import pl.edu.agh.kis.pz1.card_utils.Rank;
import pl.edu.agh.kis.pz1.card_utils.Suit;
import pl.edu.agh.kis.pz1.player_utils.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CardsTest {

    @Test
    void testProcessCommand() {
        Game mockGame = mock(Game.class);
        Player player = new Player();
        List<Card> cards = new ArrayList<>();
        cards.add(new Card(Suit.CLUBS, Rank.ACE));
        cards.add(new Card(Suit.DIAMONDS, Rank.EIGHT));
        cards.add(new Card(Suit.CLUBS, Rank.FIVE));
        cards.add(new Card(Suit.SPADES, Rank.ACE));
        cards.add(new Card(Suit.DIAMONDS, Rank.SIX));

        Cards cardsCommand = new Cards();
        player.setCards(cards);
        String result = cardsCommand.processCommand(mockGame, player, Arrays.asList("1", "cards"));
        assertEquals("""
        CLUBS ACE
        DIAMONDS EIGHT
        CLUBS FIVE
        SPADES ACE
        DIAMONDS SIX
        """, result);
    }
}