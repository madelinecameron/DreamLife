package madelinecameron.dreamlife.GameState;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import madelinecameron.dreamlife.Activities.PageType;
import madelinecameron.dreamlife.Character.GameCharacter;
import madelinecameron.dreamlife.Misc.Utilities;

/**
 * Created by madel on 9/15/2015.
 */
public class Action {
    public Integer id;
    public String name, description;
    public PageType type;
    public JSONObject effect;
    public JSONObject needed;

    public Action(Integer id, String name, String description, PageType type, JSONObject needed, JSONObject effect) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.effect = effect;
        this.needed = needed;
    }

    public void applyAction() { Utilities.causeEffects(effect, GameState.getGameCharacter()); }
}
