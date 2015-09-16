package madelinecameron.dreamlife;

import org.json.JSONArray;

import java.util.Random;

import madelinecameron.dreamlife.Character.*;
import madelinecameron.dreamlife.Character.Character;

/**
 * Created by madel on 9/15/2015.
 */
public class Utilities {
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

    public static boolean conditionFulfilled(String name, String condition, Character currentChar) {
        return false;
    }

    public static boolean hasAllReqItems(JSONArray itemArray, Character currentChar) {
        return false;
    }
}
