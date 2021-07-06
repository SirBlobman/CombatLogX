package combatlogx.expansion.compatibility.citizens.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.github.sirblobman.api.configuration.ConfigurationManager;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.nms.EntityHandler;
import com.github.sirblobman.api.nms.MultiVersionHandler;
import com.github.sirblobman.combatlogx.api.ICombatLogX;
import com.github.sirblobman.combatlogx.api.expansion.ExpansionListener;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.manager.CombatNpcManager;
import combatlogx.expansion.compatibility.citizens.object.CombatNPC;

public final class ListenerJoin extends ExpansionListener {
    private final CitizensExpansion expansion;

    public ListenerJoin(CitizensExpansion expansion) {
        super(expansion);
        this.expansion = expansion;
    }

    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void beforeLogin(AsyncPlayerPreLoginEvent e) {
        UUID uuid = e.getUniqueId();
        if(shouldAllowLogin(uuid)) return;

        CommandSender console = Bukkit.getConsoleSender();
        LanguageManager languageManager = getLanguageManager();
        String npcMessage = languageManager.getMessage(console, "expansion.citizens-join-deny", null,
                true);
        e.disallow(Result.KICK_OTHER, npcMessage);
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.setCanPickupItems(false);

        CombatNpcManager combatNpcManager = this.expansion.getCombatNpcManager();
        CombatNPC combatNPC = combatNpcManager.getNPC(player);
        if(combatNPC != null) {
            combatNpcManager.remove(combatNPC);
        }

        BukkitScheduler scheduler = Bukkit.getScheduler();
        Runnable task = () -> {
            punish(player);
            player.setCanPickupItems(true);
        };

        JavaPlugin plugin = getJavaPlugin();
        scheduler.scheduleSyncDelayedTask(plugin, task, 1L);
    }

    private YamlConfiguration getConfiguration() {
        ConfigurationManager configurationManager = getExpansionConfigurationManager();
        return configurationManager.get("citizens.yml");
    }

    private boolean shouldAllowLogin(UUID uuid) {
        YamlConfiguration configuration = getConfiguration();
        if(!configuration.getBoolean("prevent-login")) return true;

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        CombatNpcManager combatNpcManager = this.expansion.getCombatNpcManager();
        CombatNPC npc = combatNpcManager.getNPC(offlinePlayer);
        return (npc == null || npc.getOriginalNPC() == null || !npc.getOriginalNPC().isSpawned());
    }

    private void punish(Player player) {
        if(player == null || player.hasMetadata("NPC")) return;

        CombatNpcManager combatNpcManager = this.expansion.getCombatNpcManager();
        YamlConfiguration playerData = combatNpcManager.getData(player);
        if(!playerData.getBoolean("citizens-compatibility.punish")) {
            return;
        }

        playerData.set("citizens-compatibility.punish", false);
        combatNpcManager.saveData(player);

        YamlConfiguration configuration = getConfiguration();
        if(configuration.getBoolean("store-location")) {
            Location location = combatNpcManager.loadLocation(player);
            if(location != null) player.teleport(location, TeleportCause.PLUGIN);
            playerData.set("citizens-compatibility.location", null);
        }

        if(configuration.getBoolean("store-inventory")) {
            PlayerInventory playerInventory = player.getInventory();
            playerInventory.clear();
            player.updateInventory();
        }

        double health = combatNpcManager.loadHealth(player);
        setHealth(player, health);
        if(health <= 0.0D) {
            playerData.set("citizens-compatibility.inventory", null);
            playerData.set("citizens-compatibility.armor", null);
        }

        if(configuration.getBoolean("store-inventory")) {
            combatNpcManager.restoreInventory(player);
            player.updateInventory();
        }
    }

    private void setHealth(Player player, double health) {
        if(Double.isInfinite(health) || Double.isNaN(health)) {
            health = 0.0D;
        }

        ICombatLogX combatLogX = getCombatLogX();
        MultiVersionHandler multiVersionHandler = combatLogX.getMultiVersionHandler();
        EntityHandler entityHandler = multiVersionHandler.getEntityHandler();

        double maxHealth = entityHandler.getMaxHealth(player);
        if(maxHealth < health) {
            entityHandler.setMaxHealth(player, health);
        }

        player.setHealth(health);
    }
}
