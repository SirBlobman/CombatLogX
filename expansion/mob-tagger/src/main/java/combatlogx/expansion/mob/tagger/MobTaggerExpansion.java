package combatlogx.expansion.mob.tagger;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.mob.tagger.listener.ListenerDamage;
import combatlogx.expansion.mob.tagger.manager.ISpawnReasonManager;
import combatlogx.expansion.mob.tagger.manager.SpawnReasonManager_Legacy;
import combatlogx.expansion.mob.tagger.manager.SpawnReasonManager_New;

public final class MobTaggerExpansion extends Expansion {
    private ISpawnReasonManager spawnReasonManager;
    public MobTaggerExpansion(ICombatLogX plugin) {
        super(plugin);
    }
    
    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }
    
    @Override
    public void onEnable() {
        int minorVersion = VersionUtility.getMinorVersion();
        if(minorVersion < 14) {
            this.spawnReasonManager = new SpawnReasonManager_Legacy(this);
        } else {
            this.spawnReasonManager = new SpawnReasonManager_New(this);
        }

        new ListenerDamage(this).register();
    }
    
    @Override
    public void onDisable() {
        ISpawnReasonManager spawnReasonManager = getSpawnReasonManager();
        if(spawnReasonManager != null) {
            spawnReasonManager.clear();
        }
    }
    
    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
    }

    public ISpawnReasonManager getSpawnReasonManager() {
        return this.spawnReasonManager;
    }
}
