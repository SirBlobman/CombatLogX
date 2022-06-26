package combatlogx.expansion.mob.tagger;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.mob.tagger.listener.ListenerDamage;
import combatlogx.expansion.mob.tagger.manager.ISpawnReasonManager;
import combatlogx.expansion.mob.tagger.manager.SpawnReasonManager_Legacy;
import combatlogx.expansion.mob.tagger.manager.SpawnReasonManager_New;
import org.jetbrains.annotations.Nullable;

public final class MobTaggerExpansion extends Expansion {
    private ISpawnReasonManager spawnReasonManager;
    private Permission mobCombatBypassPermission;

    public MobTaggerExpansion(ICombatLogX plugin) {
        super(plugin);
        this.spawnReasonManager = null;
        this.mobCombatBypassPermission = null;
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
        setupBypassPermission();
    }

    public ISpawnReasonManager getSpawnReasonManager() {
        return this.spawnReasonManager;
    }

    @Nullable
    public Permission getMobCombatBypassPermission() {
        return this.mobCombatBypassPermission;
    }

    private void setupBypassPermission() {
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");

        String permissionName = configuration.getString("bypass-permission");
        if(permissionName == null || permissionName.isEmpty()) {
            this.mobCombatBypassPermission = null;
            return;
        }

        String description = "CombatLogX Bypass Permission for Mob Combat";
        this.mobCombatBypassPermission = new Permission(permissionName, description, PermissionDefault.FALSE);
    }
}
