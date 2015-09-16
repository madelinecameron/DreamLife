package madelinecameron.dreamlife;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by madel on 9/15/2015.
 */
public class Item {
    public Integer id;
    public String name;
    public Float cost;
    public Boolean shouldDisplay;
    public Boolean canSell;
    private JSONObject effects;

    public Item(Integer id, String name, Float cost,
                Boolean shouldDisplay, Boolean canSell, JSONObject effects) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.shouldDisplay = shouldDisplay;
        this.canSell = canSell;
        this.effects = effects;
    }

    public Float getSellCost() {
        return cost * 0.75f;
    }

    public HashMap<String, Object> createEffectObj() {
        return null;
    }
}
