/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.view.sprite;

import static fr.ubx.poo.view.image.ImageResource.*;

import fr.ubx.poo.game.Position;
import fr.ubx.poo.model.bonus.*;
import fr.ubx.poo.model.decor.*;
import fr.ubx.poo.model.go.character.Monster;
import fr.ubx.poo.model.go.character.Player;
import fr.ubx.poo.model.go.character.Princess;
import fr.ubx.poo.view.image.ImageFactory;
import javafx.scene.layout.Pane;


public final class SpriteFactory {

    public static Sprite createDecor(Pane layer, Position position, Decor decor) {
        ImageFactory factory = ImageFactory.getInstance();

        if (decor instanceof Stone)
            return new SpriteDecor(layer, factory.get(STONE), position);
        if (decor instanceof Tree)
            return new SpriteDecor(layer, factory.get(TREE), position);
        if (decor instanceof Box)
            return new SpriteDecor(layer, factory.get(BOX), position);
        if (decor instanceof Bonus)
            return new SpriteDecor(layer, factory.get(HEART), position);
        if (decor instanceof Key)
            return new SpriteDecor(layer, factory.get(KEY), position);
        return null;
    }

    public static Sprite createPlayer(Pane layer, Player player) {
        return new SpritePlayer(layer, player);
    }

    public static Sprite createPrincess(Pane layer, Princess princess) {
        ImageFactory factory = ImageFactory.getInstance();
        return new SpritePrincess(layer, factory.get(PRINCESS), princess);
    }

    public static Sprite createMonster(Pane layer, Monster monster) {
        ImageFactory factory = ImageFactory.getInstance();
        return new SpriteMonster(layer, factory.get(MONSTER), monster);
    }
}
