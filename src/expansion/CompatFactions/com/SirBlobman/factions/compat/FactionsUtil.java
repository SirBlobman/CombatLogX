package com.SirBlobman.factions.compat;

import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.Vector;

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
    
    public static Location getChunkCenter(Location to) {
        Chunk c = to.getChunk();
        int x = c.getX() * 16;
        int z = c.getZ() * 16;
        Location l = new Location(to.getWorld(), x, 64, z);
        return l;
    }
    
    public Vector getKnockbackVector(Location original, Location ploc) {
        if(isSafeZone(ploc)) {
            Location center = getChunkCenter(ploc);
            Vector from = center.toVector();
            Vector to = original.toVector();
            Vector vector = to.subtract(from);
            vector.multiply(ConfigOptions.CHEAT_PREVENT_NO_ENTRY_STRENGTH);
            vector.setY(0);
            return vector;
        } else return new Vector(0, 0, 0);
    }
    
    public abstract Object getFactionAt(Player p);
    public abstract Object getFactionAt(Location l);
    public abstract Object getCurrentFaction(Player p);
    public abstract boolean isSafeZone(Location l);
    public boolean canAttack(Player p, LivingEntity le) {return CombatUtil.canAttack(p, le);}
}