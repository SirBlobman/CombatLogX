package com.github.sirblobman.combatlogx.api.manager;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.ICombatLogXNeeded;
import com.github.sirblobman.combatlogx.api.placeholder.IPlaceholderExpansion;

import org.jetbrains.annotations.Nullable;

public interface IPlaceholderManager extends ICombatLogXNeeded {
    void registerPlaceholderExpansion(IPlaceholderExpansion expansion);
    @Nullable IPlaceholderExpansion getPlaceholderExpansion(String id);
    List<IPlaceholderExpansion> getPlaceholderExpansions();

    @Nullable String getPlaceholderReplacement(Player player, List<Entity> enemyList, String placeholder);
    String replaceAll(Player player, List<Entity> enemyList, String string);

    void runReplacedCommands(Player player, List<Entity> enemyList, Iterable<String> commands);
}
