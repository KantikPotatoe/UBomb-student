/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.*;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.decor.Box;
import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.go.GameObject;

public class Player extends GameObject implements Movable {

    private boolean alive = true;
    Direction direction;
    private boolean moveRequested = false;
    private int lives;
    private boolean winner;

    public Player(Game game, Position position) {
        super(game, position);
        this.direction = Direction.S;
        this.lives = game.getInitPlayerLives();
    }

    public int getLives() {
        return lives;
    }

    public Direction getDirection() {
        return direction;
    }

    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
        }
        moveRequested = true;
    }

    @Override
    public boolean canMove(Direction direction) {
        return this.game.getWorld().isEmpty(direction.nextPosition(getPosition())) &&
                this.game.getWorld().isInside(direction.nextPosition(getPosition()));

    }

    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Decor decor = this.game.getWorld().get(nextPos);
        if(decor instanceof Box){
            this.game.getWorld().clear(nextPos);
            this.game.getWorld().set(direction.nextPosition(getPosition()),decor );
        }
        setPosition(nextPos);
    }

    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
                if(this.game.getWorld().findPickables().contains(this.getPosition())){

                }
                if(this.game.getWorld().findMonsters().contains(this.getPosition())){
                    this.lives--;
                } else if  (this.game.getWorld().findPrincess().isPresent() &&
                        this.game.getWorld().findPrincess().get().equals(this.getPosition())) {
                    this.winner = true;
                }
                if(this.getLives() <= 0){
                    this.alive = false;
                }
            }
        }
        moveRequested = false;
    }

    public boolean isWinner() {
        return winner;
    }

    public boolean isAlive() {
        return alive;
    }

    public void moveBox(Position position){
        //TODO
    }
}
