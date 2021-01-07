/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.decor;


import fr.ubx.poo.view.image.ImageResource;

public class Tree extends Decor {
    @Override
    public String toString() {
        return "Tree";
    }
    @Override
    public ImageResource getImageResource() {
        return ImageResource.TREE;
    }
}
