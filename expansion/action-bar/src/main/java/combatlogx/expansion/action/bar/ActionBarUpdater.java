package combatlogx.expansion.action.bar;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatColor;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.configuration.PlayerDataManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.nms.PlayerHandler;
import com.github.sirblobman.api.utility.MessageUtility;
import com.github.sirblobman.api.utility.Validate;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TimerUpdater;

public final class ActionBarUpdater implements TimerUpdater {
    private final ActionBarExpansion expansion;
    
    public ActionBarUpdater(ActionBarExpansion expansion) {
        this.expansion = Validate.notNull(expansion, "expansion must not be null!");
    }
    
    @Override
    public void update(Player player, long timeLeftMillis) {
        if(isDisabled(player)) {
            return;
        }
        
        String message = getMessage(player, timeLeftMillis);
        if(message == null || message.isEmpty()) {
            return;
        }
        
        PlayerHandler playerHandler = getPlayerHandler();
        playerHandler.sendActionBar(player, message);
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
    
    private PlayerHandler getPlayerHandler() {
        ICombatLogX combatLogX = getCombatLogX();
        MultiVersionHandler multiVersionHandler = combatLogX.getMultiVersionHandler();
        return multiVersionHandler.getPlayerHandler();
    }
    
    private boolean isGlobalEnabled() {
        ActionBarExpansion expansion = getExpansion();
        ConfigurationManager configurationManager = expansion.getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return configuration.getBoolean("enabled", true);
    }
    
    private boolean isDisabled(Player player) {
        if(isGlobalEnabled()) {
            PlayerDataManager playerDataManager = getPlayerDataManager();
            YamlConfiguration playerData = playerDataManager.get(player);
            return !playerData.getBoolean("actionbar", true);
        }
        
        return true;
    }
    
    private String getMessage(Player player, long timeLeftMillis) {
        LanguageManager languageManager = getLanguageManager();
        if(timeLeftMillis <= 0) {
            String path = ("expansion.action-bar.ended");
            return languageManager.getMessage(player, path, null, true);
        }
        
        String path = ("expansion.action-bar.timer");
        String message = languageManager.getMessage(player, path, null, true);
        if(message.isEmpty()) {
            return null;
        }
        
        return replacePlaceholders(player, message, timeLeftMillis);
    }
    
    private String replacePlaceholders(Player player, String message, long timeLeftMillis) {
        if(message.contains("{bars}")) {
            String bars = getBars(player, timeLeftMillis);
            message = message.replace("{bars}", bars);
        }
    
        ICombatLogX combatLogX = getCombatLogX();
        ICombatManager combatManager = combatLogX.getCombatManager();
        LivingEntity enemy = combatManager.getEnemy(player);
        return combatManager.replaceVariables(player, enemy, message);
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
        for(long i = 0; i < leftBarsCount; i++) {
            builder.append(leftSymbol);
        }
        
        builder.append(rightColor);
        for(long i = 0; i < rightBarsCount; i++) {
            builder.append(rightSymbol);
        }
        
        String barsString = builder.toString();
        return MessageUtility.color(barsString);
    }
    
    @SuppressWarnings("deprecation")
    private ChatColor getChatColor(String value) {
        try {
            int minorVersion = VersionUtility.getMinorVersion();
            if(minorVersion < 16) {
                return ChatColor.valueOf(value);
            }
            
            return ChatColor.of(value);
        } catch(IllegalArgumentException ex) {
            return ChatColor.WHITE;
        }
    }
}
