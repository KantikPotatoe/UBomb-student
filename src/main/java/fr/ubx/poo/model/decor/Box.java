package fr.ubx.poo.model.decor;

import fr.ubx.poo.view.image.ImageResource;

public class Box extends Decor  {
    @Override
    public String toString() {
        return "Box";
    }

    @Override
    public ImageResource getImageResource() {
        return ImageResource.BOX;
    }

}
