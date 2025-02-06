package combatlogx.expansion.compatibility.husksync;

import com.github.sirblobman.api.plugin.ConfigurablePlugin;
import com.github.sirblobman.api.utility.ItemUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.PunishConfiguration;
import com.github.sirblobman.combatlogx.api.event.PlayerPunishEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.object.KillTime;
import net.william278.husksync.HuskSync;
import net.william278.husksync.api.BukkitHuskSyncAPI;
import net.william278.husksync.api.HuskSyncAPI;
import net.william278.husksync.data.BukkitData;
import net.william278.husksync.data.Data;
import net.william278.husksync.data.DataSnapshot;
import net.william278.husksync.event.BukkitDataSaveEvent;
import net.william278.husksync.user.User;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class ListenerHuskSync extends ExpansionListener {
    private final Set<UUID> punishedPlayers;
    private final Set<UUID> alreadyDied;
    private final Map<UUID, PlayerData> dataMap;
    private final HuskSyncAPI huskSyncAPI;

    public ListenerHuskSync(@NotNull HuskSyncExpansion expansion) {
        super(expansion);
        this.punishedPlayers = new HashSet<>();
        this.alreadyDied = new HashSet<>();
        this.dataMap = new HashMap<>();
        this.huskSyncAPI = BukkitHuskSyncAPI.getInstance();
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
        if(alreadyDied.remove(e.getEntity().getUniqueId())) {
            e.setDeathMessage(null);
            return;
        }
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

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if(alreadyDied.contains(e.getPlayer().getUniqueId())) {
            // this may or may not cause a race condition, but it's less annoying than showing the death message twice
            getJavaPlugin().getServer().getScheduler().runTaskLater(getJavaPlugin(), () -> {
                alreadyDied.remove(e.getPlayer().getUniqueId());
            }, 20L*5);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBukkitDataSave(@NotNull BukkitDataSaveEvent e) {
        printDebug("Detected BukkitDataSaveEvent...");

        DataSnapshot.SaveCause saveCause = e.getSaveCause();
        if (!saveCause.getDisplayName().equalsIgnoreCase("DISCONNECT")) {
            printDebug("DataSaveCause is not 'DISCONNECT', ignoring event.");
            return;
        }

        User user = e.getUser();
        PlayerData playerData = this.dataMap.remove(user.getUuid());
        if (playerData == null) {
            printDebug("User does not have death punishment data from CombatLogX, ignoring event.");
            return;
        }


        printDebug("User Name: " + user.getUsername());
        DataSnapshot.Packed userData = e.getData();
        checkData(playerData, userData);
        printDebug("Set modified data in BukkitDataSaveEvent.");
    }

    private @NotNull HuskSyncAPI getHuskSyncAPI() {
        return this.huskSyncAPI;
    }

    private void checkData(@NotNull PlayerData playerData, @NotNull DataSnapshot.Packed userData) {
        Player player = playerData.getPlayer();
        printDebug("Checking player data for player '" + player.getUniqueId() + "'.");

        HuskSyncAPI api = getHuskSyncAPI();
        HuskSync huskSync = api.getPlugin();
        userData.edit(huskSync, unpacked -> edit(playerData, unpacked));
    }

    private void edit(@NotNull PlayerData playerData, @NotNull DataSnapshot.Unpacked unpacked) {
        Optional<Data.Experience> optionalExperience = unpacked.getExperience();
        if (optionalExperience.isPresent()) {
            Data.Experience experience = optionalExperience.get();
            if (!playerData.isKeepLevel()) {
                experience.setTotalExperience(playerData.getTotalExperience());
                experience.setExpLevel(playerData.getNewLevel());
                experience.setExpProgress(playerData.getNewExperience());
                unpacked.setExperience(experience);
                printDebug("Set experience data in HuskSync.");
            }
        }

        Optional<Data.Health> optionalHealth = unpacked.getHealth();
        if (optionalHealth.isPresent()) {
            Data.Health health = optionalHealth.get();
            health.setHealth(0.0D);
            unpacked.setHealth(health);
            alreadyDied.add(playerData.getPlayer().getUniqueId());
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

            Optional<Data.Items.Inventory> optionalInventory = unpacked.getInventory();
            Data.Items.Inventory inventory = optionalInventory.orElse(BukkitData.Items.Inventory.empty());
            inventory.setContents(BukkitData.Items.Inventory.empty());
            unpacked.setInventory(inventory);
            printDebug("Set HuskSync inventory to empty.");
        }

        printDebug("Finished modifying HuskSync save data.");
    }
}
