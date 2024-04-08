package battleship.dao;

import battleship.chatting.*;
import org.springframework.stereotype.*;

import java.util.*;

@Repository
public class InMemoryDataService {
    Map<String, List<ChatMessage>> messageStore = new HashMap<>();
    public void createConversation(){};

    public MessageList getConversation(String key) {
        //logger.log("Called getConversation(String key) with key=" +key);

        MessageList ml = new MessageList();

        for (Map.Entry mapElement : messageStore.entrySet()) {

            if (mapElement.getKey().toString().equals(key)) {
                ml.setMessages( (List<ChatMessage>)mapElement.getValue() );
                //logger.log("Found conversation: " + (List<ChatMessage>)mapElement.getValue().toString().subSequence(0, 20) + "...");

                return ml;
            }
        }

        return ml;
    }

    public void updateConversation(String key, ChatMessage message) {
        boolean done = false;

        //logger.log("Called updateConversation(String key, ChatMessage message) with key=" +key + "\nmessage = " + message);

        // check for an existing conversation to update, use key
        for (Map.Entry mapElement : messageStore.entrySet()) {

            if (mapElement.getKey().toString().equals(key)) {
                ((List<ChatMessage>)mapElement.getValue()).add(message);
                done=  true;
            }
        }

        // create a new conversation if one wasn't found before
        if(!done) {
            List<ChatMessage> newConversation = new ArrayList<>();
            newConversation.add(message);
            messageStore.put(key, newConversation);
        }

    }

    public Map<String, Object> getGameContext() {
        Map<String, Object> context = new HashMap<>();


        context.put("horizontalGridSize", 20);
        context.put("verticalGridSize",   20);

        context.put("squarewidth",        30);
        context.put("TOPLEFT",            50);
        context.put("TOPDOWN",            100);
        context.put("BRDSEPERATOR",       (int)context.get("verticalGridSize") * (int)context.get("squarewidth") + 50);

        context.put("width",              (int)context.get("TOPLEFT") + (int)context.get("horizontalGridSize") * (int)context.get("squarewidth") + 500);
        context.put("height",             (int)context.get("TOPDOWN") + 2* (int)context.get("verticalGridSize") * (int)context.get("squarewidth") + (int)context.get("verticalGridSize") + 250);

        // Full game
/*        context.put("totalCarriers",      2);
        context.put("totalDestroyer",     3);
        context.put("totalFrigate",       3);
        context.put("totalSubmarine",     3);
        context.put("totalSweeper",       0);
        context.put("totalFlag",          1);*/

        // Mid-range
        context.put("totalCarriers",      0);
        context.put("totalDestroyer",     1);
        context.put("totalFrigate",       1);
        context.put("totalSubmarine",     0);
        context.put("totalSweeper",       0);
        context.put("totalFlag",          1);

        // Quick
/*        context.put("totalCarriers",      0);
        context.put("totalDestroyer",     0);
        context.put("totalFrigate",       0);
        context.put("totalSubmarine",     1);
        context.put("totalSweeper",       0);
        context.put("totalFlag",          1);*/

        context.put("victoryScore",       (int)context.get("totalCarriers") * 8+
                                          (int)context.get("totalDestroyer")* 4+
                                          (int)context.get("totalFrigate")  * 3+
                                          (int)context.get("totalSubmarine")* 2+
                                          (int)context.get("totalSweeper")  * 1);

        return context;
    }

}
