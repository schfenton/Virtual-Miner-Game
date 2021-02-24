import processing.core.PImage;

import java.util.List;

public class Ore extends ActiveEntity {

    public Ore(
            String id,
            Point position,
            List<PImage> images,
            int actionPeriod)
    {
        super(id, position, images, actionPeriod);
    }

    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Point pos = getPosition();

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        OreBlob blob = Factory.createOreBlob(getId(), pos,
                getActionPeriod(), imageStore);

        world.addEntity(blob);
        blob.scheduleActions(scheduler, world, imageStore);
    }

}
