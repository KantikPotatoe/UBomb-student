/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.model.decor;

import fr.ubx.poo.model.Entity;
import fr.ubx.poo.view.image.ImageResource;

/***
 * A decor is an element that does not know its own position in the grid.
 */
public abstract class Decor extends Entity {
    /**
     *
     * @return L'ImageResource correspondant à l'objet en question.
     */
    public abstract ImageResource getImageResource();
}
