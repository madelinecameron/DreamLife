package madelinecameron.dreamlife;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import madelinecameron.dreamlife.Character.GameCharacter;

/**
 * Created by madel on 9/15/2015.
 */

public class Utilities {

    public static final double MODIFIER_BOOST_PER_TEN_PTS = 0.01;

    public static void causeEffects(JSONObject effects, GameCharacter currentChar) {
        while(effects.keys().hasNext()) {
            String key = effects.keys().next();

            switch(key.toUpperCase()) {
                case "CHANCE":
                    try {
                        JSONObject chanceObj = effects.getJSONObject(key);
                        Double basePercent = chanceObj.getDouble("Base");
                        JSONArray modifiers = chanceObj.getJSONArray("Modifier");
                        JSONArray get = chanceObj.getJSONArray("Get");

                        for(int i = 0; i < modifiers.length(); i++) {
                            String modName = modifiers.getString(i);
                            if(currentChar.isAttribute(modName)) {
                                basePercent += currentChar.getAttrLevel(modName) * MODIFIER_BOOST_PER_TEN_PTS;
                            }
                            else {
                                basePercent += currentChar.getSkillLevel(modName) * MODIFIER_BOOST_PER_TEN_PTS;
                            }
                        }

                        if (new Random().nextDouble() < basePercent) {  //If rand smaller than base, it is successful
                            Set<Integer> addedItems = new LinkedHashSet<>();
                            for(int y = 0; y < get.length(); y++) {
                                JSONObject item = get.getJSONObject(y);
                                String itemID = item.keys().next();

                                currentChar.addItem(Integer.valueOf(itemID), item.getInt(itemID));
                                addedItems.add(Integer.valueOf(itemID));
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
                        JSONArray modifiers = progressObj.getJSONArray("Modifier");
                        JSONArray get = progressObj.getJSONArray("Get");

                        for(int i = 0; i < modifiers.length(); i++) {
                            String modName = modifiers.getString(i);
                            if(currentChar.isAttribute(modName)) {
                                basePercent += currentChar.getAttrLevel(modName) * MODIFIER_BOOST_PER_TEN_PTS;
                            }
                            else {
                                basePercent += currentChar.getSkillLevel(modName) * MODIFIER_BOOST_PER_TEN_PTS;
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
                        Integer modifyValue;

                        if(value.contains("RA")) {
                            Integer lowerBound = Integer.getInteger(value.substring(3, value.indexOf('-')));
                            Integer upperBound = Integer.getInteger(value.substring(value.indexOf('-') + 1, value.length() - value.indexOf('-') + 1));

                            modifyValue = new Random().nextInt(upperBound - lowerBound) + lowerBound;
                        }
                        else { modifyValue = Integer.valueOf(value); }

                        if(!currentChar.hasSkill(key) && !currentChar.isAttribute(key)) {
                            currentChar.addSkill(key);
                        }

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

    public static Object parseObj(String o) {
        String func = o.substring(0, 2);
        switch(func) {
            case "RA":  //Random
                Integer lowerBound = Integer.getInteger(o.substring(3, o.indexOf('-')));
                Integer upperBound = Integer.getInteger(o.substring(o.indexOf('-') + 1, o.length() - o.indexOf('-') + 1));

                return new Random().nextInt(upperBound - lowerBound) + lowerBound;
        }

        return null;
    }

    public static boolean conditionFulfilled(String name, String condition, GameCharacter currentChar) {
        String func = condition.substring(0, 2);
        Integer bound = Integer.valueOf(condition.substring(3));

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
