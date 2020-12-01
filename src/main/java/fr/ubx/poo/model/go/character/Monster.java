package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.game.World;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.go.GameObject;

public class Monster extends GameObject implements Movable {

    private World world;
    public Monster(Game game, Position position) {
        super(game, position);
        this.world = game.getWorld();
    }

    @Override
    public boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        boolean can = world.isDoor(nextPos) || world.isPickable(nextPos) || world.isEmpty(nextPos);
        return (can && world.isInside(nextPos));

    }

    @Override
    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        if(canMove(direction)){
            this.setPosition(nextPos);

            //world.askChange();
        }

    }
}
