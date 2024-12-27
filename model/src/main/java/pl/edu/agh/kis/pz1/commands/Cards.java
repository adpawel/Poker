package pl.edu.agh.kis.pz1.commands;

import pl.edu.agh.kis.pz1.Game;
import pl.edu.agh.kis.pz1.player_utils.Player;

import java.util.List;

/**
 * Klasa Cards reprezentuje komendę wyświetlania kart gracza.
 * Dziedziczy po klasie Command i implementuje metodę processCommand,
 * która zwraca listę kart należących do gracza.
 */
public class Cards extends Command {

    /**
     * Przetwarza komendę wyświetlenia kart gracza.
     *
     * @param game   Obiekt reprezentujący aktualny stan gry.
     * @param player Gracz, który wysłał komendę.
     * @param params Lista parametrów przekazanych do komendy (nieużywana w tej implementacji).
     * @return Ciąg znaków zawierający listę kart posiadanych przez gracza.
     */
    @Override
    public String processCommand(Game game, Player player, List<String> params) {
        return player.printCards();
    }
}
