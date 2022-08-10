package combatlogx.expansion.action.bar;

import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatColor;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.language.Replacer;
import com.github.sirblobman.api.utility.MessageUtility;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.manager.IPlaceholderManager;
import com.github.sirblobman.combatlogx.api.object.TagInformation;
import com.github.sirblobman.combatlogx.api.object.TimerUpdater;

public final class ActionBarUpdater implements TimerUpdater {
    private final ActionBarExpansion expansion;

    public ActionBarUpdater(ActionBarExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
    }

    @Override
    public void update(Player player, long timeLeftMillis) {
        if (isDisabled(player)) {
            return;
        }

        sendActionBar(player, timeLeftMillis);
    }

    @Override
    public void remove(Player player) {
        update(player, 0L);
    }

    private ActionBarExpansion getExpansion() {
        return this.expansion;
    }

    private ICombatLogX getCombatLogX() {
        ActionBarExpansion expansion = getExpansion();
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

    private boolean isGlobalEnabled() {
        ActionBarExpansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return configuration.getBoolean("enabled", true);
    }

    private boolean isDisabled(Player player) {
        if (isGlobalEnabled()) {
            PlayerDataManager playerDataManager = getPlayerDataManager();
            YamlConfiguration playerData = playerDataManager.get(player);
            return !playerData.getBoolean("actionbar", true);
        }

        return true;
    }

    private void sendActionBar(Player player, long timeLeftMillis) {
        LanguageManager languageManager = getLanguageManager();
        if(timeLeftMillis <= 0) {
            String path = ("expansion.action-bar.ended");
            languageManager.sendActionBar(player, path, null);
            return;
        }

        Replacer replacer = message -> replacePlaceholders(player, message, timeLeftMillis);
        languageManager.sendActionBar(player, "expansion.action-bar.timer", replacer);
    }

    private String replacePlaceholders(Player player, String message, long timeLeftMillis) {
        if (message.contains("{bars}")) {
            String bars = getBars(player, timeLeftMillis);
            message = message.replace("{bars}", bars);
        }

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

    private String getBars(Player player, long timeLeftMillis) {
        ConfigurationManager configurationManager = this.expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");

        int scale = configuration.getInt("scale");
        String leftColorString = configuration.getString("left-color", "GREEN");
        String leftSymbol = configuration.getString("left-symbol", "|");
        String rightColorString = configuration.getString("right-color", "RED");
        String rightSymbol = configuration.getString("right-symbol", "|");

        ChatColor leftColor = getChatColor(leftColorString);
        ChatColor rightColor = getChatColor(rightColorString);

        ICombatLogX plugin = getCombatLogX();
        ICombatManager combatManager = plugin.getCombatManager();
        double timerMaxMillis = (combatManager.getMaxTimerSeconds(player) * 1_000L);
        double progressPercent = ((double) timeLeftMillis / timerMaxMillis);
        long leftBarsCount = Math.round(scale * progressPercent);
        long rightBarsCount = (scale - leftBarsCount);

        StringBuilder builder = new StringBuilder();
        builder.append(leftColor);
        for (long i = 0; i < leftBarsCount; i++) {
            builder.append(leftSymbol);
        }

        builder.append(rightColor);
        for (long i = 0; i < rightBarsCount; i++) {
            builder.append(rightSymbol);
        }

        String barsString = builder.toString();
        return MessageUtility.color(barsString);
    }

    @SuppressWarnings("deprecation")
    private ChatColor getChatColor(String value) {
        try {
            int minorVersion = VersionUtility.getMinorVersion();
            if (minorVersion < 16) {
                return ChatColor.valueOf(value);
            }

            return ChatColor.of(value);
        } catch (IllegalArgumentException ex) {
            return ChatColor.WHITE;
        }
    }
}
