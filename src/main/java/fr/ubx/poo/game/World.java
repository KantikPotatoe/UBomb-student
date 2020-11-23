/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;

import fr.ubx.poo.model.bonus.Pickable;
import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.model.go.character.Princess;

import java.util.*;
import java.util.function.BiConsumer;

public class World {
    private final Map<Position, Decor> grid;
    private final WorldEntity[][] raw;
    public final Dimension dimension;
    private final Princess princess;

    public World(WorldEntity[][] raw) {
        this.raw = raw;
        dimension = new Dimension(raw.length, raw[0].length);
        grid = WorldBuilder.build(raw, dimension);
        Position positionPrincess = this.findPrincess().orElse(new Position(-1, -1));
        //this,
        princess = new Princess();
        /*try {

        } catch (PositionNotFoundException e) {
            System.err.println("Position not found : " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }*/

    }

    public Position findPlayer() throws PositionNotFoundException {
        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                if (raw[y][x] == WorldEntity.Player) {
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
        //throw new PositionNotFoundException("Princess");
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
    public List<Position> findPickables(){
        List<Position> positions = new ArrayList<>();
        List<WorldEntity> pickups = WorldEntity.listPickup();
        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                if (pickups.contains(raw[y][x])) {
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
    }

    public void clear(Position position) {
        grid.remove(position);
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
        return grid.get(position) instanceof Pickable || grid.get(position) == null;
    }
}
