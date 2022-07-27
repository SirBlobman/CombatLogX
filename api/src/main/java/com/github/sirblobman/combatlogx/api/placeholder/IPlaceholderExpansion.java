package com.github.sirblobman.combatlogx.api.placeholder;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.ICombatLogXNeeded;

public interface IPlaceholderExpansion extends ICombatLogXNeeded {
    String getId();
    String getReplacement(Player player, List<Entity> enemyList, String placeholder);
}
