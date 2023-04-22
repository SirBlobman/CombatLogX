package com.github.sirblobman.combatlogx.api.manager;

import org.bukkit.OfflinePlayer;

import com.github.sirblobman.combatlogx.api.ICombatLogXNeeded;
import com.github.sirblobman.combatlogx.api.object.CombatTag;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IForgiveManager extends ICombatLogXNeeded {
    boolean getToggleValue(@NotNull OfflinePlayer player);

    void setToggle(@NotNull OfflinePlayer player, boolean value);

    @Nullable CombatTag getActiveRequest(@NotNull OfflinePlayer player);

    void setRequest(@NotNull OfflinePlayer player, @NotNull CombatTag request);

    void removeRequest(@NotNull OfflinePlayer player);
}
