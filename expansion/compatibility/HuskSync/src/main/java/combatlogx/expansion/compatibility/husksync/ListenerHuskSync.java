package combatlogx.expansion.compatibility.husksync;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

        PlayerData playerData = new PlayerData(player, player.getLocation());
        playerData.setKeepInventory(e.getKeepInventory());
        playerData.setKeepLevel(e.getKeepLevel());
        playerData.setTotalExperience(e.getNewTotalExp());
        playerData.setNewLevel(e.getNewLevel());
        playerData.setNewExperience(e.getNewExp());

        HuskSyncAPI api = getHuskSyncAPI();
        printDebug("Fetching user with id '" + playerId + "'...");

        CompletableFuture.supplyAsync(() -> {
            Optional<User> optionalUser = api.getUser(playerId).join();
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                playerData.setUser(user);

                Optional<UserData> optionalUserData = api.getUserData(user).join();
                optionalUserData.ifPresent(playerData::setUserData);

                Optional<BukkitInventoryMap> optionalInventory = api.getPlayerInventory(user).join();
                optionalInventory.ifPresent(playerData::setInventory);
            }

            return playerData;
        }).thenAcceptAsync(this::checkData);

        if (!e.getKeepInventory()) {
            player.getInventory().clear();
            printDebug("Cleared local player inventory.");
        }
    }

    private @NotNull HuskSyncAPI getHuskSyncAPI() {
        return this.huskSyncApi;
    }

    private void checkData(@NotNull PlayerData playerData) {
        Player player = playerData.getPlayer();
        printDebug("Checking player data for player '" + player.getUniqueId() + "'.");

        Optional<User> optionalUser = playerData.getUser();
        if (!optionalUser.isPresent()) {
            printDebug("Missing user from HuskSync.");
            return;
        }

        Optional<UserData> optionalUserData = playerData.getUserData();
        if (!optionalUserData.isPresent()) {
            printDebug("Missing user data from HuskSync.");
            return;
        }

        Optional<BukkitInventoryMap> optionalInventory = playerData.getInventory();
        if (!optionalInventory.isPresent()) {
            printDebug("Missing inventory from HuskSync.");
            return;
        }

        User user = optionalUser.get();
        UserData userData = optionalUserData.get();
        BukkitInventoryMap inventoryMap = optionalInventory.get();

        Optional<StatusData> optionalStatus = userData.getStatus();
        if (optionalStatus.isPresent()) {
            StatusData statusData = optionalStatus.get();
            if (!playerData.isKeepLevel()) {
                statusData.totalExperience = playerData.getTotalExperience();
                statusData.expLevel = playerData.getNewLevel();
                statusData.expProgress = playerData.getNewExperience();
                printDebug("Set experience data in HuskSync.");
            }

            statusData.health = 0.0D;
            printDebug("Set player health to 0.0 in HuskSync.");
        }

        HuskSyncAPI api = getHuskSyncAPI();
        if (!playerData.isKeepInventory()) {
            printDebug("Death event had keepInventory = false, fetching items...");
            List<ItemStack> drops = new ArrayList<>();
            Collections.addAll(drops, inventoryMap.getContents());

            Location location = playerData.getLocation();
            ConfigurablePlugin plugin = getJavaPlugin();
            DropItemsTask task = new DropItemsTask(plugin, location, drops);
            plugin.getFoliaHelper().getScheduler().scheduleLocationTask(task);
            printDebug("Scheduled task to drop items.");

            Optional<ItemData> optionalItemData = userData.getInventory();
            ItemData itemData = optionalItemData.orElse(ItemData.empty());
            itemData.serializedItems = "";
            printDebug("Set HuskSync inventory to empty for player '" + user.uuid + "'.");
        }

        api.setUserData(user, userData).whenCompleteAsync(this::printSyncResult).join();
        printDebug("Finished HuskSync user data sync for player '" + user.uuid + "'.");
    }

    private void printSyncResult(@Nullable Void success, @Nullable Throwable failure) {
        if (failure != null) {
            Logger logger = getExpansionLogger();
            logger.log(Level.WARNING, "Failed to submit user data to HuskSync:", failure);
        } else {
            printDebug("Successfully synced data.");
        }
    }
}
