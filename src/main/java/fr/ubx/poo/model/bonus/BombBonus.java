package fr.ubx.poo.model.bonus;

import fr.ubx.poo.view.image.ImageResource;

public class BombBonus extends Pickable {
    // Définit si le bonus est en range ou non (sinon, alors augmentation du nombre)
    private final boolean range;


    //Définit si ça augmente ou non
    private final boolean up;

    public BombBonus(boolean range, boolean up){
        this.range = range;
        this.up = up;
    }

    public boolean isRange() {
        return range;
    }

    public boolean isUp() {
        return up;
    }

    public ImageResource getImageResource(){
        if(up){
            if(range){
                return ImageResource.BOMBRUP;
            } else {
                return ImageResource.BOMBNBUP;
            }
        } else {
            if(range){
                return ImageResource.BOMBRDOWN;
            } else {
                return ImageResource.BOMBNBDOWN;
            }
        }
    }

}
