package madelinecameron.dreamlife;

import java.util.HashMap;
import java.util.List;
import madelinecameron.dreamlife.Character.Character;

/**
 * Created by madel on 9/15/2015.
 */
public class Action {
    private HashMap<Integer, Integer> neededItems;  //ID, Qty
    private HashMap<String, Float> neededSkillsAndAttr;  //Skill / Attribute name, required level

    public Action() {

    }

    public boolean isUnlocked(Character currentCharacter) {
        for(Integer id : neededItems.keySet()) {
            if (!currentCharacter.ownsItem(id)) { return false; }
        }

        for(String name : neededSkillsAndAttr.keySet()) {
            if(currentCharacter.isAttribute(name)) {
                if(currentCharacter.getAttrLevel(name) < neededSkillsAndAttr.get(name)) { return false; }
            }
            else {
                if(currentCharacter.hasSkill(name)) {
                    if(currentCharacter.getSkillLevel(name) < neededSkillsAndAttr.get(name)) { return false; }
                }
                else {
                    return false;
                }
            }
        }


        return true;
    }
}
