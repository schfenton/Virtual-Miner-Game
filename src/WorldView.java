import processing.core.PApplet;
import processing.core.PImage;

import java.util.Optional;

public final class WorldView
{
    private static Viewport viewport;
    private static WorldModel world;
    public PApplet screen;
    public int tileWidth;
    public int tileHeight;

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

    public Viewport getViewport(){
        return viewport;
    }

    public WorldModel getWorld(){
        return world;
    }

    public static void shiftView(int colDelta, int rowDelta) {
        int newCol = Functions.clamp(viewport.col + colDelta, 0,
                world.numCols - viewport.numCols);
        int newRow = Functions.clamp(viewport.row + rowDelta, 0,
                world.numRows - viewport.numRows);

        viewport.shift(newCol, newRow);
    }

    public void drawBackground() {
        for (int row = 0; row < this.getViewport().numRows; row++) {
            for (int col = 0; col < this.getViewport().numCols; col++) {
                Point worldPoint = this.getViewport().viewportToWorld(col, row);
                Optional<PImage> image =
                        this.getWorld().getBackgroundImage(worldPoint);
                if (image.isPresent()) {
                    this.screen.image(image.get(), col * this.tileWidth,
                            row * this.tileHeight);
                }
            }
        }
    }

    public void drawEntities() {
        for (Entity entity : this.getWorld().entities) {
            Point pos = entity.position;

            if (this.getViewport().contains(pos)) {
                Point viewPoint = this.getViewport().worldToViewport(pos.x, pos.y);
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
