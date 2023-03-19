package combatlogx.expansion.boss.bar;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

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
import com.github.sirblobman.api.adventure.adventure.text.Component;
import com.github.sirblobman.api.adventure.adventure.text.TextComponent;
import com.github.sirblobman.api.adventure.adventure.text.TextReplacementConfig;
import com.github.sirblobman.api.adventure.adventure.text.format.TextColor;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
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
        if (isDisabled(player)) {
            actualRemove(player);
            return;
        }

        Component title = getTitle(player, timeLeftMillis);
        if (Component.empty().equals(title)) {
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
        BossBarConfiguration configuration = getConfiguration();
        return configuration.isEnabled();
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
        if (this.bossBarMap.containsKey(playerId)) {
            return this.bossBarMap.get(playerId);
        }

        if (create) {
            Component defaultTitle = Component.text("Default Title");
            BossBar defaultBossBar = BossBar.bossBar(defaultTitle, 1.0F, Color.PURPLE, Overlay.PROGRESS);
            this.bossBarMap.put(playerId, defaultBossBar);
            return defaultBossBar;
        }

        return null;
    }

    private Audience getAudience(Player player) {
        LanguageManager languageManager = getLanguageManager();
        return languageManager.getAudience(player);
    }

    private void actualRemove(Player player) {
        BossBar bossBar = getBossBar(player, false);
        if (bossBar == null) {
            return;
        }

        Audience audience = getAudience(player);
        audience.hideBossBar(bossBar);

        UUID playerId = player.getUniqueId();
        this.bossBarMap.remove(playerId);
    }

    private Color getBossBarColor() {
        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 9) {
            return Color.PURPLE;
        }

        BossBarConfiguration configuration = getConfiguration();
        return configuration.getBossBarColor();
    }

    private Overlay getBossBarOverlay() {
        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 9) {
            return Overlay.PROGRESS;
        }

        BossBarConfiguration configuration = getConfiguration();
        return configuration.getBossBarStyle();
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
        if (timeLeftMillis <= 0) {
            String path = ("expansion.boss-bar.ended");
            return languageManager.getMessage(player, path);
        }

        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();
        IPlaceholderManager placeholderManager = combatLogX.getPlaceholderManager();
        Component message = languageManager.getMessage(player, "expansion.boss-bar.timer");

        TextReplacementConfig replacementConfig = getBarsReplacement(player, timeLeftMillis);
        message = message.replaceText(replacementConfig);

        TagInformation tagInformation = combatManager.getTagInformation(player);
        if (tagInformation != null) {
            List<Entity> enemyList = tagInformation.getEnemies();
            Pattern placeholderPattern = Pattern.compile("\\{(\\S+)}");
            TextReplacementConfig.Builder builder = TextReplacementConfig.builder();
            builder.match(placeholderPattern);
            builder.replacement((matchResult, builderCopy) -> {
                String placeholder = matchResult.group(1);
                Component replacement = placeholderManager.getPlaceholderReplacementComponent(player,
                        enemyList, placeholder);
                return (replacement == null ? Component.text(placeholder) : replacement);
            });

            TextReplacementConfig replacement = builder.build();
            message = message.replaceText(replacement);
        }

        return message;
    }

    private TextReplacementConfig getBarsReplacement(Player player, long timeLeftMillis) {
        TextReplacementConfig.Builder builder = TextReplacementConfig.builder();
        builder.matchLiteral("{bars}");
        builder.replacement(getBars(player, timeLeftMillis));
        return builder.build();
    }

    private BossBarConfiguration getConfiguration() {
        BossBarExpansion expansion = getExpansion();
        return expansion.getConfiguration();
    }

    private Component getBars(Player player, long timeLeftMillis) {
        BossBarConfiguration configuration = getConfiguration();
        long scale = configuration.getScale();
        String leftSymbol = configuration.getLeftSymbol();
        String rightSymbol = configuration.getRightSymbol();
        TextColor leftColor = configuration.getLeftColor();
        TextColor rightColor = configuration.getRightColor();

        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        long timerMaxSeconds = combatManager.getMaxTimerSeconds(player);

        double timerMaxMillis = TimeUnit.SECONDS.toMillis(timerMaxSeconds);
        double scaleDouble = (double) scale;
        double timeLeftMillisDouble = (double) timeLeftMillis;

        double percent = clamp(timeLeftMillisDouble / timerMaxMillis);
        long leftBarsCount = Math.round(scaleDouble * percent);
        long rightBarsCount = (scale - leftBarsCount);

        TextComponent.Builder builder = Component.text();
        Component leftSymbolComponent = Component.text(leftSymbol, leftColor);
        Component rightSymbolComponent = Component.text(rightSymbol, rightColor);

        for (long i = 0; i < leftBarsCount; i++) {
            builder.append(leftSymbolComponent);
        }

        for (long i = 0; i < rightBarsCount; i++) {
            builder.append(rightSymbolComponent);
        }

        return builder.build();
    }

    private double clamp(double value) {
        return Math.max(0.0D, Math.min(value, 1.0D));
    }
}
