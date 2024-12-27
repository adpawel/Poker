package pl.edu.agh.kis.pz1.commands;

import lombok.Data;
import pl.edu.agh.kis.pz1.Game;
import pl.edu.agh.kis.pz1.player_utils.Player;

import java.util.List;

/**
 * Abstrakcyjna klasa bazowa dla komend gry.
 * <p>
 * Klasa reprezentuje wspólną strukturę i funkcjonalność dla wszystkich
 * komend w grze. Zawiera domyślną implementację metody przetwarzania komend,
 * która może być nadpisana przez klasy potomne. Każda komenda ma stan
 * wskazujący, czy została wykonana pomyślnie.
 */
@Data
public abstract class Command implements CommandInterface {
    private boolean succeeded = false;

    /**
     * Przetwarza komendę w kontekście gry.
     * <p>
     * Metoda wykonuje określoną logikę związaną z komendą, modyfikując stan gry
     * lub gracza na podstawie przekazanych parametrów. Domyślnie zwraca komunikat,
     * że komenda nie została zaimplementowana i powinna być nadpisana w klasach
     * potomnych.
     *
     * @param game   obiekt gry, w którym ma być wykonana komenda
     * @param player gracz, który wykonuje komendę
     * @param params lista parametrów przekazanych do komendy
     * @return wynik przetworzenia komendy w formie tekstu
     */
    @Override
    public String processCommand(Game game, Player player, List<String> params) {
        return "Komenda nie zaimplementowana";
    }
}

