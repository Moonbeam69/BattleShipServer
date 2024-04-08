package battleship.chatting;

import java.util.*;

public class MessageList {

    private List<ChatMessage> messages;

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public MessageList() {
        messages = new ArrayList<>();
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public String toString() {
        String res="";

        for (ChatMessage msg: messages) {

            res += "Message: \n" +
                    "msg: " + msg.getMessage() + "\n" +
                    "sender: " + msg.getSender() + "\n" +
                    "receiver: " + msg.getReceiver() + "\n" +
                    "timestamp: " + msg.getDate();

        }
        return res;
    }
}
