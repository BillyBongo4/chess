package websocket;

import websocket.messages.Notification;
import websocket.messages.ServerMessage;

public class NotificationHandler {
    public void handleNotification(ServerMessage message) {
        if (message.getClass() == Notification.class) {
            handleNotificationMessage((Notification) message);
        } else {
            handleOtherMessageTypes(message);
        }
    }

    private void handleNotificationMessage(Notification message) {
        System.out.println("Notification: " + message.getMessage());
    }

    private void handleOtherMessageTypes(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME:
                break;
            case ERROR:
                break;
            case NOTIFICATION:
                handleNotificationMessage((Notification) message);
                break;
        }
    }
}
