package combatlogx.expansion.boss.bar;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.sirblobman.api.adventure.adventure.audience.Audience;
import com.github.sirblobman.api.adventure.adventure.bossbar.BossBar;
import com.github.sirblobman.api.adventure.adventure.bossbar.BossBar.Color;
import com.github.sirblobman.api.adventure.adventure.bossbar.BossBar.Overlay;
import com.github.sirblobman.api.adventure.adventure.platform.bukkit.BukkitAudiences;
import com.github.sirblobman.api.adventure.adventure.text.Component;
import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.IPlaceholderManager;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TimerUpdater;

import org.jetbrains.annotations.Contract;

public final class BossBarUpdater implements TimerUpdater {
    private final BossBarExpansion expansion;
    private final Map<UUID, BossBar> bossBarMap;

    public BossBarUpdater(BossBarExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
        this.bossBarMap = new ConcurrentHashMap<>();
    }

    @Override
    public void update(Player player, long timeLeftMillis) {
        if(isDisabled(player)) {
            actualRemove(player);
            return;
        }

        Component title = getTitle(player, timeLeftMillis);
        if(Component.empty().equals(title)) {
            actualRemove(player);
            return;
        }

        float progress = getProgress(player, timeLeftMillis);
        BossBar.Color color = getBossBarColor();
        BossBar.Overlay overlay = getBossBarOverlay();

        BossBar bossBar = getBossBar(player, true);
        bossBar.progress(progress);
        bossBar.color(color);
        bossBar.overlay(overlay);
        bossBar.name(title);

        Audience audience = getAudience(player);
        audience.showBossBar(bossBar);
    }

    @Override
    public void remove(Player player) {
        update(player, 0L);

        ICombatLogX combatLogX = getCombatLogX();
        JavaPlugin plugin = combatLogX.getPlugin();
        if (!plugin.isEnabled()) {
            return;
        }

        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, () -> actualRemove(player), 10L);
    }

    private BossBarExpansion getExpansion() {
        return this.expansion;
    }

    private Logger getLogger() {
        BossBarExpansion expansion = getExpansion();
        return expansion.getLogger();
    }

    private ICombatLogX getCombatLogX() {
        BossBarExpansion expansion = getExpansion();
        return expansion.getPlugin();
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

    @Contract("_, true -> !null")
    private BossBar getBossBar(Player player, boolean create) {
        UUID playerId = player.getUniqueId();
        if(this.bossBarMap.containsKey(playerId)) {
            return this.bossBarMap.get(playerId);
        }

        if(create) {
            Component defaultTitle = Component.text("Default Title");
            BossBar defaultBossBar = BossBar.bossBar(defaultTitle, 1.0F, Color.PURPLE, Overlay.PROGRESS);
            this.bossBarMap.put(playerId, defaultBossBar);
            return defaultBossBar;
        }

        return null;
    }

    private Audience getAudience(Player player) {
        LanguageManager languageManager = getLanguageManager();
        BukkitAudiences audiences = languageManager.getAudiences();
        if(audiences == null) {
            return Audience.empty();
        }

        return audiences.player(player);
    }

    private void actualRemove(Player player) {
        BossBar bossBar = getBossBar(player, false);
        if(bossBar == null) {
            return;
        }

        Audience audience = getAudience(player);
        audience.hideBossBar(bossBar);

        UUID playerId = player.getUniqueId();
        this.bossBarMap.remove(playerId);
    }

    private String getColorString() {
        ConfigurationManager configurationManager = this.expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return configuration.getString("bar-color", "PURPLE");
    }

    private Color getBossBarColor() {
        int minorVersion = VersionUtility.getMinorVersion();
        if(minorVersion < 9) {
            return Color.PURPLE;
        }

        String colorString = getColorString();
        try {
            return Color.valueOf(colorString);
        } catch(IllegalArgumentException ex) {
            Logger logger = getLogger();
            logger.warning("Unknown boss bar color '" + colorString + "'. Defaulting to purple.");
            return Color.PURPLE;
        }
    }

    private String getOverlayString() {
        ConfigurationManager configurationManager = this.expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return configuration.getString("bar-style");
    }

    private Overlay getBossBarOverlay() {
        int minorVersion = VersionUtility.getMinorVersion();
        if(minorVersion < 9) {
            return Overlay.PROGRESS;
        }

        String overlayString = getOverlayString();
        try {
            return Overlay.valueOf(overlayString);
        } catch(IllegalArgumentException ex) {
            Logger logger = getLogger();
            logger.warning("Unknown boss bar style '" + overlayString + "'. Defaulting to progress.");
            return Overlay.PROGRESS;
        }
    }

    private float getProgress(Player player, float timeLeftMillis) {
        ICombatManager combatManager = getCombatManager();
        long timerMaxSeconds = combatManager.getMaxTimerSeconds(player);
        float timerMaxMillis = TimeUnit.SECONDS.toMillis(timerMaxSeconds);

        float barPercentage = (timeLeftMillis / timerMaxMillis);
        if (barPercentage <= 0.0F) {
            return 0.0F;
        }

        return Math.min(barPercentage, 1.0F);
    }

    private Component getTitle(Player player, long timeLeftMillis) {
        LanguageManager languageManager = getLanguageManager();
        if(timeLeftMillis <= 0) {
            String path = ("expansion.boss-bar.ended");
            return languageManager.getMessage(player, path, null);
        }

        String path = ("expansion.boss-bar.timer");
        Replacer replacer = message -> replacePlaceholders(player, message);
        return languageManager.getMessage(player, path, replacer);
    }

    private String replacePlaceholders(Player player, String message) {
        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();

        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation == null) {
            return message;
        }

        List<Entity> enemyList = tagInformation.getEnemies();
        IPlaceholderManager placeholderManager = combatLogX.getPlaceholderManager();
        return placeholderManager.replaceAll(player, enemyList, message);
    }
}
