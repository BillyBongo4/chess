package websocket;

import websocket.messages.Notification;
import websocket.messages.ServerMessage;

public class NotificationHandler {
    public void handleNotification(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME:
                System.out.println("TEST");
                break;
            case ERROR:
                break;
            case NOTIFICATION:
                Notification notification = (Notification) message;
                System.out.println("Notification: " + notification);
                break;
        }
    }
}
