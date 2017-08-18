package com.SirBlobman.factions.compat;

import com.SirBlobman.combatlogx.utility.*;

import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.plugin.*;

public abstract class FactionsUtil extends Util {
    public static FactionsUtil getFactions() {
        if(PM.isPluginEnabled("Factions")) {
            Plugin pl = PM.getPlugin("Factions");
            PluginDescriptionFile pdf = pl.getDescription();
            String version = pdf.getVersion();
            if(version.startsWith("1")) return new FactionsUUID();
            else return new FactionsNormal();
        } else if(PM.isPluginEnabled("LegacyFactions")) return new FactionsLegacy();
        else return null;
    }
    
    public abstract Object getFactionAt(Player p);
    public abstract Object getFactionAt(Location l);
    public abstract Object getCurrentFaction(Player p);
    public abstract boolean isSafeZone(Location l);
    public boolean canAttack(Player p, LivingEntity le) {return CombatUtil.canAttack(p, le);}
}