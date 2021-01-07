package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Direction;
import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.game.World;
import fr.ubx.poo.model.go.GameObject;
import fr.ubx.poo.view.image.ImageResource;
import fr.ubx.poo.model.bonus.*;
import fr.ubx.poo.model.decor.*;
import fr.ubx.poo.view.sprite.SpriteFactory;

import java.util.ArrayList;
import java.util.List;

public class Bomb extends GameObject {
    private int lifetime;
    private final int range;

    public Bomb(Game game, Position position, int range) {
        super(game, position);
        this.range = range;
        lifetime = 4;
    }

    public ImageResource getImageBomb() {
        return switch (lifetime) {
            case 4 -> ImageResource.BOMB4;
            case 3 -> ImageResource.BOMB3;
            case 2 -> ImageResource.BOMB2;
            case 1 -> ImageResource.BOMB1;
            default -> ImageResource.EXPLOSION;
        };
    }

    public int getLifetime() {
        return this.lifetime;
    }

    public void dropTime() {
        this.lifetime--;
    }


    public void destroySides(int w){

        World world = game.worldNumber(w);
        for (Direction d : Direction.values()) {
            for (int i = 1; i <= this.range; i++) {
                Position nextPos = d.nextPosition(this.getPosition(), i);
                if (world.getDecorAtPosition(nextPos) instanceof Box ||
                        (world.getDecorAtPosition(nextPos) instanceof Pickable && !(world.getDecorAtPosition(nextPos) instanceof Key))) {
                    world.clearPosition(nextPos);
                    break;
                }
                if (game.getPlayer().getPosition().equals(nextPos)) {
                    game.getPlayer().decreaseLife();
                    break;
                }
                if (game.getMonsterList().stream().anyMatch
                        (monster -> monster.getPosition().equals(nextPos))) {
                    game.getMonsterList().removeIf(monster -> monster.getPosition().equals(nextPos));
                    world.askChange();
                    break;
                }
                if (!world.isEmpty(nextPos)) {
                    break;
                }
            }
        }
    }

    public List<Bomb> createExplosions(){
        List<Bomb> bombs = new ArrayList<>();
        World world = game.getWorld();
        if(this.getLifetime() == 0) {
            for (Direction d : Direction.values()) {
                for (int i = 1; i <= this.getRange(); i++) {
                    Position nextPos = d.nextPosition(this.getPosition(), i);
                    Position previousPos = d.nextPosition(this.getPosition(), i-1);

                    if ((world.get(previousPos) instanceof Box ||
                            (world.get(previousPos) instanceof Pickable && !(world.get(previousPos) instanceof Key)))
                            || (!world.isEmpty(previousPos))){
                        break;
                    } else {
                        bombs.add( new Bomb(game, nextPos, 0 ));
                    }
                }
            }
        }
        return bombs;
    }
    public int getRange() {
        return range;
    }
}
