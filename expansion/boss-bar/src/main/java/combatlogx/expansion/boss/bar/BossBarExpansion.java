package combatlogx.expansion.boss.bar;

import java.util.Collection;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.SirBlobman.api.nms.MultiVersionHandler;
import com.SirBlobman.api.nms.bossbar.BossBarHandler;
import com.SirBlobman.api.utility.VersionUtility;
import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionConfigurationManager;
import com.SirBlobman.combatlogx.api.expansion.ExpansionManager;

import combatlogx.expansion.boss.bar.listener.ListenerBossBar;

public final class BossBarExpansion extends Expansion {
    private final ListenerBossBar listenerBossBar;
    private final BossBarHandler bossBarHandler;
    public BossBarExpansion(ICombatLogX plugin) {
        super(plugin);
        this.listenerBossBar = new ListenerBossBar(this);
        this.bossBarHandler = new MultiVersionHandler(plugin.getPlugin()).getBossBarHandler();
    }

    @Override
    public void onLoad() {
        ExpansionConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        int minorVersion = VersionUtility.getMinorVersion();
        if(minorVersion < 9 && !checkDependency("BossBarAPI", true)) {
            Logger logger = getLogger();
            logger.warning("The boss bar expansion requires BossBarAPI if you are in a version below 1.9!");

            ICombatLogX plugin = getPlugin();
            ExpansionManager expansionManager = plugin.getExpansionManager();
            expansionManager.disableExpansion(this);
            return;
        }

        this.listenerBossBar.register();
    }

    @Override
    public void onDisable() {
        Collection<? extends Player> onlinePlayerCollection = Bukkit.getOnlinePlayers();
        onlinePlayerCollection.forEach(this.listenerBossBar::removeBossBar);
    }

    @Override
    public void reloadConfig() {
        ExpansionConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
    }

    public BossBarHandler getBossBarHandler() {
        return this.bossBarHandler;
    }
}