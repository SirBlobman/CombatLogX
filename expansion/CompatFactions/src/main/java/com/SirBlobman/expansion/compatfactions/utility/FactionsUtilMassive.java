package com.SirBlobman.expansion.compatfactions.utility;

import org.bukkit.Location;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.massivecore.ps.PS;

public class FactionsUtilMassive extends FactionsUtil {
    @Override
    public Faction getFaction(Location loc) {
        PS ps = PS.valueOf(loc);
        BoardColl board = BoardColl.get();
        return board.getFactionAt(ps);
    }

    @Override
    public boolean isSafeZone(Location loc) {
        Faction faction = getFaction(loc);
        MFlag pvpFlag = MFlag.getFlagPvp();
        return !faction.getFlag(pvpFlag);
    }
}