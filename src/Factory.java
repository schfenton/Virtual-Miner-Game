import processing.core.PConstants;
import processing.core.PImage;

public class Factory {

    private static final String DEFAULT_IMAGE_NAME = "background_default";

    public static ActivityAction createActivityAction(Entity entity, WorldModel world, ImageStore imageStore) {
        return new ActivityAction(entity, world, imageStore);
    }

    public static AnimationAction createAnimationAction(Entity entity, int repeatCount) {
        return new AnimationAction(entity, repeatCount);
    }


    public static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME,
                              imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }

    public static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, PConstants.RGB);
        img.loadPixels();
        for (int i = 0; i < img.pixels.length; i++) {
            img.pixels[i] = color;
        }
        img.updatePixels();
        return img;
    }
}
