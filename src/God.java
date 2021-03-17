import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class God extends MovableEntity {

    private Point target = null;

    public God(String id,
               Point position,
               List<PImage> images,
               int actionPeriod,
               int animationPeriod){
        super(id, position, images, actionPeriod, animationPeriod);
    }

    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {

        if(target == null){
            target = world.pickEmptyPoint();
        }

        if(moveTo(world, target, scheduler) && !world.isOccupied(target)){
            Vein entity = Factory.createVein("vein_" + target.y + "_" + target.x, target,
                    Factory.rand.nextInt(11000 - 9000) + 9000,
                    imageStore.getImageList(Parser.VEIN_KEY));
            world.tryAddEntity(entity);
            entity.scheduleActions(scheduler, world, imageStore);
            target = null;
        }

        scheduler.scheduleEvent(this,
                Factory.createActivityAction(this, world, imageStore),
                getActionPeriod());
    }
}
