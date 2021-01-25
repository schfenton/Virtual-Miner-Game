import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class WorldModel
{
    public int numRows;
    public int numCols;
    public Background background[][];
    public Entity occupancy[][];
    public Set<Entity> entities;

    public WorldModel(int numRows, int numCols, Background defaultBackground) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.background = new Background[numRows][numCols];
        this.occupancy = new Entity[numRows][numCols];
        this.entities = new HashSet<>();

        for (int row = 0; row < numRows; row++) {
            Arrays.fill(this.background[row], defaultBackground);
        }
    }

    public void setOccupancyCell(Point pos, Entity entity)
    {
        occupancy[pos.y][pos.x] = entity;
    }

    public Entity getOccupancyCell(Point pos) {
        return occupancy[pos.y][pos.x];
    }

    /*
       Assumes that there is no entity currently occupying the
       intended destination cell.
    */
    public void addEntity(Entity entity) {
        if (entity.position.withinBounds(this)) {
            setOccupancyCell(entity.position, entity);
            entities.add(entity);
        }
    }

    public void tryAddEntity(Entity entity) {
        if (entity.position.isOccupied(this)) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        addEntity(entity);
    }
}
