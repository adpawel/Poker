package pl.edu.agh.kis.pz1.player_utils;
import lombok.Data;
import lombok.Getter;
import pl.edu.agh.kis.pz1.card_utils.Card;
import pl.edu.agh.kis.pz1.card_utils.Rank;
import pl.edu.agh.kis.pz1.card_utils.Suit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Klasa Hand reprezentuje rękę gracza w grze pokerowej, zawierającą pięć kart. Zawiera metody do obliczania rankingu ręki,
 * porównywania jej z innymi rękami oraz sprawdzania, jakie kombinacje kart są obecne w ręce (np. pary, strity, full house).
 *
 * <p>Ręka gracza jest tworzona na podstawie listy kart (obiektów typu Card) i przypisanego identyfikatora gracza.
 * Klasa implementuje interfejs {@link Comparable} pozwalający na porównanie rąk w celu określenia zwycięzcy.
 *
 * <p>Ręka gracza może zawierać różne kombinacje kart, a jej ranking jest obliczany na podstawie tych kombinacji.
 * Klasa oferuje również metody umożliwiające porównywanie rąk pod kątem siły ich kombinacji oraz wyłonienie zwycięzcy.
 *
 * @see Card
 * @see Rank
 * @see Suit
 */
@Data
public class Hand implements Comparable<Hand> {
    private List<Card> sortedCards;
    private List<Rank> ranks;
    private List<Suit> suits;
    private int playerId;

    /**
     * Konstruktor klasy Hand.
     * Inicjalizuje listy kart, rang i kolorów oraz sortuje karty w ręce.
     *
     * @param inputCards Lista kart gracza.
     * @param playerId ID gracza.
     */
    public Hand(List<Card> inputCards, int playerId) {
        sortedCards = new ArrayList<>(inputCards);
        ranks = new ArrayList<>();
        suits = new ArrayList<>();
        Collections.sort(sortedCards);
        for(Card c: sortedCards){
            ranks.add(c.getRank());
            suits.add(c.getSuit());
        }
        this.playerId = playerId;
    }

    /**
     * Metoda oblicza ranking ręki gracza na podstawie kombinacji kart.
     *
     * @return Wartość rankingowa ręki gracza.
     */
    public int calculateRanking(){
        if(checkRoyalFlush()){
            return Combination.ROYAL_FLUSH.getValue();
        } else if(checkStraightFlush()){
            return Combination.STRAIGHT_FLUSH.getValue();
        } else if(checkFour()){
            return Combination.FOUR.getValue();
        } else if(checkFull()){
            return Combination.FULL.getValue();
        } else if(checkFlush()){
            return Combination.FLUSH.getValue();
        } else if(checkStraight()){
            return Combination.STRAIGHT.getValue();
        } else if(checkThree()){
            return Combination.THREE.getValue();
        } else if(checkTwoPairs()){
            return Combination.TWO_PAIR.getValue();
        } else if(checkPair()){
            return Combination.PAIR.getValue();
        } else{
            return 0;
        }
    }

    /**
     * Sprawdza, czy karty w ręce są tego samego koloru.
     *
     * @return true, jeśli wszystkie karty mają ten sam kolor, false w przeciwnym przypadku.
     */
    private boolean onlyOneSuit(){
        Suit suit = suits.get(0);
        for(Suit s: suits){
            if(!s.equals(suit)){
                return false;
            }
        }
        return true;
    }

    /**
     * Sprawdza, czy karty w ręce są w porządku rosnącym (kolejność rang kart).
     *
     * @return true, jeśli karty są w kolejności rosnącej, false w przeciwnym przypadku.
     */
    private boolean checkConsecutiveRanks(){
        int i = 0;
        Rank previous = ranks.get(0);
        for(Rank r: ranks){
            if(i != 0 && r.getValue() != previous.getValue() + 1){
                return false;
            }
            previous = r;
            ++i;
        }
        return true;
    }

    /**
     * Sprawdza, czy ręka gracza to Poker Królewski.
     *
     * @return true, jeśli ręka to Poker Królewski, false w przeciwnym przypadku.
     */
    private boolean checkRoyalFlush(){
        return onlyOneSuit() && ranks.contains(Rank.ACE) && ranks.contains(Rank.QUEEN)
                && ranks.contains(Rank.JACK) && ranks.contains(Rank.TEN) && ranks.contains(Rank.KING);
    }

