package combatlogx.expansion.compatibility.husksync;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.ItemUtility;
import com.github.sirblobman.combatlogx.api.event.PlayerPunishEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import me.william278.husksync.PlayerData;
import me.william278.husksync.bukkit.api.HuskSyncAPI;
import me.william278.husksync.bukkit.data.DataSerializer;

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
            futurePlayerData.thenAcceptAsync(playerData -> new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        if(!keepInventory) {
                            ItemStack[] inventory = DataSerializer.deserializeInventory(playerData.getSerializedInventory());

                            World world = player.getWorld();
                            Location location = player.getLocation();

                            for(final ItemStack itemStack : inventory) {
                                if(ItemUtility.isAir(itemStack)) {
                                    continue;
                                }
                                
                                world.dropItemNaturally(location, itemStack);
                            }

                            String serializedInventory = DataSerializer.serializeInventory(new ItemStack[0]);
                            playerData.setSerializedInventory(serializedInventory);
                        }

                        if(!keepLevel) {
                            playerData.setTotalExperience(event.getNewTotalExp());
                            playerData.setExpLevel(event.getNewLevel());
                            playerData.setExpProgress(event.getNewExp());
                        }

                        playerData.setHealth(0);

                        huskSyncApi.updatePlayerData(playerData);
                    } catch(IOException | ClassNotFoundException ex) {
                        Logger logger = getExpansionLogger();
                        logger.log(Level.SEVERE, "An error occurred saving player data!", ex);
                    }
                }
            }.runTask(getJavaPlugin()));
        } catch(IOException ex) {
            Logger logger = getExpansionLogger();
            logger.log(Level.SEVERE, "An error occurred fetching player data!", ex);
        }
    }
}
