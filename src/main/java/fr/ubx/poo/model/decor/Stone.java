/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.decor;

import fr.ubx.poo.view.image.ImageResource;

public class Stone extends Decor {
    @Override
    public String toString() {
        return "Stone";
    }

    @Override
    public ImageResource getImageResource() {
        return ImageResource.STONE;
    }
}
