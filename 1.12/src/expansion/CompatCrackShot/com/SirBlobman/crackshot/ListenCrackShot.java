package com.SirBlobman.crackshot;

import com.SirBlobman.combatlogx.listener.event.PlayerCombatEvent;
import com.SirBlobman.combatlogx.utility.*;
import com.shampaggon.crackshot.events.WeaponDamageEntityEvent;

import org.bukkit.entity.*;
import org.bukkit.event.*;

public class ListenCrackShot implements Listener {
    @EventHandler(priority=EventPriority.HIGHEST)
    public void wdee(WeaponDamageEntityEvent e) {
        if(e.isCancelled()) return;
        double dam = e.getDamage();
        if(dam > 0) {
            Player p = e.getPlayer();
            Entity en = e.getVictim();
            if(en instanceof LivingEntity) {
                LivingEntity le = (LivingEntity) en;
                if(CombatUtil.canAttack(p, le)) {
                    PlayerCombatEvent pce = new PlayerCombatEvent(p, le, true);
                    Util.call(pce);
                    if(le instanceof Player) {
                        Player t = (Player) le;
                        PlayerCombatEvent pce2 = new PlayerCombatEvent(t, p, false);
                        Util.call(pce2);
                    }
                }
            }
        }
    }
}