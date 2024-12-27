package pl.edu.agh.kis.pz1.commands;


import pl.edu.agh.kis.pz1.Game;
import pl.edu.agh.kis.pz1.player_utils.Player;

import java.util.List;

/**
 * Interfejs CommandInterface definiuje metodę `processCommand`,
 * która jest implementowana przez klasy reprezentujące różne komendy w grze.
 * Umożliwia obsługę interakcji między graczem a stanem gry w sposób zdefiniowany przez konkretne komendy.
 */
public interface CommandInterface {
    /**
     * Przetwarza komendę w kontekście bieżącej gry i gracza.
     *
     * @param game   Obiekt reprezentujący aktualny stan gry.
     * @param player Gracz, który wysłał komendę.
     * @param params Lista parametrów przekazanych do komendy (mogą zawierać np. wartości stawek, indeksy kart itp.).
     * @return Wynik przetworzenia komendy w postaci tekstowej wiadomości:
     *         - komunikaty o powodzeniu,
     *         - błędy, jeśli wykonanie komendy było niemożliwe.
     */
    String processCommand(Game game, Player player, List<String> params);
}
