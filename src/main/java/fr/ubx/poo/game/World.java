/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;

import fr.ubx.poo.model.bonus.Pickable;
import fr.ubx.poo.model.decor.Box;
import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.decor.Door;
import fr.ubx.poo.model.go.character.Princess;
import fr.ubx.poo.view.image.ImageResource;

import java.util.*;
import java.util.function.BiConsumer;

public class World {
    private final Map<Position, Decor> grid;
    private final WorldEntity[][] raw;
    private final Dimension dimension;
    private final Princess princess;
    private boolean hasChanged;

    public World(WorldEntity[][] raw) {
        this.raw = raw;
        dimension = new Dimension(raw.length, raw[0].length);
        grid = WorldBuilder.build(raw, dimension);
        princess = new Princess();
        hasChanged = false;
    }


    public Optional<Position> findPlayerPosition() {
        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                if (raw[y][x] == WorldEntity.PLAYER || raw[y][x] == WorldEntity.DOOR_PREV_OPENED) {
                    return Optional.of(new Position(x, y));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Position> findPrincessPosition() {
        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                if (raw[y][x] == WorldEntity.PRINCESS) {
                    return Optional.of(new Position(x, y));
                }
            }
        }
        return Optional.empty();
    }

    public List<Position> initMonstersPositions() {
        List<Position> positions = new ArrayList<>();
        for (int x = 0; x < dimension.width; x++) {
            for (int y = 0; y < dimension.height; y++) {
                if (raw[y][x] == WorldEntity.MONSTER) {
                    positions.add(new Position(x, y));

                }
            }
        }
        return positions;
    }

    public Optional<Princess> getPrincess() {
        return Optional.of(princess);
    }

    public Decor getDecorAtPosition(Position position) {
        return grid.get(position);
    }

    public void setDecorAtPosition(Position position, Decor decor) {
        grid.put(position, decor);
        this.hasChanged = true;
    }

    public void clearPosition(Position position) {
        grid.remove(position);
        this.hasChanged = true;
    }

    public void forEach(BiConsumer<Position, Decor> fn) {
        grid.forEach(fn);
    }

    public boolean isInside(Position position) {
        return position.inside(this.dimension); // to update
    }

    public boolean isEmpty(Position position) {
        return grid.get(position) == null;
    }

    public boolean isDoor(Position position) {
        return grid.get(position) instanceof Door && ((Door) grid.get(position)).isOpened();
    }

    public boolean isPickable(Position position) {
        return grid.get(position) instanceof Pickable;
    }

    public boolean isBoxMovable(Position position, Direction direction) {
        Position nextPos = direction.nextPosition(position);
        return grid.get(position).getImageResource() == ImageResource.BOX && isEmpty(nextPos) && isInside(nextPos)
                && !isDoor(nextPos) && !isPickable(nextPos) ;
    }

    public boolean worldHasChanged() {
        return this.hasChanged;
    }

    public void finishChange() {
        this.hasChanged = false;
    }

    public void askChange() {
        this.hasChanged = true;
    }

    public Dimension getDimension() {
        return dimension;
    }
}
