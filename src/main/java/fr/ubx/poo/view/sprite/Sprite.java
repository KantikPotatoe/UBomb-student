/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.view.sprite;

import fr.ubx.poo.game.Position;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public abstract class Sprite {

    public static final int SIZE = 40;
    private final Pane layer;
    private ImageView imageView;
    private Image image;

    protected Sprite(Pane layer, Image image) {
        this.layer = layer;
        this.image = image;
    }

    public final void setImage(Image image) {
        if (this.image == null || this.image != image) {
            this.image = image;
        }
    }

    public abstract void updateImage();

    public abstract Position getPosition();

    public final void render() {
        if (imageView != null) {
            remove();
        }
        updateImage();
        imageView = new ImageView(this.image);
        imageView.setX(getPosition().x * (double) SIZE);
        imageView.setY(getPosition().y * (double) SIZE);
        layer.getChildren().add(imageView);
    }

    public final void remove() {
        layer.getChildren().remove(imageView);
        imageView = null;
    }
}
