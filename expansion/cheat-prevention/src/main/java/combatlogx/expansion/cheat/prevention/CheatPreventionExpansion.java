package combatlogx.expansion.cheat.prevention;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.cheat.prevention.listener.ListenerBlocks;
import combatlogx.expansion.cheat.prevention.listener.ListenerBuckets;
import combatlogx.expansion.cheat.prevention.listener.ListenerCommands;
import combatlogx.expansion.cheat.prevention.listener.ListenerDrop;
import combatlogx.expansion.cheat.prevention.listener.ListenerElytra;
import combatlogx.expansion.cheat.prevention.listener.ListenerEntities;
import combatlogx.expansion.cheat.prevention.listener.ListenerFlight;
import combatlogx.expansion.cheat.prevention.listener.ListenerGameMode;
import combatlogx.expansion.cheat.prevention.listener.ListenerInventories;
import combatlogx.expansion.cheat.prevention.listener.ListenerRiptide;
import combatlogx.expansion.cheat.prevention.listener.ListenerTeleport;
import combatlogx.expansion.cheat.prevention.listener.ListenerTotem;
import combatlogx.expansion.cheat.prevention.listener.legacy.ListenerChat;
import combatlogx.expansion.cheat.prevention.listener.legacy.ListenerLegacyItemPickup;
import combatlogx.expansion.cheat.prevention.listener.legacy.ListenerLegacyPortalCreate;
import combatlogx.expansion.cheat.prevention.listener.legacy.ListenerLegacyPotions;
import combatlogx.expansion.cheat.prevention.listener.modern.ListenerModernItemPickup;
import combatlogx.expansion.cheat.prevention.listener.modern.ListenerModernPortalCreate;
import combatlogx.expansion.cheat.prevention.listener.modern.ListenerModernPotions;
import combatlogx.expansion.cheat.prevention.listener.paper.ListenerPaperChat;
import combatlogx.expansion.cheat.prevention.task.ElytraRetagTask;
import combatlogx.expansion.cheat.prevention.task.FlightRetagTask;

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
        registerListeners();
        registerTasks();

        int minorVersion = VersionUtility.getMinorVersion();
        registerVersionListeners(minorVersion);
        registerVersionTasks(minorVersion);

        registerPaperListeners();
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

    private void registerListeners() {
        new ListenerBlocks(this).register();
        new ListenerBuckets(this).register();
        new ListenerCommands(this).register();
        new ListenerDrop(this).register();
        new ListenerEntities(this).register();
        new ListenerFlight(this).register();
        new ListenerGameMode(this).register();
        new ListenerInventories(this).register();
        new ListenerTeleport(this).register();
    }

    private void registerVersionListeners(int minorVersion) {
        // Elytra were added in 1.9.
        if (minorVersion >= 9) {
            new ListenerElytra(this).register();
        }

        // Totem of Undying was added in 1.11.
        if (minorVersion >= 11) {
            new ListenerTotem(this).register();
        }

        // PlayerPickupItemEvent was deprecated in favor of EntityPickupItemEvent in 1.12.
        if (minorVersion < 12) {
            new ListenerLegacyItemPickup(this).register();
        } else {
            new ListenerModernItemPickup(this).register();
        }

        // The Riptide enchantment was added in 1.13.
        if (minorVersion >= 13) {
            new ListenerRiptide(this).register();
        }

        // EntityPotionEffect was added in 1.13.
        if (minorVersion >= 13) {
            new ListenerModernPotions(this).register();
        } else {
            new ListenerLegacyPotions(this).register();
        }

        // EntityCreatePortalEvent was replaced with PortalCreateEvent#getEntity in 1.14.
        if (minorVersion < 14) {
            new ListenerLegacyPortalCreate(this).register();
        } else {
            new ListenerModernPortalCreate(this).register();
        }
    }

    private void registerTasks() {
        new FlightRetagTask(this).register();
    }

    private void registerVersionTasks(int minorVersion) {
        // Elytra were added in 1.9.
        if (minorVersion >= 9) {
            new ElytraRetagTask(this).register();
        }
    }

    // Paper uses a custom AsyncChatEvent
    private void registerPaperListeners() {
        try {
            Class.forName("io.papermc.paper.event.player.AsyncChatEvent");
            new ListenerPaperChat(this).register();
        } catch (ReflectiveOperationException ex) {
            new ListenerChat(this).register();
        }
    }
}
