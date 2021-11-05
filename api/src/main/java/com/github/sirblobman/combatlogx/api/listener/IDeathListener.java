package com.github.sirblobman.combatlogx.api.listener;

import org.bukkit.entity.Player;

public interface IDeathListener {
    void add(Player player);
    
    void remove(Player player);
    
    boolean contains(Player player);
}
