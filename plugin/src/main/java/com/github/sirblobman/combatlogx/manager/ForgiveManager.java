package com.github.sirblobman.combatlogx.manager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.IForgiveManager;
import com.github.sirblobman.combatlogx.api.object.CombatTag;

import org.jetbrains.annotations.Nullable;

public final class ForgiveManager extends Manager implements IForgiveManager {
    private final Map<UUID, CombatTag> requestMap;

    public ForgiveManager(ICombatLogX plugin) {
        super(plugin);
        this.requestMap = new ConcurrentHashMap<>();
    }

    public boolean getToggleValue(OfflinePlayer player) {
        ICombatLogX plugin = getCombatLogX();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        YamlConfiguration configuration = playerDataManager.get(player);
        return configuration.getBoolean("forgive-toggle", false);
    }

    public void setToggle(OfflinePlayer player, boolean value) {
        ICombatLogX plugin = getCombatLogX();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        YamlConfiguration configuration = playerDataManager.get(player);

        configuration.set("forgive-toggle", value);
        playerDataManager.save(player);
    }

    @Nullable
    public CombatTag getActiveRequest(OfflinePlayer player) {
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

    public void setRequest(OfflinePlayer player, CombatTag tag) {
        UUID playerId = player.getUniqueId();
        this.requestMap.put(playerId, tag);
    }

    public void removeRequest(OfflinePlayer player) {
        UUID playerId = player.getUniqueId();
        this.requestMap.remove(playerId);
    }
}
