package madelinecameron.dreamlife.Character;

import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;
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
    private HashMap<String, Object> attributeLimits = new HashMap<>();
    private HashMap<Integer, Integer> ownedItems = new HashMap<>();
    private HashMap<String, Integer> skillMap = new HashMap<>();
    private HashMap<String, Float> progressMap = new HashMap<>();

    public GameCharacter() {
        attributes.put("Energy", 100);
        attributes.put("Food", 100);
        attributes.put("Fun", 100);
        attributeLimits.put("Energy", 100);
        attributeLimits.put("Food", 100);
        attributeLimits.put("Fun", 100);

        attributes.put("Money", 300);
        attributes.put("PassiveIncome", 0);
        attributes.put("Karma", 0);
        attributes.put("Age", 18);
        attributes.put("HasJob", 0);
        attributes.put("IsStudying", 0);
        attributes.put("Education", Education.HIGH_SCHOOL);
        attributes.put("Home", Home.HOMELESS);
        attributes.put("Depression", 0);
        attributes.put("Weight", 150);
        attributes.put("Tired", 100);
    }

    public HashMap<String, Object> heartbeat() {
        Integer money = (int)attributes.get("Money");
        attributes.put("Money", money + (int)attributes.get("PassiveIncome"));

        return attributes;
    }

    public String getEducation() { return attributes.get("Education").toString(); }
    public String getHomeType() { return attributes.get("Home").toString(); }
    public String getJob() {
        Iterator<Integer> itemIterator = ownedItems.keySet().iterator();
        String currentJobName = "Unemployed";
        while(itemIterator.hasNext()) {
            Item currentItem = GameState.getItem(itemIterator.next());
            if(currentItem.type == "Job") {
                currentJobName = currentItem.name;
            }
        }

        return currentJobName;
    }
    public Set<Integer> getOwnedItems() { return ownedItems.keySet(); }
    public Set<String> getSkills() { return skillMap.keySet(); }
    public boolean hasSkill(String skillName) { return skillMap.keySet().contains(skillName); }
    public boolean isAttribute(String name) { return attributes.containsKey(name); }
    public Boolean ownsItem(Integer itemID) { return ownedItems.containsKey(itemID); }
    public Integer getOwnedItemQty(Integer itemID) { return ownedItems.get(itemID); }
    public Integer getSkillLevel(String skillName) { return skillMap.get(skillName); }
    public Object getAttrLevel(String attrName) { return attributes.get(attrName); }
    public Object getAttrLimit(String attrName) { return attributeLimits.get(attrName); }
    public Float getProgression(String name) { return progressMap.get(name); }

    public void modifyAttrOrSkill(String name, Integer updateValue) {
        if(isAttribute(name)) {
            if(Integer.valueOf(attributes.get(name).toString()) + updateValue < 0) {
                attributes.put(name, 0);
            }
            else {
                if (attributeLimits.containsKey(name)) {  // Check for upper limit
                    if (Integer.valueOf(attributes.get(name).toString()) + updateValue <= Integer.valueOf(attributeLimits.get(name).toString())) {
                        attributes.put(name, Integer.valueOf(attributes.get(name).toString()) + updateValue);
                    } else {
                        attributes.put(name, Integer.valueOf(attributeLimits.get(name).toString())); //Set to max
                    }
                }
                else {
                    attributes.put(name, Integer.valueOf(attributes.get(name).toString()) + updateValue);
                }
            }
        }
        else {
            skillMap.put(name, skillMap.get(name) + updateValue);
        }
    }
    public void addSkill(String skillName) { skillMap.put(skillName, 0); }
    public void addItem(Integer itemID) {
        Log.d("DreamLife", "Adding item...");
        if(ownsItem(itemID)) {
            ownedItems.put(itemID, ownedItems.get(itemID) + 1);
        }
        else {
            ownedItems.put(itemID, 1);
        }

        String message = Utilities.getItemRecievedMessage(itemID);
        Log.d("DreamLife", "message: " + message);
        if(message != null) {
            Log.d("DreamLife", "Non-null message");
            GameState.addGameEvent(new GameEvent(message, GameEventType.ITEM_ADDED));
        }
        else {
            Item item = GameState.getItem(itemID);
            Log.d("DreamLife", "Null message");
            if(item.shouldDisplay) {
                GameState.addGameEvent(new GameEvent(String.format("Received %s", item.name), GameEventType.ITEM_ADDED));
            }
        }
    }
    public void addItem(Integer itemID, Integer qty) {
        Log.d("DreamLife", "Adding item...");
        if(ownsItem(itemID)) {
            ownedItems.put(itemID, ownedItems.get(itemID) + 1);
        }
        else {
            ownedItems.put(itemID, 1);
        }

        String message = Utilities.getItemRecievedMessage(itemID);
        Log.d("DreamLife", "message: " + message);
        if(message != null) {
            Log.d("DreamLife", "Non-null message");
            GameState.addGameEvent(new GameEvent(message, GameEventType.ITEM_ADDED));
        }
        else {
            Item item = GameState.getItem(itemID);
            Log.d("DreamLife", "Null message");
            if(item.shouldDisplay) {
                GameState.addGameEvent(new GameEvent(String.format("Received %s", item.name), GameEventType.ITEM_ADDED));
            }
        }
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
            modifyAttrOrSkill("Money", (int)getAttrLevel("Money") - item.cost);

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
            modifyAttrOrSkill("Money", (int)getAttrLevel("Money") + item.getSellCost());

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
