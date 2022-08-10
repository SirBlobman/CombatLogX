package combatlogx.expansion.newbie.helper;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.manager.IPlaceholderManager;

import combatlogx.expansion.newbie.helper.command.CommandTogglePVP;
import combatlogx.expansion.newbie.helper.listener.ListenerDamage;
import combatlogx.expansion.newbie.helper.listener.ListenerJoin;
import combatlogx.expansion.newbie.helper.manager.CooldownManager;
import combatlogx.expansion.newbie.helper.manager.PVPManager;
import combatlogx.expansion.newbie.helper.manager.ProtectionManager;
import combatlogx.expansion.newbie.helper.placeholder.NewbieHelperPlaceholderExpansion;

public final class NewbieHelperExpansion extends Expansion {
    private final PVPManager pvpManager;
    private final ProtectionManager protectionManager;
    private final CooldownManager cooldownManager;

    public NewbieHelperExpansion(ICombatLogX plugin) {
        super(plugin);
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
        new ListenerJoin(this).register();
        new ListenerDamage(this).register();
        new CommandTogglePVP(this).register();

        ICombatLogX plugin = getPlugin();
        IPlaceholderManager placeholderManager = plugin.getPlaceholderManager();
        placeholderManager.registerPlaceholderExpansion(new NewbieHelperPlaceholderExpansion(this));
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

    public PVPManager getPVPManager() {
        return this.pvpManager;
    }

    public ProtectionManager getProtectionManager() {
        return this.protectionManager;
    }

    public CooldownManager getCooldownManager() {
        return this.cooldownManager;
    }

    public boolean shouldCheckDisabledWorlds() {
        ConfigurationManager configurationManager = getConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("config.yml");
        return configuration.getBoolean("prevent-pvp-toggle-in-disabled-worlds");
    }
}
