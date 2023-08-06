package combatlogx.expansion.compatibility.husksync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.github.sirblobman.api.plugin.ConfigurablePlugin;
import com.github.sirblobman.api.utility.ItemUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.PunishConfiguration;
import com.github.sirblobman.combatlogx.api.event.PlayerPunishEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.object.KillTime;

import net.william278.husksync.data.DataSaveCause;
import net.william278.husksync.data.ItemData;
import net.william278.husksync.data.StatusData;
import net.william278.husksync.data.UserData;
import net.william278.husksync.event.BukkitDataSaveEvent;
import net.william278.husksync.player.User;

public final class ListenerHuskSync extends ExpansionListener {
    private final Set<UUID> punishedPlayers;
    private final Map<UUID, PlayerData> dataMap;

    public ListenerHuskSync(@NotNull HuskSyncExpansion expansion) {
        super(expansion);
        this.punishedPlayers = new HashSet<>();
        this.dataMap = new HashMap<>();
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
        playerData.setOldInventory(player.getInventory().getContents().clone());

        this.dataMap.put(playerId, playerData);
        printDebug("Stored player data for later syncing.");
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBukkitDataSave(@NotNull BukkitDataSaveEvent e) {
        printDebug("Detected BukkitDataSaveEvent...");

        DataSaveCause saveCause = e.getSaveCause();
        if (saveCause != DataSaveCause.DISCONNECT) {
            printDebug("DataSaveCause is not 'DISCONNECT', ignoring event.");
            return;
        }

        User user = e.getUser();
        PlayerData playerData = this.dataMap.remove(user.uuid);
        if (playerData == null) {
            printDebug("User does not have death punishment data from CombatLogX, ignoring event.");
            return;
        }

        printDebug("User Name: " + user.username);
        UserData userData = e.getUserData();
        checkData(playerData, userData);
        e.setUserData(userData);
        printDebug("Set modified data in BukkitDataSaveEvent.");
    }

    private void checkData(@NotNull PlayerData playerData, @NotNull UserData userData) {
        Player player = playerData.getPlayer();
        printDebug("Checking player data for player '" + player.getUniqueId() + "'.");

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

        if (!playerData.isKeepInventory()) {
            printDebug("Death event had keepInventory = false, fetching items...");

            ItemStack[] oldInventory = playerData.getOldInventory();
            List<ItemStack> drops = new ArrayList<>();
            for (ItemStack stack : oldInventory) {
                if (!ItemUtility.isAir(stack)) {
                    drops.add(stack);
                }
            }

            Location location = playerData.getLocation();
            ConfigurablePlugin plugin = getJavaPlugin();
            DropItemsTask task = new DropItemsTask(plugin, location, drops);
            plugin.getFoliaHelper().getScheduler().scheduleLocationTask(task);
            printDebug("Scheduled task to drop items.");

            Optional<ItemData> optionalItemData = userData.getInventory();
            ItemData itemData = optionalItemData.orElse(ItemData.empty());
            itemData.serializedItems = "";
            printDebug("Set HuskSync inventory to empty.");
        }

        printDebug("Finished modifying HuskSync save data.");
    }
}
