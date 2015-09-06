package madelinecameron.dreamlife.Character;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import madelinecameron.dreamlife.DBManager;
import madelinecameron.dreamlife.PageType;

/**
 * Created by madel on 8/30/2015.
 */
public class Character {
    private Float energy, food, fun, cash, passiveIncome;
    private Integer age = 18;
    private Education highestEdu;
    private Home homeType;
    private ArrayList<Integer> ownedItems;
    private static DBManager dbManager;
    private static HashMap<Integer, HashMap<String, String>> allItems;
    private HashMap<String, Float> skillMap;

    public Character(Context context) {
        energy = 0.0f;
        food = 100.0f;
        fun = 100.0f;
        cash = 100.0f;
        passiveIncome = 0.0f;

        dbManager = new DBManager(context);

        initItemList();
    }

    public List<Float> heartbeat() {
        cash += passiveIncome;

        return Arrays.asList(energy, food, fun, cash, passiveIncome);
    }

    public String getEducation() { return highestEdu.toString(); }
    public String getHomeType() { return homeType.toString(); }
    public ArrayList<Integer> getOwnedItems() { return ownedItems; }
    public String getItemName(Integer itemID) { return allItems.get(itemID).get("Name"); }
    public Set<String> getSkills() { return skillMap.keySet(); }
    public Float getSkillLevel(Integer skillName) { return skillMap.get(skillName); }

    public void modifyCash(Float updateValue) { cash += updateValue; }
    public void modifyPassiveIncome(Float updateValue) { passiveIncome += updateValue; }
    public void modifyFood(Float updateValue) { food += updateValue; }
    public void modifyEnergy(Float updateValue) { energy += updateValue; }
    public void modifyFun(Float updateValue) {
        fun += updateValue;
    }
    public void modifyEducation(Education edu) { highestEdu = edu; }
    public void modifyAge() { age++; }

    public boolean buyItem(Integer itemID) {
        try {
            HashMap<String, String> item = allItems.get(itemID);
            JSONObject boostObj = new JSONObject(item.get("Boost"));

            while(boostObj.keys().hasNext()) {
                String selectedKey = boostObj.keys().next();

                switch(selectedKey.toUpperCase()) {
                    case "PASSIVEINCOME":
                        modifyPassiveIncome((float)boostObj.getDouble(selectedKey));
                        break;
                }
            }

            modifyCash(-1 * Float.valueOf(item.get("Cost")));
            ownedItems.add(itemID);

            return true;
        }
        catch(Exception e) {
            Log.d("DreamLife", e.toString());

            return false;
        }
    }

    public boolean sellItem(Integer itemID) {
        try {
            HashMap<String, String> item = allItems.get(itemID);
            JSONObject boostObj = new JSONObject(item.get("boost"));

            while(boostObj.keys().hasNext()) {
                String selectedKey = boostObj.keys().next();

                switch(selectedKey.toUpperCase()) {
                    case "PASSIVEINCOME":
                        modifyPassiveIncome(-1 * (float)boostObj.getDouble(selectedKey));
                        break;
                }
            }

            modifyCash(0.5f * Float.valueOf(item.get("Cost")));
            ownedItems.remove(itemID);

            return true;
        }
        catch(Exception e) {
            Log.d("DreamLife", e.toString());

            return false;
        }
    }

    private boolean initItemList() {
        try {
            SQLiteDatabase db = dbManager.getReadableDatabase();
            allItems = new HashMap<>();

            Cursor selectCursor = db.query("Items", null, null, null, null, null, null);

            if (selectCursor.moveToFirst()) {
                do {
                    HashMap<String, String> map = new HashMap<>();
                    for (int i = 0; i < selectCursor.getColumnCount(); i++) {
                        map.put(selectCursor.getColumnName(i), selectCursor.getString(i));
                    }

                    allItems.put(selectCursor.getInt(selectCursor.getColumnIndex("id")), map);
                } while (selectCursor.moveToNext());
            }
            db.close();

            return true;
        }
        catch(Exception e) {
            Log.d("DreamLife", e.toString());
            return false;
        }
    }

    public ArrayList<HashMap<String, String>> getAllAvaliableItems(PageType pageType) {
        SQLiteDatabase db = dbManager.getReadableDatabase();

        Cursor selectCursor = db.query("Items", null, "CashGT > ? AND SciGT > ? AND ReadGT > ? " +
                        "AND SpeakGT > ? AND OlderThan >= ? AND Home >= ? AND Edu >= ?",
                new String[]{cash.toString(), skillMap.get("sci").toString(), skillMap.get("read").toString(),
                        skillMap.get("speak").toString(), age.toString(), homeType.toString(), highestEdu.toString()}, null, null, null);

        ArrayList<HashMap<String, String>> gameObjList = new ArrayList<>();

        if (selectCursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<>();
                for(int i = 0; i < selectCursor.getColumnCount(); i++)
                {
                    map.put(selectCursor.getColumnName(i), selectCursor.getString(i));
                }

                gameObjList.add(map);
            } while (selectCursor.moveToNext());
        }
        db.close();

        return gameObjList;
    }

}
