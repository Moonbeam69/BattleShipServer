package battleship.model;

import java.awt.*;

public class Ship {
    public ShipType type;
    public Section[] sections;

    public Ship(ShipType type, Section[] sections) {
        this.type = type;
        this.sections = sections;
    }

    public ShipType getType() {
        return type;
    }

    public void setType(ShipType type) {
        this.type = type;
    }

    public Section[] getSections() {
        return sections;
    }

    public void setSections(Section[] sections) {
        this.sections = sections;
    }

    public void moveTo(Point newP) {
        int x_offset = newP.x-sections[0].getX();
        int y_offset = newP.y-sections[0].getY();

        for (int i=0; i< sections.length; i++) {
            sections[i].setX ( sections[i].getX() + x_offset );
            sections[i].setY ( sections[i].getY() + y_offset );
        }
    }

    public void rotate_90() {
        int tmp;

        int t_x = sections[0].getX();
        int t_y = sections[0].getY();

        // translate to origin
        for (Section s: sections) {
            s.setX(s.getX() - t_x);
            s.setY(s.getY() - t_y);
        }

        // rotate
        for (Section s: sections) {
            tmp = s.getX();
            s.setX(s.getY());
            s.setY(tmp);
        }
        // translate to original position
        for (Section s: sections) {
            s.setX(s.getX() + t_x);
            s.setY(s.getY() + t_y);
        }
    }

    public static Section[] getCarrier(int x_bord_coord, int y_bord_coord) {
        Section[] ship = new Section[8];
        ship[0] = new Section(x_bord_coord + 0, y_bord_coord);
        ship[1] = new Section(x_bord_coord + 1, y_bord_coord);
        ship[2] = new Section(x_bord_coord + 2, y_bord_coord);
        ship[3] = new Section(x_bord_coord + 3, y_bord_coord);
        ship[4] = new Section(x_bord_coord + 0, y_bord_coord + 1);
        ship[5] = new Section(x_bord_coord + 1, y_bord_coord + 1);
        ship[6] = new Section(x_bord_coord + 2, y_bord_coord + 1);
        ship[7] = new Section(x_bord_coord + 3, y_bord_coord + 1);

        return ship;
    }

    public static Section[] getDestroyer(int x_bord_coord, int y_bord_coord) {
        Section[]ship = new Section[4];
        ship[0] = new Section(x_bord_coord, y_bord_coord);
        ship[1] = new Section(x_bord_coord + 1, y_bord_coord);
        ship[2] = new Section(x_bord_coord + 2, y_bord_coord);
        ship[3] = new Section(x_bord_coord + 3, y_bord_coord);

        return ship;
    }

    public static Section[] getFrigate(int x_bord_coord, int y_bord_coord) {
        Section[] ship = new Section[3];
        ship[0] = new Section(x_bord_coord, y_bord_coord);
        ship[1] = new Section(x_bord_coord + 1, y_bord_coord);
        ship[2] = new Section(x_bord_coord + 2, y_bord_coord);

        return ship;
    }

    public static Section[] getSubmarine(int x_bord_coord, int y_bord_coord) {
        Section[] ship = new Section[2];
        ship[0] = new Section(x_bord_coord, y_bord_coord);
        ship[1] = new Section(x_bord_coord + 1, y_bord_coord);

        return ship;
    }

    public static Section[] getSweeper(int x_bord_coord, int y_bord_coord) {
        Section[] ship = new Section[1];
        ship[0] = new Section(x_bord_coord, y_bord_coord);

        return ship;
    }
}
