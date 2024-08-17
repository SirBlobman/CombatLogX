package combatlogx.expansion.cheat.prevention;

import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.VersionUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;

import combatlogx.expansion.cheat.prevention.configuration.BlockConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.BucketConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.ChatConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.CheatPreventionConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.CommandConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.EntityConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.FlightConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.GameModeConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.IBlockConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.IBucketConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.IChatConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.ICommandConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.IConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.IEntityConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.IFlightConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.IGameModeConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.IInventoryConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.IItemConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.IPotionConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.ITeleportConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.InventoryConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.ItemConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.PotionConfiguration;
import combatlogx.expansion.cheat.prevention.configuration.TeleportConfiguration;
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
import combatlogx.expansion.cheat.prevention.listener.modern.ListenerInventoriesModern;
import combatlogx.expansion.cheat.prevention.listener.modern.ListenerModernItemPickup;
import combatlogx.expansion.cheat.prevention.listener.modern.ListenerModernPortalCreate;
import combatlogx.expansion.cheat.prevention.listener.modern.ListenerModernPotions;
import combatlogx.expansion.cheat.prevention.listener.paper.ListenerPaperChat;
import combatlogx.expansion.cheat.prevention.listener.paper.ListenerPaperEntityInsideBlock;
import combatlogx.expansion.cheat.prevention.task.ElytraRetagTask;
import combatlogx.expansion.cheat.prevention.task.FlightRetagTask;

public final class CheatPreventionExpansion extends Expansion implements ICheatPreventionExpansion {
    private final CheatPreventionConfiguration configuration;
    private final BlockConfiguration blockConfiguration;
    private final BucketConfiguration bucketConfiguration;
    private final ChatConfiguration chatConfiguration;
    private final CommandConfiguration commandConfiguration;
    private final EntityConfiguration entityConfiguration;
    private final FlightConfiguration flightConfiguration;
    private final GameModeConfiguration gameModeConfiguration;
    private final InventoryConfiguration inventoryConfiguration;
    private final ItemConfiguration itemConfiguration;
    private final PotionConfiguration potionConfiguration;
    private final TeleportConfiguration teleportConfiguration;

    public CheatPreventionExpansion(ICombatLogX plugin) {
        super(plugin);
        this.configuration = new CheatPreventionConfiguration();
        this.blockConfiguration = new BlockConfiguration();
        this.bucketConfiguration = new BucketConfiguration();
        this.chatConfiguration = new ChatConfiguration();
        this.commandConfiguration = new CommandConfiguration();
        this.entityConfiguration = new EntityConfiguration();
        this.flightConfiguration = new FlightConfiguration();
        this.gameModeConfiguration = new GameModeConfiguration();
        this.inventoryConfiguration = new InventoryConfiguration();
        this.itemConfiguration = new ItemConfiguration();
        this.potionConfiguration = new PotionConfiguration();
        this.teleportConfiguration = new TeleportConfiguration();
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
        reloadConfig();
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

        getBlockConfiguration().load(configurationManager.get("blocks.yml"));
        getBucketConfiguration().load(configurationManager.get("buckets.yml"));
        getChatConfiguration().load(configurationManager.get("chat.yml"));
        getCommandConfiguration().load(configurationManager.get("commands.yml"));
        getConfiguration().load(configurationManager.get("config.yml"));
        getEntityConfiguration().load(configurationManager.get("entities.yml"));
        getFlightConfiguration().load(configurationManager.get("flight.yml"));
        getGameModeConfiguration().load(configurationManager.get("game-mode.yml"));
        getInventoryConfiguration().load(configurationManager.get("inventories.yml"));
        getItemConfiguration().load(configurationManager.get("items.yml"));
        getPotionConfiguration().load(configurationManager.get("potions.yml"));
        getTeleportConfiguration().load(configurationManager.get("teleportation.yml"));
    }

    @Override
    public @NotNull Expansion getExpansion() {
        return this;
    }

    @Override
    public @NotNull IConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public @NotNull IBlockConfiguration getBlockConfiguration() {
        return this.blockConfiguration;
    }

    @Override
    public @NotNull IBucketConfiguration getBucketConfiguration() {
        return this.bucketConfiguration;
    }

    @Override
    public @NotNull IChatConfiguration getChatConfiguration() {
        return this.chatConfiguration;
    }

    @Override
    public @NotNull ICommandConfiguration getCommandConfiguration() {
        return this.commandConfiguration;
    }

    @Override
    public @NotNull IEntityConfiguration getEntityConfiguration() {
        return this.entityConfiguration;
    }

    @Override
    public @NotNull IFlightConfiguration getFlightConfiguration() {
        return this.flightConfiguration;
    }

    @Override
    public @NotNull IGameModeConfiguration getGameModeConfiguration() {
        return this.gameModeConfiguration;
    }

    @Override
    public @NotNull IInventoryConfiguration getInventoryConfiguration() {
        return this.inventoryConfiguration;
    }

    @Override
    public @NotNull IItemConfiguration getItemConfiguration() {
        return this.itemConfiguration;
    }

    @Override
    public @NotNull IPotionConfiguration getPotionConfiguration() {
        return this.potionConfiguration;
    }

    @Override
    public @NotNull ITeleportConfiguration getTeleportConfiguration() {
        return this.teleportConfiguration;
    }

    private void registerListeners() {
        new ListenerBlocks(this).register();
        new ListenerBuckets(this).register();
        new ListenerCommands(this).register();
        new ListenerDrop(this).register();
        new ListenerEntities(this).register();
        new ListenerFlight(this).register();
        new ListenerGameMode(this).register();
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

        // InventoryView can be an interface or abstract class depending on version
        if (isAbstractInventoryView()) {
            new ListenerInventoriesModern(this).register();
        } else {
            new ListenerInventories(this).register();
        }
    }

    private boolean isAbstractInventoryView() {
        int minorVersion = VersionUtility.getMinorVersion();
        if (minorVersion > 20) {
            return true;
        }

        String minecraftVersion = VersionUtility.getMinecraftVersion();
        return (minecraftVersion.equals("1.20.5") || minecraftVersion.equals("1.20.6"));
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

    // Paper has some custom event classes that don't exist on Spigot.
    private void registerPaperListeners() {
        if (checkPaperClass("io.papermc.paper.event.player.AsyncChatEvent")) {
            new ListenerPaperChat(this).register();
        } else {
            new ListenerChat(this).register();
        }

        if (checkPaperClass("io.papermc.paper.event.entity.EntityInsideBlockEvent")) {
            new ListenerPaperEntityInsideBlock(this).register();
        }
    }

    private boolean checkPaperClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ReflectiveOperationException ex) {
            Logger logger = getLogger();
            logger.info(className + " is not supported on this server version.");
            return false;
        }
    }
}
