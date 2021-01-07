package fr.ubx.poo.game;

import static fr.ubx.poo.game.WorldEntity.*;

public class WorldStatic extends World {
    private static final WorldEntity[][] mapEntities =
            {
                    {STONE, EMPTY, HEART, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, BOMB_RANGE_DEC, EMPTY},
                    {WorldEntity.PLAYER, STONE, STONE, EMPTY, STONE, EMPTY, STONE, STONE, STONE, STONE, EMPTY, EMPTY},
                    {EMPTY, EMPTY, EMPTY, EMPTY, STONE, BOX, STONE, EMPTY, EMPTY, STONE, EMPTY, EMPTY},
                    {EMPTY, EMPTY, EMPTY, EMPTY, STONE, BOX, STONE, EMPTY, EMPTY, STONE, EMPTY, EMPTY},
                    {EMPTY, BOX, EMPTY, EMPTY, STONE, STONE, STONE, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                    {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, KEY, EMPTY, STONE, EMPTY, EMPTY},
                    {EMPTY, TREE, EMPTY, TREE, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, STONE, EMPTY, EMPTY},
                    {EMPTY, EMPTY, BOX, TREE, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, STONE, EMPTY, EMPTY},
                    {EMPTY, TREE, TREE, TREE, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, STONE, EMPTY, EMPTY},
                    {EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, BOMB_RANGE_INC, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY},
                    {STONE, STONE, STONE, EMPTY, STONE, EMPTY, BOX, BOX, STONE, STONE, BOX, STONE},
                    {EMPTY, DOOR_NEXT_CLOSED, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, MONSTER, EMPTY, EMPTY, EMPTY},
                    {EMPTY, BOMB_NUMBER_DEC, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, BOMB_NUMBER_INC, EMPTY, EMPTY, PRINCESS}
            };
    public WorldStatic() {
        super(mapEntities);
    }
}
