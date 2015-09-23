package madelinecameron.dreamlife.GameState;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import madelinecameron.dreamlife.Activities.PageType;
import madelinecameron.dreamlife.Character.GameCharacter;
import madelinecameron.dreamlife.Misc.DBManager;
import madelinecameron.dreamlife.Misc.Utilities;

/**
 * Created by madel on 9/12/2015.
 */
public class GameState {
    private static DBManager db;
    private static Context context;
    private static GameCharacter gameCharacter;
    private static HashMap<Integer, Item> allItems = new HashMap<>();
    private static HashMap<String, Action> allActions = new HashMap<>();
    private static Queue<GameEvent> eventQueue = new LinkedList<>();

    public GameState(Context context) {
        this.db = new DBManager(context);
        this.gameCharacter = new GameCharacter();
        this.context = context;
    }

    public static void loadAllItems() {
        try {
            Log.i("DreamLife", "Loading all items...");
            Cursor selectCursor = db.openDB().rawQuery("SELECT * FROM Items", null);
            Log.d("DreamLife", String.valueOf(selectCursor.getCount()));

            while (selectCursor.moveToNext()) {
                try {
                    Integer id = selectCursor.getInt(selectCursor.getColumnIndex("_id"));
                    String name = selectCursor.getString(selectCursor.getColumnIndex("Name"));
                    Integer cost = selectCursor.getInt(selectCursor.getColumnIndex("Cost"));
                    Boolean display = Boolean.valueOf(selectCursor.getString(selectCursor.getColumnIndex("Display")));
                    Boolean purchasable = Boolean.valueOf(selectCursor.getString(selectCursor.getColumnIndex("Purchasable")));
                    Boolean saleable = Boolean.valueOf(selectCursor.getString(selectCursor.getColumnIndex("Saleable")));
                    JSONObject needed = new JSONObject(selectCursor.getString(selectCursor.getColumnIndex("Needed")));
                    JSONObject results = new JSONObject(selectCursor.getString(selectCursor.getColumnIndex("Result")));

                    allItems.put(id, new Item(id, name, cost, display, purchasable, saleable, needed, results));

                    Log.i("DreamLife", String.format("%s added to all item list", name));
                } catch (Exception e) {
                    Log.e("DreamLife", e.toString());
                }
            }
        }
        catch(Exception e) {
            Log.e("DreamLife", e.toString());
        }

    }

    public static void loadAllActions() {
        try {
            Log.i("DreamLife", "Loading all actions...");
            Cursor selectCursor = db.openDB().rawQuery("SELECT * FROM Actions", null);
            Log.d("DreamLife", String.valueOf(selectCursor.getCount()));

            while (selectCursor.moveToNext()) {
                try {
                    Integer id = selectCursor.getInt(selectCursor.getColumnIndex("_id"));
                    String name = selectCursor.getString(selectCursor.getColumnIndex("Name"));
                    String descrip = selectCursor.getString(selectCursor.getColumnIndex("Description"));
                    PageType pageType = PageType.valueOf(selectCursor.getString(selectCursor.getColumnIndex("Page")).toUpperCase());
                    JSONObject needed = new JSONObject(selectCursor.getString(selectCursor.getColumnIndex("Needed")));
                    JSONObject results = new JSONObject(selectCursor.getString(selectCursor.getColumnIndex("Result")));

                    allActions.put(name, new Action(id, name, descrip, pageType, needed, results));

                    Log.i("DreamLife", String.format("%s added to all action list", name));
                } catch (Exception e) {
                    Log.e("DreamLife", e.toString());
                }
            }
        }
        catch(Exception e) {
            Log.e("DreamLife", e.toString());
        }

    }

    public static Integer getItemCount() { return allItems.keySet().size(); }
    public static GameCharacter getGameCharacter() { return gameCharacter; }

    public static HashMap<String, Action> getValidActions(PageType type) {
        boolean allNeedsMet;
        HashMap<String, Action> validActions = new HashMap<>();
        Iterator<String> actionNames = allActions.keySet().iterator();

        while(actionNames.hasNext()) {
            String currentKey = actionNames.next();
            Action currentAction = allActions.get(currentKey);

            Log.d("DreamLife", type.name() + " " + currentAction.type.name());
            if(type.name() != currentAction.type.name()) {
                Log.d("DreamLife", "Continue");
                Log.d("DreamLife", type.name() + " " + currentAction.type.name());
                continue;
            }

            JSONObject neededCriteria = currentAction.needed;
            Iterator<String> actionCriterias = neededCriteria.keys();
            allNeedsMet = true;

            Log.i("DreamLife", currentAction.name);

            while(actionCriterias.hasNext() && allNeedsMet) {

                String neededName = actionCriterias.next();
                Log.d("DreamLife", neededName);

                try {
                    switch (neededName.toUpperCase()) {
                        case "ITEM":
                            if (!Utilities.hasAllReqItems(neededCriteria.getJSONArray(neededName), getGameCharacter())) {
                                Log.i("DreamLife", "Fail2");
                                allNeedsMet = false;
                            }
                            break;
                        default: //Skills
                            if (!Utilities.conditionFulfilled(neededName, neededCriteria.get(neededName).toString(), getGameCharacter())) {
                                Log.i("DreamLife", "Fail1");
                                allNeedsMet = false;
                            }
                            break;
                    }
                }
                catch(Exception e) {
                    Log.e("DreamLife", e.toString());
                    System.exit(0);
                }
            }

            if(allNeedsMet) {
                Log.i("DreamLife", "Valid");
                validActions.put(currentKey, currentAction);
            }
            else {
                Log.i("DreamLife", "Failed");
            }
        }

        Log.i("DreamLife", "ValidSize: " + String.valueOf(validActions.size()));
        return validActions;
    }

    public static HashMap<Integer, Item> getValidItems(boolean saleable) {
        boolean allNeedsMet = true;
        HashMap<Integer, Item> validItems = new HashMap<>();

        while(allItems.keySet().iterator().hasNext()) {
            Integer currentKey = allItems.keySet().iterator().next();
            Item currentItem = allItems.get(currentKey);

            Log.i("DreamLife", currentItem.id.toString());

            JSONObject neededCriteria = currentItem.needed;
            allNeedsMet = true;
            while(neededCriteria.keys().hasNext() && allNeedsMet) {
                String neededName = neededCriteria.keys().next();

                try {
                    switch (neededName.toUpperCase()) {
                        case "ITEM":
                            if (!Utilities.hasAllReqItems(neededCriteria.getJSONArray(neededName), getGameCharacter())) {
                                Log.i("DreamLife", "Fail");
                                allNeedsMet = false;
                            }
                            break;
                        default: //Skills
                            if (!Utilities.conditionFulfilled(neededName, neededCriteria.get(neededName).toString(), getGameCharacter())) {
                                Log.i("DreamLife", "Fail");
                                allNeedsMet = false;
                            }
                            break;
                    }
                }
                catch(Exception e) {
                    Log.e("DreamLife", e.toString());
                }
            }

            if(allNeedsMet) {
                Log.i("DreamLife", "valid");
                validItems.put(currentItem.id, currentItem);
            }
        }

        return validItems;
    }

    public static void addGameEvent(GameEvent e) {
        eventQueue.add(e);
    }
}