    /**
     * Sprawdza, czy ręka gracza to Kolor Strit.
     *
     * @return true, jeśli ręka to Kolor Strit, false w przeciwnym przypadku.
     */
    private boolean checkStraightFlush(){
        return onlyOneSuit() && checkConsecutiveRanks();
    }

    /**
     * Sprawdza, czy ręka gracza to Czwórka.
     *
     * @return true, jeśli ręka to Czwórka, false w przeciwnym przypadku.
     */
    private boolean checkFour() {
        return ranks.stream().distinct().anyMatch(rank -> Collections.frequency(ranks, rank) == 4);
    }

    /**
     * Sprawdza, czy ręka gracza to Full.
     *
     * @return true, jeśli ręka to Full, false w przeciwnym przypadku.
     */
    private boolean checkFull(){
        return checkThree() && checkPair();
    }

    /**
     * Sprawdza, czy ręka gracza to Kolor.
     *
     * @return true, jeśli ręka to Kolor, false w przeciwnym przypadku.
     */
    private boolean checkFlush(){
        return onlyOneSuit();
    }

    /**
     * Sprawdza, czy ręka gracza to Strit.
     *
     * @return true, jeśli ręka to Strit, false w przeciwnym przypadku.
     */
    private boolean checkStraight(){
        return checkConsecutiveRanks();
    }

    /**
     * Sprawdza, czy ręka gracza to Trójka.
     *
     * @return true, jeśli ręka to Trójka, false w przeciwnym przypadku.
     */
    private boolean checkThree() {
        return ranks.stream().distinct().anyMatch(rank -> Collections.frequency(ranks, rank) == 3);
    }

    /**
     * Sprawdza, czy ręka gracza to Dwie Pary.
     *
     * @return true, jeśli ręka to Dwie Pary, false w przeciwnym przypadku.
     */
    private boolean checkTwoPairs(){
        int count = 0;
        for(Rank r: ranks){
            if(Collections.frequency(ranks, r) == 2){
                count++;
            }
        }
        return count == 4;
    }

    /**
     * Sprawdza, czy ręka gracza to Para.
     *
     * @return true, jeśli ręka to Para, false w przeciwnym przypadku.
     */
    private boolean checkPair() {
        return ranks.stream().distinct().anyMatch(rank -> Collections.frequency(ranks, rank) == 2);
    }

    /**
     * Porównuje dwie ręce pod względem rankingu.
     *
     * @param h Druga ręka do porównania.
     * @return 1, jeśli pierwsza ręka jest silniejsza, -1, jeśli druga ręka jest silniejsza, 0, jeśli ręce są równe.
     */
    @Override
    public int compareTo(Hand h) {
        int ranking = this.calculateRanking();
        int otherRanking = h.calculateRanking();

        if (ranking != otherRanking) {
            return Integer.compare(ranking, otherRanking);
        }

        return switch (ranking) {
            case 9 -> // Royal Flush
                    0; // Remis w Royal Flush
            case 8 -> // Straight Flush  nie git
                    compareHighCard(this.ranks, h.ranks);
            case 7 -> // Four of a Kind
                    compareFour(h);
            case 6 -> // Full House
                    compareFullHouse(h);
            case 5 -> // Flush
                    compareHighCard(this.ranks, h.ranks);
            case 4 -> // Straight
                    compareStraight(h);
            case 3 -> // Three of a Kind
                    compareSameRankHands(h, 3);
            case 2 -> // Two Pair
                    compareTwoPairs(h);
            case 1 -> // One Pair
                    compareSameRankHands(h, 2);
            default -> // High Card
                    compareHighCard(this.ranks, h.ranks);
        };
    }

