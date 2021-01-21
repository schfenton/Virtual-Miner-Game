import processing.core.PApplet;

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
}
