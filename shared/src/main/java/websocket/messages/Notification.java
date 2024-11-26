package websocket.messages;

import java.util.Objects;

public class Notification extends ServerMessage {
    private final String message;

    public Notification(String message) {
        super(ServerMessageType.NOTIFICATION);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        Notification notification = (Notification) o;
        return Objects.equals(getMessage(), notification.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getMessage());
    }
}
