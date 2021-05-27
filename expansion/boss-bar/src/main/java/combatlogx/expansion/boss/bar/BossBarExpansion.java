package combatlogx.expansion.boss.bar;

import java.util.logging.Logger;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.api.nms.bossbar.BossBarHandler;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.ITimerManager;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionManager;

public final class BossBarExpansion extends Expansion {
    private final BossBarHandler bossBarHandler;
    public BossBarExpansion(ICombatLogX plugin) {
        super(plugin);
        this.bossBarHandler = new MultiVersionHandler(plugin.getPlugin()).getBossBarHandler();
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        int minorVersion = VersionUtility.getMinorVersion();
        ICombatLogX plugin = getPlugin();
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
