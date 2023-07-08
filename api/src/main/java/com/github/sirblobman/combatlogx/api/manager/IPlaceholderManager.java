package com.github.sirblobman.combatlogx.api.manager;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.ICombatLogXNeeded;
import com.github.sirblobman.combatlogx.api.placeholder.IPlaceholderExpansion;
import com.github.sirblobman.api.shaded.adventure.text.Component;

public interface IPlaceholderManager extends ICombatLogXNeeded {
    void registerPlaceholderExpansion(@NotNull IPlaceholderExpansion expansion);

    @Nullable IPlaceholderExpansion getPlaceholderExpansion(@NotNull String id);

    @NotNull List<IPlaceholderExpansion> getPlaceholderExpansions();

    @Nullable String getPlaceholderReplacement(@NotNull Player player, @NotNull List<Entity> enemyList,
                                               @NotNull String placeholder);

    @Nullable Component getPlaceholderReplacementComponent(@NotNull Player player, @NotNull List<Entity> enemyList,
                                                           @NotNull String placeholder);

    @NotNull String replaceAll(@NotNull Player player, @NotNull List<Entity> enemyList, @NotNull String string);

    void runReplacedCommands(@NotNull Player player, @NotNull List<Entity> enemyList,
                             @NotNull Iterable<String> commands);
}
