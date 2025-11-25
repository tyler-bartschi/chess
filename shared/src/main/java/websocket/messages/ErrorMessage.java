package websocket.messages;

import java.util.Objects;

public class ErrorMessage extends ServerMessage {
    private final String errorMessage;

    public ErrorMessage(ServerMessageType serverMessageType, String errorMessage) {
        super(serverMessageType);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ErrorMessage that)) {
            return false;
        }

        return getServerMessageType() == that.getServerMessageType() && getErrorMessage().equals(that.getErrorMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType(), getErrorMessage());
    }
}
