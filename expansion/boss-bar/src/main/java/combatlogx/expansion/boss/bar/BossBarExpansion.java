package combatlogx.expansion.boss.bar;

import java.util.logging.Logger;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.bossbar.BossBarHandler;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;
import com.github.sirblobman.combatlogx.api.manager.ITimerManager;

public final class BossBarExpansion extends Expansion {
    private final BossBarHandler bossBarHandler;

    public BossBarExpansion(ICombatLogX plugin) {
        super(plugin);

        MultiVersionHandler multiVersionHandler = plugin.getMultiVersionHandler();
        this.bossBarHandler = multiVersionHandler.getBossBarHandler();
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
        if(minorVersion < 9 && !checkDependency("BossBarAPI", true)) {
            Logger logger = getLogger();
            logger.warning("The boss bar expansion requires BossBarAPI if you are in a version below 1.9!");

            ExpansionManager expansionManager = plugin.getExpansionManager();
            expansionManager.disableExpansion(this);
            return;
        }

        ITimerManager timerManager = plugin.getTimerManager();
        timerManager.addUpdaterTask(new BossBarUpdater(this));
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
    }

    public BossBarHandler getBossBarHandler() {
        return this.bossBarHandler;
    }
}
