package pl.edu.agh.kis.pz1.card_utils;


import lombok.Data;
import lombok.Getter;

/**
 * Klasa reprezentujaca pojedyncza karte do gry w pokera
 */
@Getter
@Data
public class Card implements Comparable<Card>{

    protected Suit suit;
    protected Rank rank;

    /**
     * Tworzy nowa karte o zadanym kolorze(suit) i randze(rank)
     *
     * @param suit kolor typu {@link Suit}
     * @param rank ranga typu {@link Rank}
     */
    public Card (Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    /**
     * Sprawdza czy obiekt jest rowny karcie

     * @param o obiekt, ktory jest porownywany do tej karty
     * @return true, jesli obiekty są rowne, false w przeciwnym razie
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card c1 = (Card) o;

        if (!getSuit().equals(c1.getSuit())) return false;
        return getRank().equals(c1.getRank());
    }

    /**
     * Zwraca kod haszujacy dla tego obiektu karty.
     * <p>
     * Metoda generuje kod haszujacy na podstawie wartosci kolorow (suit) i rang (rank).
     * Jest spojna z metoda {@link #equals(Object)}, co oznacza, ze jesli dwa obiekty są rowne
     * wedlug metody {@code equals}, to powinny miec ten sam kod haszujacy.
     *
     * @return kod haszujacy dla tego obiektu karty
     */
    @Override
    public int hashCode() {
        return  31 * getSuit().hashCode() + getRank().hashCode();
    }

    /**
     * Zwraca reprezentację tekstową tego obiektu karty.
     * <p>
     * Metoda generuje czytelną reprezentację karty w formacie:
     * {@code [kolor] [wartość]}. Na przykład "DIAMONDS FOUR" lub "SPADES TEN".
     *
     * @return reprezentacja tekstowa tego obiektu karty
     */
    @Override
    public String toString() {
        return suit.toString() + " " + rank.toString();
    }

    /**
     * Porównuje bieżący obiekt karty z innym obiektem karty.
     * <p>
     * Porównanie odbywa się na podstawie rang kart w kolejności
     * ustalonej przez metodę {@code compareTo} klasy {@link Rank}.
     * Wynik porównania jest liczbą całkowitą:
     * <ul>
     *   <li>Ujemną, jeśli bieżąca karta ma rangę mniejszą niż karta podana jako argument.</li>
     *   <li>Zero, jeśli obie karty mają tę samą rangę.</li>
     *   <li>Dodatnią, jeśli bieżąca karta ma rangę większą niż karta podana jako argument.</li>
     * </ul>
     * Ta metoda nie uwzględnia kolorów (suit) podczas porównania.
     *
     * @param o karta, z którą porównywana jest bieżąca karta
     * @return wynik porównania rang kart jako liczba całkowita
     * @throws NullPointerException jeśli karta podana jako argument jest {@code null}
     */
    @Override
    public int compareTo(Card o) {
        return this.rank.compareTo(o.rank);
    }
}