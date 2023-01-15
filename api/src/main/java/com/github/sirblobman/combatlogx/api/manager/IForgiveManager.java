package com.github.sirblobman.combatlogx.api.manager;

import org.bukkit.OfflinePlayer;

import com.github.sirblobman.combatlogx.api.ICombatLogXNeeded;
import com.github.sirblobman.combatlogx.api.object.CombatTag;

import org.jetbrains.annotations.Nullable;

public interface IForgiveManager extends ICombatLogXNeeded {
    boolean getToggleValue(OfflinePlayer player);

    void setToggle(OfflinePlayer player, boolean value);

    @Nullable CombatTag getActiveRequest(OfflinePlayer player);

    public void setRequest(OfflinePlayer player, CombatTag request);

    public void removeRequest(OfflinePlayer player);
}
