package com.SirBlobman.not;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.utility.CombatUtil;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class FinalMonitor implements Listener {
    @EventHandler(priority=EventPriority.MONITOR)
    public void sce(SpecialCombatEvent e) {
        if(e.isCancelled()) return;
        Player p = e.getPlayer();
        if(CombatUtil.canBeTagged(p)) Combat.tag(p, null);
    }
}