    /**
     * Porównuje dwie ręce graczy, które mają kombinację czterech kart o tej samej wartości (Four of a Kind).
     * Jeśli obie ręce zawierają cztery karty o tej samej wartości, metoda porównuje te wartości.
     * W przypadku remisu (równe wartości czterech kart), porównywane są tzw. "kicker'y" (pozostałe karty, które nie tworzą pary).
     *
     * @param h Druga ręka, która jest porównywana z obecną ręką.
     * @return Zwraca dodatnią wartość, jeśli pierwsza ręka ma wyższą wartość czterech kart,
     *         ujemną wartość, jeśli druga ręka ma wyższą, lub wynik porównania kickerów w przypadku remisu.
     */
    private int compareFour(Hand h){
        Rank rank1 = null;
        Rank rank2 = null;
        for(Rank r : this.getRanks()){
            if(Collections.frequency(this.getRanks(), r) == 4) { rank1 = r; }
        }
        for(Rank r : h.getRanks()){
            if(Collections.frequency(h.getRanks(), r) == 4) { rank2 = r; }
        }
        assert rank1 != null;
        assert rank2 != null;
        if(rank1.getValue() != rank2.getValue()){ return Integer.compare(rank1.getValue(), rank2.getValue()); }

        return compareKickers(this.getRanks(), h.getRanks(), rank1, rank2);
    }

    /**
     * Porównuje dwie ręce graczy na podstawie najwyższej karty (tzw. "High Card").
     * Metoda iteruje po kartach w rękach graczy, porównując je od najwyższej do najniższej.
     * Jeśli jedna z rąk ma wyższą kartę, zostaje zwrócony wynik porównania.
     * W przypadku remisu (równe wartości kart), metoda zwraca 0, co oznacza remis.
     *
     * @param ranks1 Lista rang kart pierwszego gracza (posortowana malejąco).
     * @param ranks2 Lista rang kart drugiego gracza (posortowana malejąco).
     * @return Zwraca dodatnią wartość, jeśli pierwsza ręka ma wyższą kartę,
     *         ujemną wartość, jeśli druga ręka ma wyższą kartę, lub 0 w przypadku remisu.
     */
    private int compareHighCard(List<Rank> ranks1, List<Rank> ranks2) {
        for(int i = ranks1.size() - 1; i >= 0; --i){
            if(ranks1.get(i).getValue() != ranks2.get(i).getValue()){
                return Integer.compare(ranks1.get(i).getValue(), ranks2.get(i).getValue());
            }
        }
        return 0;
    }

    /**
     * Porównuje dwie ręce graczy w przypadku, gdy obie mają kombinację "strita" (Straight).
     * Porównanie opiera się na wartości najwyższej karty w ręce, ponieważ w stricie liczy się tylko sekwencja kart.
     *
     * @param h Druga ręka, która jest porównywana z obecną ręką.
     * @return Zwraca dodatnią wartość, jeśli pierwsza ręka ma wyższą kartę w stricie,
     *         ujemną wartość, jeśli druga ręka ma wyższą kartę w stricie, lub 0 w przypadku remisu.
     */
    private int compareStraight(Hand h){
        return Integer.compare(this.getRanks().get(0).getValue(), h.getRanks().get(0).getValue());
    }

    /**
     * Porównuje dwie ręce graczy, które mają tą samą liczbę kart o określonej częstotliwości (np. trójki, pary).
     * Metoda szuka karty, która występuje określoną liczbę razy w ręce (określoną przez `targetFrequency`),
     * a następnie porównuje jej wartość. W przypadku remisu porównywane są pozostałe karty (tzw. "kicker'y").
     *
     * @param h Druga ręka, która jest porównywana z obecną ręką.
     * @param targetFrequency Częstotliwość kart, które są porównywane (np. 3 dla trójki, 2 dla pary).
     * @return Zwraca dodatnią wartość, jeśli pierwsza ręka ma wyższą kartę o danej częstotliwości,
     *         ujemną wartość, jeśli druga ręka ma wyższą kartę, lub wynik porównania kickerów w przypadku remisu.
     */
    private int compareSameRankHands(Hand h, int targetFrequency) {
        Rank rank1 = null;
        Rank rank2 = null;
        for(Rank r : this.getRanks()){
            if(Collections.frequency(this.getRanks(), r) == targetFrequency) { rank1 = r; }
        }
        for(Rank r : h.getRanks()){
            if(Collections.frequency(h.getRanks(), r) == targetFrequency) { rank2 = r; }
        }
        assert rank2 != null;
        assert rank1 != null;
        if(rank1.getValue() != rank2.getValue()){ return Integer.compare(rank1.getValue(), rank2.getValue()); }

        return compareKickers(this.getRanks(), h.getRanks(), rank1, rank2);
    }

