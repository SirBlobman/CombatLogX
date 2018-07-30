package com.SirBlobman.combatlogx.utility;

import com.SirBlobman.combatlogx.config.ConfigOptions;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class CombatUtil extends Util {
    public static boolean canBeTagged(Player p) {
        boolean can = true;

        if (ConfigOptions.OPTION_BYPASS_ENABLE) {
            String perm = ConfigOptions.OPTION_BYPASS_PERMISSION;
            boolean bypass = p.hasPermission(perm);
            can = !bypass;
        }

        return can;
    }

    public static boolean canAttack(LivingEntity le1, LivingEntity le2) {
        if (le1 instanceof Player) {
            if (le2 instanceof Player) {
                if (ConfigOptions.OPTION_SELF_COMBAT)
                    return true;
                else {
                    String name1 = OldUtil.getName(le1);
                    String name2 = OldUtil.getName(le2);
                    return !name1.equals(name2);
                }
            } else {
                if (ConfigOptions.OPTION_MOBS_COMBAT) {
                    List<String> list = ConfigOptions.OPTION_MOBS_BLACKLIST;
                    EntityType et = le2.getType();
                    String type = et.name();
                    return !list.contains(type);
                } else
                    return false;
            }
        }

        if (le2 instanceof Player) {
            if (le1 instanceof Player) {
                if (ConfigOptions.OPTION_SELF_COMBAT)
                    return true;
                else {
                    String name1 = OldUtil.getName(le1);
                    String name2 = OldUtil.getName(le2);
                    return !name1.equals(name2);
                }
            } else {
                if (ConfigOptions.OPTION_MOBS_COMBAT) {
                    List<String> list = ConfigOptions.OPTION_MOBS_BLACKLIST;
                    EntityType et = le1.getType();
                    String type = et.name();
                    return !list.contains(type);
                } else
                    return false;
            }
        }

        /* IF the entities are not players they can always attack each other */
        return true;
    }
}