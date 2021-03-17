import processing.core.PImage;
import java.util.*;

public class OreBlob extends MovableEntity {

    public OreBlob(
            String id,
            Point position,
            List<PImage> images,
            int actionPeriod,
            int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    public void executeActivity(
            WorldModel world,
            ImageStore imageStore,
            EventScheduler scheduler)
    {
        Optional<Entity> blobTarget =
                getPosition().findNearest(world, Vein.class);
        long nextPeriod = getActionPeriod();

        if (blobTarget.isPresent()) {
            Point tgtPos = blobTarget.get().getPosition();

            if (moveTo(world, blobTarget.get(), scheduler)) {
                Quake quake = Factory.createQuake(tgtPos,
                        imageStore.getImageList(Factory.QUAKE_KEY));

                world.addEntity(quake);
                nextPeriod += getActionPeriod();
                quake.scheduleActions(scheduler, world, imageStore);
            }
        }

        scheduler.scheduleEvent(this,
                Factory.createActivityAction(this, world, imageStore),
                nextPeriod);
    }

    protected void nextToTarget(WorldModel world, Point target, EventScheduler scheduler){
        Entity occ = world.getOccupant(target).get();
        world.removeEntity(occ);
        scheduler.unscheduleAllEvents(occ);
    }

    protected boolean _nextPositionPassHelper(WorldModel world, Point p){
        Optional<Entity> occupant = world.getOccupant(p);
        return p.withinBounds(world) && !world.isOccupied(p) || (occupant.isPresent() && occupant.get() instanceof Ore);
    }
}
