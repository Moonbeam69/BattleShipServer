package battleship.service;

import View.*;
import battleship.chatting.*;
import battleship.dao.*;
import battleship.model.*;
import battleship.utils.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.*;

@Service
public class GameService extends Base {

    GameMap gameMap;
    public Map<UUID, String> MapOfPlayers = new HashMap<>();
    public Map<String, List<Ship>> MapOfShips = new HashMap<>();
    public Map<String, Integer>     score = new HashMap<>();
    public Map<String, Integer[][]> attackResultsMap = new HashMap<>();
    Map<String, Object> gameStats = new HashMap<>();
    public boardPane pane;
    List restartList  = new ArrayList<String>();
    List playersJoinedGameList  = new ArrayList<String>();

    JList listOfPlayers;
    DefaultListModel listModel;

    String currentPlayer = "";

    int victoryScore;
    int horizontalGridSize;
    int verticalGridSize;
    public myLogger logger;

    boolean gameOver     = false;
    boolean gameCanStart = false;
    public boolean testMode = false;

    InMemoryDataService dao;

    UserManagement userManagementFrame;


    @Autowired
    public GameService(InMemoryDataService dao) {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.dao = dao;

        logger = new myLogger();
        logger.log("Constructing @Autowired GameService(InMemoryDataService dao)");

        // call this to log context
        getGameContext();

        gameMap = new GameMap(logger, horizontalGridSize, verticalGridSize);

        // create User Mgt frames
        userManagementFrame = new UserManagement(this);

        String frameTitle;
        frameTitle = "BattleShip Server God View";
        if(testMode) frameTitle += " - TEST MODE ACTIVE";
        JFrame godViewBoard = new JFrame(frameTitle);

        // godView create content
        pane = new boardPane(dao);
        pane.setGameMap(gameMap);
        pane.setGameService(this);

        godViewBoard.add(pane);
        godViewBoard.setPreferredSize(new Dimension(900, 1500));
        godViewBoard.pack();
        godViewBoard.setVisible(true);

        // set frame Locations
        userManagementFrame.setLocation((int)(.75*dim.width), (int)(100));
        godViewBoard.setLocation(       (int)(.75*dim.width), (int)(100 + userManagementFrame.getHeight() + 20));
        logger.setLocation(             (int)(.75*dim.width), (int)(100 + userManagementFrame.getHeight() + 20+20));
        logger.setAlwaysOnTop(true);

    }

    public Map<String, Object> getGameContext(){

        Map<String, Object> context = dao.getGameContext();

        victoryScore = (int)context.get("victoryScore");
        horizontalGridSize = (int)context.get("horizontalGridSize");
        verticalGridSize   = (int)context.get("verticalGridSize");

        logger.log("Server context: \n" + context.toString().replace(",", "\t\n"));

        return context;
    }

    public UUID addUser(String username) {
        Integer[][] tmp = new Integer[horizontalGridSize][verticalGridSize];
        for (int i = 0; i<horizontalGridSize; i++) {
            for (int j = 0; j < verticalGridSize; j++) {
                tmp[i][j] = -1;
            }
        }

        logger.log ("addUser called with param: " + username);
        // check with Dao whether user is registered already if so return registered UUID
        UUID uuid = UUID.randomUUID();
        logger.log("UUID generated: " + uuid.toString());

        if (!MapOfPlayers.containsValue(username)) {

            MapOfPlayers.put(uuid, username);
            userManagementFrame.addUser(username, uuid.toString(), PlayerState.LOGGEDIN);
            score.put(uuid.toString(), 0);
            attackResultsMap.put(uuid.toString(), tmp);

            logger.log("Initialising score to 0");
        }

        logger.log ("return value: " + uuid.toString());
        return uuid;
    }

