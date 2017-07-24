package com.SirBlobman.factions.compat;

import org.bukkit.Location;
import org.bukkit.entity.*;

import net.redstoneore.legacyfactions.*;
import net.redstoneore.legacyfactions.entity.*;

public class FactionsLegacy extends FactionsUtil {
    private static final Board BOARD = Board.get();
    public FactionsLegacy() {print("Adding support for LegacyFactions...");}
    
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
        Faction f = FactionColl.get(p);
        return f;
    }

    @Override
    public boolean canAttack(Player p, LivingEntity le) {
        if(le instanceof Player) {
            Player t = (Player) le;
            Faction fp = getCurrentFaction(p);
            Faction ft = getCurrentFaction(t);
            if(fp.isWilderness() || ft.isWilderness()) return true;
            if(fp.equals(ft)) return false;
            else {
                Relation rel = fp.getRelationTo(ft);
                boolean can = rel.isEnemy();
                return can;
            }
        } else return super.canAttack(p, le);
    }
    
    @Override
    public boolean isSafeZone(Location l) {
        Faction f = getFactionAt(l);
        return f.isSafeZone();
    }
}