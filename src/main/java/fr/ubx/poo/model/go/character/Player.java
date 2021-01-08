/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.*;
import fr.ubx.poo.model.Movable;
import fr.ubx.poo.model.bonus.BombBonus;
import fr.ubx.poo.model.bonus.Pickable;
import fr.ubx.poo.model.decor.Box;
import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.model.decor.Door;
import fr.ubx.poo.model.go.GameObject;
import fr.ubx.poo.view.image.ImageResource;

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

    /**
     * Réduit la vie du joueur
     */
    public void decreaseLife() {
        this.lives--;
    }

    /**
     *
     * @return Le nombre de vies du joueur, qui sera affiché dans la gameBar
     */
    public int getLives() {
        return lives;
    }

    /**
     *
     * @return La portée des bombes qui sera affichée dans la gameBar
     */
    public int getSizeBombs() {
        return sizeBombs;
    }

    /**
     *
     * @return Le nombre de clés qui sera affiché dans la gameBar
     */
    public int getKeys() {
        return keys;
    }

    /**
     *
     * @return Le nombre de bombes que le joueur porte actuellement, qui sera affiché dans la gamebar
     */
    public int getBombsNumber() {
        return bombsNumber;
    }

    /**
     *
     * @return Renvoie la direction dans laquelle le joueur est tourné
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     *
     * @param direction La direction du joueur va changer si celle-ci est différente de l'actuelle.
     * Un mouvement est demandé donc on met bien à jour moveRequested.
     */
    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
        }
        moveRequested = true;
    }

    /**
     *
     * @param direction La direction dans laquelle le joueur veut se déplacer
     * @return Retourne vrai, s'il peut se déplacer dans la direction, s'il peut bouger la box et
     * qu'aucun ennemi n'est présent derrière et qu'il reste dans l'espace du monde.
     */
    @Override
    public boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        boolean can = world.isDoor(nextPos) || world.isPickable(nextPos) || world.isEmpty(nextPos);
        return (can || (world.isBoxMovable(nextPos, direction) && !game.containsMonster(direction.nextPosition(nextPos))))
                && world.isInside(nextPos);

    }

    /**
     *
     * @param direction Direction dans laquelle le joueur veut se déplacer
     * Quand il se déplace, plusieurs actions sont possibles selon ce qu'il y a devant lui.
     */
    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        Decor decor = world.getDecorAtPosition(nextPos);
        if (decor instanceof Box ) {
            world.clearPosition(nextPos);
            world.setDecorAtPosition(direction.nextPosition(nextPos), decor);
        } else if (decor instanceof Door) {
            game.changeLevel(((Door) decor).isUp());
        } else if (decor instanceof Pickable) {
            world.clearPosition(nextPos);
            pickItem((Pickable) decor);
        }
        setPosition(nextPos);
    }

    /**
     * Fonction lancée tous les ticks
     * @param now
     * Si il se déplace et qu'il y a un monstre, il perd une vie
     * Si il y a la princesse dans ce monde et qu'il est dessus, il gagne.
     * Si il n'a plus de vie, il perd.
     */
    public void update(long now) {

        if (isMoveRequestedAndCanMove(direction)) {
            doMove(direction);
            if (game.getMonsterList().stream().anyMatch
                    (monster -> monster.getPosition().equals(this.getPosition()))) {
                this.lives--;
            }
            moveRequested = false;

        }
        if (world.findPrincessPosition().isPresent() &&
                world.findPrincessPosition().get().equals(this.getPosition())) {
            this.winner = true;
        }
        if (this.getLives() <= 0) {
            this.alive = false;
        }
    }

    /**
     *
     * @param direction Direction dans laquelle le mouvement est demandé
     * @return Si le mouvement est demandé et qu'il peut bouger.
     */
    private boolean isMoveRequestedAndCanMove(Direction direction) {
        return (moveRequested && canMove(direction));
    }

    /**
     *
     * @return Le joueur a gagné ou non (il a trouvé la princesse ou non)
     */
    public boolean isWinner() {
        return winner;
    }

    /**
     *
     * @return Le joueur a toujours des vies
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Lorsque la fonction est lancée, on va essayer d'ouvrir une porte en face.
     * Si le joueur a au moins une clé, on peut l'ouvrir, la porte est ouverte et on va pouvoir aller à l'étage supérieur.
     */
    public void openDoor() {
        Position nextPos = direction.nextPosition(getPosition());
        Decor front = world.getDecorAtPosition(nextPos);
        if (getKeys() > 0 && front.getImageResource() == ImageResource.DOOR_C) {
            // Sinon, si on a des clés, alors on ouvre la porte et on change le monde
            this.keys--;
            world.clearPosition(nextPos);
            world.setDecorAtPosition(nextPos, new Door(((Door) front).isUp(), true));
        }
    }

    /**
     *
      * @param item L'objet que le joueur va ramasser si il se trouve sur un objet ramassable
     *  S'il s'agit d'une clé, on va en ajouter une à l'inventaire du joueur
     *  S'il s'agit d'un coeur, on va en ajouter un à la vie du joueur
     *  S'il s'agit un bonus de bombe :
     *              - S'il s'agit d'un bonus de range :
     *                  + Si c'est un bonus, on rajoute 1 à la range
     *                  + Si c'est un malus et que la portée est différente de 1, on baisse
     *              - S'il s'agit d'un bonus de nombre :
     *                  + Si c'est un bonus, on ajoute une bombe à l'inventaire
     *                  + Si c'est un malus et que le joueur a plus d'une bombre, on baisse
     *
     */
    public void pickItem(Pickable item) {
        if (item.getImageResource() == ImageResource.KEY)
            this.keys++;
        else if (item.getImageResource() == ImageResource.HEART)
            this.lives++;
        else if (item instanceof BombBonus) {
            BombBonus bombBonus = (BombBonus) item;
            if (bombBonus.isRange()) {
                if (bombBonus.isUp()) {
                    this.sizeBombs ++;
                } else if(sizeBombs != 1){
                    this.sizeBombs --;
                }
            } else {
                if (bombBonus.isUp()) {
                    changeNumberOfBombs(1);
                } else if (bombsNumber != 1) {
                    changeNumberOfBombs(-1);
                }
            }
        }
    }

    /**
     * Met à jour le monde actuel en fonction de game.
     */
    public void changeWorld() {
        this.world = game.getWorld();
    }

    /**
     *
     * @param bombsNumber Incrémente ou décrémente le nombre de bombes
     */
    public void changeNumberOfBombs(int bombsNumber) {
        this.bombsNumber += bombsNumber;
    }
}
