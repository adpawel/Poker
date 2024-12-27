package pl.edu.agh.kis.pz1.card_utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    Deck deck;
    List<Card> newDeck = new ArrayList<>();

    @BeforeEach
    void setUp() {
        deck = new Deck();
        newDeck = deck.createNewDeck();
    }

    @Test
    void testCreateNewDeck() {
        // Sprawdzamy, czy nowa talia ma 52 karty
        assertEquals(52, newDeck.size());

        // Sprawdzamy, czy talia zawiera wszystkie kombinacje Suit i Rank
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                assertTrue(newDeck.contains(new Card(suit, rank)));
            }
        }

        // Sprawdzamy, czy talia jest posortowana
        List<Card> sortedDeck = new ArrayList<>(newDeck);
        sortedDeck.sort(Comparator.comparing(Card::getSuit).thenComparing(Card::getRank));
        assertEquals(sortedDeck, newDeck);
    }

    @Test
    void testShuffle() {
        List<Card> originalDeck = new ArrayList<>(newDeck);

        deck.shuffle(newDeck);

        // Sprawdzamy, czy talia ma nadal 52 karty po przetasowaniu
        assertEquals(52, newDeck.size());

        // Sprawdzamy, czy talia różni się od posortowanej wersji
        assertNotEquals(originalDeck, newDeck);

        // Sprawdzamy, czy nadal zawiera te same karty
        newDeck.sort(Comparator.comparing(Card::getSuit).thenComparing(Card::getRank));
        assertEquals(originalDeck, newDeck);
    }

    @Test
    void testDealCard() {
        deck.shuffle(newDeck);

        Card firstCard = deck.dealCard();

        // Sprawdzamy, czy pierwsza karta została usunięta z talii
        assertFalse(newDeck.contains(firstCard));
        assertEquals(51, newDeck.size()); // 52 karty - 1 karta rozdana

        // Sprawdzamy, czy zwracana karta nie jest null
        assertNotNull(firstCard);
    }

    @Test
    void testDeckReshuffleAfterEmpty() {
        deck.shuffle(newDeck);
        List<Card> removedCards = new ArrayList<>(List.of(
                new Card(Suit.CLUBS, Rank.FIVE),
                new Card(Suit.CLUBS, Rank.SIX),
                new Card(Suit.SPADES, Rank.FIVE),
                new Card(Suit.CLUBS, Rank.ACE)
        ));
        deck.setRemovedCards(removedCards);


        // Rozdajemy wszystkie karty
        for (int i = 0; i < 52; i++) {
            deck.dealCard();
        }
        assertEquals(0, newDeck.size());
        // Rozdajemy kartę z pustej talii (wymusza przetasowanie)
        Card reshuffledCard = deck.dealCard();
        assertFalse(newDeck.contains(reshuffledCard));
        assertNotNull(reshuffledCard);
    }
}
