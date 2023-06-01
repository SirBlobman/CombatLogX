package combatlogx.expansion.compatibility.husksync;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.github.sirblobman.api.plugin.ConfigurablePlugin;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.PunishConfiguration;
import com.github.sirblobman.combatlogx.api.event.PlayerPunishEvent;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.object.KillTime;

import net.william278.husksync.api.HuskSyncAPI;
import net.william278.husksync.data.BukkitInventoryMap;
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

        Location location = player.getLocation();
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

                                ConfigurablePlugin plugin = getJavaPlugin();
                                CheckPlayerDataTask task = new CheckPlayerDataTask(plugin, location, api, playerData);
                                plugin.getFoliaHelper().getScheduler().scheduleLocationTask(task);
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
}
