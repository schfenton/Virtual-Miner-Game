import processing.core.PImage;

import java.util.List;
import java.util.Optional;

abstract class Miner extends MovableEntity {

    private static final int TRANSFORM_RADIUS = 3;

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

    protected boolean transformAngel(
            WorldModel world,
            EventScheduler scheduler,
            ImageStore imageStore)
    {
        Optional<Entity> sanctuary = getPosition().findNearest(world, Sanctuary.class);

        if(sanctuary.isPresent() &&
                sanctuary.get().getPosition().distanceSquared(getPosition()) <= Math.pow(TRANSFORM_RADIUS, 2)){
            Angel angel = Factory.createAngel(getPosition(), imageStore.getImageList(Factory.ANGEL_KEY));

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.tryAddEntity(angel);
            angel.scheduleActions(scheduler, world, imageStore);
            return true;
        }

        return false;
    }

}
