package com.github.sirblobman.combatlogx.api.manager;

import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;
import com.github.sirblobman.combatlogx.api.object.UntagReason;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ICombatManager {
    boolean tag(Player player, LivingEntity enemy, TagType tagType, TagReason tagReason);

    boolean tag(Player player, LivingEntity enemy, TagType tagType, TagReason tagReason, long customEndMillis);

    void untag(Player player, UntagReason untagReason);

    boolean isInCombat(Player player);

    @NotNull List<Player> getPlayersInCombat();

    @Nullable LivingEntity getEnemy(Player player);

    @Nullable OfflinePlayer getByEnemy(LivingEntity enemy);

    long getTimerLeftMillis(Player player);

    int getTimerLeftSeconds(Player player);

    int getMaxTimerSeconds(Player player);

    String replaceVariables(Player player, LivingEntity enemy, String string);

    @Nullable Permission getBypassPermission();

    boolean canBypass(Player player);

    void onReload();
}
