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
    public Player(Game game, Position position) {
        super(game, position);
        this.direction = Direction.S;
        this.lives = game.getInitPlayerLives();
        this.keys = 0;
        this.sizeBombs = 1;
        this.bombsNumber = 3;
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
        return this.game.getWorld().isEmpty(direction.nextPosition(getPosition())) &&
                this.game.getWorld().isInside(direction.nextPosition(getPosition()));

    }

    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Decor decor = this.game.getWorld().get(nextPos);
        if(decor instanceof Box){
            this.game.getWorld().clear(nextPos);
            Box box = new Box();
            this.game.getWorld().set(direction.nextPosition(nextPos), box );
        } else if (decor instanceof Door){
            if(((Door) decor).opened) {
                this.game.getWorld().changeLevel((((Door) decor).direction));
            } else {
                //TODO réduit les clés de l'inventaire
            }
        }
        else if (decor instanceof Pickable){
            this.game.getWorld().clear(nextPos);
            pickItem((Pickable)decor);
            //TODO Que faire ? Ajouter à un inventaire, ajouter un integer...
        }
        setPosition(nextPos);
    }

    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);
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

    public void pickItem(Pickable item){
        if (item instanceof Key)
            this.keys++;
        else if (item instanceof Heart)
            this.lives++;
        else if (item instanceof BombBonus){
            BombBonus b = (BombBonus) item;
            if (b.isRange()){
                this.sizeBombs += b.isUp() ? 1: -1;
            } else {
                this.bombsNumber += b.isUp() ? 1: -1;
            }
        }
    }
}
