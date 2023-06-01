package combatlogx.expansion.compatibility.husksync;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.github.sirblobman.api.folia.details.LocationTaskDetails;
import com.github.sirblobman.api.utility.ItemUtility;

import net.william278.husksync.api.HuskSyncAPI;
import net.william278.husksync.data.BukkitInventoryMap;
import net.william278.husksync.data.StatusData;
import net.william278.husksync.data.UserData;
import net.william278.husksync.player.User;

public final class CheckPlayerDataTask extends LocationTaskDetails {
    private final HuskSyncAPI api;
    private final PlayerData playerData;

    public CheckPlayerDataTask(@NotNull Plugin plugin, @NotNull Location location,
                               @NotNull HuskSyncAPI api, @NotNull PlayerData playerData) {
        super(plugin, location);
        this.api = api;
        this.playerData = playerData;
    }

    @Override
    public void run() {
        PlayerData playerData = getPlayerData();
        User user = playerData.getUser();
        UserData userData = playerData.getUserData();
        HuskSyncAPI api = getHuskSyncAPI();

        if(!playerData.isKeepInventory()) {
            World world = getWorld();
            Location location = getLocation();

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

    private @NotNull HuskSyncAPI getHuskSyncAPI() {
        return this.api;
    }

    private @NotNull PlayerData getPlayerData() {
        return this.playerData;
    }

    private @NotNull World getWorld() {
        Location location = getLocation();
        return location.getWorld();
    }
}