    public void setConfirmedShips(String Stringuid, List<Ship> confirmedShips) {
        int xnew = 0;
        int ynew = 0;

        logger.log ("setConfirmedShips(String uuid, List<Ship> confirmedShips) called with param: \n" + Stringuid + "\n" + confirmedShips.toString().replace(",", "    \n"));

        if (!gameCanStart) {
            // store list of Ships in the MapOfShips main map
            logger.log("Update MapOfShips with confirmedShips");
            MapOfShips.put(Stringuid, confirmedShips);
            pane.setMapOfShips(MapOfShips);
        } else { // during game play a use can submit a new confirmedShipList with an updated FLAG location

            // extract the new x,y of the FLAG
            for (Ship ship: confirmedShips) {
                if (ship.type==ShipType.FLAG) {
                    xnew = ship.getSections()[0].getX();
                    ynew = ship.getSections()[0].getY();
                }
            }

            // assign new x,y to the FLAG in the confirmedShipList
            //List<Ship> ListOfShips; // = MapOfShips.get(Stringuid);
/*            for (Ship ship: ListOfShips) {
                if (ship.type == ShipType.FLAG) {
                    ship.getSections()[0].setX(xnew);
                    ship.getSections()[0].setY(ynew);
                }
            }*/
            List<Ship> ListOfShips = new ArrayList<>();

            for (Map.Entry mapElement : MapOfShips.entrySet()) {
                if (mapElement.getKey().toString().equals(Stringuid)) {

                    ListOfShips = (List<Ship>)mapElement.getValue();

                    for (Ship ship: ListOfShips) {
                        if (ship.type == ShipType.FLAG) {
                            ship.getSections()[0].setX(xnew);
                            ship.getSections()[0].setY(ynew);
                        }
                    }
                }
            }

        }

        logger.log ("repaint boardPane");
        pane.repaintIt();

        if (MapOfShips.size()==2) {
            gameCanStart = true;
            gameOver     = false;
        }
    }

    public String canGameStart() {
        String ret = "NOGO";

        // if both players have confirmed their fleet, game can start
        if (MapOfShips.size() == 2 ) {
             ret = "GO";

            Set set = MapOfPlayers.keySet();

            // determine who goes first
            if (currentPlayer.equals("")) { // check if currentPlayer has already been set

                int size = set.size();
                int item = new Random().nextInt(size);
                int i = 0;

                for (Map.Entry mapElement : MapOfPlayers.entrySet()) {
                    if (i == item) {
                        currentPlayer = mapElement.getKey().toString();
                        pane.currentPlayer = currentPlayer;
                        pane.repaintIt();
                    }
                    i++;
                }
            }
        }
        return ret;
    }

