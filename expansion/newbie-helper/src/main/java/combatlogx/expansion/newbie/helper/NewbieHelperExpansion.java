package combatlogx.expansion.newbie.helper;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.manager.IPlaceholderManager;

import combatlogx.expansion.newbie.helper.command.CommandTogglePVP;
import combatlogx.expansion.newbie.helper.configuration.NewbieHelperConfiguration;
import combatlogx.expansion.newbie.helper.configuration.WorldsConfiguration;
import combatlogx.expansion.newbie.helper.listener.ListenerDamage;
import combatlogx.expansion.newbie.helper.listener.ListenerJoin;
import combatlogx.expansion.newbie.helper.manager.CooldownManager;
import combatlogx.expansion.newbie.helper.manager.PVPManager;
import combatlogx.expansion.newbie.helper.manager.ProtectionManager;
import combatlogx.expansion.newbie.helper.placeholder.NewbieHelperPlaceholderExpansion;

public final class NewbieHelperExpansion extends Expansion {
    private final NewbieHelperConfiguration configuration;
    private final WorldsConfiguration worldsConfiguration;

    private final PVPManager pvpManager;
    private final ProtectionManager protectionManager;
    private final CooldownManager cooldownManager;

    public NewbieHelperExpansion(ICombatLogX plugin) {
        super(plugin);

        this.configuration = new NewbieHelperConfiguration();
        this.worldsConfiguration = new WorldsConfiguration();

        this.pvpManager = new PVPManager(this);
        this.protectionManager = new ProtectionManager(this);
        this.cooldownManager = new CooldownManager(this);
    }

    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        reloadConfig();
        new ListenerJoin(this).register();
        new ListenerDamage(this).register();
        new CommandTogglePVP(this).register();
        registerPlaceholderExpansion();
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
        configurationManager.reload("worlds.yml");

        getConfiguration().load(configurationManager.get("config.yml"));
        getWorldsConfiguration().load(configurationManager.get("worlds.yml"));
    }

    public @NotNull NewbieHelperConfiguration getConfiguration() {
        return this.configuration;
    }

    public @NotNull WorldsConfiguration getWorldsConfiguration() {
        return this.worldsConfiguration;
    }

    public @NotNull PVPManager getPVPManager() {
        return this.pvpManager;
    }

    public @NotNull ProtectionManager getProtectionManager() {
        return this.protectionManager;
    }

    public @NotNull CooldownManager getCooldownManager() {
        return this.cooldownManager;
    }

    private void registerPlaceholderExpansion() {
        ICombatLogX plugin = getPlugin();
        IPlaceholderManager placeholderManager = plugin.getPlaceholderManager();
        NewbieHelperPlaceholderExpansion expansion = new NewbieHelperPlaceholderExpansion(this);
        placeholderManager.registerPlaceholderExpansion(expansion);
    }
}
