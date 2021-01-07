package fr.ubx.poo.model.bonus;


import fr.ubx.poo.view.image.ImageResource;

public class Key extends Pickable {

    @Override
    public String toString() {
        return "Key";
    }
    @Override
    public ImageResource getImageResource() {
        return ImageResource.KEY;
    }
}
