package combatlogx.expansion.compatibility.husksync;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.github.sirblobman.api.plugin.ConfigurablePlugin;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.PunishConfiguration;
import com.github.sirblobman.combatlogx.api.event.PlayerPunishEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.object.KillTime;

import net.william278.husksync.api.HuskSyncAPI;
import net.william278.husksync.data.BukkitInventoryMap;
import net.william278.husksync.data.ItemData;
import net.william278.husksync.data.StatusData;
import net.william278.husksync.data.UserData;
import net.william278.husksync.player.User;

public final class ListenerHuskSync extends ExpansionListener {
    private final HuskSyncAPI huskSyncApi;
    private final Set<UUID> punishedPlayers;

    public ListenerHuskSync(@NotNull HuskSyncExpansion expansion) {
        super(expansion);
        this.huskSyncApi = HuskSyncAPI.getInstance();
        this.punishedPlayers = new HashSet<>();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPunish(PlayerPunishEvent event) {
        printDebug("Detected PlayerPunishEvent...");

        ICombatLogX combatLogX = getCombatLogX();
        PunishConfiguration punishConfiguration = combatLogX.getPunishConfiguration();
        KillTime killTime = punishConfiguration.getKillTime();
        if (killTime != KillTime.QUIT) {
            printDebug("Kill time is not QUIT, ignoring.");
            return;
        }

        UUID playerId = event.getPlayer().getUniqueId();
        this.punishedPlayers.add(playerId);
        printDebug("Added player '" + playerId + "' to punishments map.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent e) {
        printDebug("Detected PlayerDeathEvent...");

        Player player = e.getEntity();
        UUID playerId = player.getUniqueId();
        if (!this.punishedPlayers.remove(playerId)) {
            printDebug("Punishments map did not contain player '" + playerId + "'. Ignoring.");
            return;
        }
        Location location = player.getLocation();
        boolean keepInventory = e.getKeepInventory();
        boolean keepLevel = e.getKeepLevel();
        int totalExperience = e.getNewTotalExp();
        int newLevel = e.getNewLevel();
        float newExperience = e.getNewExp();

        HuskSyncAPI api = getHuskSyncAPI();
        printDebug("Fetching user with id '" + playerId + "'...");
        api.getUser(playerId).thenAcceptAsync(optionalUser -> {
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                printDebug("Found user, fetching data...");
                api.getUserData(user).thenAcceptAsync(optionalData -> {
                    if (optionalData.isPresent()) {
                        printDebug("Found data, fetching inventory...");
                        UserData userData = optionalData.get();
                        api.getPlayerInventory(user).thenAcceptAsync(optionalInventory -> {
                            if (optionalInventory.isPresent()) {
                                printDebug("Found inventory.");
                                BukkitInventoryMap inventory = optionalInventory.get();
                                PlayerData playerData = new PlayerData(user, userData, inventory, location);
                                playerData.setKeepInventory(keepInventory);
                                playerData.setKeepLevel(keepLevel);
                                playerData.setTotalExperience(totalExperience);
                                playerData.setNewLevel(newLevel);
                                playerData.setNewExperience(newExperience);

                                printDebug("Syncing death data to HuskSync...");
                                checkData(playerData);
                            }
                        });
                    }
                });
            }
        });
    }

    private @NotNull HuskSyncAPI getHuskSyncAPI() {
        return this.huskSyncApi;
    }

    private void checkData(@NotNull PlayerData playerData) {
        User user = playerData.getUser();
        UserData userData = playerData.getUserData();
        HuskSyncAPI api = getHuskSyncAPI();

        if(!playerData.isKeepInventory()) {
            printDebug("Death event had keepInventory = false, fetching items...");
            BukkitInventoryMap inventoryMap = playerData.getInventory();
            List<ItemStack> drops = new ArrayList<>();
            Collections.addAll(drops, inventoryMap.getContents());

            Optional<ItemData> optionalItemData = userData.getInventory();
            if (optionalItemData.isPresent()) {
                ItemData itemData = optionalItemData.get();
                itemData.serializedItems = "";
                printDebug("Set husk sync inventory to empty.");
            }

            Location location = playerData.getLocation();
            ConfigurablePlugin plugin = getJavaPlugin();
            DropItemsTask task = new DropItemsTask(plugin, location, drops);
            plugin.getFoliaHelper().getScheduler().scheduleLocationTask(task);
            printDebug("Scheduled task to drop items.");
        }

        Optional<StatusData> optionalStatus = userData.getStatus();
        if (optionalStatus.isPresent()) {
            StatusData statusData = optionalStatus.get();
            if(!playerData.isKeepLevel()) {
                statusData.totalExperience = playerData.getTotalExperience();
                statusData.expLevel = playerData.getNewLevel();
                statusData.expProgress = playerData.getNewExperience();
                printDebug("Set experience data in HuskSync.");
            }

            statusData.health = 0.0D;
            printDebug("Set player health to 0.0 in HuskSync.");
        }

        printDebug("Syncing HuskSync user data for player '" + user.uuid + "'...");
        api.setUserData(user, userData).whenCompleteAsync((success, failure) -> {
            if (failure != null) {
                printDebug("Failed to sync user data.");
                getExpansionLogger().log(Level.WARNING, "Failed to sync HuskSync User Data:", failure);
            } else {
                printDebug("Successfully synced data.");
            }
        });
    }
}