    /**
     * Porównuje dwie ręce graczy, które mają kombinację Full House.
     * Full House składa się z trzech kart o tej samej wartości (tzw. "trójka") oraz pary kart o tej samej wartości.
     * Pierwszym krokiem porównania jest porównanie wartości trójek, a jeśli są one takie same, porównywana jest wartość pary.
     *
     * @param h Druga ręka, która jest porównywana z obecną ręką.
     * @return Zwraca dodatnią wartość, jeśli pierwsza ręka ma wyższą trójkę lub wyższą parę,
     *         ujemną wartość, jeśli druga ręka ma wyższą trójkę lub parę, lub 0 w przypadku remisu.
     */
    private int compareFullHouse(Hand h) {
        Rank thisThree = ranks.stream()
                .filter(rank -> Collections.frequency(ranks, rank) == 3)
                .findFirst().orElse(null);

        Rank otherThree = h.getRanks().stream()
                .filter(rank -> Collections.frequency(h.getRanks(), rank) == 3)
                .findFirst().orElse(null);

        assert thisThree != null;
        assert otherThree != null;
        int cmp = Integer.compare(thisThree.getValue(), otherThree.getValue());
        if (cmp != 0) return cmp;

        Rank thisPair = ranks.stream()
                .filter(rank -> Collections.frequency(ranks, rank) == 2)
                .findFirst().orElse(null);

        Rank otherPair = h.getRanks().stream()
                .filter(rank -> Collections.frequency(h.getRanks(), rank) == 2)
                .findFirst().orElse(null);

        assert thisPair != null;
        assert otherPair != null;
        return Integer.compare(thisPair.getValue(), otherPair.getValue());
    }

    /**
     * Porównuje dwie ręce graczy, które mają kombinację Two Pair (dwie pary).
     * Pierwszym krokiem porównania jest porównanie wartości wyższej pary. Jeśli są one równe,
     * porównywana jest wartość niższej pary. Jeśli nadal są równe, porównywane są tzw. "kicker'y" (pozostałe karty bez pary).
     *
     * @param h Druga ręka, która jest porównywana z obecną ręką.
     * @return Zwraca dodatnią wartość, jeśli pierwsza ręka ma wyższą parę, wyższą niższą parę lub wyższy kicker,
     *         ujemną wartość, jeśli druga ręka ma wyższą parę, wyższą niższą parę lub wyższy kicker,
     *         lub 0 w przypadku remisu.
     */
    private int compareTwoPairs(Hand h) {
        // Znajdź wyższe pary
        Rank rank1High = findPairRank(this, true);
        Rank rank2High = findPairRank(h, true);

        // Porównaj wyższe pary
        int result = Integer.compare(rank1High.getValue(), rank2High.getValue());
        if (result != 0) {
            return result;
        }

        // Znajdź niższe pary
        Rank rank1Low = findPairRank(this, false);
        Rank rank2Low = findPairRank(h, false);
        result = Integer.compare(rank1Low.getValue(), rank2Low.getValue());
        if (result != 0) {
            return result;
        }

        // Znajdź "kicker'y" (pozostałe karty bez pary)
        Rank kicker1 = findKicker(this);
        Rank kicker2 = findKicker(h);
        assert kicker2 != null;
        assert kicker1 != null;
        return Integer.compare(kicker1.getValue(), kicker2.getValue());
    }

