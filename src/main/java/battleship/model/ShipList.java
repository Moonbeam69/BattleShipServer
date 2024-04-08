package battleship.model;

import java.util.*;

public class ShipList {
    List<Ship> list;

    public ShipList(){}

    public ShipList(List<Ship> list) {
        this.list = list;
    }

    public List<Ship> getList() {
        return list;
    }

    public void setList(List<Ship> list) {
        this.list = list;
    }

    public String toString() {
        String res="";

        for (Ship ship: list) {

            res += ship.type.toString() + ": ";

            for (Section section: ship.getSections()) {
                if (section.getState().toString().equals(State.UNDAMAGED)) {
                    res += "O";
                } else {
                    res += "X";
                }
            }
            res += "\n";
        }


        return res;
    }

}
