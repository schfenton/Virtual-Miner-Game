import processing.core.PImage;

import java.util.List;

public class Blacksmith implements Entity {
    private final String id;
    private Point position;
    private final List<PImage> images;
    private int imageIndex = 0;

    public Blacksmith(String id, Point position, List<PImage> images){
        this.id = id;
        this.position = position;
        this.images = images;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public PImage getCurrentImage() {
        return images.get(imageIndex);
    }
}
