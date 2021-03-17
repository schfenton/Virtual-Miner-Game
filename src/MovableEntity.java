import processing.core.PImage;

import java.util.List;
import java.util.Optional;

abstract class MovableEntity extends AnimatedEntity {

    public MovableEntity(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    protected boolean moveTo(
            WorldModel world,
            Point target,
            EventScheduler scheduler)
    {
        if (getPosition().adjacent(target)) {
            nextToTarget(world, target, scheduler);
            return true;
        }
        else {
            Point nextPos = nextPosition(world, target);

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

    protected boolean moveTo(
            WorldModel world,
            Entity target,
            EventScheduler scheduler)
    {
        return moveTo(world, target.getPosition(), scheduler);
    }

    protected void nextToTarget(WorldModel world, Point target, EventScheduler scheduler){}

    protected Point nextPosition(WorldModel world, Point destPos) {
        List<Point> points;
        PathingStrategy strat = new AStarPathingStrategy();

        points = strat.computePath(getPosition(), destPos,
                p -> _nextPositionPassHelper(world, p),
                PathingStrategy.NEIGHBORS,
                PathingStrategy.CARDINAL_NEIGHBORS);
        //DIAGONAL_NEIGHBORS);
        //DIAGONAL_CARDINAL_NEIGHBORS);

        if (points.size() == 0)
        {
            return getPosition();
        }

        return points.get(0);
    }

    protected boolean _nextPositionPassHelper(WorldModel world, Point p){
        return p.withinBounds(world) && !world.isOccupied(p);
    }

}
