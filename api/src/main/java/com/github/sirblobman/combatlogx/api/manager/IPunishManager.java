package com.github.sirblobman.combatlogx.api.manager;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.object.UntagReason;

public interface IPunishManager {
    boolean punish(Player player, UntagReason punishReason, LivingEntity previousEnemy);
    
    long getPunishmentCount(OfflinePlayer player);
}
