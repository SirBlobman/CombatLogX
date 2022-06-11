package combatlogx.expansion.compatibility.husksync;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.combatlogx.api.event.PlayerPunishEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import net.william278.husksync.bukkit.events.SyncEvent;
import net.william278.husksync.PlayerData;
import net.william278.husksync.bukkit.api.HuskSyncAPI;
import net.william278.husksync.bukkit.data.DataSerializer;

public final class ListenerHuskSync extends ExpansionListener {
    private final HuskSyncAPI huskSyncApi;
    private final Set<UUID> punishedPlayers;

    public ListenerHuskSync(Expansion expansion) {
        super(expansion);
        this.huskSyncApi = HuskSyncAPI.getInstance();
        this.punishedPlayers = new HashSet<>();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPunish(PlayerPunishEvent event) {
        ConfigurationManager configurationManager = getPluginConfigurationManager();
        YamlConfiguration configuration = configurationManager.get("punish.yml");
        String killOptionString = configuration.getString("kill-time");

        if(!(killOptionString == null || killOptionString.equals("QUIT"))) {
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

        if(!punishedPlayers.remove(playerId)) {
            return;
        }

        try {
            CompletableFuture<PlayerData> futurePlayerData = huskSyncApi.getPlayerData(playerId);
            futurePlayerData.thenAcceptAsync(playerData -> {
                if(!keepInventory) {
                    String serializedInventory = DataSerializer.serializeInventory(new ItemStack[0]);
                    playerData.setSerializedInventory(serializedInventory);
                }

                if(!keepLevel) {
                    playerData.setTotalExperience(event.getNewTotalExp());
                    playerData.setExpLevel(event.getNewLevel());
                    playerData.setExpProgress(event.getNewExp());
                }

                playerData.setHealth(0);

                try {
                    huskSyncApi.updatePlayerData(playerData);
                } catch(IOException ex) {
                    Logger logger = getExpansionLogger();
                    logger.log(Level.SEVERE, "An error occurred saving player data!", ex);
                }
            });
        } catch(IOException ex) {
            Logger logger = getExpansionLogger();
            logger.log(Level.SEVERE, "An error occurred fetching player data!", ex);
        }
    }

    @EventHandler
    public void onSync(SyncEvent event) {
        Player player = event.getPlayer();
        PlayerData data = event.getData();
        if(player.getHealth() <= 0 && data.getHealth() > 0) player.spigot().respawn();
    }
}
