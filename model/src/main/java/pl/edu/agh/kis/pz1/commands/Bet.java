package pl.edu.agh.kis.pz1.commands;

import lombok.Data;
import pl.edu.agh.kis.pz1.Game;
import pl.edu.agh.kis.pz1.player_utils.Player;

import java.util.List;
import java.util.Objects;

import static pl.edu.agh.kis.pz1.Constants.*;

/**
 * Klasa reprezentująca komendę zakładu (Bet) w grze.
 * <p>
 * Komenda pozwala graczowi na obstawienie zakładu w rundzie obstawiania,
 * pod warunkiem spełnienia odpowiednich warunków (np. aktywności gracza,
 * dostępności żetonów, właściwej fazy gry).
 */
@Data
public class Bet extends Command {

    /**
     * Przetwarza komendę zakładu w kontekście gry.
     * <p>
     * Metoda sprawdza, czy możliwe jest wykonanie zakładu w aktualnym stanie gry,
     * i aktualizuje odpowiednie dane (np. żetony gracza, pule gry, maksymalny zakład).
     * Jeśli zakład jest niepoprawny, zwraca odpowiedni komunikat błędu.
     *
     * @param game   obiekt gry, w której ma być wykonany zakład
     * @param player gracz, który wykonuje zakład
     * @param params lista parametrów komendy (w tym wysokość zakładu)
     * @return komunikat o wyniku wykonania zakładu, np. sukces lub błąd
     */
    @Override
    public String processCommand(Game game, Player player, List<String> params) {
        try{
            int number = Integer.parseInt(params.get(2));
            if(!Objects.equals(game.getPhase(), "bet")){
                return NOT_BET_ROUND;
            } else if(!player.isActive()){
                return ALREADY_FOLD;
            }
            else if(game.getCurrentMaxBet() != 0){
                return BET_EXIST;
            }
            else if(number > player.getRemainingChips()){
                return NOT_ENOUGH_CHIPS;
            }
            else{
                player.setPlayed(true);
                game.setCheckCount(0);
                game.setPot(game.getPot() + number);
                game.setCurrentMaxBet(number);
                player.setRemainingChips(player.getRemainingChips() - number);
                player.setContributedChips(number);
                setSucceeded(true);
                return "Poprawnie przyjęto zakład";
            }
        } catch (Exception e) {
            return "Nieprawidłowy format parametrów. Spróbuj ponownie.\n";
        }
    }
}
