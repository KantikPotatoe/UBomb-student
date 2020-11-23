package fr.ubx.poo.model.decor;

import fr.ubx.poo.model.Entity;
import fr.ubx.poo.view.image.ImageResource;

public class Door  extends Decor{
    // Determine which level the door lead to
    public boolean direction;
    // Determine if the door is open or not
    public boolean opened;

    public Door(boolean direction, boolean opened){
        this.direction = direction;
        this.opened = opened;
    }

    public ImageResource getDoorEntity(){
        return opened ? ImageResource.DOOR_O: ImageResource.DOOR_C;
    }
}
