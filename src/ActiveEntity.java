import processing.core.PImage;

import java.util.List;

abstract class ActiveEntity extends Entity {

    private final int actionPeriod;

    public ActiveEntity(String id, Point position, List<PImage> images, int actionPeriod) {
        super(id, position, images);
        this.actionPeriod = actionPeriod;
    }

    protected int getActionPeriod() {
        return actionPeriod;
    }

    public void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore) {
        scheduler.scheduleEvent(this,
                Factory.createActivityAction(this, world, imageStore),
                this.actionPeriod);
    }

    abstract void executeActivity(WorldModel world,
                                ImageStore imageStore,
                                EventScheduler scheduler);
}
