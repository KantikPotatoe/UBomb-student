package fr.ubx.poo.model.go.character;

import fr.ubx.poo.model.decor.Decor;
import fr.ubx.poo.view.image.ImageResource;


public class Princess extends Decor {
    @Override
    public String toString() {
        return "Princess";
    }
    @Override
    public ImageResource getImageResource() {
        return ImageResource.PRINCESS;
    }
}
