package com.SirBlobman.parties;

import java.util.UUID;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.SirBlobman.combatlogx.utility.Util;
import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.alessiodp.parties.api.interfaces.PartyPlayer;

public class PartyUtil extends Util {
    public static PartiesAPI getAPI() {
        PartiesAPI papi = Parties.getApi();
        return papi;
    }

    public static boolean canAttack(Player p, LivingEntity le) {
        if (le instanceof Player) {
            PartiesAPI api = getAPI();
            Player t = (Player) le;
            UUID puuid = p.getUniqueId();
            UUID tuuid = t.getUniqueId();
            PartyPlayer attacker = api.getPartyPlayer(puuid);
            PartyPlayer victim = api.getPartyPlayer(tuuid);
            if (attacker == null || victim == null)
                return true;
            else {
                String aparty = attacker.getPartyName();
                String vparty = victim.getPartyName();
                if (aparty.isEmpty() || vparty.isEmpty())
                    return true;
                else if (aparty.equals(vparty))
                    return false;
                else
                    return true;
            }
        } else
            return true;
    }
}