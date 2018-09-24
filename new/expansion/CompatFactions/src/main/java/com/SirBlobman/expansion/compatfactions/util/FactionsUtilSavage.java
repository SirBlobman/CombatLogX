package com.SirBlobman.expansion.compatfactions.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.massivecraft.factions.*;
import com.massivecraft.factions.struct.Relation;

public class FactionsUtilSavage extends FactionsUtil {
    @Override
    public Faction getFaction(Player player) {
        FPlayers fplayers = FPlayers.getInstance();
        FPlayer fplayer = fplayers.getByPlayer(player);
        Faction faction = fplayer.getFaction();
        return faction;
    }

    @Override
    public Faction getFaction(Location loc) {
        Board board = Board.getInstance();
        FLocation floc = new FLocation(loc);
        Faction faction = board.getFactionAt(floc);
        return faction;
    }
    
    @Override
    public boolean canAttack(Player player1, Player player2) {
        Faction faction1 = getFaction(player1);
        Faction faction2 = getFaction(player2);
        
        Location loc1 = player1.getLocation();
        Location loc2 = player2.getLocation();
        Faction factionLoc1 = getFaction(loc1);
        Faction factionLoc2 = getFaction(loc2);
        
        if(factionLoc1.isSafeZone() || factionLoc1.noPvPInTerritory()) return false;
        else if(factionLoc2.isSafeZone() || factionLoc2.noPvPInTerritory()) return false;
        else if(faction1.equals(faction2)) return false;
        else {
            Relation rel = faction1.getRelationTo(faction2);
            return !rel.isAlly();
        }
    }
    
    @Override
    public boolean isSafeZone(Location loc) {
        Faction faction = getFaction(loc);
        return (faction.isSafeZone() || faction.noPvPInTerritory());
    }
}