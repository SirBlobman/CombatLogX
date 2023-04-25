package combatlogx.expansion.compatibility.citizens.listener;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.sirblobman.api.folia.scheduler.TaskScheduler;
import com.github.sirblobman.api.language.ComponentHelper;
import com.github.sirblobman.api.language.LanguageManager;
import com.github.sirblobman.api.plugin.ConfigurablePlugin;
import com.github.sirblobman.api.shaded.adventure.text.Component;

import combatlogx.expansion.compatibility.citizens.CitizensExpansion;
import combatlogx.expansion.compatibility.citizens.configuration.CitizensConfiguration;
import combatlogx.expansion.compatibility.citizens.manager.CombatNpcManager;
import combatlogx.expansion.compatibility.citizens.object.CombatNPC;
import combatlogx.expansion.compatibility.citizens.task.PunishTask;
import net.citizensnpcs.api.npc.NPC;

public final class ListenerJoin extends CitizensExpansionListener {
    public ListenerJoin(@NotNull CitizensExpansion expansion) {
        super(expansion);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void beforeLogin(AsyncPlayerPreLoginEvent e) {
        printDebug("Detected AsyncPlayerPreLoginEvent...");

        UUID playerId = e.getUniqueId();
        printDebug("Checking if player with uuid=" + playerId + " can login...");
        if (shouldAllowLogin(playerId)) {
            printDebug("Login allowed, ignoring event.");
            return;
        }

        CommandSender console = Bukkit.getConsoleSender();
        LanguageManager languageManager = getLanguageManager();
        String path = ("expansion.citizens-compatibility.prevent-join");
        Component npcMessage = languageManager.getMessage(console, path);
        e.disallow(Result.KICK_OTHER, ComponentHelper.toLegacy(npcMessage));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e) {
        printDebug("Detected PlayerJoinEvent...");

        Player player = e.getPlayer();
        printDebug("Player: " + player.getName());

        printDebug("Disabled item pickup for player.");
        player.setCanPickupItems(false);

        CombatNpcManager combatNpcManager = getCombatNpcManager();
        CombatNPC combatNPC = combatNpcManager.getNPC(player);
        if (combatNPC != null) {
            printDebug("Combat NPC exists for player, removing.");
            combatNpcManager.remove(combatNPC);
        }

        PunishTask punishTask = new PunishTask(getCitizensExpansion(), player);
        TaskScheduler<ConfigurablePlugin> scheduler = getCombatLogX().getFoliaHelper().getScheduler();
        scheduler.scheduleEntityTask(punishTask);
    }

    private boolean shouldAllowLogin(@NotNull UUID uuid) {
        CitizensConfiguration configuration = getCitizensConfiguration();
        if (!configuration.isPreventLogin()) {
            printDebug("Prevent login option disabled, login allowed.");
            return true;
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        CombatNpcManager combatNpcManager = getCombatNpcManager();
        CombatNPC combatNPC = combatNpcManager.getNPC(offlinePlayer);
        if (combatNPC == null) {
            printDebug("Combat NPC not found for that player, login allowed.");
            return true;
        }

        NPC originalNPC = combatNPC.getOriginalNPC();
        if (!originalNPC.isSpawned()) {
            printDebug("Combat NPC was removed, login allowed.");
            return true;
        }

        printDebug("Combat NPC exists and is spawned, login blocked.");
        return false;
    }
}
