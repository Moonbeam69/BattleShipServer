package battleship.model;

import java.util.*;

public class Player {
    UUID uuid;
    String userName;

    public Player(UUID uuid) {
        this.uuid = uuid;
    }

    public Player(String userName) {
        this.userName = userName;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
