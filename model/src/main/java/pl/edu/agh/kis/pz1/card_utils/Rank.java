package pl.edu.agh.kis.pz1.card_utils;

import lombok.Getter;

/**
 * Klasa reprezentujaca rangi kart do gry w pokera
 */
@Getter
public enum Rank {
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    JACK(11),
    QUEEN(12),
    KING(13),
    ACE(14);

    // Getter do pobierania wartości
    private final int value;

    Rank(int value) {
        this.value = value;
    }
}

