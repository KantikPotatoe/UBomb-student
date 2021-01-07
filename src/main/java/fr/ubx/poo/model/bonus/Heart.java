package fr.ubx.poo.model.bonus;

import fr.ubx.poo.view.image.ImageResource;

public class Heart extends Pickable{
    @Override
    public String toString() {
        return "Heart";
    }

    @Override
    public ImageResource getImageResource() {
        return ImageResource.HEART;
    }
}
