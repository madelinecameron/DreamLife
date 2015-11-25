package madelinecameron.dreamlife.Activities;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;

import madelinecameron.dreamlife.Character.GameCharacter;
import madelinecameron.dreamlife.GameState.GameEvent;
import madelinecameron.dreamlife.GameState.GameState;
import madelinecameron.dreamlife.R;

/**
 * Created by madel on 9/23/2015.
 */
public class GameLoop implements Runnable {
    private Context currentContext;
    private View currentView;
    private Handler uiHandler;
    private volatile boolean isRunning = true;

    public GameLoop(Handler handler, Context context, View view) {
        this.currentContext = context;
        this.currentView = view;
        this.uiHandler = handler;

        Log.d("DreamLife", "Game loop instantiated");
    }

    public void stop() {
        isRunning = false;
    }

    public void run() {
        Log.d("DreamLife", "Trying to run");
        isRunning = true;
        while(isRunning) {
            try {
                GameEvent gameEvent = GameState.getNextGameEvent();
                GameCharacter currentChar = GameState.getGameCharacter();

                HashMap<String, Object> updatedValues = currentChar.heartbeat();
                Message sendMessage = new Message();

                HashMap<String, Object> worldAttr = GameState.worldHeartbeat();
                for(String s : worldAttr.keySet()) {
                    Log.d("DreamLife", s);
                    updatedValues.put(s, worldAttr.get(s));
                }

                updatedValues.put("Job", currentChar.getJob());

                sendMessage.obj = updatedValues;

                uiHandler.sendMessage(sendMessage);
                Thread.sleep(250);  //Update every 250ms
            }
            catch(Exception e) {
                Log.e("DreamLife", e.toString());
            }
        }
    }
}
