package com.SirBlobman.parties;

import com.SirBlobman.combatlogx.event.PlayerCombatEvent;
import com.SirBlobman.combatlogx.expansion.CLXExpansion;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CompatParties implements CLXExpansion, Listener {
    public void enable() {
        if (Util.PM.isPluginEnabled("Parties")) {
            Util.regEvents(this);
        } else {
            String error = "Parties is not installed. This expansion is useless!";
            print(error);
        }
    }

    public String getUnlocalizedName() {
        return "CompatParties";
    }

    public String getName() {
        return "Parties Compatibility";
    }

    public String getVersion() {
        return "1";
    }

    @EventHandler
    public void pce(PlayerCombatEvent e) {
        LivingEntity ler = e.getAttacker();
        LivingEntity led = e.getTarget();
        if (ler instanceof Player) {
            Player p = (Player) ler;
            boolean can = PartyUtil.canAttack(p, led);
            if (!can)
                e.setCancelled(true);
        }
    }
}