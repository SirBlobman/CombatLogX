package com.SirBlobman.combatlogx.expansion.compatibility.factions.hook;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.massivecraft.factions.*;

public class HookFactionsUUID extends FactionsHook {
    @Override
    public Faction getFactionAt(Location location) {
        if(location == null) return null;

        Board board = Board.getInstance();
        FLocation flocation = new FLocation(location);
        return board.getFactionAt(flocation);
    }

    @Override
    public Faction getFactionFor(OfflinePlayer offline) {
        if(offline == null) return null;

        FPlayers fplayers = FPlayers.getInstance();
        if(offline.isOnline()) {
            Player player = offline.getPlayer();
            FPlayer fplayer = fplayers.getByPlayer(player);
            return fplayer.getFaction();
        }

        FPlayer fplayer = fplayers.getByOfflinePlayer(offline);
        return fplayer.getFaction();
    }

    @Override
    public boolean isSafeZone(Location location) {
        Faction faction = getFactionAt(location);
        if(faction == null) return false;

        return faction.isSafeZone();
    }
}