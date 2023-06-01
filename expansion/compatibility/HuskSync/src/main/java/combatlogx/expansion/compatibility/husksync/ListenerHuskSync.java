package combatlogx.expansion.compatibility.husksync;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.github.sirblobman.api.utility.ItemUtility;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.PunishConfiguration;
import com.github.sirblobman.combatlogx.api.event.PlayerPunishEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.object.KillTime;

import net.william278.husksync.api.HuskSyncAPI;
import net.william278.husksync.data.BukkitInventoryMap;
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
        ICombatLogX combatLogX = getCombatLogX();
        PunishConfiguration punishConfiguration = combatLogX.getPunishConfiguration();
        KillTime killTime = punishConfiguration.getKillTime();
        if (killTime != KillTime.QUIT) {
            return;
        }

        UUID playerId = event.getPlayer().getUniqueId();
        this.punishedPlayers.add(playerId);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        UUID playerId = player.getUniqueId();
        if (!this.punishedPlayers.remove(playerId)) {
            return;
        }

        boolean keepInventory = e.getKeepInventory();
        boolean keepLevel = e.getKeepLevel();
        int totalExperience = e.getNewTotalExp();
        int newLevel = e.getNewLevel();
        float newExperience = e.getNewExp();

        HuskSyncAPI api = getHuskSyncAPI();
        api.getUser(playerId).thenAcceptAsync(optionalUser -> {
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                api.getUserData(user).thenAcceptAsync(optionalData -> {
                    if (optionalData.isPresent()) {
                        UserData userData = optionalData.get();
                        api.getPlayerInventory(user).thenAcceptAsync(optionalInventory -> {
                            if (optionalInventory.isPresent()) {
                                BukkitInventoryMap inventory = optionalInventory.get();
                                PlayerData playerData = new PlayerData(player, user, userData, inventory);
                                playerData.setKeepInventory(keepInventory);
                                playerData.setKeepLevel(keepLevel);
                                playerData.setTotalExperience(totalExperience);
                                playerData.setNewLevel(newLevel);
                                playerData.setNewExperience(newExperience);
                                checkUser(playerData);
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

    private void checkUser(@NotNull PlayerData playerData) {
        Player player = playerData.getPlayer();
        User user = playerData.getUser();
        UserData userData = playerData.getUserData();
        HuskSyncAPI api = getHuskSyncAPI();

        if(!playerData.isKeepInventory()) {
            World world = player.getWorld();
            Location location = player.getLocation();

            BukkitInventoryMap inventoryMap = playerData.getInventory();
            ItemStack[] contents = inventoryMap.getContents();
            for(ItemStack itemStack : contents) {
                if(ItemUtility.isAir(itemStack)) {
                    continue;
                }

                world.dropItemNaturally(location, itemStack);
            }

            api.setInventoryData(user, new ItemStack[0]);
        }

        Optional<StatusData> optionalStatus = userData.getStatus();
        if (optionalStatus.isPresent()) {
            StatusData statusData = optionalStatus.get();
            if(!playerData.isKeepLevel()) {
                statusData.totalExperience = playerData.getTotalExperience();
                statusData.expLevel = playerData.getNewLevel();
                statusData.expProgress = playerData.getNewExperience();
            }

            statusData.health = 0.0D;
            api.setUserData(user, userData);
        }
    }
}
