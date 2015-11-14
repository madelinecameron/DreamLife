package madelinecameron.dreamlife.Misc;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import madelinecameron.dreamlife.Character.GameCharacter;
import madelinecameron.dreamlife.GameState.GameState;
import madelinecameron.dreamlife.GameState.Item;

/**
 * Created by madel on 9/15/2015.
 */

public class Utilities {

    public static final double MODIFIER_BOOST_PER_TEN_PTS = 0.01;

    private static double parseModifier(JSONArray modifierArray, double basePercent) {
        GameCharacter currentChar = GameState.getGameCharacter();
        for(int i = 0; i < modifierArray.length(); i++) {
            try {
                JSONObject selectedObj = modifierArray.getJSONObject(i);
                Iterator<String> objKeys = selectedObj.keys();
                while (objKeys.hasNext()) {
                    try {
                        String key = objKeys.next();
                        Double value = Double.valueOf(selectedObj.getString(key));

                        if (key.matches("^[0-9]*$")) {  //If key is item
                            if (currentChar.ownsItem(Integer.valueOf(key))) {
                                basePercent += (currentChar.getOwnedItemQty(Integer.valueOf(key)) * value);
                            }
                        } else {
                            if (currentChar.hasSkill(key)) {
                                basePercent += (currentChar.getSkillLevel(key) * value);
                            } else {
                                if (currentChar.isAttribute(key)) {
                                    basePercent += (Double.valueOf(currentChar.getAttrLevel(key).toString()) * value);
                                }
                            }
                        }
                    }
                    catch (Exception e) {
                        Log.e("DreamLife", e.toString());
                    }
                }
            }
            catch(Exception e) {
                Log.e("DreamLife", e.toString());
            }
        }

        return basePercent;
    }

