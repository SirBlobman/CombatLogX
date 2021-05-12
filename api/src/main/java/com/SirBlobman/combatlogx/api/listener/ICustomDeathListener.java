package com.SirBlobman.combatlogx.api.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface ICustomDeathListener extends Listener {
    void add(Player player);
    void remove(Player player);
    boolean contains(Player player);
}
