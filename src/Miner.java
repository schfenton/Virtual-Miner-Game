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

    protected Point nextPosition(
            WorldModel world, Point destPos)
    {
        List<Point> points;
        PathingStrategy strat = new AStarPathingStrategy();

        points = strat.computePath(getPosition(), destPos,
                p ->  p.withinBounds(world) && !world.isOccupied(p),
                PathingStrategy.NEIGHBORS,
                PathingStrategy.CARDINAL_NEIGHBORS);
        //DIAGONAL_NEIGHBORS);
        //DIAGONAL_CARDINAL_NEIGHBORS);

        if (points.size() == 0)
        {
            System.out.println("No path found");
            return getPosition();
        }

        return points.get(0);
    }

}
