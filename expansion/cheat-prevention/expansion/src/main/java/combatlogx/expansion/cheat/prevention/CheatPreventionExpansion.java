package combatlogx.expansion.cheat.prevention;

import combatlogx.expansion.cheat.prevention.listener.*;

import com.SirBlobman.api.utility.VersionUtility;
import com.SirBlobman.combatlogx.api.ICombatLogX;
import com.SirBlobman.combatlogx.api.expansion.Expansion;
import com.SirBlobman.combatlogx.api.expansion.ExpansionConfigurationManager;

public final class CheatPreventionExpansion extends Expansion {
    public CheatPreventionExpansion(ICombatLogX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad() {
        ExpansionConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("config.yml");
        configurationManager.saveDefault("blocks.yml");
        configurationManager.saveDefault("items.yml");
    }

    @Override
    public void onEnable() {
        new ListenerBlocks(this).register();
        new ListenerChat(this).register();
        new ListenerCommands(this).register();
        /*
        new ListenerEntities(this).register();
        new ListenerFlight(this).register();
        new ListenerGameMode(this).register();
        new ListenerInventories(this).register();
        new ListenerPotions(this).register();
        new ListenerTeleport(this).register();
         */

        // 1.9: Elytra
        int minorVersion = VersionUtility.getMinorVersion();
        // if(minorVersion >= 9) new ListenerElytra(this).register();

        // 1.11: Totem of Undying
        // if(minorVersion >= 11) new ListenerTotem(this).register();

        // 1.12: PlayerPickupItemEvent --> EntityPickupItemEvent
        CheatPreventionListener listenerItemPickup = (minorVersion < 12 ? new ListenerLegacyItemPickup(this) : new ListenerModernItemPickup(this));
        listenerItemPickup.register();

        // 1.13: Riptide Enchantment
        // if(minorVersion >= 13) new ListenerRiptide(this).register();
    }

    @Override
    public void onDisable() {
        // Do Nothing
    }

    @Override
    public void reloadConfig() {
        ExpansionConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("config.yml");
        configurationManager.reload("blocks.yml");
        configurationManager.reload("items.yml");
    }
}