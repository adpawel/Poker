package pl.edu.agh.kis.pz1.commands;

import lombok.Data;
import pl.edu.agh.kis.pz1.Game;
import pl.edu.agh.kis.pz1.card_utils.Card;
import pl.edu.agh.kis.pz1.card_utils.Deck;
import pl.edu.agh.kis.pz1.player_utils.Player;

import java.util.List;
import java.util.Objects;

import static java.lang.Math.min;
import static pl.edu.agh.kis.pz1.Constants.NOT_DRAW_ROUND;
import static pl.edu.agh.kis.pz1.Constants.WRONG_INDEX;

/**
 * Klasa Exchange reprezentuje komendę wymiany kart przez gracza podczas rundy wymiany w grze.
 * Gracz wskazuje indeksy kart do wymiany, które są zastępowane nowymi kartami z talii gry.
 * <p>
 * Metoda `processCommand` obsługuje wymianę kart oraz aktualizuje stan gracza i talii gry.
 */
@Data
public class Exchange extends Command  {

/**
 * Przetwarza komendę wymiany kart gracza.
 *
 * @param game   Obiekt reprezentujący aktualny stan gry.
 * @param player Gracz wykonujący wymianę kart.
 * @param params Lista parametrów komendy, w tym indeksy kart do wymiany
 *               (numeracja od 1, kolejne parametry w liście).
 * @return Wynik operacji:
 *         - Komunikat o sukcesie, jeśli wymiana przebiegła poprawnie,
 *         - Komunikaty o błędach, jeśli:
 *           - wymiana jest niedozwolona w obecnej fazie gry (`NOT_DRAW_ROUND`),
 *           - podano niepoprawne indeksy kart (`WRONG_INDEX`).
 */
    @Override
    public String processCommand(Game game, Player player, List<String> params) {
        if(!Objects.equals(game.getPhase(), "draw")){
            return NOT_DRAW_ROUND;
        }else{
            int cardIndex;
            List<Card> playersCards = player.getCards();
            for(int i = 2; i < min(8, params.size()); i++) {
                if(!params.get(i).equals(" ")){
                    System.out.println(player.printCards());
                    cardIndex = Integer.parseInt(params.get(i)) - 1;
                    if (cardIndex < 0 || cardIndex >= playersCards.size()) {
                        return WRONG_INDEX;
                    }
                    Deck deck = game.getDeck();
                    List<Card> removedCards = deck.getRemovedCards();
                    removedCards.add(playersCards.remove(cardIndex));
                    deck.setRemovedCards(removedCards);
                    game.setDeck(deck);
                    playersCards.add(cardIndex, game.getDeck().dealCard());
                    player.setCards(playersCards);
                }
            }
            game.sendMessage(player.getChannel(), player.printCards());
            player.setPlayed(true);
            setSucceeded(true);
            return "Pomyślnie dokonałeś wymiany kart!";
        }

    }
}
