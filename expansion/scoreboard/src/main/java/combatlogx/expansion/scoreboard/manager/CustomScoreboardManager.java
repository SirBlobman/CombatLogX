package combatlogx.expansion.scoreboard.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;

import combatlogx.expansion.scoreboard.ScoreboardConfiguration;
import combatlogx.expansion.scoreboard.ScoreboardExpansion;
import combatlogx.expansion.scoreboard.scoreboard.CustomScoreboard;

public final class CustomScoreboardManager {
    private final ScoreboardExpansion expansion;
    private final Map<UUID, Scoreboard> oldScoreboardMap;
    private final Map<UUID, CustomScoreboard> combatScoreboardMap;

    public CustomScoreboardManager(@NotNull ScoreboardExpansion expansion) {
        this.expansion = expansion;
        this.oldScoreboardMap = new HashMap<>();
        this.combatScoreboardMap = new HashMap<>();
    }

    public @NotNull ScoreboardExpansion getExpansion() {
        return this.expansion;
    }

    private @NotNull ICombatLogX getCombatLogX() {
        ScoreboardExpansion expansion = getExpansion();
        return expansion.getPlugin();
    }

    private @NotNull PlayerDataManager getPlayerDataManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getPlayerDataManager();
    }

    private @NotNull ScoreboardConfiguration getConfiguration() {
        ScoreboardExpansion expansion = getExpansion();
        return expansion.getConfiguration();
    }

    private boolean isGlobalEnabled() {
        ScoreboardConfiguration configuration = getConfiguration();
        return configuration.isEnabled();
    }

    private boolean isDisabled(@NotNull Player player) {
        if (isGlobalEnabled()) {
            PlayerDataManager playerDataManager = getPlayerDataManager();
            YamlConfiguration configuration = playerDataManager.get(player);
            return !configuration.getBoolean("scoreboard", true);
        }

        return true;
    }

    private boolean shouldIgnorePrevious() {
        ScoreboardConfiguration configuration = getConfiguration();
        return !configuration.isSavePrevious();
    }

    public void updateScoreboard(@NotNull Player player) {
        UUID playerId = player.getUniqueId();
        if (isDisabled(player)) {
            removeScoreboard(player);
            return;
        }

        CustomScoreboard customScoreboard = this.combatScoreboardMap.getOrDefault(playerId, null);
        if (customScoreboard == null) {
            createScoreboard(player);
            return;
        }

        customScoreboard.updateScoreboard();
    }

    private void createScoreboard(@NotNull Player player) {
        CustomScoreboard customScoreboard = enableScoreboard(player);
        if (customScoreboard != null) {
            customScoreboard.updateScoreboard();
        }
    }

    public void removeScoreboard(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        CustomScoreboard customScoreboard = this.combatScoreboardMap.remove(uuid);
        if (customScoreboard == null) {
            return;
        }

        Scoreboard oldScoreboard = this.oldScoreboardMap.remove(uuid);
        if (oldScoreboard != null) {
            player.setScoreboard(oldScoreboard);
        } else {
            customScoreboard.disableScoreboard();
        }
    }

    public void removeAll() {
        Collection<? extends Player> onlinePlayerCollection = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayerCollection) {
            removeScoreboard(player);
        }
    }

    private @Nullable CustomScoreboard enableScoreboard(@NotNull Player player) {
        if (isDisabled(player)) {
            return null;
        }

        UUID uuid = player.getUniqueId();
        savePreviousScoreboard(player);

        ScoreboardExpansion expansion = getExpansion();
        CustomScoreboard customScoreboard = new CustomScoreboard(expansion, player);
        customScoreboard.enableScoreboard();

        this.combatScoreboardMap.put(uuid, customScoreboard);
        return customScoreboard;
    }

    private void savePreviousScoreboard(@NotNull Player player) {
        if (shouldIgnorePrevious()) {
            return;
        }

        Scoreboard oldScoreboard = player.getScoreboard();
        Objective objective = oldScoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (objective != null) {
            String objectiveName = objective.getName();
            if (objectiveName.equals("combatlogx")) {
                return;
            }
        }

        UUID playerId = player.getUniqueId();
        this.oldScoreboardMap.put(playerId, oldScoreboard);
    }
}
