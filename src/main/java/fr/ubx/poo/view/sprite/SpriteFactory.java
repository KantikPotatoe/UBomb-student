/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.view.sprite;

import static fr.ubx.poo.view.image.ImageResource.*;

import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.bonus.*;
import fr.ubx.poo.model.decor.*;
import fr.ubx.poo.model.go.character.Bomb;
import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.model.go.character.Player;
import fr.ubx.poo.model.go.character.Princess;
import fr.ubx.poo.view.image.ImageFactory;
import javafx.scene.layout.Pane;

import java.awt.*;


public final class SpriteFactory {

    private SpriteFactory(){
        throw  new IllegalStateException("Static class");
    }

    public static Sprite createDecor(Pane layer, Position position, Decor decor) {
        ImageFactory factory = ImageFactory.getInstance();

        return new SpriteDecor(layer, factory.get(decor.getImageResource()), position);

    }

    public static Sprite createPlayer(Pane layer, Player player) {
        return new SpritePlayer(layer, player);
    }

    public static Sprite createBomb(Pane layer, Bomb bomb) {
        ImageFactory factory = ImageFactory.getInstance();
        return new SpriteBomb(layer, factory.get(bomb.getImageBomb()), bomb);
    }

    public static Sprite createExplosion(Pane layer, Bomb bomb){
        ImageFactory factory = ImageFactory.getInstance();
        return new SpriteBomb(layer, factory.get(EXPLOSION), bomb);
    }
    public static Sprite createMonster(Pane layer, Monster monster) {
        ImageFactory factory = ImageFactory.getInstance();
        return new SpriteMonster(layer, factory.get(MONSTER), monster);
    }
}
