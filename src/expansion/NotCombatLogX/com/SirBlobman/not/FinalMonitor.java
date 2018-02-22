package com.SirBlobman.not;

import com.SirBlobman.combatlogx.Combat;
import com.SirBlobman.combatlogx.config.ConfigOptions;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;

import org.bukkit.entity.LivingEntity;
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
    
    @EventHandler(priority=EventPriority.MONITOR)
    public void onCombat(SpecialCombatEvent e) {
        if(ConfigOptions.OPTION_LOG_TO_FILE) {
            LivingEntity attacker = e.getAttacker();
            LivingEntity target = e.getTarget();
            String msg = Combat.log(attacker, target);
            if(ConfigOptions.OPTION_LOG_TO_CONSOLE) Util.print(msg);
        }
    }
}