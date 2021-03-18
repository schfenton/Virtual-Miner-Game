import processing.core.PImage;

import java.util.List;
import java.util.Optional;

public class Angel extends MovableEntity {
    public Angel(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    @Override
    void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = getPosition().findNearest(world, OreBlob.class);
        if(target.isPresent() && moveTo(world, target.get().getPosition(), scheduler)){ //if adjacent
            world.removeEntity(target.get());
            scheduler.unscheduleAllEvents(target.get());
        }
        scheduler.scheduleEvent(this,
                Factory.createActivityAction(this, world, imageStore),
                getActionPeriod());
    }
}
