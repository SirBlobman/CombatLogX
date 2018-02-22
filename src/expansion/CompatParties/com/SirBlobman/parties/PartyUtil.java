package com.SirBlobman.parties;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.alessiodp.partiesapi.Parties;
import com.alessiodp.partiesapi.interfaces.PartiesAPI;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class PartyUtil extends Util {
    /*
     * Updated for Parties 2.0.X
     */
    public static PartiesAPI getAPI() {
        PartiesAPI papi = Parties.getApi();
        return papi;
    }
    
    public static String getParty(Player p) {
        PartiesAPI api = getAPI();
        String partyName = api.getPartyName(p.getUniqueId());
        if(!partyName.isEmpty()) {
            return partyName;
        }
        return null;
    }
    public static boolean canAttack(Player p, LivingEntity le) {
        if(le instanceof Player) {
            Player t = (Player) le;
            String party1 = getParty(p);
            if (party1 != null) {
                String party2 = getParty(t);
                if (party2 != null && party1.equals(party2)) {
                    return false;
                }
            }
        }
        return CombatUtil.canAttack(p, le);
    }
}