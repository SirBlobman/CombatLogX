package combatlogx.expansion.compatibility.husksync;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Player.Spigot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.configuration.PunishConfiguration;
import com.github.sirblobman.combatlogx.api.event.PlayerPunishEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;
import com.github.sirblobman.combatlogx.api.object.KillTime;

import net.william278.husksync.api.HuskSyncAPI;
import net.william278.husksync.data.ItemData;
import net.william278.husksync.data.StatusData;
import net.william278.husksync.data.UserData;
import net.william278.husksync.event.BukkitPreSyncEvent;
import net.william278.husksync.player.OnlineUser;

public final class ListenerHuskSync extends ExpansionListener {
    private final HuskSyncAPI huskSyncApi;
    private final Set<UUID> punishedPlayers;

    public ListenerHuskSync(Expansion expansion) {
        super(expansion);
        this.huskSyncApi = HuskSyncAPI.getInstance();
        this.punishedPlayers = new HashSet<>();
    }

    private @NotNull HuskSyncAPI getHuskSyncAPI() {
        return this.huskSyncApi;
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerId = player.getUniqueId();
        boolean keepInventory = event.getKeepInventory();
        boolean keepLevel = event.getKeepLevel();

        if (!this.punishedPlayers.remove(playerId)) {
            return;
        }

        HuskSyncAPI huskSyncAPI = getHuskSyncAPI();
        OnlineUser playerUser = huskSyncAPI.getUser(player);
        if (!keepInventory) {
            String emptyItems = huskSyncAPI.serializeItemStackArray(new ItemStack[0]).join();
            ItemData emptyData = new ItemData(emptyItems);
            playerUser.setInventory(emptyData);
        }

        StatusData statusData = playerUser.getStatus().join();
        if (!keepLevel) {
            statusData.totalExperience = event.getNewTotalExp();
            statusData.expLevel = event.getNewLevel();
            statusData.expProgress = event.getNewExp();
        }

        statusData.health = 0.0D;
    }

    @EventHandler
    public void onSync(BukkitPreSyncEvent e) {
        OnlineUser onlineUser = e.getUser();
        Player player = Bukkit.getPlayer(onlineUser.uuid);
        if (player == null) {
            return;
        }

        UserData userData = e.getUserData();
        Optional<StatusData> optionalStatusData = userData.getStatus();
        if (!optionalStatusData.isPresent()) {
            return;
        }

        StatusData statusData = optionalStatusData.get();
        double playerHealth = player.getHealth();

        if (playerHealth <= 0.0D && statusData.health >= 0.0D) {
            Spigot spigot = player.spigot();
            spigot.respawn();
        }
    }
}
