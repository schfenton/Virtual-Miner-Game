import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;

public class Sanctuary extends NullEntity {

    private ArrayList<Obstacle> pillars = new ArrayList<>();

    public Sanctuary(String id, Point position, WorldModel world, ImageStore imageStore) {
        super(id, position);
        buildSanctuary(world, imageStore);
    }

    private void buildSanctuary(WorldModel world, ImageStore imageStore){
        for(int x = -1; x <= 1; x++){
            for(int y = -1; y <= 1; y++){
                Point pt = new Point(getPosition().x+x, getPosition().y+y);
                world.setBackground(pt, new Background("marble", imageStore.getImageList("marble")));

                if(!(pt.x == getPosition().x) && !(pt.y == getPosition().y) &&
                        pt.withinBounds(world) && !world.isOccupied(pt)){
                    Obstacle pillar = Factory.createObstacle("pillar_"+pt.y+"_"+pt.x, pt,
                            imageStore.getImageList("pillar"));
                    world.tryAddEntity(pillar);
                    pillars.add(pillar);
                }
            }
        }
    }
}
