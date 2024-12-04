package websocket;

import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

public class NotificationHandler {
    public void handleNotification(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME:
                LoadGame loadGame = (LoadGame) message;
                System.out.println(loadGame.getGame().toString());
                break;
            case ERROR:
                break;
            case NOTIFICATION:
                Notification notification = (Notification) message;
                System.out.println("Notification: " + notification.getMessage());
                break;
        }
    }
}
