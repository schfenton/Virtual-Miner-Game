import processing.core.PImage;

import java.util.List;

public class Angel extends MovableEntity {
    public Angel(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod, animationPeriod);
    }

    @Override
    void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

    }
}
