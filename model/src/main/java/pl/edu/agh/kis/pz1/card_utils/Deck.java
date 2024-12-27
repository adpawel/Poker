package pl.edu.agh.kis.pz1.card_utils;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Klasa reprezentujaca talie kart do gry w pokera
 */
@Data
public class Deck {
    private List<Card> cards;
    private List<Card> removedCards;
    public Deck() {
        cards = new ArrayList<>();
        removedCards = new ArrayList<>();
    }

    /**
     * Zwraca posortowana talie kart

     * @return lista kart posortowanych według kolejno: koloru({@link Suit}) i wartosci({@link Suit})
     */
    public List<Card> createNewDeck(){
        List<Card> newCards = new ArrayList<>();

        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                newCards.add(new Card(suit, rank));
            }
        }

        // Sortowanie kart według koloru, a następnie wartości
        newCards.sort(Comparator.comparing(Card::getSuit).thenComparing(Card::getRank));

        return newCards;
    }

    /**
     * Tasuje talie kart
     */
    public void shuffle(List<Card> inputCards){
        Collections.shuffle(inputCards);
        cards = inputCards;
    }

    /**
     * Wydaje jedną kartę z aktualnej talii
     * @return karta {@link Card}
     */
    public Card dealCard(){
        if(cards.isEmpty()){
            cards.addAll(removedCards);
            removedCards.clear();
            shuffle(cards);
        }
        return cards.remove(0);
    }
}
