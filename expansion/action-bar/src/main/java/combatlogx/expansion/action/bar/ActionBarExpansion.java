package combatlogx.expansion.action.bar;

import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.manager.ITimerManager;

public final class ActionBarExpansion extends Expansion {
    private final ActionBarConfiguration configuration;

    public ActionBarExpansion(ICombatLogX plugin) {
        super(plugin);
        this.configuration = new ActionBarConfiguration();
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        ICombatLogX plugin = getPlugin();
        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion < 8) {
            Logger logger = getLogger();
            logger.warning("This expansion requires Spigot 1.8.8 or higher.");
            selfDisable();
            return;
        }

        reloadConfig();
        ITimerManager timerManager = plugin.getTimerManager();
        timerManager.addUpdaterTask(new ActionBarUpdater(this));
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");

        ActionBarConfiguration configuration = getConfiguration();
        YamlConfiguration yamlConfiguration = configurationManager.get("config.yml");
        configuration.load(yamlConfiguration);
    }

    ActionBarConfiguration getConfiguration() {
        return this.configuration;
    }
}
