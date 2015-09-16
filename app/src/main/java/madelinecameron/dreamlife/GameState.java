package madelinecameron.dreamlife;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

import madelinecameron.dreamlife.Character.*;
import madelinecameron.dreamlife.Character.Character;
import madelinecameron.dreamlife.DBManager;
import madelinecameron.dreamlife.Utilities;

/**
 * Created by madel on 9/12/2015.
 */
public class GameState {
    private static DBManager db;
    private Character character;
    private static HashMap<String, Item> allItems;

    public GameState(Context context) {
        this.db = new DBManager(context);
        this.character = new Character();
    }

    public Character getCharacter() { return character; }
    public HashMap<String, Action> getValidActions() {
        return null;
    }

    public HashMap<String, Item> getValidItems(boolean saleable) {
        Cursor selectCursor = db.getReadableDatabase().rawQuery("SELECT * FROM Items WHERE Saleable = ?", new String[]{ String.valueOf(saleable) } );
        HashMap<String, Item> validItems = new HashMap<>();

        while(selectCursor.moveToNext()) {
            try {
                JSONObject needed = new JSONObject(selectCursor.getString(selectCursor.getColumnIndex("Needed")));
                boolean allNeedsMet = true;
                while(needed.keys().hasNext() && allNeedsMet) {
                    String currentKey = needed.keys().next();
                    switch(currentKey.toUpperCase()) {
                        case "ITEM":
                            if(!Utilities.hasAllReqItems(needed.getJSONArray("Item"), getCharacter())) {
                                allNeedsMet = false;
                            }
                            break;
                        default: //Skills
                            if(!Utilities.conditionFulfilled(currentKey, needed.get(currentKey).toString(), getCharacter())) {
                                allNeedsMet = false;
                            }
                            break;
                    }
                }

                Integer id = selectCursor.getInt(selectCursor.getColumnIndex("ID"));
                String name = selectCursor.getString(selectCursor.getColumnIndex("Name"));
                Boolean shouldDisplay = Boolean.getBoolean(selectCursor.getString(selectCursor.getColumnIndex("Display")));
                Boolean canSell = Boolean.getBoolean(selectCursor.getString(selectCursor.getColumnIndex("Saleable")));
                JSONObject effects = new JSONObject(selectCursor.getString(selectCursor.getColumnIndex("Effects")));

                return null;
            }
            catch(Exception e) {
                Log.d("DreamLife", e.toString());

                return null;
            }
        }

        return null;
    }
}

