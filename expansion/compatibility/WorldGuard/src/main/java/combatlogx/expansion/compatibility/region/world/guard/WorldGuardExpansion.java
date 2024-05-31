package combatlogx.expansion.compatibility.region.world.guard;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionExpansion;
import com.github.sirblobman.combatlogx.api.expansion.region.RegionHandler;

import combatlogx.expansion.compatibility.region.world.guard.handler.WorldGuardRegionHandler;
import combatlogx.expansion.compatibility.region.world.guard.hook.HookWorldGuard;
import combatlogx.expansion.compatibility.region.world.guard.listener.ListenerPreventLeaving;
import combatlogx.expansion.compatibility.region.world.guard.listener.ListenerWorldGuard;

public final class WorldGuardExpansion extends RegionExpansion {
    private final WorldGuardConfiguration worldGuardConfiguration;
    private final HookWorldGuard hookWorldGuard;
    private RegionHandler<?> regionHandler;

    public WorldGuardExpansion(@NotNull ICombatLogX plugin) {
        super(plugin);
        this.worldGuardConfiguration = new WorldGuardConfiguration();
        this.hookWorldGuard = new HookWorldGuard(this);
        this.regionHandler = null;
    }

    @Override
    public void onLoad() {
        super.onLoad();

        if (!checkDependency("WorldGuard", false)) {
            return;
        }

        HookWorldGuard hook = getHookWorldGuard();
        hook.registerFlags();

        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("worldguard.yml");
    }

    @Override
    public boolean checkDependencies() {
        return checkDependency("WorldGuard", true);
    }

    @Override
    public void afterEnable() {
        new ListenerWorldGuard(this).register();
        new ListenerPreventLeaving(this).register();
    }

    @Override
    public @NotNull RegionHandler<?> getRegionHandler() {
        if (this.regionHandler == null) {
            this.regionHandler = new WorldGuardRegionHandler(this);
        }

        return this.regionHandler;
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();

        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("worldguard.yml");
        getWorldGuardConfiguration().load(configurationManager.get("worldguard.yml"));
    }

    public @NotNull HookWorldGuard getHookWorldGuard() {
        return this.hookWorldGuard;
    }

    public @NotNull WorldGuardConfiguration getWorldGuardConfiguration() {
        return this.worldGuardConfiguration;
    }
}
