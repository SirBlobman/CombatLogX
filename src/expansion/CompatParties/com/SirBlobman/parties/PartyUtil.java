package com.SirBlobman.parties;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;
import com.alessiodp.partiesapi.Parties;
import com.alessiodp.partiesapi.interfaces.PartiesAPI;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class PartyUtil extends Util {
    /*
     * Updated for Parties 2.2.X
     */
    public static PartiesAPI getAPI() {
        PartiesAPI papi = Parties.getApi();
        return papi;
    }
    
    public static boolean canAttack(Player p, LivingEntity le) {
        if(le instanceof Player) {
            PartiesAPI api = getAPI();
            PartyPlayer attacker = api.getPartyPlayer(UUID.randomUUID());
            PartyPlayer victim = api.getPartyPlayer(UUID.randomUUID());
            if(!attacker.getPartyName().isEmpty() && attacker.getPartyName().equals(victim.getPartyName())) {
                return false;
            }
        return CombatUtil.canAttack(p, le);
    }
}