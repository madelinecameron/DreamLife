package madelinecameron.dreamlife.Character;

import android.util.Log;

import java.util.HashMap;
import java.util.Set;

import madelinecameron.dreamlife.GameState.GameEvent;
import madelinecameron.dreamlife.GameState.GameEventType;
import madelinecameron.dreamlife.GameState.GameState;
import madelinecameron.dreamlife.GameState.Item;
import madelinecameron.dreamlife.Misc.Utilities;

/**
 * Created by madel on 8/30/2015.
 */
public class GameCharacter {
    private HashMap<String, Object> attributes = new HashMap<>();
    private HashMap<Integer, Integer> ownedItems = new HashMap<>();
    private HashMap<String, Float> skillMap = new HashMap<>();
    private HashMap<String, Float> progressMap = new HashMap<>();

    public GameCharacter() {
        attributes.put("Energy", 0.0f);
        attributes.put("Food", 100.0f);
        attributes.put("Fun", 100.0f);
        attributes.put("Money", 100.0f);
        attributes.put("PassiveIncome", 0.0f);
        attributes.put("Karma", 0.0f);
        attributes.put("Age", 18);
        attributes.put("Education", Education.HIGH_SCHOOL);
        attributes.put("Home", Home.HOMELESS);
    }

    public HashMap<String, Object> heartbeat() {
        Float Money = (Float)attributes.get("Money");
        attributes.put("Money", Money + (Float)attributes.get("PassiveIncome"));

        return attributes;
    }

    public String getEducation() { return attributes.get("Education").toString(); }
    public String getHomeType() { return attributes.get("Home").toString(); }
    public Set<Integer> getOwnedItems() { return ownedItems.keySet(); }
    public Set<String> getSkills() { return skillMap.keySet(); }
    public boolean hasSkill(String skillName) { return skillMap.keySet().contains(skillName); }
    public boolean isAttribute(String name) { return attributes.containsKey(name); }
    public Boolean ownsItem(Integer itemID) { return ownedItems.containsKey(itemID); }
    public Integer getOwnedItemQty(Integer itemID) { return ownedItems.get(itemID); }
    public Float getSkillLevel(String skillName) { return skillMap.get(skillName); }
    public Float getAttrLevel(String attrName) { return (Float)attributes.get(attrName); }
    public Float getProgression(String name) { return progressMap.get(name); }

    public void modifyAttrOrSkill(String name, Float updateValue) {
        if(isAttribute(name)) { attributes.put(name, (Float)attributes.get(name) + updateValue); }
        else { skillMap.put(name, skillMap.get(name) + updateValue); }
    }
    public void addSkill(String skillName) { skillMap.put(skillName, 0.0f); }
    public void addItem(Integer itemID) { ownedItems.put(itemID, ownedItems.get(itemID) + 1); }
    public void addItem(Integer itemID, Integer qty) {
        ownedItems.put(itemID, ownedItems.get(itemID) + qty);

        GameState.addGameEvent(new GameEvent(String.format("%d added", itemID), GameEventType.ITEM_ADDED));
    }
    public void updateProgress(String name, Float progression) {
        if(progressMap.containsKey(name)) {
            progressMap.put(name, progressMap.get(name) + progression);
        }
        else {
            progressMap.put(name, progression);
        }

        GameState.addGameEvent(new GameEvent(String.format("%s% progress in %s", progression.toString(), name), GameEventType.PROGRESS));
    }

    public boolean buyItem(Item item) {
        try {
            Utilities.causeEffects(item.results, this);
            modifyAttrOrSkill("Money", getAttrLevel("Money") - Float.valueOf(item.cost));

            if(ownedItems.containsKey(item.id)) {
                ownedItems.put(item.id, ownedItems.get(item.id));
            }
            else {
                ownedItems.put(item.id, 1);
            }

            GameState.addGameEvent(new GameEvent(String.format("%s bought", item.name), GameEventType.ITEM_ADDED));

            return true;
        }
        catch(Exception e) {
            Log.e("DreamLife", e.toString());

            return false;
        }
    }

    public boolean sellItem(Item item) {
        try {
            Utilities.removeEffects(item.results, this);
            modifyAttrOrSkill("Money", getAttrLevel("Money") - item.getSellCost());

            ownedItems.put(item.id, ownedItems.get(item.id) - 1);

            GameState.addGameEvent(new GameEvent(String.format("%s sold", item.name), GameEventType.ITEM_LOST));
            return true;
        }
        catch(Exception e) {
            Log.e("DreamLife", e.toString());

            return false;
        }
    }
}
