package battleship.model;

public class Section {

    private int x;
    private int y;
    private State state;

    public Section(){}

    public Section(int x, int y) {
        this.x = x;
        this.y = y;
        this.state = State.UNDAMAGED;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }
}
