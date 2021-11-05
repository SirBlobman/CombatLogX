package combatlogx.expansion.cheat.prevention;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.cheat.prevention.listener.ListenerBlocks;
import combatlogx.expansion.cheat.prevention.listener.ListenerBuckets;
import combatlogx.expansion.cheat.prevention.listener.ListenerChat;
import combatlogx.expansion.cheat.prevention.listener.ListenerCommands;
import combatlogx.expansion.cheat.prevention.listener.ListenerDrop;
import combatlogx.expansion.cheat.prevention.listener.ListenerElytra;
import combatlogx.expansion.cheat.prevention.listener.ListenerEntities;
import combatlogx.expansion.cheat.prevention.listener.ListenerFlight;
import combatlogx.expansion.cheat.prevention.listener.ListenerGameMode;
import combatlogx.expansion.cheat.prevention.listener.ListenerInventories;
import combatlogx.expansion.cheat.prevention.listener.ListenerPotions;
import combatlogx.expansion.cheat.prevention.listener.ListenerRiptide;
import combatlogx.expansion.cheat.prevention.listener.ListenerTeleport;
import combatlogx.expansion.cheat.prevention.listener.ListenerTotem;
import combatlogx.expansion.cheat.prevention.listener.legacy.ListenerLegacyItemPickup;
import combatlogx.expansion.cheat.prevention.listener.legacy.ListenerLegacyPortalCreate;
import combatlogx.expansion.cheat.prevention.listener.modern.ListenerModernItemPickup;
import combatlogx.expansion.cheat.prevention.listener.modern.ListenerModernPortalCreate;

public final class CheatPreventionExpansion extends Expansion {
    public CheatPreventionExpansion(ICombatLogX plugin) {
        super(plugin);
    }
    
    @Override
    public void onLoad() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.saveDefault("blocks.yml");
        configurationManager.saveDefault("buckets.yml");
        configurationManager.saveDefault("chat.yml");
        configurationManager.saveDefault("commands.yml");
        configurationManager.saveDefault("config.yml");
        configurationManager.saveDefault("entities.yml");
        configurationManager.saveDefault("flight.yml");
        configurationManager.saveDefault("game-mode.yml");
        configurationManager.saveDefault("inventories.yml");
        configurationManager.saveDefault("items.yml");
        configurationManager.saveDefault("potions.yml");
        configurationManager.saveDefault("teleportation.yml");
    }
    
    @Override
    public void onEnable() {
        new ListenerBlocks(this).register();
        new ListenerBuckets(this).register();
        new ListenerChat(this).register();
        new ListenerCommands(this).register();
        new ListenerDrop(this).register();
        new ListenerEntities(this).register();
        new ListenerFlight(this).register();
        new ListenerGameMode(this).register();
        new ListenerInventories(this).register();
        new ListenerPotions(this).register();
        new ListenerTeleport(this).register();
        
        // 1.9: Elytra
        int minorVersion = VersionUtility.getMinorVersion();
        if(minorVersion >= 9) {
            new ListenerElytra(this).register();
        }
        
        // 1.11: Totem of Undying
        if(minorVersion >= 11) {
            new ListenerTotem(this).register();
        }
        
        // 1.12: PlayerPickupItemEvent --> EntityPickupItemEvent
        if(minorVersion < 12) {
            new ListenerLegacyItemPickup(this).register();
        } else {
            new ListenerModernItemPickup(this).register();
        }
        
        // 1.13: Riptide Enchantment
        if(minorVersion >= 13) {
            new ListenerRiptide(this).register();
        }
        
        // 1.14: EntityCreatePortalEvent --> PortalCreateEvent with getEntity
        if(minorVersion < 14) {
            new ListenerLegacyPortalCreate(this).register();
        } else {
            new ListenerModernPortalCreate(this).register();
        }
    }
    
    @Override
    public void onDisable() {
        // Do Nothing
    }
    
    @Override
    public void reloadConfig() {
        ConfigurationManager configurationManager = getConfigurationManager();
        configurationManager.reload("blocks.yml");
        configurationManager.reload("buckets.yml");
        configurationManager.reload("chat.yml");
        configurationManager.reload("commands.yml");
        configurationManager.reload("config.yml");
        configurationManager.reload("entities.yml");
        configurationManager.reload("flight.yml");
        configurationManager.reload("game-mode.yml");
        configurationManager.reload("inventories.yml");
        configurationManager.reload("items.yml");
        configurationManager.reload("potions.yml");
        configurationManager.reload("teleportation.yml");
    }
}
