package combatlogx.expansion.compatibility.husksync;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.utility.ItemUtility;
import com.github.sirblobman.combatlogx.api.event.PlayerPunishEvent;
import com.github.sirblobman.combatlogx.api.expansion.Expansion;
import me.william278.husksync.PlayerData;
import me.william278.husksync.bukkit.api.HuskSyncAPI;
import me.william278.husksync.bukkit.data.DataSerializer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class ExpansionListener implements Listener {

    private final Plugin plugin;
    private final ConfigurationManager configurationManager;
    private final HuskSyncAPI huskSyncApi = HuskSyncAPI.getInstance();

    private final Set<UUID> punishedPlayers = new HashSet<>();

    public ExpansionListener(Plugin plugin, Expansion expansion) {
        this.configurationManager = expansion.getConfigurationManager();
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPunish(PlayerPunishEvent event) {
        final YamlConfiguration configuration = configurationManager.get("punish.yml");
        final String killOptionString = configuration.getString("kill-time");

        if(!(killOptionString == null || killOptionString.equals("QUIT"))) return;

        punishedPlayers.add(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final UUID uniqueId = player.getUniqueId();
        final boolean keepInventory = event.getKeepInventory();
        final boolean keepLevel = event.getKeepLevel();

        if(!punishedPlayers.remove(uniqueId)) return;

        try {

            CompletableFuture<PlayerData> futurePlayerData = huskSyncApi.getPlayerData(uniqueId);
            futurePlayerData.thenAcceptAsync(playerData -> new BukkitRunnable() {
                @Override
                public void run() {
                    try {

                        if(!keepInventory) {
                            ItemStack[] inventory = DataSerializer.deserializeInventory(playerData.getSerializedInventory());

                            World world = player.getWorld();
                            Location location = player.getLocation();

                            for(final ItemStack itemStack : inventory) {
                                if(ItemUtility.isAir(itemStack)) continue;
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
                        plugin.getLogger().log(Level.SEVERE, "An error occurred saving player data!", ex);
                    }
                }
            }.runTask(plugin));
        } catch(IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "An error occurred fetching player data!", ex);
        }
    }

}
