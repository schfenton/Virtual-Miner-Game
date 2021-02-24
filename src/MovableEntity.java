import processing.core.PImage;

import java.util.List;
import java.util.Optional;

abstract class MovableEntity extends AnimatedEntity {

    public MovableEntity(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    protected boolean moveTo(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        if (getPosition().adjacent(target.getPosition())) {
            _moveToHelper(world, target, scheduler);
            return true;
        }
        else {
            Point nextPos = nextPosition(world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                Optional<Entity> occupant = world.getOccupant(nextPos);
                if (occupant.isPresent()) {
                    scheduler.unscheduleAllEvents(occupant.get());
                }

                world.moveEntity(this, nextPos);
            }
            return false;
        }
    }

    protected void _moveToHelper(WorldModel world, Entity target, EventScheduler scheduler){}

    abstract Point nextPosition(WorldModel world, Point destPos);

}
