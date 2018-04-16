package com.SirBlobman.factions.compat;

import com.massivecraft.factions.*;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FactionsSavage extends FactionsUtil {
    private static final Board BOARD = Board.getInstance();
    public FactionsSavage() {print("Adding support for SavageFactions...");}
    
    @Override
    public Faction getFactionAt(Player p) {
        Location l = p.getLocation();
        Faction f = getFactionAt(l);
        return f;
    }

    @Override
    public Faction getFactionAt(Location l) {
        FLocation fl = new FLocation(l);
        Faction f = BOARD.getFactionAt(fl);
        return f;
    }

    @Override
    public Faction getCurrentFaction(Player p) {
        FPlayers fps = FPlayers.getInstance();
        FPlayer fp = fps.getByPlayer(p);
        Faction f = fp.getFaction();
        return f;
    }

    @Override
    public boolean isSafeZone(Location l) {
        Faction f = getFactionAt(l);
        return f.isSafeZone();
    }

    @Override
    public boolean isSafeFromMobs(Location l) {
        Faction f = getFactionAt(l);
        return f.noMonstersInTerritory();
    }
}