    public int fireCoordinates(int x, int y, String Stringuid) {
        logger.log ("called fireCoordinates(int x, int y, String uuid) called with params: " + x + ", " + y + ", " +  Stringuid);
        Integer[][] tmp;
        boolean sunk;
        int hit = 0;
        Ship damagedShip = null;

        for (Map.Entry mapElement : MapOfShips.entrySet()) {

            if (!mapElement.getKey().toString().equals(Stringuid)) {

                for (Ship ship : (List<Ship>) mapElement.getValue()) {

                    StringBuilder sections = new StringBuilder();


                    // parse all elements looking for single-element hit
                    for (Section s: ship.getSections()) {

                        if(s.getX()==x && s.getY()==y && s.getState()==State.UNDAMAGED) {
                            s.setState(State.DAMAGED);
                            score.put(Stringuid, score.get(Stringuid)+1);

                            logger.log("Hit detected om positon " + x + ", " + y);
                            logger.log("Ship type=" + ship.type.toString());

                            hit = 1;
                            damagedShip = ship;
                        }

/*                        // instant death flag hit
                        if (s.getX()==x && s.getY()==y && ship.type==ShipType.FLAG) {

                            // Sudden death!
                            score.put(Stringuid, 9999);

                            logger.log("##########################");
                            logger.log("      SUDDEN DEATH");
                            logger.log("##########################");

                            hit = 99;
                        }*/

                    }

                    // determine if whole ship is sunk
                    // determine if the ship that was attacked is completely sunk
                    if (damagedShip!=null) {
                        sunk = true;
                        for (Section section : damagedShip.getSections()) {
                            if (section.getState() == State.UNDAMAGED) sunk = false;
                        }
                        if (sunk && damagedShip.type == ShipType.CARRIER) hit = 10;
                        if (sunk && damagedShip.type == ShipType.DESTROYER) hit = 11;
                        if (sunk && damagedShip.type == ShipType.FRIGATE) hit = 12;
                        if (sunk && damagedShip.type == ShipType.SUBMARINE) hit = 13;
                        if (sunk && damagedShip.type == ShipType.FLAG) {
                            score.put(Stringuid, 9999); // this to hit the winning score
                            hit = 99; // this to inform the client pof sudden death condition
                            logger.log("##########################");
                            logger.log("      SUDDEN DEATH");
                            logger.log("##########################");
                        }
                    }

                }
            }

        }


        // update attack result
        tmp = attackResultsMap.get(Stringuid);
        tmp[x][y]= hit>0 ? 1 : 0;
        attackResultsMap.put(Stringuid, tmp);

        // Game switch player
        for (Map.Entry mapElement: MapOfPlayers.entrySet()) {
            if (!mapElement.getKey().toString().equals(Stringuid)) {
                currentPlayer = mapElement.getKey().toString();
                logger.log ("Switch player. New player: " + currentPlayer);
            }
        }

        pane.setMapOfShips(MapOfShips);
        pane.setScore(score);
        pane.repaintIt();
        logger.log ("repaint boardPane");

/*        0 miss
        1 simgle hit
        10 carrier sunk
        11 destroyer sunk
        12 frigate sunk
        13 sub sunk
        99 instant death*/
        return hit;
    }

    public Map<UUID, String> getMapOfPlayers() {
        return MapOfPlayers;
    }

    public int getTurn(String Stringuid) {
        logger.log ("Called getTurn(String Stringuid) with param: " + Stringuid);
        int res = 0;

        if (MapOfPlayers.size()==2 && Stringuid.equals(currentPlayer) && gameCanStart)  res = 1;

        if (testMode) res = 1;

        return res;
    }

    public Map<String, Object> getGameStats(String Stringuid) {
        logger.log ("Called public Map<String, Object> getGameStats(String Stringuid) with param: " + Stringuid);


        //initialise
        gameStats.put("winner", 0);
        gameStats.put("loser", 0);

        if ((int)score.get(Stringuid) >= victoryScore) {
            gameStats.put("winner", 1);
            gameOver = true;
        } else {
            gameStats.put("winner", 0);
        }
        if(gameOver && (int)score.get(Stringuid) < victoryScore) {
            gameStats.put("loser", 1);
        }

        gameStats.put("score", score.get(Stringuid));

        logger.log ("Return value: " + gameStats);
        return gameStats;
    }

    public Map<UUID, String> getPlayers() {
        return MapOfPlayers;
    }

    public Map<String, List<Ship>> getMapOfShips(){
        return MapOfShips;
    }

    public ShipList getListOfShips(String Stringuid) {
        ShipList list = new ShipList();
        List<Ship> listOfShip = new ArrayList<>();

        for (Map.Entry mapElement : MapOfShips.entrySet()) {
            if (mapElement.getKey().toString().equals(Stringuid)) {
                listOfShip = (List<Ship>)mapElement.getValue();
            }
        }
        list.setList(listOfShip);

        return list;
    }

    public Integer[][] getAttackArray(String Stringuid) {
        logger.log ("called getAttackArray(String Stringuid) with Stringuid=" + Stringuid);
/*        Integer[][] tmp = new Integer[horizontalGridSize][verticalGridSize];

/         for (Map.Entry mapElement : attackResultsMap.entrySet()) {
            if (mapElement.getKey().toString().equals(Stringuid)) {
                tmp = (Integer[][])mapElement.getValue();
            }
        }
        return tmp;*/

        return attackResultsMap.get(Stringuid);
    }

