package madelinecameron.dreamlife;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.AbstractQueue;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;

import madelinecameron.dreamlife.Character.GameCharacter;

/**
 * Created by madel on 9/12/2015.
 */
public class GameState {
    private static DBManager db;
    private static Context context;
    private static GameCharacter gameCharacter;
    private static HashMap<Integer, Item> allItems = new HashMap<>();
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

            HashMap<String, Item> validItems = new HashMap<>();

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
    public static Integer getItemCount() { return allItems.keySet().size(); }
    public static GameCharacter getGameCharacter() { return gameCharacter; }
    public static HashMap<String, Action> getValidActions() {
        return null;
    }
    public static HashMap<Integer, Item> getValidItems(boolean saleable) {
        boolean allNeedsMet = true;
        HashMap<Integer, Item> validItems = new HashMap<>();

        while(allItems.keySet().iterator().hasNext()) {
            Integer currentKey = allItems.keySet().iterator().next();
            Item currentItem = allItems.get(currentKey);

            Log.i("DreamLife", currentItem.id.toString());

            JSONObject needed = currentItem.needed;
            allNeedsMet = true;
            while(needed.keys().hasNext() && allNeedsMet) {
                String neededName = needed.keys().next();

                try {
                    switch (neededName.toUpperCase()) {
                        case "ITEM":
                            if (!Utilities.hasAllReqItems(needed.getJSONArray(neededName), getGameCharacter())) {
                                Log.i("DreamLife", "Fail");
                                allNeedsMet = false;
                            }
                            break;
                        default: //Skills
                            if (!Utilities.conditionFulfilled(neededName, needed.get(neededName).toString(), getGameCharacter())) {
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

