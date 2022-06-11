package com.github.sirblobman.combatlogx.api.manager;

import org.bukkit.entity.Player;

public interface IDeathManager {

    void kill(Player player);

    boolean wasPunishKilled(Player player);

    boolean stopTracking(Player player);

}
