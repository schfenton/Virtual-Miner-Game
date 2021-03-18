import processing.core.*;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public final class VirtualWorld extends PApplet
{
    private static final int TIMER_ACTION_PERIOD = 100;

    private static final int VIEW_WIDTH = 640;
    private static final int VIEW_HEIGHT = 480;
    private static final int TILE_WIDTH = 32;
    private static final int TILE_HEIGHT = 32;
    private static final int WORLD_WIDTH_SCALE = 2;
    private static final int WORLD_HEIGHT_SCALE = 2;

    private static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    private static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
    public static final int WORLD_COLS = VIEW_COLS * WORLD_WIDTH_SCALE;
    public static final int WORLD_ROWS = VIEW_ROWS * WORLD_HEIGHT_SCALE;

    private static final String IMAGE_LIST_FILE_NAME = "imagelist";
    private static final int DEFAULT_IMAGE_COLOR = 0x808080;

    private static final String LOAD_FILE_NAME = "world.sav";

    private ImageStore imageStore;
    private WorldModel world;
    private WorldView view;
    private EventScheduler scheduler;

    private long nextTime;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        this.imageStore = new ImageStore(
                Factory.createImageColored(TILE_WIDTH, TILE_HEIGHT,
                                   DEFAULT_IMAGE_COLOR));
        this.world = new WorldModel(WORLD_ROWS, WORLD_COLS,
                                    Factory.createDefaultBackground(imageStore));
        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH,
                                  TILE_HEIGHT);
        this.scheduler = new EventScheduler(Parser.timeScale);

        Functions.loadImages(IMAGE_LIST_FILE_NAME, imageStore, this);
        Functions.loadWorld(world, LOAD_FILE_NAME, imageStore);

        scheduleActions(world, scheduler, imageStore);

        nextTime = System.currentTimeMillis() + TIMER_ACTION_PERIOD;
    }

    public void draw() {
        long time = System.currentTimeMillis();
        if (time >= nextTime) {
            this.scheduler.updateOnTime(time);
            nextTime = time + TIMER_ACTION_PERIOD;
        }

        view.drawViewport();
    }

    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP:
                    dy = -1;
                    break;
                case DOWN:
                    dy = 1;
                    break;
                case LEFT:
                    dx = -1;
                    break;
                case RIGHT:
                    dx = 1;
                    break;
            }
            WorldView.shiftView(dx, dy);
        }
    }

    public void mousePressed() {
        Point pressed = WorldView.getViewport().viewportToWorld(mouseX/TILE_WIDTH, mouseY/TILE_HEIGHT);
        if(!world.isOccupied(pressed)) {

            God god = Factory.createGod(pressed, imageStore.getImageList(Factory.GOD_KEY));
            world.tryAddEntity(god);
            god.scheduleActions(scheduler, world, imageStore);

            Sanctuary sanctuary = Factory.createSanctuary(pressed, world, imageStore);
            world.tryAddEntity(sanctuary);

            redraw();
        }
    }

    private static void scheduleActions(
            WorldModel world, EventScheduler scheduler, ImageStore imageStore)
    {
        for (Entity entity : world.getEntities()) {
            if (entity instanceof ActiveEntity) {
                ((ActiveEntity) entity).scheduleActions(scheduler, world, imageStore);
            }
        }
    }

    public static void main(String[] args) {
        Parser.parseCommandLine(args);
        PApplet.main(VirtualWorld.class);
    }
}
