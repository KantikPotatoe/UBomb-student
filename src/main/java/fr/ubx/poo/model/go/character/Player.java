/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.*;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.bonus.BombBonus;
import fr.ubx.poo.model.bonus.Heart;
import fr.ubx.poo.model.bonus.Key;
import fr.ubx.poo.model.bonus.Pickable;
import fr.ubx.poo.model.decor.Box;
import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.decor.Door;
import fr.ubx.poo.model.go.GameObject;

import java.util.ArrayList;
import java.util.List;

public class Player extends GameObject implements Movable {

    private boolean alive = true;
    Direction direction;
    private boolean moveRequested = false;
    private int lives;
    private boolean winner;
    private int sizeBombs;
    private int keys;
    private int bombsNumber;
    private World world;
    public Player(Game game, Position position) {
        super(game, position);
        this.world = this.game.getWorld();
        this.direction = Direction.S;
        this.lives = game.getInitPlayerLives();
        this.keys = 0;
        this.sizeBombs = 1;
        this.bombsNumber = 1;
    }

    public void decreaseLife() {
        this.lives--;
    }

    public int getLives() {
        return lives;
    }

    public int getSizeBombs() {
        return sizeBombs;
    }

    public int getKeys() {
        return keys;
    }

    public int getBombsNumber() {
        return bombsNumber;
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
        Position nextPos = direction.nextPosition(getPosition());
        boolean can = world.isDoor(nextPos) || world.isPickable(nextPos) || world.isEmpty(nextPos);
        return (can || world.isBoxMovable(nextPos, direction)) &&
                world.isInside(nextPos);

    }

    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Decor decor = world.get(nextPos);
        if(decor instanceof Box){
                world.clear(nextPos);
                world.set(direction.nextPosition(nextPos), decor);
        } else if (decor instanceof Door){
            game.changeLevel(((Door) decor).isUp());
        }
        else if (decor instanceof Pickable){
            world.clear(nextPos);
            pickItem((Pickable)decor);
        }
        setPosition(nextPos);
    }

    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
                if(game.getMonsterList().stream().anyMatch
                        (monster -> monster.getPosition().equals(this.getPosition()))){
                    this.lives--;
                } else if (world.findPrincess().isPresent() &&
                        world.findPrincess().get().equals(this.getPosition())) {
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

    public void openDoor(){
        Position nextPos = direction.nextPosition(getPosition());
        Decor front = world.get(nextPos);
        if (getKeys() > 0 && front instanceof Door) {
            // Sinon, si on a des clés, alors on ouvre la porte et on change le monde
            this.keys--;
            world.clear(nextPos);
            world.set(nextPos, new Door(((Door)front).isUp(), true));
        }
    }
    public void pickItem(Pickable item){
        if (item instanceof Key)
            this.keys++;
        else if (item instanceof Heart)
            this.lives++;
        else if (item instanceof BombBonus){
            BombBonus b = (BombBonus) item;
            if (b.isRange()){
                this.sizeBombs += b.isUp() ? 1: (this.sizeBombs == 1 ? 0 : -1);
            } else {
                this.bombsNumber += b.isUp() ? 1: -1;
            }
        }
    }

    public void changeWorld(){
        this.world = game.getWorld();
    }
    public void incDecBomb(int bombsNumber){
        this.bombsNumber+= bombsNumber;
    }
}
