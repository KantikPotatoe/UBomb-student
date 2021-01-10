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


    /**
     *
     * @return Position où le joueur est sensé se trouver à la génération du monde
     */
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

    /**
     *
     * @return La position de la princesse si elle se trouve dans le monde actuel
     */
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

    /**
     *
     * @return La liste des positions ou les monstres vont commencer, utilisée à la génération du monde
     */
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

    /**
     *
     * @return La princesse si elle est bien présente dans le monde
     */
    public Optional<Princess> getPrincess() {
        return Optional.of(princess);
    }

    /**
     *
     * @param position Position ou on veut récupérer l'objet
     * @return Le décor à la position que l'on veut
     */
    public Decor getDecorAtPosition(Position position) {
        return grid.get(position);
    }

    /**
     *
     * @param position Position ou on veut rajouter un décor
     * @param decor Le décor que l'on veut rajouter
     */
    public void setDecorAtPosition(Position position, Decor decor) {
        grid.put(position, decor);
        this.hasChanged = true;
    }

    public void clearPosition(Position position) {
        grid.remove(position);
        //On demande un changement dans le monde pour montrer qu'il faut recharger les sprites : une mise à jour a été faite dans le décor
        this.hasChanged = true;
    }

    public void forEach(BiConsumer<Position, Decor> fn) {
        grid.forEach(fn);
    }

    /**
     *
     * @param position Position que l'on veut vérifier
     * @return position est dans la dimension du monde ou non
     */
    public boolean isInside(Position position) {
        return position.inside(this.dimension); // to update
    }

    /**
     *
     * @param position Position que l'on veut vérifier
     * @return Il n'y a rien (pas de décor ou autre) dans la position entrée en paramètre.
     */
    public boolean isEmpty(Position position) {
        return grid.get(position) == null;
    }


    /**
     *
     * @param position Position que l'on veut vérifier
     * @return A la position entrée en paramètre se trouve une porte.
     */
    public boolean isDoor(Position position) {
        return grid.get(position) instanceof Door
                && ((Door) grid.get(position)).isOpened();
    }

    /**
     *
     * @param position Position que l'on veut vérifier
     * @return A la position entrée en paramètre se trouve un objet ramassable
     */
    public boolean isPickable(Position position) {
        return grid.get(position) instanceof Pickable;
    }

    /**
     *
     * @param position Position que l'on veut vérifier
     * @param direction Direction que l'on veut vérifier
     * @return La box a la position entrée en paramètre est déplaçable dans la direction entrée en paramètre
     */
    public boolean isBoxMovable(Position position, Direction direction) {
        Position nextPos = direction.nextPosition(position);
        return grid.get(position).getImageResource() == ImageResource.BOX && isEmpty(nextPos) && isInside(nextPos)
                && !isDoor(nextPos) && !isPickable(nextPos) ;
    }

    /**
     *
     * @return Si le monde a changé ou non
     */
    public boolean worldHasChanged() {
        return this.hasChanged;
    }

    /**
     * Le monde a fini de changé, on change le boolean
     */
    public void finishChange() {
        this.hasChanged = false;
    }

    /**
     * Le monde a changé, il faut prévenir de la mise à jour
     */
    public void askChange() {
        this.hasChanged = true;
    }

    public Dimension getDimension() {
        return dimension;
    }
}