    public String getEnemyUID(String Stringuid) {
        logger.log ("called getEnemyUID(String Stringuid) with Stringuid=" + Stringuid);
        String res="";

        if (MapOfPlayers.size()==2) { // if more than 2, cannot determine who opponent is
            for (Map.Entry mapElement : MapOfPlayers.entrySet()) {
                if (!mapElement.getKey().toString().equals(Stringuid)) {
                    res = mapElement.getKey().toString();
                }
            }
        }
        return res;
    }

   public GameMap getGameMap() {
        return gameMap;
    }

    public int setRestart(String Stringuid, int flag) {
        Integer[][] tmp = new Integer[horizontalGridSize][verticalGridSize];
        for (int i = 0; i<horizontalGridSize; i++) {
            for (int j = 0; j < verticalGridSize; j++) {
                tmp[i][j] = -1;
            }
        }

        // a player wants to play again
        if (flag==1 && !restartList.contains(Stringuid)) {
            restartList.add(Stringuid);
        // if one player does not want to play again, the game ends
        } else {
            kickUser(UUID.fromString(Stringuid));
        }

        // both players agree to restart
        if (restartList.size()==2) {

            MapOfShips.clear();

            // initialise the score for each player to zero, like in addUser for the first game
            for(Map.Entry mapElement: score.entrySet()){
                mapElement.setValue(0);
            }

            // initialise the attackResultsMap with an empty tmp array like in addUser for the first game
            for(Map.Entry mapElement: attackResultsMap.entrySet()){
                mapElement.setValue(tmp);
            }

            gameStats.clear();
            gameOver = true; // will be reset when both players have submitted their confirmedShips
            gameCanStart = false; // this prevents a turn indicator from being issued so players can set up
        }

        return 1;
    }

    //playerJoinedGameList tracks number of unique player uids who clicked "engage" in the Lobby
    public int playerJoinedGame(String Stringuid) {

        if (!playersJoinedGameList.contains(Stringuid)) {
            playersJoinedGameList.add(Stringuid);

            userManagementFrame.updateUserStatus(Stringuid, PlayerState.INGAME);
        }
        return playersJoinedGameList.size();
    }

    // Manage players on server
    public void kickUser(UUID uid) {
        //MapOfPlayers.remove(UUID.fromString(value.split("=")[1])); // remove by uuid

        logger.log("kickUser(UUID uid)");
        logger.log("called MapOfPlayers.remove(uid) with uid=" + uid.toString());

        MapOfPlayers.remove(uid); // remove by uuid

        logger.log("called MapOfShips.remove(uid.toString()); with uid=" + uid.toString());
        MapOfShips.remove(uid.toString()); // remove by uuid
        pane.setMapOfShips(MapOfShips);

        score.clear();

        attackResultsMap.clear();

        playersJoinedGameList.remove(uid.toString());

        gameCanStart = false;
        gameOver     = true;

        pane.repaintIt();
    }

    public void kickAll() {
        MapOfPlayers.clear();
        MapOfShips.clear();
        score.clear();
        attackResultsMap.clear();
        gameOver=false;
        restartList.clear();
        playersJoinedGameList.clear();
        currentPlayer = "";
    }

    public int Ping(String Stringuid) {

        userManagementFrame.ping(Stringuid);

        return 1;
    }

    public myLogger getLogger() {
        return logger;
    }

    // Chat methods
    public MessageList getConversation(String key){

        logger.log("called getConversation(String key) with param" + key);

        return dao.getConversation(key);
    }

    public void updateConversation(String key, ChatMessage message){

        logger.log("called updateConversation(String key, ChatMessage message) with params: key=" + key + " message= "+ message.toString());

        dao.updateConversation(key, message);
    }

}