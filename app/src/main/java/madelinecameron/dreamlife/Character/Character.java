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
import madelinecameron.dreamlife.Item;
import madelinecameron.dreamlife.PageType;

/**
 * Created by madel on 8/30/2015.
 */
public class Character {
    private HashMap<String, Object> attributes;
    private ArrayList<Integer> ownedItems;
    private HashMap<String, Float> skillMap;

    public Character() {
        attributes.put("Energy", 0.0f);
        attributes.put("Food", 100.0f);
        attributes.put("Fun", 100.0f);
        attributes.put("Cash", 100.0f);
        attributes.put("PassiveIncome", 0.0f);
        attributes.put("Karma", 0.0f);
        attributes.put("Age", 18);
        attributes.put("Education", Education.HIGH_SCHOOL);
        attributes.put("Home", Home.HOMELESS);
    }

    public HashMap<String, Object> heartbeat() {
        Float cash = (Float)attributes.get("Cash");
        attributes.put("Cash", cash + (Float)attributes.get("PassiveIncome"));

        return attributes;
    }

    public String getEducation() { return attributes.get("Education").toString(); }
    public String getHomeType() { return attributes.get("Home").toString(); }
    public ArrayList<Integer> getOwnedItems() { return ownedItems; }
    public Set<String> getSkills() { return skillMap.keySet(); }
    public boolean hasSkill(String skillName) { return skillMap.keySet().contains(skillName); }
    public boolean isAttribute(String name) { return attributes.containsKey(name); }
    public Boolean ownsItem(Integer itemID) { return ownedItems.contains(itemID); }
    public Float getSkillLevel(String skillName) { return skillMap.get(skillName); }
    public Float getAttrLevel(String attrName) { return (Float)attributes.get(attrName); }

    public void modifyAttr(String attrName, Object updateValue) { attributes.put(attrName, updateValue); }

    public boolean buyItem(Item item) {
        try {
            HashMap<String, Object> effects = item.createEffectObj();

            for(String s : effects.keySet()) {
                switch(s.toUpperCase()) {
                    case "PASSIVEINCOME":
                        modifyAttr("PassiveIncome", getAttrLevel("PassiveIncome") + (Float)effects.get("PassiveIncome"));
                        break;
                }
            }

            modifyAttr("Cash", getAttrLevel("Cash") - Float.valueOf(item.cost));
            ownedItems.add(item.id);

            return true;
        }
        catch(Exception e) {
            Log.d("DreamLife", e.toString());

            return false;
        }
    }

    public boolean sellItem(Item item) {
        try {
            HashMap<String, Object> effects = item.createEffectObj();

            for(String s : effects.keySet()) {
                switch(s.toUpperCase()) {
                    case "PASSIVEINCOME":
                        modifyAttr("PassiveIncome", getAttrLevel("PassiveIncome") - (Float)effects.get("PassiveIncome"));
                        break;
                }
            }

            modifyAttr("Cash", getAttrLevel("Cash") - item.getSellCost());
            ownedItems.remove(item.id);

            return true;
        }
        catch(Exception e) {
            Log.d("DreamLife", e.toString());

            return false;
        }
    }
}