    public static void causeEffects(JSONObject effects, GameCharacter currentChar) {
        Iterator<String> effectsKeys = effects.keys();
        while(effectsKeys.hasNext()) {
            String key = effectsKeys.next();

            switch(key.toUpperCase()) {
                case "CHANCE":
                    try {
                        JSONObject chanceObj = effects.getJSONObject(key);
                        Double basePercent = chanceObj.getDouble("Base");
                        JSONArray get = chanceObj.getJSONArray("Get");

                        if(chanceObj.has("Modifier")) {
                            Log.d("DreamLife", "Has modifier");
                            JSONArray modifiers = chanceObj.getJSONArray("Modifier");

                            Double percentIncrease = parseModifier(chanceObj.getJSONArray("Modifier"), basePercent);
                        }

                        if (new Random().nextDouble() < basePercent) {  //If rand smaller than base, it is successful
                            Log.d("DreamLife", "Got it!");
                            Set<Integer> addedItems = new LinkedHashSet<>();
                            for(int y = 0; y < get.length(); y++) {
                                JSONObject item = get.getJSONObject(y);
                                String itemID = item.keys().next();
                                Log.d("DreamLife", "ItemID: " + itemID);
                                Log.d("DreamLife", String.valueOf(item.getInt(itemID)));
                                Log.d("DreamLife", Integer.valueOf(itemID).toString());

                                currentChar.addItem(Integer.valueOf(itemID), item.getInt(itemID));
                                addedItems.add(Integer.valueOf(itemID));

                                Log.d("DreamLife", "NEXT");
                            }
                        }
                    }
                    catch(Exception e) {
                        Log.d("DreamLife", e.toString());
                    }
                    break;
                case "PROGRESS":
                    try {
                        JSONObject progressObj = effects.getJSONObject(key);
                        Double basePercent = progressObj.getDouble("Base");
                        JSONArray get = progressObj.getJSONArray("Get");

                        Double percentIncrease = parseModifier(progressObj.getJSONArray("Modifier"), basePercent);

                        for(int y = 0; y < get.length(); y++) {
                            JSONObject item = get.getJSONObject(y);
                            String name = item.keys().next();

                            currentChar.updateProgress(name, Float.valueOf(percentIncrease.toString()));
                        }
                    }
                    catch(Exception e) {
                        Log.d("DreamLife", e.toString());
                    }
                    break;
                case "GET":
                    try {
                        JSONArray getArray = effects.getJSONArray(key);
                        for(int i = 0; i < getArray.length(); i++) {
                            JSONObject itemObj = getArray.getJSONObject(i);

                            Iterator<String> itemIterator = itemObj.keys();
                            Integer itemID = 0;
                            while(itemIterator.hasNext()) {
                                itemID = Integer.valueOf(itemIterator.next());
                            }

                            Integer qty = itemObj.getInt(itemID.toString());

                            GameState.getGameCharacter().addItem(itemID, qty);
                        }
                    }
                    catch(Exception e) {
                        Log.e("DreamLife", e.toString());
                    }
                    break;
                case "REMOVE":
                    try {
                        JSONObject removeObj = effects.getJSONObject(key);
                        Iterator<String> removeKeys = removeObj.keys();

                        while(removeKeys.hasNext()) {
                            String removeKey = removeKeys.next();

                            JSONArray itemArray = removeObj.getJSONArray(removeKey);
                            switch(removeKey.toUpperCase()) {
                                case "LOOKUP":
                                    for(int i = 0; i < itemArray.length(); i++) {
                                        Log.i("DreamLife", itemArray.getString(i));
                                    }
                                    break;
                                case "ITEM":
                                    for(int i = 0; i < itemArray.length(); i++) {
                                        Log.i("DreamLife", itemArray.getString(i));
                                    }
                                    break;
                            }
                        }
                    }
                    catch(Exception e) {
                        Log.e("DreamLife", e.toString());
                    }
                    break;
                case "WORLD":
                    try {
                        JSONArray worldAttrList = effects.getJSONArray(key);
                        for (int i = 0; i < worldAttrList.length(); i++) {
                            JSONObject attrObj = worldAttrList.getJSONObject(i);

                            String attrName = "";
                            Iterator<String> attrKeys = attrObj.keys();
                            while (attrKeys.hasNext()) {
                                attrName = attrKeys.next();
                            }

                            String value = attrObj.getString(attrName).replace(" ", "");
                            Integer modifyValue;

                            if (value.contains("RA")) {
                                Integer lowerBound = Integer.valueOf(value.substring(2, value.indexOf("-")));
                                Integer upperBound = Integer.valueOf(value.substring(value.indexOf("-") + 1));

                                modifyValue = new Random().nextInt(upperBound - lowerBound) + lowerBound;
                            } else {
                                if (value.matches("^[A-Za-z]*$")) {
                                    String booleanVal = value;
                                    modifyValue = 0;
                                    if (Boolean.valueOf(booleanVal)) {
                                        modifyValue = 1;
                                    }
                                } else {
                                    modifyValue = Integer.valueOf(value);
                                }
                            }

                            Log.d("DreamLife", "Modifying " + attrName + " by " + modifyValue.toString());
                            GameState.modifyWorldAttribute(attrName, modifyValue);
                        }
                    }
                    catch(Exception e) {
                        Log.d("DreamLife", e.toString());
                    }
                    break;
                default:  //Attr / skill modifiers
                    try {
                        String value = effects.getString(key).replace(" ", "");
                        Integer modifyValue;

                        if(value.contains("RA")) {
                            Integer lowerBound = Integer.valueOf(value.substring(2, value.indexOf("-")));
                            Integer upperBound = Integer.valueOf(value.substring(value.indexOf("-") + 1));

                            modifyValue = new Random().nextInt(upperBound - lowerBound) + lowerBound;
                        }
                        else {
                            if (value.matches("^[A-Za-z]*$")) {
                                String textValue = value;
                                modifyValue = 0;
                                switch(textValue.toUpperCase()) {
                                    case "TRUE":
                                        modifyValue = 1;
                                        break;
                                    case "MAX":
                                        modifyValue = Integer.valueOf(currentChar.getAttrLimit(key).toString());
                                        break;
                                }
                            } else {
                                modifyValue = Integer.valueOf(value);
                            }
                        }

                        if(!currentChar.hasSkill(key) && !currentChar.isAttribute(key)) {
                            Log.d("DreamLife", "Adding " + key);
                            currentChar.addSkill(key);
                        }

                        Log.d("DreamLife", "Modifying " + key + " by " + modifyValue.toString());
                        currentChar.modifyAttrOrSkill(key, modifyValue);
                    }
                    catch(Exception e) {
                        Log.e("DreamLife", e.toString());
                    }
                    break;
            }
        }
    }

