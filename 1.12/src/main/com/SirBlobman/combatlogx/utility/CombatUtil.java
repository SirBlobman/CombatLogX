package com.SirBlobman.combatlogx.utility;

import com.SirBlobman.combatlogx.config.Config;

import org.bukkit.entity.*;

import java.util.List;

public class CombatUtil extends Util {
    public static boolean canBeTagged(Player p) {
        boolean can = true;
        
        if(Config.OPTION_BYPASS_ENABLE) {
            String perm = Config.OPTION_BYPASS_PERMISSION;
            boolean bypass = p.hasPermission(perm);
            can = !bypass;
        }
        
        return can;
    }
    
    public static boolean canAttack(LivingEntity le1, LivingEntity le2) {
        if(le1 instanceof Player) {
            if(le2 instanceof Player) {
                if(Config.OPTION_SELF_COMBAT) return true;
                else {
                    String name1 = OldUtil.getName(le1);
                    String name2 = OldUtil.getName(le2);
                    if(name1.equals(name2)) return false;
                    else return true;
                }
            } else {
                if(Config.OPTION_MOBS_COMBAT) {
                    List<String> list = Config.OPTION_MOBS_BLACKLIST;
                    EntityType et = le2.getType();
                    String type = et.name();
                    if(list.contains(type)) return false;
                    else return true;
                } else return false;
            }
        }
        
        if(le2 instanceof Player) {
            if(le1 instanceof Player) {
                if(Config.OPTION_SELF_COMBAT) return true;
                else {
                    String name1 = OldUtil.getName(le1);
                    String name2 = OldUtil.getName(le2);
                    if(name1.equals(name2)) return false;
                    else return true;
                }
            } else {
                if(Config.OPTION_MOBS_COMBAT) {
                    List<String> list = Config.OPTION_MOBS_BLACKLIST;
                    EntityType et = le1.getType();
                    String type = et.name();
                    if(list.contains(type)) return false;
                    else return true;
                } else return false;
            }
        }
        
        /*IF the entities are not players they can always attack each other*/
        return true;
    }
}