package combatlogx.expansion.boss.bar;

import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.nms.bossbar.BossBarHandler;
import com.github.sirblobman.api.utility.MessageUtility;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TimerUpdater;
import com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper;

public final class BossBarUpdater implements TimerUpdater {
    private final BossBarExpansion expansion;

    public BossBarUpdater(BossBarExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
    }

    private ICombatLogX getCombatLogX() {
        return this.expansion.getPlugin();
    }

    private BossBarHandler getBossBarHandler() {
        return this.expansion.getBossBarHandler();
    }

    @Override
    public void update(Player player, long timeLeftMillis) {
        if(isDisabled(player)) {
            actualRemove(player);
            return;
        }

        String message = getMessage(player, timeLeftMillis);
        if(message == null || message.isEmpty()) {
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

    private void actualRemove(Player player) {
        BossBarHandler bossBarHandler = getBossBarHandler();
        bossBarHandler.removeBossBar(player);
    }

    private boolean isDisabled(Player player) {
        ICombatLogX plugin = getCombatLogX();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        YamlConfiguration configuration = playerDataManager.get(player);
        return !configuration.getBoolean("bossbar", true);
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
        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();

        long timerMaxSeconds = combatManager.getMaxTimerSeconds(player);
        double timerMaxMillis = TimeUnit.SECONDS.toMillis(timerMaxSeconds);
        double barPercentage = (timeLeftMillis / timerMaxMillis);

        if(barPercentage <= 0.0D) return 0.0D;
        return Math.min(barPercentage, 1.0D);
    }

    private String getMessage(Player player, long timeLeftMillis) {
        ConfigurationManager configurationManager = this.expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        if(timeLeftMillis <= 0) {
            String message = configuration.getString("ended");
            return MessageUtility.color(message);
        }

        ICombatLogX plugin = getCombatLogX();
        String message = configuration.getString("timer");
        if(message == null || message.isEmpty()) return null;
        message = MessageUtility.color(message);

        if(message.contains("{time_left}")) {
            String timeLeftNormal = PlaceholderHelper.getTimeLeft(plugin, player);
            message = message.replace("{time_left}", timeLeftNormal);
        }

        if(message.contains("{time_left_decimal}")) {
            String timeLeftDecimal = PlaceholderHelper.getTimeLeftDecimal(plugin, player);
            message = message.replace("{time_left_decimal}", timeLeftDecimal);
        }

        return message;
    }
}