    public static void removeEffects(JSONObject effects, GameCharacter currentChar) {
    }

    public static boolean conditionFulfilled(String name, String condition, GameCharacter currentChar) {
        String func = "";
        Integer bound = -1;
        if(condition.length() > 2) {
            if(condition.matches("^[A-Za-z]*$")) {
                if(currentChar.isAttribute(name)) {
                    String booleanVal = condition;
                    int booleanInt = 0;
                    if (Boolean.valueOf(booleanVal)) {
                        booleanInt = 1;
                    }
                    return GameState.getGameCharacter().getAttrLevel(name) == booleanInt;
                }
                else {
                    return false;
                }
            }
            else {
                if (condition.substring(0, 2).matches("([A-Za-z]{2})")) {
                    func = condition.substring(0, 2);
                    bound = Integer.valueOf(condition.substring(2));
                } else {
                    bound = Integer.valueOf(condition);
                }
            }
        }
        else {
            bound = Integer.valueOf(condition);
        }

        switch(func) {
            case "LT":  //Less than
                if(currentChar.isAttribute(name)) {
                    return (int)currentChar.getAttrLevel(name) < bound;
                }
                else {
                    return currentChar.hasSkill(name) && currentChar.getSkillLevel(name) < bound;
                }
            default:  //Greater than
                if(currentChar.isAttribute(name)) {
                    return (int)currentChar.getAttrLevel(name) > bound;
                }
                else {
                    return currentChar.hasSkill(name) && currentChar.getSkillLevel(name) > bound;
                }
        }
    }

    public static String getItemRecievedMessage(int itemID) {
        Item item = GameState.getItem(itemID);
        return item.itemReceivedMessage;
    }

    public static boolean hasAllReqItems(JSONArray itemArray, GameCharacter currentChar) {
        if(itemArray == null) { return true; }
        for(int i = 0; i < itemArray.length(); i++) {
            try {
                JSONObject neededItem = itemArray.getJSONObject(i);

                if(neededItem == JSONObject.NULL) { return true; }
                if(neededItem.has("Or")) {
                    JSONArray orArray = neededItem.getJSONArray("Or");

                    for(int y = 0; y < orArray.length(); y++) {
                        try {
                            JSONObject orItem = orArray.getJSONObject(y);

                            String itemID = "";
                            Iterator<String> itemKeys = orItem.keys();
                            while (itemKeys.hasNext()) {
                                itemID = itemKeys.next();
                            }

                            if (currentChar.ownsItem(Integer.valueOf(itemID))) {
                                return true;
                            }
                        }
                        catch(Exception e) {
                            Log.d("DreamLife", e.toString());
                        }
                    }
                }
                else {
                    String itemID = "";
                    Iterator<String> itemKeys = neededItem.keys();
                    while (itemKeys.hasNext()) {
                        itemID = itemKeys.next();
                    }
                    Integer qty = neededItem.getInt(itemID);

                    if (!currentChar.ownsItem(Integer.valueOf(itemID))) {
                        if (qty > 0) {
                            return false;
                        }
                    } else {
                        int ownedQty = currentChar.getOwnedItemQty(Integer.valueOf(itemID));
                        if (ownedQty < qty) {
                            return false;
                        }
                        if (ownedQty > qty && qty == 0) {
                            return false;
                        }
                    }
                }
            }
            catch(Exception e) {
                Log.d("DreamLife", e.toString());

                return false;
            }
        }

        return true;
    }
}
