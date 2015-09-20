package madelinecameron.dreamlife;

import java.util.HashMap;

import madelinecameron.dreamlife.Character.GameCharacter;

/**
 * Created by madel on 9/15/2015.
 */
public class Action {
    private HashMap<Integer, Integer> neededItems;  //ID, Qty
    private HashMap<String, Float> neededSkillsAndAttr;  //Skill / Attribute name, required level

    public Action() {

    }

    public boolean isUnlocked(GameCharacter currentGameCharacter) {
        for(Integer id : neededItems.keySet()) {
            if (!currentGameCharacter.ownsItem(id)) { return false; }
        }

        for(String name : neededSkillsAndAttr.keySet()) {
            if(currentGameCharacter.isAttribute(name)) {
                if(currentGameCharacter.getAttrLevel(name) < neededSkillsAndAttr.get(name)) { return false; }
            }
            else {
                if(currentGameCharacter.hasSkill(name)) {
                    if(currentGameCharacter.getSkillLevel(name) < neededSkillsAndAttr.get(name)) { return false; }
                }
                else {
                    return false;
                }
            }
        }


        return true;
    }
}
