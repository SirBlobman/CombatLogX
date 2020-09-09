package combatlogx.expansion.newbie.helper;

import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionConfigurationManager;

import combatlogx.expansion.newbie.helper.command.CommandTogglePVP;
import combatlogx.expansion.newbie.helper.listener.ListenerDamage;
import combatlogx.expansion.newbie.helper.listener.ListenerJoin;
import combatlogx.expansion.newbie.helper.manager.PVPManager;
import combatlogx.expansion.newbie.helper.manager.ProtectionManager;

public final class NewbieHelperExpansion extends Expansion {
    private final PVPManager pvpManager;
    private final ProtectionManager protectionManager;
    public NewbieHelperExpansion(ICombatLogX plugin) {
        super(plugin);
        this.pvpManager = new PVPManager();
        this.protectionManager = new ProtectionManager(this);
    }

    @Override
    public void onLoad() {
        ExpansionConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
    }

    @Override
    public void onEnable() {
        new ListenerJoin(this).register();
        new ListenerDamage(this).register();
        new CommandTogglePVP(this).register();
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    @Override
    public void reloadConfig() {
        ExpansionConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
    }

    public PVPManager getPVPManager() {
        return this.pvpManager;
    }

    public ProtectionManager getProtectionManager() {
        return this.protectionManager;
    }
}