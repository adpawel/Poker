package pl.edu.agh.kis.pz1.player_utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.edu.agh.kis.pz1.card_utils.Card;
import pl.edu.agh.kis.pz1.card_utils.Rank;
import pl.edu.agh.kis.pz1.card_utils.Suit;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HandTest {
    Hand hand;
    Player player;

    @BeforeEach
    void setUp() {
        player = new Player();
    }

    @Test
    void testCalculateRankingRoyalFlush() {
        player.setCards(List.of(
                new Card(Suit.HEARTS, Rank.TEN),
                new Card(Suit.HEARTS, Rank.JACK),
                new Card(Suit.HEARTS, Rank.QUEEN),
                new Card(Suit.HEARTS, Rank.KING),
                new Card(Suit.HEARTS, Rank.ACE)
        ));
        hand = new Hand(player.getCards(), 21);
        player.setHand(hand);

        int ranking = player.getHand().calculateRanking();
        assertEquals(Combination.ROYAL_FLUSH.getValue(), ranking);
    }

    @Test
    void testCalculateRankingStraightFlush() {
        player.setCards(List.of(
                new Card(Suit.HEARTS, Rank.SIX),
                new Card(Suit.HEARTS, Rank.SEVEN),
                new Card(Suit.HEARTS, Rank.EIGHT),
                new Card(Suit.HEARTS, Rank.NINE),
                new Card(Suit.HEARTS, Rank.TEN)
        ));
        hand = new Hand(player.getCards(), 21);
        player.setHand(hand);

        int ranking = player.getHand().calculateRanking();
        assertEquals(Combination.STRAIGHT_FLUSH.getValue(), ranking);
    }

    @Test
    void testCalculateRankingFour() {
        player.setCards(List.of(
                new Card(Suit.HEARTS, Rank.SIX),
                new Card(Suit.CLUBS, Rank.SIX),
                new Card(Suit.SPADES, Rank.SIX),
                new Card(Suit.DIAMONDS, Rank.SEVEN),
                new Card(Suit.HEARTS, Rank.SIX)
        ));
        hand = new Hand(player.getCards(), 21);
        player.setHand(hand);

        int ranking = player.getHand().calculateRanking();
        assertEquals(Combination.FOUR.getValue(), ranking);
    }

    @Test
    void testCalculateRankingFull() {
        player.setCards(List.of(
                new Card(Suit.HEARTS, Rank.SIX),
                new Card(Suit.CLUBS, Rank.SIX),
                new Card(Suit.SPADES, Rank.SIX),
                new Card(Suit.DIAMONDS, Rank.SEVEN),
                new Card(Suit.HEARTS, Rank.SEVEN)
        ));
        hand = new Hand(player.getCards(), 21);
        player.setHand(hand);

        int ranking = player.getHand().calculateRanking();
        assertEquals(Combination.FULL.getValue(), ranking);
    }

    @Test
    void testCalculateRankingFlush() {
        player.setCards(List.of(
                new Card(Suit.DIAMONDS, Rank.TEN),
                new Card(Suit.DIAMONDS, Rank.TWO),
                new Card(Suit.DIAMONDS, Rank.SIX),
                new Card(Suit.DIAMONDS, Rank.ACE),
                new Card(Suit.DIAMONDS, Rank.THREE)
        ));
        hand = new Hand(player.getCards(), 21);
        player.setHand(hand);

        int ranking = player.getHand().calculateRanking();
        assertEquals(Combination.FLUSH.getValue(), ranking);
    }

    @Test
    void testCalculateRankingStraight() {
        player.setCards(List.of(
                new Card(Suit.DIAMONDS, Rank.THREE),
                new Card(Suit.DIAMONDS, Rank.FOUR),
                new Card(Suit.CLUBS, Rank.FIVE),
                new Card(Suit.DIAMONDS, Rank.SIX),
                new Card(Suit.SPADES, Rank.SEVEN)
        ));
        hand = new Hand(player.getCards(), 21);
        player.setHand(hand);

        int ranking = player.getHand().calculateRanking();
        assertEquals(Combination.STRAIGHT.getValue(), ranking);
    }

    @Test
    void testCalculateRankingThree() {
        player.setCards(List.of(
                new Card(Suit.DIAMONDS, Rank.THREE),
                new Card(Suit.DIAMONDS, Rank.SIX),
                new Card(Suit.CLUBS, Rank.FIVE),
                new Card(Suit.DIAMONDS, Rank.SIX),
                new Card(Suit.SPADES, Rank.SIX)
        ));
        hand = new Hand(player.getCards(), 21);
        player.setHand(hand);

        int ranking = player.getHand().calculateRanking();
        assertEquals(Combination.THREE.getValue(), ranking);
    }

    @Test
    void testCalculateRankingTwoPairs() {
        player.setCards(List.of(
                new Card(Suit.DIAMONDS, Rank.THREE),
                new Card(Suit.DIAMONDS, Rank.THREE),
                new Card(Suit.CLUBS, Rank.FIVE),
                new Card(Suit.DIAMONDS, Rank.SIX),
                new Card(Suit.SPADES, Rank.SIX)
        ));
        hand = new Hand(player.getCards(), 21);
        player.setHand(hand);

        int ranking = player.getHand().calculateRanking();
        assertEquals(Combination.TWO_PAIR.getValue(), ranking);
    }

    @Test
    void testCalculateRankingPair() {
        player.setCards(List.of(
                new Card(Suit.DIAMONDS, Rank.THREE),
                new Card(Suit.DIAMONDS, Rank.SIX),
                new Card(Suit.CLUBS, Rank.FIVE),
                new Card(Suit.DIAMONDS, Rank.ACE),
                new Card(Suit.SPADES, Rank.SIX)
        ));
        hand = new Hand(player.getCards(), 21);
        player.setHand(hand);

        int ranking = player.getHand().calculateRanking();
        assertEquals(Combination.PAIR.getValue(), ranking);
    }

    @Test
    void testCalculateRankingNull() {
        player.setCards(List.of(
                new Card(Suit.DIAMONDS, Rank.THREE),
                new Card(Suit.DIAMONDS, Rank.QUEEN),
                new Card(Suit.CLUBS, Rank.FIVE),
                new Card(Suit.DIAMONDS, Rank.ACE),
                new Card(Suit.SPADES, Rank.SIX)
        ));
        hand = new Hand(player.getCards(), 21);
        player.setHand(hand);

        int ranking = player.getHand().calculateRanking();
        assertEquals(0, ranking);
    }

    @Test
    void testCompareToWhenDifferentRanking(){
        List<Card> cards1 = new ArrayList<>(List.of(
                new Card(Suit.CLUBS, Rank.FIVE),
                new Card(Suit.CLUBS, Rank.SIX),
                new Card(Suit.SPADES, Rank.FIVE),
                new Card(Suit.CLUBS, Rank.ACE),
                new Card(Suit.DIAMONDS, Rank.SIX)
        ));
        List<Card> cards2 = new ArrayList<>(List.of(
                new Card(Suit.CLUBS, Rank.FIVE),
                new Card(Suit.CLUBS, Rank.SIX),
                new Card(Suit.CLUBS, Rank.FIVE),
                new Card(Suit.CLUBS, Rank.ACE),
                new Card(Suit.CLUBS, Rank.SIX)
        ));
        Hand hand1 = new Hand(cards1, 21);
        Hand hand2 = new Hand(cards2, 1);

        assertEquals(hand1.compareTo(hand2), -1);
    }

    @Test
    void testCompareToWhenSameRankingRoyalFlush(){
        List<Card> cards1 = new ArrayList<>(List.of(
                new Card(Suit.CLUBS, Rank.TEN),
                new Card(Suit.CLUBS, Rank.JACK),
                new Card(Suit.CLUBS, Rank.QUEEN),
                new Card(Suit.CLUBS, Rank.KING),
                new Card(Suit.CLUBS, Rank.ACE)
        ));
        List<Card> cards2 = new ArrayList<>(List.of(
                new Card(Suit.DIAMONDS, Rank.TEN),
                new Card(Suit.DIAMONDS, Rank.JACK),
                new Card(Suit.DIAMONDS, Rank.QUEEN),
                new Card(Suit.DIAMONDS, Rank.KING),
                new Card(Suit.DIAMONDS, Rank.ACE)
        ));
        Hand hand1 = new Hand(cards1, 21);
        Hand hand2 = new Hand(cards2, 1);

        assertEquals(0, hand1.compareTo(hand2));
    }

    @Test
    void testCompareToWhenSameRankingStraightFlush(){
        List<Card> cards1 = new ArrayList<>(List.of(
                new Card(Suit.CLUBS, Rank.TWO),
                new Card(Suit.CLUBS, Rank.THREE),
                new Card(Suit.CLUBS, Rank.FOUR),
                new Card(Suit.CLUBS, Rank.FIVE),
                new Card(Suit.CLUBS, Rank.SIX)
        ));
        List<Card> cards2 = new ArrayList<>(List.of(
                new Card(Suit.DIAMONDS, Rank.THREE),
                new Card(Suit.DIAMONDS, Rank.FOUR),
                new Card(Suit.DIAMONDS, Rank.FIVE),
                new Card(Suit.DIAMONDS, Rank.SIX),
                new Card(Suit.DIAMONDS, Rank.SEVEN)
        ));
        Hand hand1 = new Hand(cards1, 21);
        Hand hand2 = new Hand(cards2, 1);

        assertEquals(hand1.compareTo(hand2), -1);
    }

    @Test
    void testCompareToWhenSameRankingFour(){
        List<Card> cards1 = new ArrayList<>(List.of(
                new Card(Suit.CLUBS, Rank.TEN),
                new Card(Suit.HEARTS, Rank.TEN),
                new Card(Suit.CLUBS, Rank.FOUR),
                new Card(Suit.SPADES, Rank.TEN),
                new Card(Suit.DIAMONDS, Rank.TEN)
        ));
        List<Card> cards2 = new ArrayList<>(List.of(
                new Card(Suit.DIAMONDS, Rank.TEN),
                new Card(Suit.HEARTS, Rank.TEN),
                new Card(Suit.SPADES, Rank.TEN),
                new Card(Suit.HEARTS, Rank.TWO),
                new Card(Suit.DIAMONDS, Rank.TEN)
        ));
        Hand hand1 = new Hand(cards1, 21);
        Hand hand2 = new Hand(cards2, 1);

        assertEquals( 1, hand1.compareTo(hand2));
    }

    @Test
    void testCompareToWhenSameRankingFull(){
        List<Card> cards1 = new ArrayList<>(List.of(
                new Card(Suit.CLUBS, Rank.TEN),
                new Card(Suit.HEARTS, Rank.TEN),
                new Card(Suit.CLUBS, Rank.FOUR),
                new Card(Suit.SPADES, Rank.TEN),
                new Card(Suit.DIAMONDS, Rank.FOUR)
        ));
        List<Card> cards2 = new ArrayList<>(List.of(
                new Card(Suit.DIAMONDS, Rank.TEN),
                new Card(Suit.HEARTS, Rank.TEN),
                new Card(Suit.SPADES, Rank.TWO),
                new Card(Suit.HEARTS, Rank.TWO),
                new Card(Suit.DIAMONDS, Rank.TEN)
        ));
        Hand hand1 = new Hand(cards1, 21);
        Hand hand2 = new Hand(cards2, 1);

        assertEquals( 1, hand1.compareTo(hand2));
    }

    @Test
    void testCompareToWhenSameRankingFLush(){
        List<Card> cards1 = new ArrayList<>(List.of(
                new Card(Suit.CLUBS, Rank.TEN),
                new Card(Suit.CLUBS, Rank.SEVEN),
                new Card(Suit.CLUBS, Rank.ACE),
                new Card(Suit.CLUBS, Rank.QUEEN),
                new Card(Suit.CLUBS, Rank.TWO)
        ));
        List<Card> cards2 = new ArrayList<>(List.of(
                new Card(Suit.DIAMONDS, Rank.TEN),
                new Card(Suit.DIAMONDS, Rank.ACE),
                new Card(Suit.DIAMONDS, Rank.QUEEN),
                new Card(Suit.DIAMONDS, Rank.SEVEN),
                new Card(Suit.DIAMONDS, Rank.TWO)
        ));
        Hand hand1 = new Hand(cards1, 21);
        Hand hand2 = new Hand(cards2, 1);

        assertEquals( 0, hand1.compareTo(hand2));
    }

    @Test
    void testCompareToWhenSameRankingStraight(){
        List<Card> cards1 = new ArrayList<>(List.of(
                new Card(Suit.CLUBS, Rank.EIGHT),
                new Card(Suit.SPADES, Rank.NINE),
                new Card(Suit.DIAMONDS, Rank.QUEEN),
                new Card(Suit.SPADES, Rank.TEN),
                new Card(Suit.CLUBS, Rank.JACK)
        ));
        List<Card> cards2 = new ArrayList<>(List.of(
                new Card(Suit.DIAMONDS, Rank.NINE),
                new Card(Suit.HEARTS, Rank.KING),
                new Card(Suit.HEARTS, Rank.TEN),
                new Card(Suit.DIAMONDS, Rank.QUEEN),
                new Card(Suit.HEARTS, Rank.JACK)
        ));
        Hand hand1 = new Hand(cards1, 21);
        Hand hand2 = new Hand(cards2, 1);

        assertEquals( -1, hand1.compareTo(hand2));
    }

    @Test
    void testCompareToWhenSameRankingThree(){
        List<Card> cards1 = new ArrayList<>(List.of(
                new Card(Suit.CLUBS, Rank.EIGHT),
                new Card(Suit.HEARTS, Rank.THREE),
                new Card(Suit.DIAMONDS, Rank.EIGHT),
                new Card(Suit.SPADES, Rank.EIGHT),
                new Card(Suit.CLUBS, Rank.JACK)
        ));
        List<Card> cards2 = new ArrayList<>(List.of(
                new Card(Suit.HEARTS, Rank.EIGHT),
                new Card(Suit.HEARTS, Rank.QUEEN),
                new Card(Suit.CLUBS, Rank.QUEEN),
                new Card(Suit.DIAMONDS, Rank.QUEEN),
                new Card(Suit.HEARTS, Rank.JACK)
        ));
        Hand hand1 = new Hand(cards1, 21);
        Hand hand2 = new Hand(cards2, 1);

        assertEquals( -1, hand1.compareTo(hand2));
    }

    @Test
    void testCompareToWhenSameRankingTwoPairs(){
        List<Card> cards1 = new ArrayList<>(List.of(
                new Card(Suit.CLUBS, Rank.EIGHT),
                new Card(Suit.HEARTS, Rank.JACK),
                new Card(Suit.DIAMONDS, Rank.EIGHT),
                new Card(Suit.SPADES, Rank.TWO),
                new Card(Suit.CLUBS, Rank.JACK)
        ));
        List<Card> cards2 = new ArrayList<>(List.of(
                new Card(Suit.HEARTS, Rank.THREE),
                new Card(Suit.HEARTS, Rank.THREE),
                new Card(Suit.CLUBS, Rank.JACK),
                new Card(Suit.DIAMONDS, Rank.QUEEN),
                new Card(Suit.HEARTS, Rank.JACK)
        ));
        Hand hand1 = new Hand(cards1, 21);
        Hand hand2 = new Hand(cards2, 1);

        assertEquals( 1, hand1.compareTo(hand2));
    }

    @Test
    void testCompareToWhenSameRankingTwoPairs2(){
        List<Card> cards2 = new ArrayList<>(List.of(
                new Card(Suit.CLUBS, Rank.EIGHT),
                new Card(Suit.HEARTS, Rank.JACK),
                new Card(Suit.DIAMONDS, Rank.EIGHT),
                new Card(Suit.SPADES, Rank.TWO),
                new Card(Suit.CLUBS, Rank.JACK)
        ));
        List<Card> cards1 = new ArrayList<>(List.of(
                new Card(Suit.HEARTS, Rank.THREE),
                new Card(Suit.HEARTS, Rank.THREE),
                new Card(Suit.CLUBS, Rank.JACK),
                new Card(Suit.DIAMONDS, Rank.QUEEN),
                new Card(Suit.HEARTS, Rank.JACK)
        ));
        Hand hand1 = new Hand(cards1, 21);
        Hand hand2 = new Hand(cards2, 1);

        assertEquals( -1, hand1.compareTo(hand2));
    }

    @Test
    void testCompareToWhenSameRankingTwoPairs3(){
        List<Card> cards1 = new ArrayList<>(List.of(
                new Card(Suit.CLUBS, Rank.EIGHT),
                new Card(Suit.HEARTS, Rank.JACK),
                new Card(Suit.DIAMONDS, Rank.EIGHT),
                new Card(Suit.SPADES, Rank.TWO),
                new Card(Suit.CLUBS, Rank.JACK)
        ));
        List<Card> cards2 = new ArrayList<>(List.of(
                new Card(Suit.HEARTS, Rank.EIGHT),
                new Card(Suit.HEARTS, Rank.EIGHT),
                new Card(Suit.CLUBS, Rank.JACK),
                new Card(Suit.DIAMONDS, Rank.QUEEN),
                new Card(Suit.HEARTS, Rank.JACK)
        ));
        Hand hand1 = new Hand(cards1, 21);
        Hand hand2 = new Hand(cards2, 1);

        assertEquals( -1, hand1.compareTo(hand2));
    }

    @Test
    void testCompareToWhenSameRankingPair(){
        List<Card> cards1 = new ArrayList<>(List.of(
                new Card(Suit.CLUBS, Rank.EIGHT),
                new Card(Suit.HEARTS, Rank.JACK),
                new Card(Suit.DIAMONDS, Rank.EIGHT),
                new Card(Suit.SPADES, Rank.TWO),
                new Card(Suit.CLUBS, Rank.QUEEN)
        ));
        List<Card> cards2 = new ArrayList<>(List.of(
                new Card(Suit.HEARTS, Rank.EIGHT),
                new Card(Suit.HEARTS, Rank.EIGHT),
                new Card(Suit.CLUBS, Rank.JACK),
                new Card(Suit.DIAMONDS, Rank.QUEEN),
                new Card(Suit.HEARTS, Rank.FIVE)
        ));
        Hand hand1 = new Hand(cards1, 21);
        Hand hand2 = new Hand(cards2, 1);

        assertEquals( -1, hand1.compareTo(hand2));
    }
}