package com.SirBlobman.combatlogx.expansion.compatibility.factions.hook;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;

public class HookMassiveFactions extends FactionsHook {
    @Override
    public Faction getFactionAt(Location location) {
        if(location == null) return null;

        BoardColl boardColl = BoardColl.get();
        PS ps = PS.valueOf(location);
        return boardColl.getFactionAt(ps);
    }

    @Override
    public Faction getFactionFor(OfflinePlayer offline) {
        if(offline == null) return null;

        MPlayer mplayer = MPlayer.get(offline);
        if(mplayer == null) return null;

        return mplayer.getFaction();
    }

    @Override
    public boolean isSafeZone(Location location) {
        Faction faction = getFactionAt(location);
        if(faction == null) return false;

        String id = faction.getId();
        return id.equals(Factions.ID_SAFEZONE);
    }
}