package pl.edu.agh.kis.pz1.commands;

import lombok.Data;
import pl.edu.agh.kis.pz1.Game;
import pl.edu.agh.kis.pz1.player_utils.Player;

import java.util.List;
import java.util.Objects;

import static pl.edu.agh.kis.pz1.Constants.*;

/**
 * Klasa Raise reprezentuje komendę, która pozwala graczowi na podbicie zakładu w trakcie gry.
 * Dziedziczy po klasie Command i implementuje metodę processCommand, która obsługuje logikę tej akcji.
 *
 * <p>Komenda ta przetwarza akcję gracza podczas rundy "bet" (stawianie zakładów).
 * Zanim podbicie zostanie zaakceptowane, sprawdzane są różne warunki, takie jak:
 * czy gra jest w fazie "bet", czy gracz jest aktywny, czy gracz ma wystarczającą ilość żetonów,
 * oraz czy proponowane podbicie jest większe niż obecny maksymalny zakład.
 * Jeśli wszystkie warunki są spełnione, podbicie jest realizowane, a stan gry i gracza jest odpowiednio aktualizowany.
 *
 * @see Command
 * @see Game
 * @see Player
 */
@Data
public class Raise extends Command  {

    /**
     * Przetwarza komendę 'raise' (podbicie zakładu) dla gracza podczas rundy "bet".
     *
     * @param game Obecna instancja gry, zawierająca dane o fazach, zakładach i stanie gry.
     * @param player Gracz, który wykonuje akcję podbicia zakładu.
     * @param params Lista parametrów, gdzie trzeci parametr (params.get(2)) zawiera wartość podbicia.
     * @return Komunikat w postaci napisu, informujący o wyniku wykonania komendy (sukces lub błąd).
     */
    @Override
    public String processCommand(Game game, Player player, List<String> params){
        try{
            int number = Integer.parseInt(params.get(2));
            if(!Objects.equals(game.getPhase(), "bet")){
                return NOT_BET_ROUND;
            } else if(!player.isActive()){
                return ALREADY_FOLD;
            } else if(number > player.getRemainingChips()){
                return NOT_ENOUGH_CHIPS;
            }
            else if(player.getContributedChips() + number <= game.getCurrentMaxBet()){
                return "Podana liczba żetonów dodana do twojego wkładu("+ player.getContributedChips()+ ") musi być większa niż obecny zakład (" + game.getCurrentMaxBet() + ")";
            }
            else{
                player.setPlayed(true);
                game.setCheckCount(0);
                game.setCurrentMaxBet(player.getContributedChips() + number);
                player.setRemainingChips(player.getRemainingChips() - number);
                game.setPot(game.getPot() + number);
                player.setContributedChips(player.getContributedChips() + number);
                // resetowanie pasywnych ruchów
                setSucceeded(true);
                return "Pomyślnie dokonałeś 'raise'";
            }
        } catch (Exception e) {
            return "Nieprawidłowy format parametrów. Spróbuj ponownie.\n";
        }
    }
}
