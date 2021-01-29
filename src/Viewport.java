public final class Viewport
{
    private int row;
    private int col;
    private int numRows;
    private int numCols;

    public Viewport(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
    }

    public void shift(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public boolean contains(Point p) {
        return p.y >= getRow() && p.y < getRow() + getNumRows()
                && p.x >= getCol() && p.x < getCol() + getNumCols();
    }
    
    public Point viewportToWorld(int col, int row) {
        return new Point(col + this.getCol(), row + this.getRow());
    }

    public Point worldToViewport(int col, int row) {
        return new Point(col - this.getCol(), row - this.getRow());
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }
}
