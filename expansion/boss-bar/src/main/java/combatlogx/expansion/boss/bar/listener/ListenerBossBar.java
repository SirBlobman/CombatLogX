package combatlogx.expansion.boss.bar.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.nms.bossbar.BossBarHandler;
import com.github.sirblobman.api.utility.MessageUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ICombatManager;
import com.github.sirblobman.combatlogx.api.event.PlayerCombatTimerChangeEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper;

import combatlogx.expansion.boss.bar.BossBarExpansion;

public final class ListenerBossBar extends ExpansionListener {
    private final BossBarExpansion expansion;
    public ListenerBossBar(BossBarExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        updateBossBar(player);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onUpdate(PlayerCombatTimerChangeEvent e) {
        Player player = e.getPlayer();
        updateBossBar(player);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onUntag(PlayerUntagEvent e) {
        Player player = e.getPlayer();
        removeBossBar(player);
    }

    public void removeBossBar(Player player) {
        BossBarHandler bossBarHandler = getBossBarHandler();
        bossBarHandler.removeBossBar(player);
    }

    private boolean isDisabled(Player player) {
        ICombatLogX plugin = getCombatLogX();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        YamlConfiguration configuration = playerDataManager.get(player);
        return !configuration.getBoolean("bossbar", true);
    }

    private BossBarHandler getBossBarHandler() {
        return this.expansion.getBossBarHandler();
    }

    private void updateBossBar(Player player) {
        if(isDisabled(player)) {
            removeBossBar(player);
            return;
        }

        String message = getMessage(player);
        double progress = getProgress(player);
        String color = getColor();
        String style = getStyle();

        BossBarHandler bossBarHandler = getBossBarHandler();
        bossBarHandler.updateBossBar(player, message, progress, color, style);
    }

    private String getColor() {
        Expansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return configuration.getString("bar-color");
    }

    private String getStyle() {
        Expansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return configuration.getString("bar-style");
    }

    private double getProgress(Player player) {
        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        double timerLeftMillis = combatManager.getTimerLeftMillis(player);
        double timerMaxMillis = (1_000L * combatManager.getMaxTimerSeconds(player));
        return Math.max(0.0D, Math.min((timerLeftMillis / timerMaxMillis), 1.0D));
    }

    private String getMessage(Player player) {
        Expansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");

        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        int timerLeftSeconds = combatManager.getTimerLeftSeconds(player);

        if(timerLeftSeconds <= 0) {
            String message = configuration.getString("ended");
            return MessageUtility.color(message);
        }

        String message = configuration.getString("timer");
        String messageColored = MessageUtility.color(message);

        String timeLeft = PlaceholderHelper.getTimeLeft(plugin, player);
        String timeLeftDecimal = PlaceholderHelper.getTimeLeftDecimal(plugin, player);
        return messageColored.replace("{time_left}", timeLeft).replace("{time_left_decimal}", timeLeftDecimal);
    }
}