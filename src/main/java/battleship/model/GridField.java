package battleship.model;

public class GridField {
    int x;
    int y;
    boolean moveable=false;



    public GridField(int x, int y, boolean moveable) {
        this.x = x;
        this.y = y;
        this.moveable = moveable;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;


    }

    public void setMoveable(boolean moveable) {
        this.moveable = moveable;
    }

    public boolean isMoveable() {
        return moveable;
    }
}
