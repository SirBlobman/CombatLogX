package com.github.sirblobman.combatlogx.api.manager;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.github.sirblobman.combatlogx.api.ICombatLogXNeeded;

public interface ICrystalManager extends ICombatLogXNeeded {
    @Nullable Player getPlacer(@NotNull Entity crystal);

    void setPlacer(@NotNull Entity crystal, @NotNull Player player);

    void remove(@NotNull UUID crystalId);
}