    /**
     * Znajduje parę w ręce. Można wskazać, czy interesuje nas najwyższa para, czy najniższa.
     * Jeśli parametr `highest` jest ustawiony na `true`, metoda zwróci najwyższą parę w ręce.
     * Jeśli parametr `highest` jest ustawiony na `false`, metoda zwróci najniższą parę w ręce.
     *
     * @param hand Ręka gracza, w której szukamy pary.
     * @param highest Flaga wskazująca, czy szukamy najwyższej (true) czy najniższej (false) pary.
     * @return Zwraca obiekt `Rank`, który reprezentuje znalezioną parę, lub `null`, jeśli para nie została znaleziona.
     */
    private Rank findPairRank(Hand hand, boolean highest) {
        List<Rank> ranks1 = hand.getRanks();
        Rank pair = null;

        // Przechodzimy przez posortowaną listę i szukamy par
        for (int i = ranks1.size() - 1; i > 0; i--) {
            if (ranks1.get(i).equals(ranks1.get(i - 1))) {
                pair = ranks1.get(i);
                if (highest) {
                    return pair;
                }
                break;
            }
        }

        if (!highest) {
            // Jeśli szukamy najniższej pary, musimy przejść po liście od początku
            for (int i = 0; i < ranks1.size() - 1; i++) {
                if (ranks1.get(i).equals(ranks1.get(i + 1))) {
                    pair = ranks1.get(i);
                    break;
                }
            }
        }
        return pair;
    }

    /**
     * Znajduje kartę kicker (kartę, która nie jest częścią pary) w ręce gracza.
     * Kicker to karta, która nie tworzy pary, trójki, czwórki ani innej kombinacji w ręce,
     * ale jest istotna w przypadku porównań rąk, które mają te same kombinacje (np. para).
     * Metoda przechodzi przez listę rang kart w ręce i zwraca pierwszą kartę, która nie ma częstotliwości 2 (czyli nie jest częścią pary).
     * Jeśli wszystkie karty są częścią pary, zwraca `null`.
     *
     * @param hand Ręka gracza, w której szukamy kicker'a.
     * @return Zwraca obiekt `Rank`, który reprezentuje kicker, lub `null`, jeśli kicker nie został znaleziony.
     */
    private Rank findKicker(Hand hand) {
        List<Rank> ranks1 = hand.getRanks();

        for (int i = 0; i < ranks1.size(); i++) {
            if (Collections.frequency(ranks1, ranks1.get(i)) != 2) {
                return ranks1.get(i);
            }
        }
        return null; // Jeśli nie znaleziono "kicker'a"
    }

    /**
     * Porównuje karty z "kickerem" (kartą, która nie tworzy pary) w przypadku równości kombinacji.
     *
     * @param ranks1 Karty pierwszej ręki.
     * @param ranks2 Karty drugiej ręki.
     * @param excluded1 Para wykluczona z porównania.
     * @param excluded2 Para wykluczona z porównania.
     * @return Porównanie kickerów.
     */
    private int compareKickers(List<Rank> ranks1, List<Rank> ranks2, Rank excluded1, Rank excluded2) {
        List<Rank> kickers1 = new ArrayList<>(ranks1);
        List<Rank> kickers2 = new ArrayList<>(ranks2);

        if (excluded1 != null) kickers1.removeIf(rank -> rank.equals(excluded1));
        if (excluded2 != null) kickers2.removeIf(rank -> rank.equals(excluded2));

        return compareHighCard(kickers1, kickers2);
    }
}


/**
 * Enum Combination reprezentuje różne kombinacje kart w grze pokerowej oraz przypisane do nich wartości.
 * Każda kombinacja ma przypisaną wartość, która jest używana do oceny siły kombinacji w grze.
 *
 * <p>Enum zawiera następujące kombinacje, zaczynając od najwyższej (ROYAL_FLUSH) do najniższej (PAIR):
 * - ROYAL_FLUSH (9)
 * - STRAIGHT_FLUSH (8)
 * - FOUR (7)
 * - FULL (6)
 * - FLUSH (5)
 * - STRAIGHT (4)
 * - THREE (3)
 * - TWO_PAIR (2)
 * - PAIR (1)
 *
 * <p>Wartości kombinacji są używane do porównywania rąk graczy w celu określenia zwycięzcy.
 *
 * @see Hand
 */
@Getter
enum Combination {
    ROYAL_FLUSH(9),
    STRAIGHT_FLUSH(8),
    FOUR(7),
    FULL(6),
    FLUSH(5),
    STRAIGHT(4),
    THREE(3),
    TWO_PAIR(2),
    PAIR(1);

    // Getter do pobierania wartości
    private final int value;

    /**
     * Konstruktor przypisujący wartość do kombinacji.
     *
     * @param value Wartość przypisana do kombinacji.
     */
    Combination(int value) {
        this.value = value;
    }
}
