package madelinecameron.dreamlife;

import org.json.JSONObject;

import java.util.HashMap;

import madelinecameron.dreamlife.Character.GameCharacter;

/**
 * Created by madel on 9/15/2015.
 */
public class Item {
    public Integer id;
    public String name;
    public Integer cost;
    public Boolean shouldDisplay;
    public Boolean canSell;
    public Boolean canPurchase;
    public JSONObject needed;
    public JSONObject results;

    public Item(Integer id, String name, Integer cost,
                Boolean shouldDisplay, Boolean canPurchase, Boolean canSell, JSONObject needed, JSONObject results) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.shouldDisplay = shouldDisplay;
        this.canSell = canSell;
        this.canPurchase = canPurchase;
        this.needed = needed;
        this.results = results;
    }

    public Integer getSellCost() {
        return Math.round(cost * 0.75f);
    }
}
