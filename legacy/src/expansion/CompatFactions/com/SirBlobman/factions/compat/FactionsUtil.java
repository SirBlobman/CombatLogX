package com.SirBlobman.factions.compat;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.Vector;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.SirBlobman.factions.config.ConfigFactions;

public abstract class FactionsUtil extends Util {
    public static FactionsUtil getFactions() {
        if (PM.isPluginEnabled("Factions")) {
            Plugin pl = PM.getPlugin("Factions");
            PluginDescriptionFile pdf = pl.getDescription();
            String version = pdf.getVersion();
            List<String> authors = pdf.getAuthors();
            if (authors.contains("ProSavage"))
                return new FactionsSavage();
            else if (version.startsWith("1"))
                return new FactionsUUID();
            else
                return new FactionsNormal();
        } else if (PM.isPluginEnabled("LegacyFactions"))
            return new FactionsLegacy();
        else
            return null;
    }

    public static Location getChunkCenter(Location to) {
        Chunk c = to.getChunk();
        int x = c.getX() * 16;
        int z = c.getZ() * 16;
        Location l = new Location(to.getWorld(), x, 64, z);
        return l;
    }

    public Vector getSafeZoneKnockbackVector(Location original, Location ploc) {
        if (isSafeZone(ploc)) {
            Location center = getChunkCenter(ploc);
            Vector from = center.toVector();
            Vector to = original.toVector();
            Vector vector = to.subtract(from);
            vector.multiply(ConfigFactions.OPTION_NO_SAFEZONE_ENTRY_STRENGTH);
            vector.setY(0);
            return vector;
        } else
            return new Vector(0, 0, 0);
    }

    public Vector getMobsZoneKnockbackVector(Location original, Location ploc) {
        if (isSafeFromMobs(ploc)) {
            Location center = getChunkCenter(ploc);
            Vector from = center.toVector();
            Vector to = original.toVector();
            Vector vector = to.subtract(from);
            vector.multiply(ConfigFactions.OPTION_NO_SAFEZONE_ENTRY_STRENGTH);
            vector.setY(0);
            return vector;
        } else
            return new Vector(0, 0, 0);
    }

    public abstract Object getFactionAt(Player p);

    public abstract Object getFactionAt(Location l);

    public abstract Object getCurrentFaction(Player p);

    public abstract boolean isSafeZone(Location l);

    public abstract boolean isSafeFromMobs(Location l);

    public boolean canAttack(Player p, LivingEntity le) {
        return CombatUtil.canAttack(p, le);
    }
}