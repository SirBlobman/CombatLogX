package com.SirBlobman.combatlogx.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.SirBlobman.combatlogx.config.ConfigLang;
import com.SirBlobman.combatlogx.event.PlayerPunishEvent.PunishReason;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent;
import com.SirBlobman.combatlogx.event.PlayerUntagEvent.UntagReason;
import com.SirBlobman.combatlogx.utility.CombatUtil;
import com.SirBlobman.combatlogx.utility.Util;

public class FinalMonitor implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onUntag(PlayerUntagEvent e) {
        Player player = e.getPlayer();
        UntagReason reason = e.getUntagReason();
        LivingEntity enemy = e.getPreviousEnemy();
        
        switch (reason) {
        case QUIT:
            CombatUtil.punish(player, PunishReason.DISCONNECTED, enemy);
            break;
        case KICK:
            CombatUtil.punish(player, PunishReason.KICKED, enemy);
            break;
        case EXPIRE:
            CombatUtil.punish(player, PunishReason.UNKNOWN, enemy);
            String msgExpire = ConfigLang.getWithPrefix("messages.combat.expire");
            Util.sendMessage(player, msgExpire);
            break;
        case EXPIRE_ENEMY_DEATH:
            CombatUtil.punish(player, PunishReason.UNKNOWN, enemy);
            String msgExpireDeath = ConfigLang.getWithPrefix("messages.combat.enemy death");
            Util.sendMessage(player, msgExpireDeath);
            break;
        }
    }
}