package pl.edu.agh.kis.pz1.commands;

import lombok.Data;
import pl.edu.agh.kis.pz1.Game;
import pl.edu.agh.kis.pz1.player_utils.Player;

import java.util.List;
import java.util.Objects;

import static pl.edu.agh.kis.pz1.Constants.NOT_DRAW_ROUND;

/**
 * Klasa Stand reprezentuje komendę, która pozwala graczowi na wykonanie ruchu 'stand' w trakcie gry.
 * Dziedziczy po klasie Command i implementuje metodę processCommand, która obsługuje logikę tej akcji.
 *
 * <p>Ta komenda przetwarza akcję gracza w trakcie fazy "draw" (dobieranie kart) gry.
 * Jeśli gra nie znajduje się w fazie "draw", zwrócony zostanie komunikat o błędzie.
 * W przeciwnym przypadku, akcja gracza jest rejestrowana, a zwracany jest komunikat o powodzeniu.
 *
 * @see Command
 * @see Game
 * @see Player
 */
@Data
public class Stand extends Command  {

    /**
     * Przetwarza komendę 'stand' dla gracza w trakcie fazy "draw" gry.
     *
     * @param game Obecna instancja gry, zawierająca dane o fazach i stanie gry.
     * @param player Gracz, który wykonuje akcję 'stand'.
     * @param params Lista parametrów, chociaż w tej komendzie nie jest używana.
     * @return Komunikat w postaci napisu, informujący o wyniku wykonania komendy (sukces lub błąd).
     */
    @Override
    public String processCommand(Game game, Player player, List<String> params) {
        if(!Objects.equals(game.getPhase(), "draw")){
            return NOT_DRAW_ROUND;
        }else{
            player.setPlayed(true);
            setSucceeded(true);
            return "Pomyślnie dokonałeś 'stand'";
        }
    }
}
