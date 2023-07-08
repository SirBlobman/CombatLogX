package com.github.sirblobman.combatlogx.manager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.IForgiveManager;
import com.github.sirblobman.combatlogx.api.object.CombatTag;

public final class ForgiveManager extends Manager implements IForgiveManager {
    private final Map<UUID, CombatTag> requestMap;

    public ForgiveManager(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.requestMap = new ConcurrentHashMap<>();
    }

    @Override
    public boolean getToggleValue(@NotNull OfflinePlayer player) {
        ICombatLogX plugin = getCombatLogX();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        YamlConfiguration configuration = playerDataManager.get(player);
        return configuration.getBoolean("forgive-toggle", false);
    }

    @Override
    public void setToggle(@NotNull OfflinePlayer player, boolean value) {
        ICombatLogX plugin = getCombatLogX();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        YamlConfiguration configuration = playerDataManager.get(player);

        configuration.set("forgive-toggle", value);
        playerDataManager.save(player);
    }

    @Override
    public @Nullable CombatTag getActiveRequest(@NotNull OfflinePlayer player) {
        UUID playerId = player.getUniqueId();
        CombatTag combatTag = this.requestMap.get(playerId);
        if (combatTag == null) {
            return null;
        }

        if (combatTag.isExpired()) {
            removeRequest(player);
            return null;
        }

        return combatTag;
    }

    @Override
    public void setRequest(@NotNull OfflinePlayer player, @NotNull CombatTag tag) {
        UUID playerId = player.getUniqueId();
        this.requestMap.put(playerId, tag);
    }

    @Override
    public void removeRequest(@NotNull OfflinePlayer player) {
        UUID playerId = player.getUniqueId();
        this.requestMap.remove(playerId);
    }
}
