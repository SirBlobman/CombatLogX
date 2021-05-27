package combatlogx.expansion.action.bar;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.nms.PlayerHandler;
import com.github.sirblobman.api.utility.MessageUtility;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TimerUpdater;
import com.github.sirblobman.combatlogx.api.utility.PlaceholderHelper;

public final class ActionBarUpdater implements TimerUpdater {
    private final ActionBarExpansion expansion;
    public ActionBarUpdater(ActionBarExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
    }

    private ICombatLogX getCombatLogX() {
        return this.expansion.getPlugin();
    }

    private PlayerHandler getPlayerHandler() {
        ICombatLogX combatLogX = getCombatLogX();
        MultiVersionHandler multiVersionHandler = combatLogX.getMultiVersionHandler();
        return multiVersionHandler.getPlayerHandler();
    }

    @Override
    public void update(Player player, long timeLeftMillis) {
        if(isDisabled(player)) return;

        String message = getMessage(player, timeLeftMillis);
        if(message == null || message.isEmpty()) return;

        PlayerHandler playerHandler = getPlayerHandler();
        playerHandler.sendActionBar(player, message);
    }

    @Override
    public void remove(Player player) {
        update(player, 0L);
    }

    private boolean isDisabled(Player player) {
        ICombatLogX combatLogX = getCombatLogX();
        PlayerDataManager playerDataManager = combatLogX.getPlayerDataManager();
        YamlConfiguration playerData = playerDataManager.get(player);
        return !playerData.getBoolean("actionbar", true);
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

        if(message.contains("{bars}")) {
            String bars = getBars(player, timeLeftMillis);
            message = message.replace("{bars}", bars);
        }

        return message;
    }

    private String getBars(Player player, long timeLeftMillis) {
        ConfigurationManager configurationManager = this.expansion.getConfigurationManager();
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
        double timerMaxMillis = (combatManager.getMaxTimerSeconds(player) * 1_000L);
        double progressPercent = ((double) timeLeftMillis / timerMaxMillis);
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
