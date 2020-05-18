package com.SirBlobman.combatlogx.expansion.compatibility.factions.hook;

import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import net.prosavage.factionsx.core.FPlayer;
import net.prosavage.factionsx.core.Faction;
import net.prosavage.factionsx.manager.GridManager;
import net.prosavage.factionsx.manager.PlayerManager;

public class HookFactionsX extends FactionsHook {
    @Override
    public Faction getFactionAt(Location location) {
        Chunk chunk = location.getChunk();
        return GridManager.INSTANCE.getFactionAt(chunk);
    }
    
    @Override
    public Faction getFactionFor(OfflinePlayer offline) {
        UUID uuid = offline.getUniqueId();
        FPlayer fplayer = PlayerManager.INSTANCE.getFPlayer(uuid);
        return (fplayer == null ? null : fplayer.getFaction());
    }
    
    @Override
    public boolean isSafeZone(Location location) {
        Faction faction = getFactionAt(location);
        return faction.isSafezone();
    }
}