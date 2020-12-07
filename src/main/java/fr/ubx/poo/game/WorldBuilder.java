package fr.ubx.poo.game;

import fr.ubx.poo.model.bonus.BombBonus;
import fr.ubx.poo.model.bonus.Heart;
import fr.ubx.poo.model.bonus.Key;
import fr.ubx.poo.model.decor.*;

import java.io.File;
import java.util.*;


public class WorldBuilder {
    private final Map<Position, Decor> grid = new HashMap<>();

    private WorldBuilder() {

    }

    public static WorldEntity[][] generateWorld(String path) {
        List<WorldEntity[]> listEntities = new ArrayList<>();
        try (Scanner levelFile = new Scanner(new File(path))) {
            String line;
            while (levelFile.hasNext()) {
                line = levelFile.next();
                listEntities.add(buildLine(line));
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        WorldEntity[][] world = new WorldEntity[listEntities.size()][listEntities.get(0).length];
        for (WorldEntity[] lineE : listEntities) {
            world[listEntities.indexOf(lineE)] = lineE;
        }
        return world;
    }

    public static WorldEntity[] buildLine(String line) {
        WorldEntity[] worldLine = new WorldEntity[line.length()];
        int i = 0;
        for (char a : line.toCharArray()) {
            Optional<WorldEntity> we = WorldEntity.fromCode(a);
            if (we.isPresent()) {
                worldLine[i] = we.get();
            }
            i++;
        }
        return worldLine;
    }

    public static Map<Position, Decor> build(WorldEntity[][] raw, Dimension dimension) {
        WorldBuilder builder = new WorldBuilder();
        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                Position pos = new Position(x, y);
                Decor decor = processEntity(raw[y][x]);
                if (decor != null) {
                    builder.grid.put(pos, decor);
                }
            }
        }
        return builder.grid;
    }

    private static Decor processEntity(WorldEntity entity) {
        return switch (entity) {
            case STONE -> new Stone();
            case TREE -> new Tree();
            case BOX -> new Box();
            case KEY -> new Key();
            case HEART -> new Heart();
            case BOMB_NUMBER_DEC -> new BombBonus(false, false);
            case BOMB_NUMBER_INC -> new BombBonus(false, true);
            case BOMB_RANGE_DEC -> new BombBonus(true, false);
            case BOMB_RANGE_INC -> new BombBonus(true, true);
            case DOOR_PREV_OPENED -> new Door(false, true);
            case DOOR_NEXT_OPENED -> new Door(true, true);
            case DOOR_NEXT_CLOSED -> new Door(true, false);
            default -> null;
        };
    }
}
