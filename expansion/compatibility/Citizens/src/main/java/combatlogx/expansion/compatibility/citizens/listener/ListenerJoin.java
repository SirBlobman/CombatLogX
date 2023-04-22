package combatlogx.expansion.compatibility.citizens.listener;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.sirblobman.api.language.ComponentHelper;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.manager.ICombatManager;
import com.github.sirblobman.combatlogx.api.object.TagReason;
import com.github.sirblobman.combatlogx.api.object.TagType;
import com.github.sirblobman.api.shaded.adventure.text.Component;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.configuration.CitizensConfiguration;
import combatlogx.expansion.compatibility.citizens.manager.CombatNpcManager;
import combatlogx.expansion.compatibility.citizens.manager.InventoryManager;
import combatlogx.expansion.compatibility.citizens.object.CombatNPC;
import net.citizensnpcs.api.npc.NPC;

public final class ListenerJoin extends CitizensExpansionListener {
    public ListenerJoin(@NotNull CitizensExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void beforeLogin(AsyncPlayerPreLoginEvent e) {
        printDebug("Detected AsyncPlayerPreLoginEvent...");

        UUID playerId = e.getUniqueId();
        printDebug("Checking if player with uuid=" + playerId + " can login...");
        if (shouldAllowLogin(playerId)) {
            printDebug("Login allowed, ignoring event.");
            return;
        }

        CommandSender console = Bukkit.getConsoleSender();
        LanguageManager languageManager = getLanguageManager();
        String path = ("expansion.citizens-compatibility.prevent-join");
        Component npcMessage = languageManager.getMessage(console, path);
        e.disallow(Result.KICK_OTHER, ComponentHelper.toLegacy(npcMessage));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e) {
        printDebug("Detected PlayerJoinEvent...");

        Player player = e.getPlayer();
        printDebug("Player: " + player.getName());

        printDebug("Disabled item pickup for player.");
        player.setCanPickupItems(false);

        CombatNpcManager combatNpcManager = getCombatNpcManager();
        CombatNPC combatNPC = combatNpcManager.getNPC(player);
        if (combatNPC != null) {
            printDebug("Combat NPC exists for player, removing.");
            combatNpcManager.remove(combatNPC);
        }

        BukkitScheduler scheduler = Bukkit.getScheduler();
        Runnable task = () -> {
            printDebug("Scheduled punishment running.");
            punish(player);

            printDebug("Finished punishment, allowing item pickup.");
            player.setCanPickupItems(true);
        };

        JavaPlugin plugin = getJavaPlugin();
        scheduler.scheduleSyncDelayedTask(plugin, task, 1L);
        printDebug("Scheduled punishment for one tick after login.");
    }

    private boolean shouldAllowLogin(@NotNull UUID uuid) {
        CitizensConfiguration configuration = getCitizensConfiguration();
        if (!configuration.isPreventLogin()) {
            printDebug("Prevent login option disabled, login allowed.");
            return true;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        CombatNpcManager combatNpcManager = getCombatNpcManager();
        CombatNPC combatNPC = combatNpcManager.getNPC(offlinePlayer);
        if (combatNPC == null) {
            printDebug("Combat NPC not found for that player, login allowed.");
            return true;
        }

        NPC originalNPC = combatNPC.getOriginalNPC();
        if (originalNPC == null || !originalNPC.isSpawned()) {
            printDebug("Combat NPC was despawned or doesn't exist, login allowed.");
            return true;
        }

        printDebug("Combat NPC exists and is spawned, login blocked.");
        return false;
    }

    private void punish(@NotNull Player player) {
        if (player.hasMetadata("NPC")) {
            printDebug("Could not punish player, they are an NPC.");
            return;
        }

        CombatNpcManager combatNpcManager = getCombatNpcManager();
        YamlConfiguration playerData = combatNpcManager.getData(player);
        if (!playerData.getBoolean("citizens-compatibility.punish")) {
            printDebug("Punishment set to false in player data.");
            return;
        }

        playerData.set("citizens-compatibility.punish", false);
        combatNpcManager.saveData(player);
        printDebug("Setting punish to false in player data to prevent double punishment.");

        CitizensConfiguration configuration = getCitizensConfiguration();
        if (configuration.isStoreLocation()) {
            Location location = combatNpcManager.loadLocation(player);
            if (location != null) {
                printDebug("Teleporting player to last known location for NPC.");
                player.teleport(location, TeleportCause.PLUGIN);
            }

            playerData.set("citizens-compatibility.location", null);
        }

        if (configuration.isStoreInventory()) {
            printDebug("Clearing player inventory.");
            PlayerInventory playerInventory = player.getInventory();
            playerInventory.clear();
        }

        double health = combatNpcManager.loadHealth(player);
        setHealth(player, health);
        printDebug("Set player health to " + health);

        if (health <= 0.0D) {
            printDebug("Player died or health was invalid, removing stored inventory to prevent duplication.");
            playerData.set("citizens-compatibility.inventory", null);
            playerData.set("citizens-compatibility.armor", null);
        }

        if (configuration.isStoreInventory()) {
            printDebug("Restoring player inventory if possible.");
            InventoryManager inventoryManager = getInventoryManager();
            inventoryManager.restoreInventory(player);
        }

        if (configuration.isTagPlayer() && health > 0.0D) {
            ICombatManager combatManager = getCombatManager();
            combatManager.tag(player, null, TagType.UNKNOWN, TagReason.UNKNOWN);
        }
    }

    private void setHealth(@NotNull Player player, double health) {
        if (Double.isInfinite(health) || Double.isNaN(health)) {
            health = 0.0D;
        }

        ICombatLogX combatLogX = getCombatLogX();
        MultiVersionHandler multiVersionHandler = combatLogX.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();

        double maxHealth = entityHandler.getMaxHealth(player);
        if (maxHealth < health) {
            entityHandler.setMaxHealth(player, health);
        }

        player.setHealth(health);
    }
}
