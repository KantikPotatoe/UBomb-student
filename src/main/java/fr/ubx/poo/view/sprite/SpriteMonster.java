package fr.ubx.poo.view.sprite;

import fr.ubx.poo.model.go.GameObject;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class SpriteMonster extends SpriteGameObject {
    public SpriteMonster(Pane layer, Image image, GameObject go) {
        super(layer, image, go);
    }

    @Override
    public void updateImage() {

    }
}
