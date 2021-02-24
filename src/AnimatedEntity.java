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
}
