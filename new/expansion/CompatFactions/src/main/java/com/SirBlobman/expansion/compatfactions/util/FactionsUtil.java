package com.SirBlobman.expansion.compatfactions.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.utility.PluginUtil;
import com.SirBlobman.combatlogx.utility.Util;

public abstract class FactionsUtil extends Util {
    public abstract Object getFaction(Player player);
    public abstract Object getFaction(Location loc);
    public abstract boolean canAttack(Player player1, Player player2);
    public abstract boolean isSafeZone(Location loc);
    
    public static FactionsUtil getFactionsUtil() {
        if(PluginUtil.isEnabled("Factions", "ProSavage")) return new FactionsUtilSavage();
        
        else return null;
    }
}