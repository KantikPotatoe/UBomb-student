/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.game;

import java.util.Objects;

public class Position {
    public final int x;
    public final int y;
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position(Position position) {
        this.x = position.x;
        this.y = position.y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x &&
                y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public boolean inside(Dimension d) {
        return x >= 0 && x < d.width && y >= 0 && y < d.height;
    }

    /**
     *
     * @param position Position que l'on veut voir par rapport à l'actuelle.
     * @return La distance entre les deux positions;
     */
    public double distance(Position position){
        return Math.sqrt(Math.pow(this.x - position.x,2) + Math.pow(this.y - position.y ,2));
    }
}
