package madelinecameron.dreamlife.GameState;

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
    public String type;
    public Boolean shouldDisplay;
    public Boolean canSell;
    public Boolean canPurchase;
    public JSONObject needed;
    public JSONObject results;
    public String itemReceivedMessage;

    public Item(Integer id, String name, Integer cost, String type, Boolean shouldDisplay, Boolean canPurchase,
                Boolean canSell, JSONObject needed, JSONObject results, String itemMessage) {
        this.id = id;
        this.name = name;
        this.cost = cost;
        this.type = type;
        this.shouldDisplay = shouldDisplay;
        this.canSell = canSell;
        this.canPurchase = canPurchase;
        this.needed = needed;
        this.results = results;
        this.itemReceivedMessage = itemMessage;
    }

    public Integer getSellCost() {
        return Math.round(cost * 0.75f);
    }
}
