package battleship.api;

import battleship.chatting.*;
import battleship.model.*;
import battleship.service.*;
import battleship.utils.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.env.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@SuppressWarnings("SameReturnValue")
@RestController
public class Controller implements Runnable {

    private final GameService gameService;
    final myLogger logger;
    @Autowired
    private Environment env;

    public int msgreceived = 0;

    @Autowired
    public Controller(GameService gameService) {
        this.gameService = gameService;
        this.logger = gameService.getLogger();

        Thread thread = new Thread(this);
        thread.start();
    }


    // Start of game

    @RequestMapping(value = "/api/v1/getcontext", method = RequestMethod.GET)
    public Map<String, Object> getContext(){
        logger.log("called /api/v1/getcontext");
        msgreceived++;

        Map<String, Object> res = gameService.getGameContext();

        logger.log("responded");
        return res;
    }

    @RequestMapping(value = "/api/v1/loginToken/{username}", method = RequestMethod.GET)
    public String loginToken( @PathVariable("username") String username) {
        logger.log("/api/v1/loginToken/{username} with username="+ username);
        msgreceived++;

        String res = gameService.addUser(username).toString();

        logger.log("responded");
        return res;
    }

    @RequestMapping(value = "/api/v1/canGameStart", method = RequestMethod.GET)
    public String waitingFoGame() {
        logger.log("/api/v1/canGameStart/");
        msgreceived++;

        String res =gameService.canGameStart();

        logger.log("responded with res=" + res);
        return res;
    }

    @RequestMapping(value = "/api/v1/confirmShips/{uid}", method = RequestMethod.POST)
    public void sendConfirmShips(@PathVariable("uid") String Stringuid, @RequestBody List<Ship> confirmedShips) {
        logger.log("/api/v1/confirmShips/{uid} with uid="+ Stringuid);
        msgreceived++;

        gameService.setConfirmedShips(Stringuid, confirmedShips);

        logger.log("done");
    }

    // game loop

    @RequestMapping(value = "/api/v1/Fire/{x}/{y}/{uid}", method = RequestMethod.GET)
    public int fireSolution(@PathVariable("x") int x_coord, @PathVariable("y") int y_coord, @PathVariable("uid") String Stringuid) {
        logger.log("/api/v1/Fire/{x}/{y}/{uid} with x=" + getCharForNumber(x_coord) + ", y="+ y_coord +" uid="+ Stringuid);
        msgreceived++;

        int res = gameService.fireCoordinates(x_coord, y_coord, Stringuid);

        logger.log("responded, res=" + res);
        return res;
    }

    private String getCharForNumber(int i) {
        return i > 0 && i < 27 ? String.valueOf((char)(i + 64)) : null;
    }

    @RequestMapping(value = "/api/v1/getturn/{uid}", method = RequestMethod.GET)
    public int getTurn(@PathVariable("uid") String Stringuid) {
        logger.log("/api/v1/getturn/{uid} with uid="+ Stringuid);
        msgreceived++;

        int res = gameService.getTurn(Stringuid); // 1=yes, 0=no

        logger.log("responded with res=" + res);
        return res;
    }

    @RequestMapping(value = "/api/v1/ping/{uid}", method = RequestMethod.GET)
    public int Ping(@PathVariable("uid") String Stringuid) {
        logger.log("/api/v1/ping/{uid} with uid="+ Stringuid);
        msgreceived++;

        int res = gameService.Ping(Stringuid);

        return res;
    }

    @RequestMapping(value = "/api/v1/getGameStats/{uid}", method = RequestMethod.GET)
    public Map<String, Object> getGameStats(@PathVariable("uid") String Stringuid) {
        logger.log("/api/v1/getGameStats/{uid} with uid="+ Stringuid);
        msgreceived++;

        Map<String, Object> res = gameService.getGameStats(Stringuid);

        logger.log("responded");
        return res;
    }

    @RequestMapping(value = "/api/v1/getPlayers", method = RequestMethod.GET)
    public Map<UUID, String> getPlayers() {
        Map<UUID, String> res;
        logger.log("Received HTTP GET: /api/v1/getPlayers " );
        msgreceived++;

        res = gameService.getPlayers();

        return res;
    }

    @RequestMapping(value = "/api/v1/getListOfShips/{uid}", method = RequestMethod.GET)
    public ShipList getListOfShips(@PathVariable("uid") String Stringuid) {
        logger.log("Received HTTP GET: /api/v1/getListOfShips/{uid} with uid=" + Stringuid);
        msgreceived++;

        ShipList list = gameService.getListOfShips(Stringuid);

        logger.log("responded");
        return list;
    }

