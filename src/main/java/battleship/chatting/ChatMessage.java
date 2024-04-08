package battleship.chatting;

import java.util.*;

public class ChatMessage {
    String message;
    String sender;
    String receiver;
    Calendar date;
    int sent=0;
    int receiced=0;

    public ChatMessage(){}

    public ChatMessage(String message, String sender, String receiver, Calendar date) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public int getSentStatus() {
        return sent;
    }

    public void setSentStatus(int sent) {
        this.sent = sent;
    }

    public int getReceicedStatus() {
        return receiced;
    }

    public void setReceicedStatus(int receiced) {
        this.receiced = receiced;
    }
}
