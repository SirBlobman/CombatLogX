package combatlogx.expansion.boss.bar;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.bossbar.BossBarHandler;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TimerUpdater;

public final class BossBarUpdater implements TimerUpdater {
    private final BossBarExpansion expansion;

    public BossBarUpdater(BossBarExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
    }

    @Override
    public void update(Player player, long timeLeftMillis) {
        if (isDisabled(player)) {
            actualRemove(player);
            return;
        }

        String message = getMessage(player, timeLeftMillis);
        if (message == null || message.isEmpty()) {
            actualRemove(player);
            return;
        }

        double progress = getProgress(player, timeLeftMillis);
        String color = getColor();
        String style = getStyle();

        BossBarHandler bossBarHandler = getBossBarHandler();
        bossBarHandler.updateBossBar(player, message, progress, color, style);
    }

    @Override
    public void remove(Player player) {
        update(player, 0L);

        JavaPlugin plugin = getCombatLogX().getPlugin();
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, () -> actualRemove(player), 10L);
    }

    private BossBarExpansion getExpansion() {
        return this.expansion;
    }

    private ICombatLogX getCombatLogX() {
        BossBarExpansion expansion = getExpansion();
        return expansion.getPlugin();
    }

    private BossBarHandler getBossBarHandler() {
        BossBarExpansion expansion = getExpansion();
        return expansion.getBossBarHandler();
    }

    private LanguageManager getLanguageManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getLanguageManager();
    }

    private PlayerDataManager getPlayerDataManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getPlayerDataManager();
    }

    private ICombatManager getCombatManager() {
        ICombatLogX combatLogX = getCombatLogX();
        return combatLogX.getCombatManager();
    }

    private boolean isGlobalEnabled() {
        BossBarExpansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return configuration.getBoolean("enabled", true);
    }

    private boolean isDisabled(Player player) {
        if (isGlobalEnabled()) {
            PlayerDataManager playerDataManager = getPlayerDataManager();
            YamlConfiguration playerData = playerDataManager.get(player);
            return !playerData.getBoolean("bossbar", true);
        }

        return true;
    }

    private void actualRemove(Player player) {
        if (!player.isOnline()) {
            return;
        }

        BossBarHandler bossBarHandler = getBossBarHandler();
        bossBarHandler.removeBossBar(player);
    }

    private String getColor() {
        ConfigurationManager configurationManager = this.expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return configuration.getString("bar-color");
    }

    private String getStyle() {
        ConfigurationManager configurationManager = this.expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return configuration.getString("bar-style");
    }

    private double getProgress(Player player, double timeLeftMillis) {
        ICombatManager combatManager = getCombatManager();
        long timerMaxSeconds = combatManager.getMaxTimerSeconds(player);
        double timerMaxMillis = TimeUnit.SECONDS.toMillis(timerMaxSeconds);

        double barPercentage = (timeLeftMillis / timerMaxMillis);
        if (barPercentage <= 0.0D) {
            return 0.0D;
        }

        return Math.min(barPercentage, 1.0D);
    }

    private String getMessage(Player player, long timeLeftMillis) {
        LanguageManager languageManager = getLanguageManager();
        if (timeLeftMillis <= 0) {
            String path = ("expansion.boss-bar.ended");
            return languageManager.getMessage(player, path, null, true);
        }

        String path = ("expansion.boss-bar.timer");
        String message = languageManager.getMessage(player, path, null, true);
        if (message.isEmpty()) {
            return null;
        }

        return replacePlaceholders(player, message);
    }

    private String replacePlaceholders(Player player, String message) {
        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();

        TagInformation tagInformation = combatManager.getTagInformation(player);
        if(tagInformation == null) {
            return message;
        }

        Entity enemy = tagInformation.getCurrentEnemy();
        return combatManager.replaceVariables(player, enemy, message);
    }
}