    @RequestMapping(value = "/api/v1/getAttackArray/{uid}", method = RequestMethod.GET)
    public Integer[][] getAttackArray(@PathVariable("uid") String Stringuid) {
        logger.log ("Received HTTP GET:/api/v1/getAttackArray/{uid}, with uid="+Stringuid);
        msgreceived++;
        Integer[][] res = gameService.getAttackArray(Stringuid);

        logger.log("responded");
        return res;
    }

    @RequestMapping(value = "/api/v1/getEnemyUID/{uid}", method = RequestMethod.GET)
    public String getEnemyUID(@PathVariable("uid") String Stringuid) {
        logger.log ("Received HTTP GET:/api/v1/getAttackArray/{uid}, with uid="+Stringuid);
        msgreceived++;

        String res =gameService.getEnemyUID(Stringuid);

        logger.log ("responded");
        return res;

    }

    @RequestMapping(value = "/api/v1/version", method = RequestMethod.GET)
    public String getServerVersion() {
        logger.log("/api/v1/version");
        msgreceived++;

        return "0.0.2";
    }

    @RequestMapping(value = "/api/v1/getmap", method = RequestMethod.GET)
    public GameMap getGameMap() {
        logger.log("/api/v1/getmap");
        msgreceived++;

        return gameService.getGameMap();
    }

    @RequestMapping(value = "/api/v1/setRestart/{uid}/{flag}", method = RequestMethod.GET)
    public Integer setRestart(@PathVariable("uid") String Stringuid, @PathVariable("flag") int flag) {
        logger.log("Received HTTP GET: /api/v1/setRestart/{uid}/{flag} with uid="+Stringuid +", flag=" + flag);
        msgreceived++;

        return gameService.setRestart(Stringuid, flag);
    }

    @RequestMapping(value = "/api/v1/playerJoinedGame/{uid}", method = RequestMethod.GET)
    public int playerJoinedGame(@PathVariable("uid") String Stringuid) {
        logger.log("Received HTTP GET: /api/v1/playerJoinedGame/{uid} with uid="+Stringuid);
        msgreceived++;

        return gameService.playerJoinedGame(Stringuid);
    }


    // Test

    @GetMapping("/property")
    public String getPropertyValue(@RequestParam("key") String key) {
        msgreceived++;
        String keyValue = env.getProperty(key);

        logger.log("Version: " + keyValue);

        return keyValue;
    }


    // Mail

    //@GetMapping(path = "{key}")
    @RequestMapping(value = "/api/v1/getConversation/{key}", method = RequestMethod.GET)
    public MessageList getConversation(@PathVariable("key") String key ) {
        logger.log("Received HTTP GET: /api/v1/getConversation/{key} with key="+key);
        msgreceived++;

        MessageList res = gameService.getConversation(key);

        logger.log("responded");
        return res;
    }

    //@GetMapping(path = "{key}")
    @RequestMapping(value = "/api/v1/updateConversation/{key}", method = RequestMethod.POST)
    public void addMessage(@PathVariable("key") String key, @RequestBody ChatMessage message ) {
        logger.log("Received HTTP POST: /api/v1/updateConversation/{key} with key=" + key );
        logger.log("@RequestBody = " + message);
        msgreceived++;

        gameService.updateConversation(key, message);

        logger.log("responded");
    }

    @Override
    public void run() {
        int n = 0;
        long start = 0L;
        long end = 0L;

        while (true) {

            n++;
            if (n>9) {
                end = System.nanoTime();
                logger.log("Performance time interval: " + (end-start)/1000000d + "ms");
                logger.log("Message received: " + msgreceived);
                logger.log("Message/sec: " + msgreceived/ ((end-start)/1000000000d) );
                start = System.nanoTime();
                n = 0;
                msgreceived = 0;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}

// test messages
/*        List<ChatMessage> res = new ArrayList<>(); // this comes from the server
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 1);
        res.add(new ChatMessage("Hello", "A", "B", calendar));
        calendar.add(Calendar.SECOND, 2);
        res.add(new ChatMessage("Hello back", "B", "A", calendar));
        calendar.add(Calendar.SECOND, 3);
        res.add(new ChatMessage("Fancy a game?", "A", "B", calendar));
        calendar.add(Calendar.SECOND, 4);
        res.add(new ChatMessage("sure why not", "B", "A", calendar));
        calendar.add(Calendar.SECOND, 5);
        res.add(new ChatMessage("Wanna play now?", "B", "A", calendar));*/