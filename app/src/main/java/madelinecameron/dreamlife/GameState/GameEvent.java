package madelinecameron.dreamlife.GameState;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
    public void popMessageToast(Context context) {
        Log.d("DreamLife", "Popping toast");
        Log.d("DreamLife", message);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
