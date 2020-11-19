package fr.ubx.poo.game;

import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.decor.Stone;
import fr.ubx.poo.model.decor.Tree;
import fr.ubx.poo.model.go.GameObject;
import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.model.bonus.*;
import fr.ubx.poo.model.decor.*;

import java.io.File;
import java.util.*;

public class WorldBuilder {
    private final Map<Position, Decor> grid = new Hashtable<>();

    private WorldBuilder() {

    }

    public static WorldEntity[][] generateWorld(String path){
        List<WorldEntity[]> listEntities = new ArrayList<>();
        try {
            Scanner levelFile = new Scanner(new File(path));

            String line;
            // while loop
            while (levelFile.hasNext() ) {
                // find next line
                line = levelFile.next();
                listEntities.add(buildLine(line));
            }
            levelFile.close();
        } catch (Exception e){
            System.out.println(e);
        }
        WorldEntity[][] world = new WorldEntity[listEntities.get(0).length][listEntities.size()];
        for(WorldEntity[] lineE : listEntities){
            world[listEntities.indexOf(lineE)] = lineE;
        }
        return world;
    }
    public static WorldEntity[] buildLine(String line){
        WorldEntity[] worldLine = new WorldEntity[line.length()];
        int i = 0;
        for(char a : line.toCharArray()){
            Optional<WorldEntity> we = WorldEntity.fromCode(a);
            if (we.isPresent()){
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
        switch (entity) {
            case Stone:
                return new Stone();
            case Tree:
                return new Tree();
            case Box:
                return new Box();
            case Key:
                return new Key();
            default:
                return null;
        }
    }
}
