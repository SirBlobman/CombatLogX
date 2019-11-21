package com.SirBlobman.combatlogx.api.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface ICustomDeathListener extends Listener {
    public void add(Player player);
    public void remove(Player player);
}