package service;

import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.HashMap;

public class ConnectionContainer {

    private final HashMap<Integer, ArrayList<Tuple>> gameTracker;

    private record Tuple(String username, Session session) {
        public void send(String message) {
            try {
                session.getRemote().sendString(message);
            } catch (Throwable ex) {
                System.out.println("Error sending websocket message: " + ex.getMessage());
            }
        }
    }

    public ConnectionContainer() {
        gameTracker = new HashMap<>();
    }

    public void addUser(int gameID, String username, Session session) {
        if (!gameTracker.containsKey(gameID)) {
            gameTracker.put(gameID, new ArrayList<Tuple>());
        }
        gameTracker.get(gameID).add(new Tuple(username, session));
    }

    public void removeUser(int gameID, String username, Session session) {
        checkIfGameExists(gameID);
        gameTracker.get(gameID).remove(new Tuple(username, session));
    }

    public void sendToAll(int gameID, String message) {
        checkIfGameExists(gameID);
        ArrayList<Tuple> users = gameTracker.get(gameID);
        for (Tuple user : users) {
            user.send(message);
        }
    }

    public void sendToAllExcept(int gameID, String exceptUsername, String message) {
        checkIfGameExists(gameID);
         ArrayList<Tuple> users = gameTracker.get(gameID);
         for (Tuple user : users) {
             if (!user.username.equals(exceptUsername)) {
                 user.send(message);
             }
         }
    }

    private void checkIfGameExists(int gameID) {
        if (!gameTracker.containsKey(gameID)) {
            throw new RuntimeException("Error: no game by that gameID in memory");
        }
    }
}
