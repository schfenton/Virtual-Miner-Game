import processing.core.PImage;

import java.util.List;

abstract class AnimatedEntity extends ActiveEntity {

    private final int animationPeriod;

    public AnimatedEntity(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod) {
        super(id, position, images, actionPeriod);
        this.animationPeriod = animationPeriod;
    }

    public int getAnimationPeriod() {
        return animationPeriod;
    }

    public void nextImage(){
        setImageIndex((getImageIndex() + 1) % getImages().size());
    }

    public void scheduleActions(
            EventScheduler scheduler,
            WorldModel world,
            ImageStore imageStore) {
        scheduler.scheduleEvent(this,
                Factory.createActivityAction(this, world, imageStore),
                this.getActionPeriod());
        scheduler.scheduleEvent(this,
                Factory.createAnimationAction(this, 0), this.getAnimationPeriod());
    }
}
