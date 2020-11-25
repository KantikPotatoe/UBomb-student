package fr.ubx.poo.model.decor;

import fr.ubx.poo.model.Entity;
import fr.ubx.poo.view.image.ImageResource;

public class Door  extends Decor{
    // Determine which level the door lead to
    private boolean up;
    // Determine if the door is open or not
    private boolean opened;

    public Door(boolean up, boolean opened){
        this.up = up;
        this.opened = opened;
    }

    public ImageResource getDoorEntity(){
        return opened ? ImageResource.DOOR_O: ImageResource.DOOR_C;
    }

    public boolean isUp() {
        return up;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }
}
