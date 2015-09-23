package madelinecameron.dreamlife.GameState;

/**
 * Created by madel on 9/16/2015.
 */
public class GameEvent {
    private String message;
    private GameEventType type;

    public GameEvent(String message, GameEventType type) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() { return message; }
    public GameEventType getType() { return type; }
}
