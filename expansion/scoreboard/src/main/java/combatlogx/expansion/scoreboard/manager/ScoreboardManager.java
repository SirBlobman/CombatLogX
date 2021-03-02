package combatlogx.expansion.scoreboard.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionConfigurationManager;

import combatlogx.expansion.scoreboard.ScoreboardExpansion;
import combatlogx.expansion.scoreboard.scoreboard.CustomScoreboard;

public final class ScoreboardManager {
    private final ScoreboardExpansion expansion;
    private final Map<UUID, Scoreboard> oldScoreboardMap;
    private final Map<UUID, CustomScoreboard> combatScoreboardMap;
    public ScoreboardManager(ScoreboardExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
        this.oldScoreboardMap = new HashMap<>();
        this.combatScoreboardMap = new HashMap<>();
    }

    public ScoreboardExpansion getExpansion() {
        return this.expansion;
    }

    private boolean isDisabled(Player player) {
        Expansion expansion = getExpansion();
        ICombatLogX plugin = expansion.getPlugin();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        YamlConfiguration configuration = playerDataManager.get(player);
        return !configuration.getBoolean("scoreboard", true);
    }

    private boolean shouldIgnorePrevious() {
        Expansion expansion = getExpansion();
        ExpansionConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return !configuration.getBoolean("save-previous");
    }

    public void updateScoreboard(Player player) {
        UUID uuid = player.getUniqueId();
        Expansion expansion = getExpansion();
        org.bukkit.scoreboard.ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();

        if(scoreboardManager == null) {
            Logger logger = expansion.getLogger();
            logger.warning("The Bukkit scoreboard manager is not available yet!");
            return;
        }

        if(isDisabled(player)) {
            removeScoreboard(player);
            return;
        }

        CustomScoreboard customScoreboard = this.combatScoreboardMap.getOrDefault(uuid, null);
        if(customScoreboard == null) {
            customScoreboard = enableScoreboard(player);
            if(customScoreboard == null) return;
        }

        customScoreboard.updateScoreboard();
    }

    public void removeScoreboard(Player player) {
        UUID uuid = player.getUniqueId();
        CustomScoreboard customScoreboard = this.combatScoreboardMap.remove(uuid);
        if(customScoreboard == null) return;

        Scoreboard oldScoreboard = this.oldScoreboardMap.remove(uuid);
        if(oldScoreboard != null) player.setScoreboard(oldScoreboard);
        else customScoreboard.disableScoreboard();
    }

    public void removeAll() {
        Collection<? extends Player> onlinePlayerCollection = Bukkit.getOnlinePlayers();
        onlinePlayerCollection.forEach(this::removeScoreboard);
    }

    private CustomScoreboard enableScoreboard(Player player) {
        if(isDisabled(player)) return null;
        UUID uuid = player.getUniqueId();
        savePreviousScoreboard(player);

        ScoreboardExpansion expansion = getExpansion();
        CustomScoreboard customScoreboard = new CustomScoreboard(expansion, player);
        customScoreboard.enableScoreboard();

        this.combatScoreboardMap.put(uuid, customScoreboard);
        return customScoreboard;
    }

    private void savePreviousScoreboard(Player player) {
        if(shouldIgnorePrevious()) return;
        Scoreboard oldScoreboard = player.getScoreboard();

        Objective objective = oldScoreboard.getObjective(DisplaySlot.SIDEBAR);
        if(objective != null) {
            String objectiveName = objective.getName();
            if(objectiveName.equals("combatlogx")) return;
        }

        UUID uuid = player.getUniqueId();
        this.oldScoreboardMap.put(uuid, oldScoreboard);
    }
}