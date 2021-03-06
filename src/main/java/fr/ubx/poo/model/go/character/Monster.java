package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.game.World;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.go.GameObject;

public class Monster extends GameObject implements Movable {

    private final World world;

    public Monster(Game game, Position position) {
        super(game, position);
        this.world = game.getWorld();
    }

    @Override
    public boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        boolean can = world.isPickable(nextPos) || world.isEmpty(nextPos);
        return (can && world.isInside(nextPos));

    }

    @Override
    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        if (canMove(direction)) {
            this.setPosition(nextPos);
        }
    }


    public void updatePosition(){
        //Si le monstre se trouve dans le même niveau que le niveau actuel, alors il va bouger intelligemment
        if (this.world.equals(game.getWorld())){
            doMove(choosePosition());
        } else { //Sinon il bouge aléatoirement
            doMove(Direction.random());
        }
        if (this.game.getPlayer().getPosition().equals(this.getPosition())){
            this.game.getPlayer().decreaseLife();
        }
    }


    /**
     * Fonction permettant de choisir la direction rapprochant le plus le monstre du joueur
     * @return la direction correspondante
     */
    public Direction choosePosition(){
        Direction dir = Direction.random();
        Position pos;
        double distanceMin = -1;
        double distanceNew;
        for(Direction d : Direction.values()){
            pos = d.nextPosition(this.getPosition());
            distanceNew = game.distancePlayer(pos);
            //Le choix de la nouvelle position se fait ici
            if((distanceMin == -1 || distanceMin >= distanceNew) && this.canMove(d)) {
                distanceMin = distanceNew;
                dir = d;
            }

        }

        return dir;
    }
}
