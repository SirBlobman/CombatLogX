package com.SirBlobman.expansion.compatfactions.util;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import org.bukkit.Location;

public class FactionsUtilSavage extends FactionsUtil {

    @Override
    public Faction getFaction(Location loc) {
        Board board = Board.getInstance();
        FLocation floc = new FLocation(loc);
        return board.getFactionAt(floc);
    }

    @Override
    public boolean isSafeZone(Location loc) {
        Faction faction = getFaction(loc);
        return (faction.isSafeZone() || faction.noPvPInTerritory());
    }
}