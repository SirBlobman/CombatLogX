package com.SirBlobman.factions.compat;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.*;
import com.massivecraft.massivecore.ps.PS;

import org.bukkit.Location;
import org.bukkit.entity.*;

public class FactionsNormal extends FactionsUtil {
    private static final BoardColl BC = BoardColl.get();
    public FactionsNormal() {print("Adding support for Factions...");}

    @Override
    public Faction getFactionAt(Player p) {
        Location l = p.getLocation();
        Faction f = getFactionAt(l);
        return f;
    }

    @Override
    public Faction getFactionAt(Location l) {
        PS ps = PS.valueOf(l);
        Faction f = BC.getFactionAt(ps);
        return f;
    }

    @Override
    public Faction getCurrentFaction(Player p) {
        MPlayer mp = MPlayer.get(p);
        Faction f = mp.getFaction();
        return f;
    }
    
    @Override
    public boolean canAttack(Player p, LivingEntity le) {
        if(le instanceof Player) {
            Player t = (Player) le;
            Faction fp = getCurrentFaction(p);
            Faction ft = getCurrentFaction(t);
            if(fp.isNone() || ft.isNone()) return true;
            if(fp.equals(ft)) {
                String pvp = MFlag.ID_FRIENDLYFIRE;
                boolean can = fp.getFlag(pvp);
                return can;
            } else {
                Rel rel = fp.getRelationTo(ft);
                boolean can = !rel.isFriend();
                return can;
            }
        } else return super.canAttack(p, le);
    }
    
    @Override
    public boolean isSafeZone(Location l) {
        Faction f = getFactionAt(l);
        String flag = MFlag.ID_PVP;
        boolean pvp = f.getFlag(flag);
        return !pvp;
    }
}