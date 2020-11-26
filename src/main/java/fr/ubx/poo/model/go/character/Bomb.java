package fr.ubx.poo.model.go.character;

import fr.ubx.poo.game.Game;
import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.go.GameObject;
import fr.ubx.poo.view.image.ImageResource;
import fr.ubx.poo.view.sprite.Sprite;

public class Bomb extends GameObject {
    private int lifetime;
    private int range;
    public Bomb(Game game, Position position, int range) {
        super(game, position);
        this.range = range;
        lifetime = 4;
    }

    public ImageResource getImageBomb(){
        return switch (lifetime) {
            case 4 -> ImageResource.BOMB4;
            case 3 -> ImageResource.BOMB3;
            case 2 -> ImageResource.BOMB2;
            case 1 -> ImageResource.BOMB1;
            default -> ImageResource.EXPLOSION;
        };
    }

    public int getLifetime(){
        return this.lifetime;
    }

    public void dropTime(long now){
        this.lifetime -= now;
    }
}
