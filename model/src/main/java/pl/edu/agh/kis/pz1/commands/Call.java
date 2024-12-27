package pl.edu.agh.kis.pz1.commands;

import lombok.Data;
import pl.edu.agh.kis.pz1.Game;
import pl.edu.agh.kis.pz1.player_utils.Player;

import java.util.List;
import java.util.Objects;

import static pl.edu.agh.kis.pz1.Constants.*;

/**
 * Klasa reprezentująca komendę "Call" w grze.
 * <p>
 * Komenda pozwala graczowi wyrównać aktualny najwyższy zakład w rundzie obstawiania.
 * W przypadku powodzenia, gracz dokłada brakującą ilość żetonów do puli gry.
 */
@Data
public class Call extends Command  {

    /**
     * Przetwarza komendę "Call" w kontekście gry.
     * <p>
     * Metoda sprawdza, czy gracz może wyrównać aktualny najwyższy zakład,
     * uwzględniając jego status w grze, pozostałe żetony oraz stan puli zakładów.
     * W przypadku powodzenia, aktualizuje stan żetonów gracza, jego wkład w pulę
     * oraz pulę gry.
     *
     * @param game   obiekt gry, w której ma być wykonany "Call"
     * @param player gracz, który wykonuje komendę "Call"
     * @param params lista parametrów komendy (nieużywana w tej implementacji)
     * @return komunikat o wyniku wykonania komendy, np. sukces lub błąd
     */
    @Override
    public String processCommand(Game game, Player player, List<String> params) {
        if(!Objects.equals(game.getPhase(), "bet")){
            return NOT_BET_ROUND;
        } else if(!player.isActive()){
            return ALREADY_FOLD;
        } else if(game.getCurrentMaxBet() - player.getContributedChips() > player.getRemainingChips()){
            return NOT_ENOUGH_CHIPS;
        } else if(game.getCurrentMaxBet() == player.getContributedChips()){
            return NO_BET_TO_CALL;
        } else {
            player.setPlayed(true);
            game.setCheckCount(0);
            player.setRemainingChips(player.getRemainingChips() - game.getCurrentMaxBet() + player.getContributedChips());
            game.setPot(game.getPot() + game.getCurrentMaxBet() - player.getContributedChips());
            player.setContributedChips(game.getCurrentMaxBet());
            setSucceeded(true);
            return "Pomyślnie dokonałeś 'call'";
        }
    }
}
