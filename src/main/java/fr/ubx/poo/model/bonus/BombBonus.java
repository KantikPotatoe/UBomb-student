package fr.ubx.poo.model.bonus;

public class BombBonus extends Pickable {
    // Définit si le bonus est en range ou non (sinon, alors augmentation du nombre)
    private boolean range;
    //Définit si ça augmente ou non
    private boolean up;

    public BombBonus(boolean range, boolean up){
        this.range = range;
        this.up = up;
    }
}
