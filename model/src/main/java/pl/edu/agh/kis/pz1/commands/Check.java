package pl.edu.agh.kis.pz1.commands;

import lombok.Data;
import pl.edu.agh.kis.pz1.Game;
import pl.edu.agh.kis.pz1.player_utils.Player;

import java.util.List;
import java.util.Objects;

import static pl.edu.agh.kis.pz1.Constants.*;

/**
 * Klasa Check reprezentuje komendę wykonania ruchu "check" w fazie licytacji.
 * Dziedziczy po klasie Command i implementuje metodę processCommand,
 * która sprawdza, czy gracz może wykonać ruch "check" i przetwarza tę akcję.
 */
@Data
public class Check extends Command {

    /**
     * Przetwarza komendę "check" od gracza.
     *
     * @param game   Obiekt reprezentujący aktualny stan gry.
     * @param player Gracz, który wysłał komendę.
     * @param params Lista parametrów przekazanych do komendy (nieużywana w tej implementacji).
     * @return Wiadomość informująca o wyniku wykonania komendy:
     *         - "Pomyślnie wykonano ruch: 'check'" jeśli akcja się powiodła,
     *         - lub odpowiedni komunikat błędu, jeśli wykonanie komendy było niemożliwe.
     */
    @Override
    public String processCommand(Game game, Player player, List<String> params) {
        if(!Objects.equals(game.getPhase(), "bet")){
            return NOT_BET_ROUND;
        } else if(!player.isActive()){
            return ALREADY_FOLD;
        } else if(game.getCurrentMaxBet() - player.getContributedChips() > 0){
            return EXISTS_BET_TO_CALL;
        }
        else{
            player.setPlayed(true);
            game.setCheckCount(game.getCheckCount() + 1);
            setSucceeded(true);
            return "Pomyślnie wykonano ruch: 'check'";
        }
    }
}
