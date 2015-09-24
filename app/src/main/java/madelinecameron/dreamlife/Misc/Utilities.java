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

/**
 * Created by madel on 9/15/2015.
 */

public class Utilities {

    public static final double MODIFIER_BOOST_PER_TEN_PTS = 0.01;

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

                            for (int i = 0; i < modifiers.length(); i++) {
                                String modName = modifiers.getString(i);
                                if (currentChar.isAttribute(modName)) {
                                    Log.d("DreamLife", modName + " is attr");
                                    basePercent += currentChar.getAttrLevel(modName) * MODIFIER_BOOST_PER_TEN_PTS;
                                } else {
                                    Log.d("DreamLife", modName + " is skill");
                                    if(currentChar.hasSkill(modName)) {
                                        basePercent += currentChar.getSkillLevel(modName) * MODIFIER_BOOST_PER_TEN_PTS;
                                    }
                                }
                            }
                        }

                        if (new Random().nextDouble() < basePercent + 100) {  //If rand smaller than base, it is successful
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

                        if(progressObj.has("Modifier")) {
                            JSONArray modifiers = progressObj.getJSONArray("Modifier");

                            for (int i = 0; i < modifiers.length(); i++) {
                                String modName = modifiers.getString(i);
                                if (currentChar.isAttribute(modName)) {
                                    basePercent += currentChar.getAttrLevel(modName) * MODIFIER_BOOST_PER_TEN_PTS;
                                } else {
                                    basePercent += currentChar.getSkillLevel(modName) * MODIFIER_BOOST_PER_TEN_PTS;
                                }
                            }
                        }

                        for(int y = 0; y < get.length(); y++) {
                            JSONObject item = get.getJSONObject(y);
                            String name = item.keys().next();

                            currentChar.updateProgress(name, Float.valueOf(basePercent.toString()));
                        }
                    }
                    catch(Exception e) {
                        Log.d("DreamLife", e.toString());
                    }
                    break;
                case "GET":
                    break;
                default:  //Attr / skill modifiers
                    try {
                        String value = effects.getString(key).replace(" ", "");
                        Log.d("DreamLife", value);
                        Integer modifyValue;

                        if(value.contains("RA")) {
                            Integer lowerBound = Integer.valueOf(value.substring(2, value.indexOf("-")));
                            Integer upperBound = Integer.valueOf(value.substring(value.indexOf("-") + 1));

                            modifyValue = new Random().nextInt(upperBound - lowerBound) + lowerBound;
                        }
                        else { modifyValue = Integer.valueOf(value); }

                        if(!currentChar.hasSkill(key) && !currentChar.isAttribute(key)) {
                            Log.d("DreamLife", "Adding " + key);
                            currentChar.addSkill(key);
                        }

                        Log.d("DreamLife", "Modifying " + key + " by " + modifyValue.toString());
                        currentChar.modifyAttrOrSkill(key, Float.valueOf(modifyValue));
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
            if (condition.substring(0, 2).matches("([A-Za-z]{2})")) {
                func = condition.substring(0, 2);
                bound = Integer.valueOf(condition.substring(2));
            } else {
                bound = Integer.valueOf(condition);
            }
        }
        else {
            bound = Integer.valueOf(condition);
        }

        switch(func) {
            case "LT":  //Less than
                if(currentChar.isAttribute(name)) {
                    Log.i("DreamLife", "IsAttr_LT");
                    return currentChar.getAttrLevel(name) < bound;
                }
                else {
                    Log.i("DreamLife", "IsSkill_LT");
                    return currentChar.hasSkill(name) && currentChar.getSkillLevel(name) < bound;
                }
            default:  //Greater than
                if(currentChar.isAttribute(name)) {
                    Log.i("DreamLife", "IsAttr_GT");
                    return currentChar.getAttrLevel(name) > bound;
                }
                else {
                    Log.i("DreamLife", "IsSkill_GT");
                    return currentChar.hasSkill(name) && currentChar.getSkillLevel(name) > bound;
                }
        }
    }

    public static boolean hasAllReqItems(JSONArray itemArray, GameCharacter currentChar) {
        if(itemArray == null) { return true; }
        for(int i = 0; i < itemArray.length(); i++) {
            try {
                JSONObject neededItem = itemArray.getJSONObject(i);

                String itemID = neededItem.keys().next();
                Integer qty = neededItem.getInt(itemID);

                if(!currentChar.ownsItem(Integer.valueOf(itemID))) { return false; }
                if(currentChar.getOwnedItemQty(Integer.valueOf(itemID)) < qty) { return false; }
            }
            catch(Exception e) {
                Log.d("DreamLife", e.toString());
            }
        }

        return true;
    }
}
