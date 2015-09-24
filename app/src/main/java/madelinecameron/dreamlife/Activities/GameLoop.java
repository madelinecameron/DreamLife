package madelinecameron.dreamlife.Activities;

import android.app.Activity;
import android.content.Context;
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
    private boolean isRunning = true;

    public GameLoop(Context context, View view) {
        this.currentContext = context;
        this.currentView = view;

        Log.d("DreamLife", "Game loop instantiated");
    }

    public void stop() {
        isRunning = false;
    }

    public void run() {
        Log.d("DreamLife", "Trying to run");
        while(isRunning) {
            try {
                GameEvent gameEvent = GameState.getNextGameEvent();
                GameCharacter currentChar = GameState.getGameCharacter();
                TextView energyText = (TextView) currentView.findViewById(R.id.energy_text);
                TextView foodText = (TextView) currentView.findViewById(R.id.food_text);
                TextView funText = (TextView) currentView.findViewById(R.id.fun_text);
                ProgressBar energyBar = (ProgressBar) currentView.findViewById(R.id.energy_bar);
                ProgressBar foodBar = (ProgressBar) currentView.findViewById(R.id.food_bar);
                ProgressBar funBar = (ProgressBar) currentView.findViewById(R.id.fun_bar);

                HashMap<String, Object> updatedValues = currentChar.heartbeat();

                Integer energyValue = (Integer) updatedValues.get("Energy");
                Integer foodValue = (Integer) updatedValues.get("Food");
                Integer funValue = (Integer) updatedValues.get("Fun");

                if(energyBar != null && foodBar != null && funBar != null) { //Takes a bit to retrieve
                    if (energyBar.getProgress() != energyValue) {  //Update energy
                        energyBar.setProgress(energyValue);
                        energyText.setText(energyValue.toString() + "/100");
                    }
                    if (foodBar.getProgress() != foodValue) {  //Update food
                        foodBar.setProgress(foodValue);
                        foodText.setText(foodValue.toString() + "/100");
                    }
                    if (funBar.getProgress() != funValue) {  //Update fun
                        funBar.setProgress(funValue);
                        funText.setText(funValue.toString() + "/100");
                    }
                }

                Thread.sleep(250);
            }
            catch(Exception e) {
                Log.e("DreamLife", e.toString());
            }
        }
    }
}
