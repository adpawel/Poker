package pl.edu.agh.kis.pz1.player_utils;

import lombok.Data;
import pl.edu.agh.kis.pz1.Game;
import pl.edu.agh.kis.pz1.card_utils.Card;

import java.nio.channels.SocketChannel;
import java.util.List;

import static pl.edu.agh.kis.pz1.Constants.NUMBER_OF_CHIPS;


/**
 * Klasa Player reprezentuje gracza w grze. Zawiera informacje o jego stanie, kartach, żetonach
 * oraz stanie aktywności. Obsługuje również metody umożliwiające zarządzanie kartami i resetowanie stanu gracza.
 *
 * <p>Każdy gracz posiada przypisaną liczbę żetonów (numberOfChips), kanał komunikacji (channel),
 * unikalny identyfikator (playerId), rękę kart (cards) oraz inne informacje dotyczące aktywności gracza
 * w trakcie gry (remainingChips, contributedChips, isActive, played). Klasa umożliwia także resetowanie
 * stanu gracza na początku każdej rundy.
 *
 * @see Card
 * @see Hand
 * @see Game
 */
@Data
public class Player {
    private final SocketChannel channel;
    private int playerId;
    private List<Card> cards;
    private int remainingChips;
    private int contributedChips;
    private boolean isActive;
    private boolean played;
    private Hand hand;
    private Game game;

    /**
     * Konstruktor bezargumentowy - tworzy gracza bez przypisanego kanału komunikacji.
     */
    public Player(){
        channel = null;
    }

    /**
     * Konstruktor z przypisanym kanałem komunikacyjnym. Inicjalizuje liczbę żetonów, stan aktywności gracza
     * oraz inne zmienne związane ze stanem gry.
     *
     * @param channel Kanał komunikacji, przez który gracz będzie połączony z grą.
     */
    public Player(SocketChannel channel) {
        this.channel = channel;
        this.remainingChips = NUMBER_OF_CHIPS;
        this.contributedChips = 0;
        this.isActive = true;
        played = false;
    }

    /**
     * Zwraca karty gracza w postaci łańcucha znaków.
     *
     * @return Łańcuch znaków przedstawiający karty gracza.
     */
    public String printCards(){
        StringBuilder message = new StringBuilder();
        for(Card card : cards){
            message.append(card.toString()).append("\n");
        }
        return message.toString();
    }

    /**
     * Resetuje stan gracza, przywracając początkową liczbę żetonów, wyzerowując wkład w grze
     * oraz ustawiając gracza jako aktywnego i niegrającego.
     */
    public void reset() {
        remainingChips = NUMBER_OF_CHIPS;
        contributedChips = 0;
        isActive = true;
        played = false;
    }
}
