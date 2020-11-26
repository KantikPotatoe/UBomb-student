/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;

import fr.ubx.poo.model.bonus.Pickable;
import fr.ubx.poo.model.decor.Box;
import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.decor.Door;
import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.model.go.character.Princess;

import java.util.*;
import java.util.function.BiConsumer;

public class World {
    private Map<Position, Decor> grid;
    private WorldEntity[][] raw;
    public Dimension dimension;
    private final Princess princess;
    private final int levels;
    private final String worldPath;
    private final String prefix;
    private int actualLevel;
    private boolean hasChanged;
    private boolean newWorld;
    //Seulement dans le cas de WorldStatic
    public World(WorldEntity[][] raw){
        this.raw = raw;
        this.worldPath = "";
        this.prefix = "";
        this.levels = 0;
        this.actualLevel = 0;
        dimension = new Dimension(raw.length, raw[0].length);
        grid = WorldBuilder.build(raw, dimension);
        Position positionPrincess = this.findPrincess().orElse(new Position(-1, -1));
        princess = new Princess();
        hasChanged = false;
        newWorld = false;
    }
    //Dans le cas de la récupération du fichier de configuration
    public World(String worldPath, String prefix, int levels) {
        this.worldPath = worldPath;
        this.prefix = prefix;
        this.levels = levels;
        this.actualLevel = 1;
        this.raw = WorldBuilder.generateWorld(worldPath+"/"+prefix+actualLevel+".txt");

        dimension = new Dimension(raw.length, raw[0].length);
        grid = WorldBuilder.build(raw, dimension);
        Position positionPrincess = this.findPrincess().orElse(new Position(-1, -1));
        princess = new Princess();
        hasChanged = false;
        newWorld = false;
    }

    public Position findPlayer() throws PositionNotFoundException {
        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                if (raw[y][x] == WorldEntity.Player || raw[y][x] == WorldEntity.DoorPrevOpened) {
                    return new Position(x, y);
                }
            }
        }
        throw new PositionNotFoundException("Player");
    }

    public Optional<Position> findPrincess()/* throws PositionNotFoundException*/ {
        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                if (raw[y][x] == WorldEntity.Princess) {
                    return Optional.of(new Position(x, y));
                }
            }
        }
        return Optional.empty();
    }

    public List<Position> findMonsters() {
        List<Position> positions = new ArrayList<>();
        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                if (raw[y][x] == WorldEntity.Monster) {
                    positions.add(new Position(x, y));
                }
            }
        }
        return positions;
    }

    public Optional<Princess> getPrincess() {
        return Optional.of(princess);
    }

    public Decor get(Position position) {
        return grid.get(position);
    }

    public void set(Position position, Decor decor) {
        grid.put(position, decor);
        this.hasChanged = true;
    }

    public void clear(Position position) {
        grid.remove(position);
        this.hasChanged = true;
    }

    public void forEach(BiConsumer<Position, Decor> fn) {
        grid.forEach(fn);
    }

    public Collection<Decor> values() {
        return grid.values();
    }

    public boolean isInside(Position position) {
        return position.inside(this.dimension); // to update
    }

    public boolean isEmpty(Position position) {
        return grid.get(position) == null;
    }

    public boolean isDoor(Position position){
        return grid.get(position) instanceof Door ;
    }
    public boolean isPickable(Position position){
        return grid.get(position) instanceof Pickable;
    }
    public boolean isBoxMovable(Position position, Direction direction){
        Position nextPos = direction.nextPosition(position);
        return grid.get(position) instanceof Box && isEmpty(nextPos) && isInside(nextPos)
                && !isDoor(nextPos) && !isPickable(nextPos);
    }
    public void changeLevel(boolean up) {
        actualLevel += up ? 1 : -1;
        this.raw = WorldBuilder.generateWorld(worldPath+"/"+prefix+actualLevel+".txt");
        dimension = new Dimension(raw.length, raw[0].length);
        grid = WorldBuilder.build(raw, dimension);

        this.newWorld = true;
        this.hasChanged = true;
    }

    public int getActualLevel(){
        return this.actualLevel;
    }

    public boolean worldHasChanged(){
        return this.hasChanged;
    }

    public void finishChange(){
        this.hasChanged = false;
    }

    public void finishNewWorld() {
        this.newWorld = false;
    }

    public boolean isNewWorld(){
        return this.newWorld;
    }

    public void askChange() {
        this.hasChanged = true;
    }

}
