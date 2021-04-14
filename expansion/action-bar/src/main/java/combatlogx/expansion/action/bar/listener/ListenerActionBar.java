package combatlogx.expansion.action.bar.listener;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.nms.PlayerHandler;
import com.github.sirblobman.api.utility.MessageUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ICombatManager;
import com.github.sirblobman.combatlogx.api.event.PlayerCombatTimerChangeEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerTagEvent;
import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper;

import combatlogx.expansion.action.bar.ActionBarExpansion;

public final class ListenerActionBar extends ExpansionListener {
    public ListenerActionBar(ActionBarExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onTag(PlayerTagEvent e) {
        Player player = e.getPlayer();
        updateActionBar(player);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onUpdate(PlayerCombatTimerChangeEvent e) {
        Player player = e.getPlayer();
        updateActionBar(player);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onUntag(PlayerUntagEvent e) {
        Player player = e.getPlayer();
        updateActionBar(player);
    }

    private void updateActionBar(Player player) {
        ICombatLogX plugin = getCombatLogX();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        YamlConfiguration configuration = playerDataManager.get(player);
        if(!configuration.getBoolean("actionbar", true)) return;

        String message = getMessage(player);
        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        PlayerHandler playerHandler = multiVersionHandler.getPlayerHandler();
        playerHandler.sendActionBar(player, message);
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

        String timeLeftNormal = PlaceholderHelper.getTimeLeft(plugin, player);
        String timeLeftDecimal = PlaceholderHelper.getTimeLeftDecimal(plugin, player);
        String barsString = getBars(player);

        String message = configuration.getString("timer");
        String messageColored = MessageUtility.color(message);
        return messageColored.replace("{time_left}", timeLeftNormal).replace("{time_left_decimal}", timeLeftDecimal).replace("{bars}", barsString);
    }

    private String getBars(Player player) {
        Expansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");

        int scale = configuration.getInt("scale");
        String leftColor = configuration.getString("left-color");
        String leftSymbol = configuration.getString("left-symbol");
        String rightColor = configuration.getString("right-color");
        String rightSymbol = configuration.getString("right-symbol");
        if(leftColor == null) leftColor = "&a";
        if(leftSymbol == null) leftSymbol = "|";
        if(rightColor == null) rightColor = "&a";
        if(rightSymbol == null) rightSymbol = "|";

        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        double timerLeftMillis = combatManager.getTimerLeftMillis(player);
        double timerMaxMillis = (combatManager.getMaxTimerSeconds(player) * 1_000L);
        double progressPercent = (timerLeftMillis / timerMaxMillis);
        long leftBarsCount = Math.round(scale * progressPercent);
        long rightBarsCount = (scale - leftBarsCount);

        StringBuilder builder = new StringBuilder(leftColor);
        for(long i = 0; i < leftBarsCount; i++) builder.append(leftSymbol);

        builder.append(rightColor);
        for(long i = 0; i < rightBarsCount; i++) builder.append(rightSymbol);

        String barsString = builder.toString();
        return MessageUtility.color(barsString);
    }
}