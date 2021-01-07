/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;

import java.util.Arrays;
import java.util.Optional;

public enum WorldEntity {
    EMPTY('_'),
    BOX('B'),
    HEART('H'),
    KEY('K'),
    MONSTER('M'),
    DOOR_PREV_OPENED('V'),
    DOOR_NEXT_OPENED('N'),
    DOOR_NEXT_CLOSED('n'),
    PLAYER('P'),
    STONE('S'),
    TREE('T'),
    PRINCESS('W'),
    BOMB_RANGE_INC('>'),
    BOMB_RANGE_DEC('<'),
    BOMB_NUMBER_INC('+'),
    BOMB_NUMBER_DEC('-');


    private final char code;

    WorldEntity(char code) {
        this.code = code;
    }

    public static Optional<WorldEntity> fromCode(char code) {
        return Arrays.stream(values())
                .filter(e -> e.acceptCode(code))
                .findFirst();
    }

    private boolean acceptCode(char code) {
        return this.code == code;
    }

    @Override
    public String toString() {
        return "" + code;
    }

}
