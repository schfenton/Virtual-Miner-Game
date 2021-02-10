import processing.core.PImage;

import java.util.List;

public class Obstacle implements Entity {
    private final String id;
    private Point position;
    private final List<PImage> images;
    private int imageIndex = 0;

    public Obstacle(String id, Point position, List<PImage> images){
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
