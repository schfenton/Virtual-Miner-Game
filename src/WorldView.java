import processing.core.PApplet;
import processing.core.PImage;

import java.util.Optional;

public final class WorldView
{
    private static Viewport viewport;
    private static WorldModel world;
    private final PApplet screen;
    private final int tileWidth;
    private final int tileHeight;

    public WorldView(
            int numRows,
            int numCols,
            PApplet screen,
            WorldModel world,
            int tileWidth,
            int tileHeight)
    {
        this.screen = screen;
        this.world = world;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.viewport = new Viewport(numRows, numCols);
    }

    public static Viewport getViewport(){
        return viewport;
    }

    public static WorldModel getWorld(){
        return world;
    }

    public static void shiftView(int colDelta, int rowDelta) {
        int newCol = Functions.clamp(viewport.getCol() + colDelta, 0,
                world.getNumCols() - viewport.getNumCols());
        int newRow = Functions.clamp(viewport.getRow() + rowDelta, 0,
                world.getNumRows() - viewport.getNumRows());

        viewport.shift(newCol, newRow);
    }

    private void drawBackground() {
        for (int row = 0; row < viewport.getNumRows(); row++) {
            for (int col = 0; col < viewport.getNumCols(); col++) {
                Point worldPoint = viewport.viewportToWorld(col, row);
                Optional<PImage> image =
                        world.getBackgroundImage(worldPoint);
                if (image.isPresent()) {
                    this.screen.image(image.get(), col * this.tileWidth,
                            row * this.tileHeight);
                }
            }
        }
    }

    private void drawEntities() {
        for (Entity entity : world.getEntities()) {
            Point pos = entity.getPosition();

            if (viewport.contains(pos)) {
                Point viewPoint = viewport.worldToViewport(pos.x, pos.y);
                this.screen.image(entity.getCurrentImage(),
                        viewPoint.x * this.tileWidth,
                        viewPoint.y * this.tileHeight);
            }
        }
    }

    public void drawViewport() {
        drawBackground();
        drawEntities();
    }
}
