package pl.edu.agh.kis.pz1.commands;

import lombok.Data;
import pl.edu.agh.kis.pz1.Game;
import pl.edu.agh.kis.pz1.player_utils.Player;

import java.util.List;
import java.util.Objects;

import static pl.edu.agh.kis.pz1.Constants.ALREADY_FOLD;
import static pl.edu.agh.kis.pz1.Constants.NOT_BET_ROUND;

/**
 * Klasa Fold reprezentuje komendę pasowania przez gracza podczas rundy licytacji w grze.
 * Pasowanie oznacza, że gracz rezygnuje z dalszego udziału w bieżącej rozgrywce.
 */
@Data
public class Fold extends Command  {

    /**
     * Przetwarza komendę pasowania gracza.
     *
     * @param game   Obiekt reprezentujący aktualny stan gry.
     * @param player Gracz wykonujący pas.
     * @param params Lista parametrów komendy (w tym przypadku pusta lub z ignorowanymi wartościami).
     * @return Wynik operacji:
     *         - "Pomyślnie spasowałeś!" w przypadku powodzenia,
     *         - Komunikaty o błędach, jeśli:
     *           - faza gry nie pozwala na pasowanie (`NOT_BET_ROUND`),
     *           - gracz już wcześniej spasował (`ALREADY_FOLD`).
     */
    @Override
    public String processCommand(Game game, Player player, List<String> params){
        if(!Objects.equals(game.getPhase(), "bet")){
            return NOT_BET_ROUND;
        }
        else if(!player.isActive()){
            return ALREADY_FOLD;
        }
        player.setPlayed(true);
        game.setCheckCount(0);
        player.setActive(false);
        setSucceeded(true);
        return "Pomyślnie spasowałeś!";
    }
}
