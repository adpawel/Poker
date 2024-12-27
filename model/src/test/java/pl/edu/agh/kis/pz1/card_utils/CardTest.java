package pl.edu.agh.kis.pz1.card_utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {
    @Test
    void testCardConstructor() {
        Card card = new Card(Suit.HEARTS, Rank.ACE);
        assertEquals(Suit.HEARTS, card.getSuit());
        assertEquals(Rank.ACE, card.getRank());
    }

    @Test
    void testEqualsSameObject() {
        Card card = new Card(Suit.HEARTS, Rank.ACE);
        assertEquals(card, card); // Powinno być true, gdy porównujemy ten sam obiekt
    }

    @Test
    void testEqualsDifferentObjectsSameValues() {
        Card card1 = new Card(Suit.HEARTS, Rank.ACE);
        Card card2 = new Card(Suit.HEARTS, Rank.ACE);
        assertEquals(card1, card2); // Powinno być true, gdy wartości są takie same
    }

    @Test
    void testEqualsDifferentSuit() {
        Card card1 = new Card(Suit.HEARTS, Rank.ACE);
        Card card2 = new Card(Suit.SPADES, Rank.ACE);
        assertNotEquals(card1, card2); // Różne kolory, więc false
    }

    @Test
    void testEqualsDifferentRank() {
        Card card1 = new Card(Suit.HEARTS, Rank.ACE);
        Card card2 = new Card(Suit.HEARTS, Rank.KING);
        assertNotEquals(card1, card2); // Różne rangi, więc false
    }

    @Test
    void testEqualsNull() {
        Card card = new Card(Suit.HEARTS, Rank.ACE);
        assertNotEquals(null, card); // Powinno być false
    }

    @Test
    void testEqualsDifferentClass() {
        Card card = new Card(Suit.HEARTS, Rank.ACE);
        assertNotEquals("NotACard", card); // Powinno być false
    }

    @Test
    void testHashCodeSameValues() {
        Card card1 = new Card(Suit.HEARTS, Rank.ACE);
        Card card2 = new Card(Suit.HEARTS, Rank.ACE);
        assertEquals(card1.hashCode(), card2.hashCode()); // HashCode powinien być identyczny
    }

    @Test
    void testHashCodeDifferentValues() {
        Card card1 = new Card(Suit.HEARTS, Rank.ACE);
        Card card2 = new Card(Suit.SPADES, Rank.ACE);
        assertNotEquals(card1.hashCode(), card2.hashCode()); // HashCode powinien być różny
    }

    @Test
    void testToString() {
        Card card = new Card(Suit.HEARTS, Rank.ACE);
        assertEquals("HEARTS ACE", card.toString()); // Testuje reprezentację tekstową
    }

    @Test
    void testCompareToSameRank() {
        Card card1 = new Card(Suit.HEARTS, Rank.ACE);
        Card card2 = new Card(Suit.SPADES, Rank.ACE);
        assertEquals(0, card1.compareTo(card2)); // Powinno zwrócić 0, gdy rangi są takie same
    }

    @Test
    void testCompareToHigherRank() {
        Card card1 = new Card(Suit.HEARTS, Rank.KING);
        Card card2 = new Card(Suit.SPADES, Rank.QUEEN);
        assertTrue(card1.compareTo(card2) > 0); // Powinno zwrócić wartość dodatnią
    }

    @Test
    void testCompareToLowerRank() {
        Card card1 = new Card(Suit.HEARTS, Rank.TWO);
        Card card2 = new Card(Suit.SPADES, Rank.THREE);
        assertTrue(card1.compareTo(card2) < 0); // Powinno zwrócić wartość ujemną
    }
}
