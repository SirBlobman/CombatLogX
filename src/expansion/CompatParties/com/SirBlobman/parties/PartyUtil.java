package com.SirBlobman.parties;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.alessiodp.parties.utils.api.PartiesAPI;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PartyUtil extends Util {
    public static PartiesAPI getAPI() {
        PartiesAPI papi = new PartiesAPI();
        return papi;
    }
    
    public static String getParty(Player p) {
        PartiesAPI api = getAPI();
        UUID uuid = p.getUniqueId();
        boolean has = api.haveParty(uuid);
        if(has) {
            String party = api.getPartyName(uuid);
            return party;
        } else return null;
    }
    
    public static boolean canAttack(Player p, LivingEntity le) {
        if(le instanceof Player) {
            Player t = (Player) le;
            String party1 = getParty(p);
            if(party1 != null) {
                String party2 = getParty(t);
                if(party2 != null) {
                    if(party1.equals(party2)) return false;
                    else return CombatUtil.canAttack(p, le);
                }
            }
        } return CombatUtil.canAttack(p, le);
    }
}