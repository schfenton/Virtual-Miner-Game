import processing.core.PImage;

import java.util.List;
import java.util.Optional;

abstract class Miner extends MovableEntity {

    private final int resourceLimit;

    public Miner(
            String id,
            Point position,
            List<PImage> images,
            int resourceLimit,
            int actionPeriod,
            int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);
        this.resourceLimit = resourceLimit;
    }

    protected int getResourceLimit(){
        return resourceLimit;
    }

}
