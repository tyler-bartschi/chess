package websocket.messages;

import chess.ChessGame;

import java.util.Objects;

public class LoadGameMessage extends ServerMessage {
    private final ChessGame game;

    public LoadGameMessage(ServerMessageType serverMessageType, ChessGame game) {
        super(serverMessageType);
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof LoadGameMessage that)) {
            return false;
        }

        return getServerMessageType() == that.getServerMessageType() && getGame().equals(that.getGame());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType(), getGame().hashCode());
    }
}
