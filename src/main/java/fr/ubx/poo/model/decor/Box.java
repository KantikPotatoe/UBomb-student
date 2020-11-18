package fr.ubx.poo.model.decor;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.model.Entity;
import fr.ubx.poo.model.Movable;

public class Box extends Decor implements Movable {

    @Override
    public boolean canMove(Direction direction) {
        return true;
    }
    @Override
    public void doMove(Direction direction){

    }
}
